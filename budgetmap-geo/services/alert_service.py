from typing import List, Dict, Optional
from datetime import datetime, timedelta
from app import db
from models.geo_models import ConfigAlerta, Notificacion
from services.geo_engine import GeoEngine

class AlertService:
    
    @staticmethod
    def obtener_config_usuario(usuario_id: int) -> Optional[ConfigAlerta]:
        return ConfigAlerta.query.filter_by(usuario_id=usuario_id, activo=True).first()
    
    @staticmethod
    def crear_configuracion(usuario_id: int, radio_metros: int = 1000,
                            tipos_lugares: str = None, tipos_establecimientos: str = None,
                            notificar_promociones: bool = True, notificar_eventos: bool = True) -> ConfigAlerta:
        config = ConfigAlerta.query.filter_by(usuario_id=usuario_id).first()
        
        if config:
            config.radio_metros = radio_metros
            config.tipos_lugares = tipos_lugares
            config.tipos_establecimientos = tipos_establecimientos
            config.notificar_promociones = notificar_promociones
            config.notificar_eventos = notificar_eventos
            config.activo = True
        else:
            config = ConfigAlerta(
                usuario_id=usuario_id,
                radio_metros=radio_metros,
                tipos_lugares=tipos_lugares,
                tipos_establecimientos=tipos_establecimientos,
                notificar_promociones=notificar_promociones,
                notificar_eventos=notificar_eventos,
                activo=True
            )
            db.session.add(config)
        
        db.session.commit()
        return config
    
    @staticmethod
    def desactivar_alertas(usuario_id: int) -> bool:
        config = ConfigAlerta.query.filter_by(usuario_id=usuario_id).first()
        if config:
            config.activo = False
            db.session.commit()
            return True
        return False
    
    @classmethod
    def verificar_proximidad(cls, usuario_id: int, lat: float, lon: float) -> List[Dict]:
        config = cls.obtener_config_usuario(usuario_id)
        if not config:
            return []
        
        alertas_generadas = []
        radio_km = config.radio_metros / 1000.0
        hubo_nuevas_alertas = False

        if config.notificar_eventos:
            lugares = GeoEngine.buscar_lugares_cercanos(lat, lon, radio_km)
            for lugar in lugares:
                if not cls._existe_alerta_reciente(usuario_id, lugar['id'], 'LUGAR'):
                    alerta = cls._crear_alerta_proximidad(
                        usuario_id=usuario_id,
                        tipo='ALERTA_PROXIMIDAD',
                        titulo=f'Estás cerca de {lugar["nombre"]}',
                        mensaje=f'Estás a {lugar["distancia_km"]} km de {lugar["nombre"]}',
                        referencia_id=lugar['id'],
                        referencia_tipo='LUGAR'
                    )
                    hubo_nuevas_alertas = True
                    alertas_generadas.append({
                        'tipo': 'lugar',
                        'alerta': alerta.to_dict(),
                        'lugar': lugar
                    })

        if config.notificar_promociones:
            establecimientos = GeoEngine.buscar_establecimientos_cercanos(lat, lon, radio_km)
            for est in establecimientos:
                if not cls._existe_alerta_reciente(usuario_id, est['id'], 'ESTABLECIMIENTO'):
                    alerta = cls._crear_alerta_proximidad(
                        usuario_id=usuario_id,
                        tipo='ALERTA_PROXIMIDAD',
                        titulo=f'Estás cerca de {est["nombre"]}',
                        mensaje=f'Estás a {est["distancia_km"]} km de {est["nombre"]}',
                        referencia_id=est['id'],
                        referencia_tipo='ESTABLECIMIENTO'
                    )
                    hubo_nuevas_alertas = True
                    alertas_generadas.append({
                        'tipo': 'establecimiento',
                        'alerta': alerta.to_dict(),
                        'establecimiento': est
                    })

        if hubo_nuevas_alertas:
            db.session.commit()
            
        return alertas_generadas
    
    @staticmethod
    def _existe_alerta_reciente(usuario_id: int, referencia_id: int, 
                                 referencia_tipo: str, minutos: int = 60) -> bool:
        tiempo_limite = datetime.utcnow() - timedelta(minutes=minutos)
        
        alerta = Notificacion.query.filter(
            Notificacion.usuario_id == usuario_id,
            Notificacion.referencia_id == referencia_id,
            Notificacion.referencia_tipo == referencia_tipo,
            Notificacion.created_at >= tiempo_limite
        ).first()
        
        return alerta is not None
    
    @staticmethod
    def _crear_alerta_proximidad(usuario_id: int, tipo: str, titulo: str,
                                 mensaje: str, referencia_id: int = None,
                                 referencia_tipo: str = None) -> Notificacion:
        notificacion = Notificacion(
            usuario_id=usuario_id,
            tipo=tipo,
            titulo=titulo,
            mensaje=mensaje,
            referencia_id=referencia_id,
            referencia_tipo=referencia_tipo,
            leida=False,
            origen='FLASK'
        )
        db.session.add(notificacion)
        return notificacion
    
    @classmethod
    def procesar_telemetria(cls, usuario_id: int, lat: float, lon: float,
                            velocidad: float = None, direccion: float = None) -> Dict:
        alertas = cls.verificar_proximidad(usuario_id, lat, lon)
        
        return {
            'usuario_id': usuario_id,
            'posicion': {'lat': lat, 'lon': lon},
            'velocidad': velocidad,
            'direccion': direccion,
            'timestamp': datetime.utcnow().isoformat(),
            'alertas_generadas': len(alertas),
            'alertas': alertas
        }
    
    @staticmethod
    def obtener_historial_alertas(usuario_id: int, limite: int = 50) -> List[Dict]:
        alertas = Notificacion.query.filter(
            Notificacion.usuario_id == usuario_id,
            Notificacion.tipo == 'ALERTA_PROXIMIDAD'
        ).order_by(Notificacion.created_at.desc()).limit(limite).all()
        
        return [a.to_dict() for a in alertas]
    
    @staticmethod
    def geofence_check_batch(usuario_id: int, puntos: List[Dict]) -> List[Dict]:
        config = AlertService.obtener_config_usuario(usuario_id)
        if not config:
            return []
        
        resultados = []
        estaba_dentro = False
        
        for punto in puntos:
            lat = punto.get('lat')
            lon = punto.get('lon')
            timestamp = punto.get('timestamp')
            
            lugares = GeoEngine.buscar_lugares_cercanos(lat, lon, config.radio_metros / 1000.0)
            
            for lugar in lugares:
                resultado = GeoEngine.verificar_geofence_entrada(
                    lat, lon, lugar['latitud'], lugar['longitud'],
                    config.radio_metros, estaba_dentro
                )
                
                if resultado['evento']:
                    resultados.append({
                        'lugar_id': lugar['id'],
                        'lugar_nombre': lugar['nombre'],
                        'evento': resultado['evento'],
                        'timestamp': timestamp,
                        'distancia_metros': resultado['distancia_metros']
                    })
                    
                    if resultado['evento'] == 'ENTRADA':
                        estaba_dentro = True
                    elif resultado['evento'] == 'SALIDA':
                        estaba_dentro = False
        
        return resultados