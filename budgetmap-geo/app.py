import os
from flask import Flask
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
from dotenv import load_dotenv

load_dotenv()

db = SQLAlchemy()

def create_app():
    app = Flask(__name__)

    CORS(app)

    @app.route('/')
    def health_check():
        return {"status": "ok", "message": "BudgetMap Geo-Service is running"}, 200

    app.config['SQLALCHEMY_DATABASE_URI'] = os.getenv('DATABASE_URL', 'sqlite:///local.db')
    app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False

    db.init_app(app)

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