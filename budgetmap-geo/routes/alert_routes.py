from flask import Blueprint, request, jsonify
from services.alert_service import AlertService

alert_bp = Blueprint('alerts', __name__)

@alert_bp.route('/health', methods=['GET'])
def health():
    return jsonify({'status': 'OK', 'service': 'alerts'})

@alert_bp.route('/config/<int:usuario_id>', methods=['GET'])
def obtener_config(usuario_id):
    """Obtiene la configuración de alertas de un usuario"""
    try:
        config = AlertService.obtener_config_usuario(usuario_id)
        if config:
            return jsonify({
                'success': True,
                'data': config.to_dict()
            })
        return jsonify({
            'success': False,
            'error': 'Configuración no encontrada'
        }), 404
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@alert_bp.route('/config', methods=['POST'])
def crear_config():
    """Crea o actualiza la configuración de alertas"""
    try:
        data = request.get_json()
        if not data:
            return jsonify({'success': False, 'error': 'No se envió cuerpo JSON'}), 400
            
        usuario_id = data.get('usuario_id')
        if not usuario_id:
            return jsonify({'success': False, 'error': 'usuario_id es obligatorio'}), 400

        radio_metros = data.get('radio_metros', 1000)
        tipos_lugares = data.get('tipos_lugares')
        tipos_establecimientos = data.get('tipos_establecimientos')
        notificar_promociones = data.get('notificar_promociones', True)
        notificar_eventos = data.get('notificar_eventos', True)
        
        config = AlertService.crear_configuracion(
            usuario_id, radio_metros, tipos_lugares,
            tipos_establecimientos, notificar_promociones,
            notificar_eventos
        )
        
        return jsonify({
            'success': True,
            'data': config.to_dict()
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@alert_bp.route('/config/<int:usuario_id>/desactivar', methods=['PUT'])
def desactivar_alertas(usuario_id):
    """Desactiva las alertas de un usuario"""
    try:
        resultado = AlertService.desactivar_alertas(usuario_id)
        if resultado:
            return jsonify({
                'success': True,
                'message': 'Alertas desactivadas correctamente'
            })
        return jsonify({
            'success': False,
            'error': 'Configuración no encontrada'
        }), 404
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@alert_bp.route('/verificar', methods=['POST'])
def verificar_proximidad():
    """Verifica lugares y establecimientos cercanos"""
    try:
        data = request.get_json()
        if not data:
            return jsonify({'success': False, 'error': 'JSON requerido'}), 400
            
        usuario_id = data.get('usuario_id')
        lat = data.get('lat')
        lon = data.get('lon')
        
        if any(v is None for v in [usuario_id, lat, lon]):
            return jsonify({'success': False, 'error': 'usuario_id, lat y lon son obligatorios'}), 400
            
        alertas = AlertService.verificar_proximidad(int(usuario_id), float(lat), float(lon))
        
        return jsonify({
            'success': True,
            'alertas_generadas': len(alertas),
            'data': alertas
        })
    except ValueError:
        return jsonify({'success': False, 'error': 'Formatos de datos inválidos (lat/lon deben ser números)'}), 400
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@alert_bp.route('/telemetria', methods=['POST'])
def procesar_telemetria():
    """Procesa datos de telemetría del usuario"""
    try:
        data = request.get_json()
        if not data:
            return jsonify({'success': False, 'error': 'JSON requerido'}), 400
            
        usuario_id = data.get('usuario_id')
        lat = data.get('lat')
        lon = data.get('lon')
        
        if any(v is None for v in [usuario_id, lat, lon]):
            return jsonify({'success': False, 'error': 'usuario_id, lat y lon son obligatorios'}), 400
            
        velocidad = data.get('velocidad')
        direccion = data.get('direccion')
        
        resultado = AlertService.procesar_telemetria(
            int(usuario_id), float(lat), float(lon), velocidad, direccion
        )
        
        return jsonify({
            'success': True,
            'data': resultado
        })
    except ValueError:
        return jsonify({'success': False, 'error': 'Coordenadas inválidas'}), 400
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@alert_bp.route('/historial/<int:usuario_id>', methods=['GET'])
def historial_alertas(usuario_id):
    """Obtiene el historial de alertas de un usuario"""
    try:
        limite = request.args.get('limite', 50, type=int)
        alertas = AlertService.obtener_historial_alertas(usuario_id, limite)
        
        return jsonify({
            'success': True,
            'count': len(alertas),
            'data': alertas
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500

@alert_bp.route('/geofence/batch', methods=['POST'])
def geofence_batch():
    """Verifica múltiples puntos contra geofences"""
    try:
        data = request.get_json()
        if not data:
            return jsonify({'success': False, 'error': 'JSON requerido'}), 400
            
        usuario_id = data.get('usuario_id')
        puntos = data.get('puntos', [])
        
        if not usuario_id or not isinstance(puntos, list):
            return jsonify({'success': False, 'error': 'usuario_id y una lista de puntos son requeridos'}), 400
            
        resultados = AlertService.geofence_check_batch(int(usuario_id), puntos)
        
        return jsonify({
            'success': True,
            'eventos_detectados': len(resultados),
            'data': resultados
        })
    except Exception as e:
        return jsonify({'success': False, 'error': str(e)}), 500