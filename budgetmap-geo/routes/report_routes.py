from flask import Blueprint, request, jsonify
from services.report_engine import ReportEngine
from datetime import datetime

report_bp = Blueprint('reports', __name__)

@report_bp.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'OK', 'service': 'reports'})

def _parsear_fecha(fecha_str):
    """Función auxiliar para parsear fechas de forma segura"""
    if not fecha_str:
        return None
    # Elimina la 'Z' de UTC si el frontend (JS) la envía, para compatibilidad
    fecha_str = fecha_str.replace('Z', '+00:00')
    return datetime.fromisoformat(fecha_str)

@report_bp.route('/visitas', methods=['GET'])
def reporte_visitas():
    try:
        establecimiento_id = request.args.get('establecimiento_id', type=int)
        fecha_inicio = _parsear_fecha(request.args.get('fecha_inicio'))
        fecha_fin = _parsear_fecha(request.args.get('fecha_fin'))
        
        reporte = ReportEngine.generar_reporte_visitas(
            establecimiento_id, fecha_inicio, fecha_fin
        )
        
        return jsonify({
            'success': True,
            'data': reporte
        })
    except ValueError:
        return jsonify({'success': False, 'error': 'Formato de fecha inválido. Use ISO 8601'}), 400
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@report_bp.route('/promociones', methods=['GET'])
def reporte_promociones():
    try:
        establecimiento_id = request.args.get('establecimiento_id', type=int)
        fecha_inicio = _parsear_fecha(request.args.get('fecha_inicio'))
        fecha_fin = _parsear_fecha(request.args.get('fecha_fin'))
        
        reporte = ReportEngine.generar_reporte_ventas(
            establecimiento_id, fecha_inicio, fecha_fin
        )
        
        return jsonify({
            'success': True,
            'data': reporte
        })
    except ValueError:
        return jsonify({'success': False, 'error': 'Formato de fecha inválido'}), 400
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@report_bp.route('/geografico', methods=['GET'])
def reporte_geografico():
    try:
        reporte = ReportEngine.generar_reporte_geografico()
        return jsonify({
            'success': True,
            'data': reporte
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@report_bp.route('/alertas', methods=['GET'])
def reporte_alertas():
    try:
        usuario_id = request.args.get('usuario_id', type=int)
        fecha_inicio = _parsear_fecha(request.args.get('fecha_inicio'))
        fecha_fin = _parsear_fecha(request.args.get('fecha_fin'))
        
        reporte = ReportEngine.generar_reporte_alertas(
            usuario_id, fecha_inicio, fecha_fin
        )
        
        return jsonify({
            'success': True,
            'data': reporte
        })
    except ValueError:
        return jsonify({'success': False, 'error': 'Formato de fecha inválido'}), 400
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@report_bp.route('/dashboard', methods=['GET'])
def dashboard_admin():
    try:
        reporte = ReportEngine.generar_dashboard_admin()
        return jsonify({
            'success': True,
            'data': reporte
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@report_bp.route('/exportar/<tipo>', methods=['GET'])
def exportar_reporte(tipo):
    try:
        formato = request.args.get('formato', 'json').lower()
        
        if tipo == 'visitas':
            reporte = ReportEngine.generar_reporte_visitas()
            filas_csv = reporte.get('datos', [])
        elif tipo == 'promociones':
            reporte = ReportEngine.generar_reporte_ventas()
            filas_csv = reporte.get('datos', [])
        elif tipo == 'geografico':
            reporte = ReportEngine.generar_reporte_geografico()
            filas_csv = reporte['puntos']['lugares'] + reporte['puntos']['establecimientos']
        else:
            return jsonify({'success': False, 'error': 'Tipo de reporte no válido'}), 400
        
        if formato == 'csv':
            import csv
            import io
            
            output = io.StringIO()
            if filas_csv:
                writer = csv.DictWriter(output, fieldnames=filas_csv[0].keys())
                writer.writeheader()
                writer.writerows(filas_csv)
            else:
                output.write("No hay datos disponibles para este periodo.")
            
            return output.getvalue(), 200, {
                'Content-Type': 'text/csv; charset=utf-8',
                'Content-Disposition': f'attachment; filename=reporte_{tipo}.csv'
            }
        
        return jsonify({
            'success': True,
            'data': reporte
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500