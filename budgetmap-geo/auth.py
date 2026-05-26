import os
import jwt
from functools import wraps
from flask import request, jsonify

# Toma el secreto directamente desde tu .env
JWT_SECRET = os.getenv('JWT_SECRET')

def verify_jwt_token(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        token = None
        
        # 1. Buscar el token en la cabecera 'Authorization'
        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            try:
                token = auth_header.split(" ")[1]  # Extrae el token sin la palabra "Bearer"
            except IndexError:
                return jsonify({"error": "Formato inválido. Usa 'Bearer <token>'"}), 401
        
        if not token:
            return jsonify({"error": "Token requerido. Acceso denegado."}), 401
        
        try:
            # 2. Flask verifica que la firma coincida con la de Spring Boot
            payload = jwt.decode(token, JWT_SECRET, algorithms=['HS256'])
            request.usuario_id = payload.get('sub') 
            
        except jwt.ExpiredSignatureError:
            return jsonify({"error": "El token ha expirado. Inicia sesión de nuevo."}), 401
        except jwt.InvalidTokenError:
            return jsonify({"error": "Token inválido o corrupto."}), 401
        
        return f(*args, **kwargs)
    return decorated_function