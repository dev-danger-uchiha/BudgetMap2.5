import os
from flask import Flask
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from dotenv import load_dotenv

# NUEVAS IMPORTACIONES DE SEGURIDAD
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address

load_dotenv()

db = SQLAlchemy()

# 1. INICIALIZAR EL LIMITADOR (Conectado a Redis)
# Usamos default_limits para proteger todos los endpoints por defecto
limiter = Limiter(
    key_func=get_remote_address,
    default_limits=["200 per day", "50 per hour"],
    storage_uri="memory://"
)

def create_app():
    app = Flask(__name__)

    CORS(app)

    # 2. CONFIGURACIÓN DE BASE DE DATOS
    app.config['SQLALCHEMY_DATABASE_URI'] = os.getenv('DATABASE_URL', 'sqlite:///local.db')
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
    
    db.init_app(app)
    
    # 3. VINCULAR EL LIMITADOR A LA APLICACIÓN
    limiter.init_app(app)

    # 4. RUTA BLINDADA (Ejemplo de límite estricto personalizado)
    @app.route('/')
    @limiter.limit("5 per minute") # Máximo 5 peticiones por minuto por IP
    def health_check():
        return {"status": "ok", "message": "BudgetMap Geo-Service is running and rate-limited"}, 200

    from routes.geo_routes import geo_bp
    from routes.alert_routes import alert_bp
    from routes.report_routes import report_bp

    app.register_blueprint(geo_bp, url_prefix='/api/geo')
    app.register_blueprint(alert_bp, url_prefix='/api/filtros')
    app.register_blueprint(report_bp, url_prefix='/api/reportes')

    return app

if __name__ == '__main__':
    app = create_app()
    app.run(host='0.0.0.0', port=5000, debug=True)