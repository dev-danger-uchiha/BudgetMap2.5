import pandas as pd
from datetime import datetime, timedelta
from typing import Dict, List
from sqlalchemy import text
from app import db
from models.geo_models import Lugar, Establecimiento, Notificacion
import json

class ReportEngine:
    
    @staticmethod
    def generar_reporte_visitas(establecimiento_id: int = None, 
                                fecha_inicio: datetime = None,
                                fecha_fin: datetime = None) -> Dict:
        
        if not fecha_inicio:
            fecha_inicio = datetime.now() - timedelta(days=30)
        if not fecha_fin:
            fecha_fin = datetime.now()
        
        query = """
            SELECT 
                DATE(r.created_at) as fecha,
                COUNT(*) as total_reservas,
                SUM(r.numero_personas) as total_personas,
                r.estado
            FROM reservas r
            WHERE r.created_at BETWEEN :inicio AND :fin
        """
        
        params = {'inicio': fecha_inicio, 'fin': fecha_fin}
        
        if establecimiento_id:
            query += " AND r.establecimiento_id = :est_id"
            params['est_id'] = establecimiento_id
        
        query += " GROUP BY DATE(r.created_at), r.estado ORDER BY fecha"
        
        result = db.session.execute(text(query), params)
        
        datos = []
        for row in result:
            row_dict = row._mapping
            datos.append({
                'fecha': row_dict['fecha'].isoformat() if row_dict['fecha'] else None,
                'total_reservas': row_dict['total_reservas'],
                'total_personas': row_dict['total_personas'] or 0,
                'estado': row_dict['estado']
            })
        
        return {
            'tipo': 'reporte_visitas',
            'periodo': {
                'inicio': fecha_inicio.isoformat(),
                'fin': fecha_fin.isoformat()
            },
            'datos': datos,
            'totales': {
                'total_reservas': sum(d['total_reservas'] for d in datos),
                'total_personas': sum(d['total_personas'] for d in datos)
            }
        }
    
    @staticmethod
    def generar_reporte_ventas(establecimiento_id: int = None,
                                fecha_inicio: datetime = None,
                                fecha_fin: datetime = None) -> Dict:
        """Genera reporte de ventas/promociones"""
        
        if not fecha_inicio:
            fecha_inicio = datetime.now() - timedelta(days=30)
        if not fecha_fin:
            fecha_fin = datetime.now()
        
        query = """
            SELECT 
                p.id,
                p.titulo,
                p.codigo_cupon,
                p.usos_actuales,
                p.usos_maximos,
                p.descuento_porcentaje,
                p.descuento_valor,
                e.nombre as establecimiento
            FROM promociones p
            JOIN establecimientos e ON p.establecimiento_id = e.id
            WHERE p.created_at BETWEEN :inicio AND :fin
            AND p.activo = TRUE
        """
        
        params = {'inicio': fecha_inicio, 'fin': fecha_fin}
        
        if establecimiento_id:
            query += " AND p.establecimiento_id = :est_id"
            params['est_id'] = establecimiento_id
            
        result = db.session.execute(text(query), params)
        
        datos = []
        for row in result:
            row_dict = row._mapping
            usos_act = row_dict['usos_actuales'] or 0
            usos_max = row_dict['usos_maximos'] or 0
            
            datos.append({
                'promocion_id': row_dict['id'],
                'titulo': row_dict['titulo'],
                'codigo_cupon': row_dict['codigo_cupon'],
                'usos_actuales': usos_act,
                'usos_maximos': usos_max,
                'efectividad': round((usos_act / usos_max * 100), 2) if usos_max > 0 else 0,
                'descuento': row_dict['descuento_porcentaje'] or row_dict['descuento_valor'],
                'establecimiento': row_dict['establecimiento']
            })
        
        return {
            'tipo': 'reporte_promociones',
            'periodo': {
                'inicio': fecha_inicio.isoformat(),
                'fin': fecha_fin.isoformat()
            },
            'datos': datos,
            'totales': {
                'total_promociones': len(datos),
                'total_usos': sum(d['usos_actuales'] for d in datos)
            }
        }
    
    @staticmethod
    def generar_reporte_geografico() -> Dict:
        lugares = Lugar.query.filter_by(estado='APROBADO', activo=True).all()
        establecimientos = Establecimiento.query.filter_by(estado='APROBADO', activo=True).all()
        
        cats_lugares = {}
        for l in lugares:
            cats_lugares[l.categoria] = cats_lugares.get(l.categoria, 0) + 1
        
        cats_est = {}
        for e in establecimientos:
            cats_est[e.categoria] = cats_est.get(e.categoria, 0) + 1
            
        return {
            'tipo': 'reporte_geografico',
            'resumen': {
                'total_lugares': len(lugares),
                'total_establecimientos': len(establecimientos),
                'total_puntos_interes': len(lugares) + len(establecimientos)
            },
            'distribucion_lugares': cats_lugares,
            'distribucion_establecimientos': cats_est,
            'puntos': {
                'lugares': [{'id': l.id, 'tipo_punto': 'Lugar', 'nombre': l.nombre, 'lat': l.latitud, 'lon': l.longitud, 'cat': l.categoria} for l in lugares],
                'establecimientos': [{'id': e.id, 'tipo_punto': 'Establecimiento', 'nombre': e.nombre, 'lat': e.latitud, 'lon': e.longitud, 'cat': e.categoria} for e in establecimientos]
            }
        }
    
    @staticmethod
    def generar_reporte_alertas(usuario_id: int = None,
                                 fecha_inicio: datetime = None,
                                 fecha_fin: datetime = None) -> Dict:
        """Genera reporte de alertas de proximidad"""
        if not fecha_inicio:
            fecha_inicio = datetime.now() - timedelta(days=30)
        if not fecha_fin:
            fecha_fin = datetime.now()
        
        query = Notificacion.query.filter(
            Notificacion.tipo == 'ALERTA_PROXIMIDAD',
            Notificacion.created_at >= fecha_inicio,
            Notificacion.created_at <= fecha_fin
        )
        
        if usuario_id:
            query = query.filter(Notificacion.usuario_id == usuario_id)
            
        alertas = query.all()
        
        return {
            'tipo': 'reporte_alertas',
            'periodo': {
                'inicio': fecha_inicio.isoformat(),
                'fin': fecha_fin.isoformat()
            },
            'total_alertas': len(alertas),
            'alertas_leidas': sum(1 for a in alertas if a.leida),
            'alertas_no_leidas': sum(1 for a in alertas if not a.leida),
            'datos': [a.to_dict() for a in alertas]
        }
    
    @staticmethod
    def generar_dashboard_admin() -> Dict:
        query_usuarios = "SELECT rol, COUNT(*) as total FROM usuarios WHERE activo = TRUE GROUP BY rol"
        result_usuarios = db.session.execute(text(query_usuarios))
        usuarios_por_rol = {row._mapping['rol']: row._mapping['total'] for row in result_usuarios}

        query_lugares = "SELECT estado, COUNT(*) as total FROM lugares GROUP BY estado"
        result_lugares = db.session.execute(text(query_lugares))
        lugares_por_estado = {row._mapping['estado']: row._mapping['total'] for row in result_lugares}
        
        query_est = "SELECT estado, COUNT(*) as total FROM establecimientos GROUP BY estado"
        result_est = db.session.execute(text(query_est))
        est_por_estado = {row._mapping['estado']: row._mapping['total'] for row in result_est}

        hoy = datetime.now()
        inicio_mes = hoy.replace(day=1, hour=0, minute=0, second=0, microsecond=0)
        
        query_reservas = """
            SELECT 
                COUNT(*) as total,
                SUM(CASE WHEN estado = 'CONFIRMADA' THEN 1 ELSE 0 END) as confirmadas,
                SUM(CASE WHEN estado = 'CANCELADA' THEN 1 ELSE 0 END) as canceladas
            FROM reservas 
            WHERE created_at >= :inicio_mes
        """
        result_reservas = db.session.execute(text(query_reservas), {'inicio_mes': inicio_mes}).fetchone()
        row_res = result_reservas._mapping
        
        return {
            'tipo': 'dashboard_admin',
            'fecha_generacion': hoy.isoformat(),
            'usuarios': {
                'total': sum(usuarios_por_rol.values()),
                'por_rol': usuarios_por_rol
            },
            'lugares': {
                'total': sum(lugares_por_estado.values()),
                'por_estado': lugares_por_estado
            },
            'establecimientos': {
                'total': sum(est_por_estado.values()),
                'por_estado': est_por_estado
            },
            'reservas_mes': {
                'total': row_res['total'] or 0,
                'confirmadas': row_res['confirmadas'] or 0,
                'canceladas': row_res['canceladas'] or 0
            }
        }