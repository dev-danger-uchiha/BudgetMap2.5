from flask import Blueprint, request, jsonify
from services.geo_engine import GeoEngine
import logging
from functools import wraps
from time import time

# 1. IMPORTAMOS TU NUEVO CANDADO DE SEGURIDAD
from auth import verify_jwt_token 

logger = logging.getLogger(__name__)
geo_bp = Blueprint('geo', __name__)

request_counts = {}
RATE_LIMIT = 100
RATE_WINDOW = 60

def rate_limit(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        client_ip = request.remote_addr or 'unknown'
        current_time = int(time())
        window_key = current_time // RATE_WINDOW

        keys_to_remove = [k for k in request_counts if k[1] < window_key - 1]
        for k in keys_to_remove:
            del request_counts[k]

        key = (client_ip, window_key)
        request_counts[key] = request_counts.get(key, 0) + 1
        
        if request_counts[key] > RATE_LIMIT:
            logger.warning(f"Rate limit excedido para IP: {client_ip}")
            return jsonify({
                'success': False,
                'error': 'Demasiadas solicitudes. Intente mas tarde.'
            }), 429
        
        return f(*args, **kwargs)
    return decorated_function

def handle_error(error, status_code=500):
    logger.error(f"Error: {str(error)}", exc_info=True)

    if status_code == 400:
        message = str(error) if isinstance(error, ValueError) else 'Solicitud invalida'
    elif status_code == 429:
        message = 'Demasiadas solicitudes'
    else:
        message = 'Error interno del servidor'
    
    return jsonify({
        'success': False,
        'error': message
    }), status_code

# EL HEALTH CHECK SE QUEDA PÚBLICO
@geo_bp.route('/health', methods=['GET'])
@rate_limit
def health():
    return jsonify({'status': 'OK', 'service': 'geo'})


# 2. APLICAMOS EL CANDADO A LAS RUTAS PRIVADAS
@geo_bp.route('/lugares/cercanos', methods=['GET'])
@rate_limit
@verify_jwt_token
def lugares_cercanos():
    try:
        lat_str = request.args.get('lat')
        lon_str = request.args.get('lon')
        
        if lat_str is None or lon_str is None:
            return handle_error('Parametros lat y lon son obligatorios', 400)
        
        try:
            lat = float(lat_str)
            lon = float(lon_str)
        except ValueError:
            return handle_error('Las coordenadas deben ser numeros validos', 400)

        try:
            radio_km = float(request.args.get('radio_km', 5.0))
        except ValueError:
            radio_km = 5.0
        
        categoria = request.args.get('categoria')

        lugares = GeoEngine.buscar_lugares_cercanos(lat, lon, radio_km, categoria)
        
        return jsonify({
            'success': True,
            'count': len(lugares),
            'data': lugares
        })
        
    except ValueError as e:
        return handle_error(e, 400)
    except Exception as e:
        return handle_error(e, 500)

@geo_bp.route('/establecimientos/cercanos', methods=['GET'])
@rate_limit
@verify_jwt_token
def establecimientos_cercanos():
    try:
        lat_str = request.args.get('lat')
        lon_str = request.args.get('lon')
        
        if lat_str is None or lon_str is None:
            return handle_error('Parametros lat y lon son obligatorios', 400)
        
        try:
            lat = float(lat_str)
            lon = float(lon_str)
        except ValueError:
            return handle_error('Las coordenadas deben ser numeros validos', 400)
        
        try:
            radio_km = float(request.args.get('radio_km', 5.0))
        except ValueError:
            radio_km = 5.0
        
        categoria = request.args.get('categoria')
        
        establecimientos = GeoEngine.buscar_establecimientos_cercanos(lat, lon, radio_km, categoria)
        
        return jsonify({
            'success': True,
            'count': len(establecimientos),
            'data': establecimientos
        })
        
    except ValueError as e:
        return handle_error(e, 400)
    except Exception as e:
        return handle_error(e, 500)

@geo_bp.route('/todo-cercano', methods=['GET'])
@rate_limit
@verify_jwt_token
def todo_cercano():
    try:
        lat_str = request.args.get('lat')
        lon_str = request.args.get('lon')
        
        if lat_str is None or lon_str is None:
            return handle_error('Parametros lat y lon son obligatorios', 400)
        
        try:
            lat = float(lat_str)
            lon = float(lon_str)
        except ValueError:
            return handle_error('Las coordenadas deben ser numeros validos', 400)
        
        try:
            radio_km = float(request.args.get('radio_km', 5.0))
        except ValueError:
            radio_km = 5.0
        
        resultado = GeoEngine.buscar_todo_cercano(lat, lon, radio_km)
        
        return jsonify({
            'success': True,
            'data': resultado
        })
        
    except ValueError as e:
        return handle_error(e, 400)
    except Exception as e:
        return handle_error(e, 500)

@geo_bp.route('/distancia', methods=['GET'])
@rate_limit
@verify_jwt_token
def calcular_distancia():
    try:
        lat1 = request.args.get('lat1')
        lon1 = request.args.get('lon1')
        lat2 = request.args.get('lat2')
        lon2 = request.args.get('lon2')

        if any(v is None for v in [lat1, lon1, lat2, lon2]):
            return handle_error('lat1, lon1, lat2, lon2 son obligatorios', 400)
        
        try:
            lat1_f = float(lat1)
            lon1_f = float(lon1)
            lat2_f = float(lat2)
            lon2_f = float(lon2)
        except ValueError:
            return handle_error('Las coordenadas deben ser numeros validos', 400)
            
        distancia_km = GeoEngine.haversine_distance(lat1_f, lon1_f, lat2_f, lon2_f)
        distancia_m = distancia_km * 1000
        
        return jsonify({
            'success': True,
            'distancia_km': round(distancia_km, 2),
            'distancia_metros': round(distancia_m, 2)
        })
        
    except ValueError as e:
        return handle_error(e, 400)
    except Exception as e:
        return handle_error(e, 500)

@geo_bp.route('/dentro-radio', methods=['POST'])
@rate_limit
@verify_jwt_token
def dentro_radio():
    try:
        data = request.get_json()
        if not data:
            return handle_error('No se envio cuerpo JSON', 400)

        required = ['lat_usuario', 'lon_usuario', 'lat_centro', 'lon_centro', 'radio_metros']
        for param in required:
            if param not in data:
                return handle_error(f'Parametro requerido: {param}', 400)
        
        try:
            lat_usuario = float(data.get('lat_usuario'))
            lon_usuario = float(data.get('lon_usuario'))
            lat_centro = float(data.get('lat_centro'))
            lon_centro = float(data.get('lon_centro'))
            radio_metros = float(data.get('radio_metros'))
        except (TypeError, ValueError):
            return handle_error('Todos los parametros deben ser numericos', 400)
        
        dentro = GeoEngine.esta_dentro_radio(
            lat_usuario, lon_usuario, 
            lat_centro, lon_centro, 
            radio_metros
        )
        distancia = GeoEngine.calcular_distancia_metros(
            lat_usuario, lon_usuario,
            lat_centro, lon_centro
        )
        
        return jsonify({
            'success': True,
            'dentro_radio': dentro,
            'distancia_metros': round(distancia, 2)
        })
        
    except ValueError as e:
        return handle_error(e, 400)
    except Exception as e:
        return handle_error(e, 500)

@geo_bp.route('/geofence/verificar', methods=['POST'])
@rate_limit
@verify_jwt_token
def verificar_geofence():
    try:
        data = request.get_json()
        if not data:
            return handle_error('No se envio cuerpo JSON', 400)

        required = ['lat_usuario', 'lon_usuario', 'lat_centro', 'lon_centro', 'radio_metros']
        for param in required:
            if param not in data:
                return handle_error(f'Parametro requerido: {param}', 400)
        
        try:
            lat_usuario = float(data.get('lat_usuario'))
            lon_usuario = float(data.get('lon_usuario'))
            lat_centro = float(data.get('lat_centro'))
            lon_centro = float(data.get('lon_centro'))
            radio_metros = float(data.get('radio_metros'))
            estaba_dentro = bool(data.get('estaba_dentro', False))
        except (TypeError, ValueError):
            return handle_error('Formato de parametros invalido', 400)
        
        resultado = GeoEngine.verificar_geofence_entrada(
            lat_usuario, lon_usuario, lat_centro, lon_centro,
            radio_metros, estaba_dentro
        )
        
        return jsonify({
            'success': True,
            'data': resultado
        })
        
    except ValueError as e:
        return handle_error(e, 400)
    except Exception as e:
        return handle_error(e, 500)

@geo_bp.route('/bounding-box', methods=['GET'])
@rate_limit
@verify_jwt_token
def bounding_box():
    try:
        lat_str = request.args.get('lat')
        lon_str = request.args.get('lon')
        
        if lat_str is None or lon_str is None:
            return handle_error('Parametros lat y lon son obligatorios', 400)

        try:
            lat = float(lat_str)
            lon = float(lon_str)
        except ValueError:
            return handle_error('Las coordenadas deben ser numeros validos', 400)
        
        try:
            radio_km = float(request.args.get('radio_km', 5.0))
        except ValueError:
            radio_km = 5.0
        
        bbox = GeoEngine.calcular_bounding_box(lat, lon, radio_km)
        
        return jsonify({
            'success': True,
            'data': bbox
        })
        
    except ValueError as e:
        return handle_error(e, 400)
    except Exception as e:
        return handle_error(e, 500)