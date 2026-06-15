from app import db
from sqlalchemy import Column, Integer, String, Float, DateTime, Boolean, Text, ForeignKey
from sqlalchemy.orm import relationship
from geoalchemy2 import Geometry
from datetime import datetime

class Lugar(db.Model):
    __tablename__ = 'lugares'
    
    id = Column(Integer, primary_key=True)
    nombre = Column(String(200), nullable=False)
    descripcion = Column(Text)
    categoria = Column(String(30), nullable=False)
    direccion = Column(String(300))
    latitud = Column(Float, nullable=False)
    longitud = Column(Float, nullable=False)
    
    # IMPORTANTE: srid=4326 define el sistema de coordenadas GPS estándar (WGS 84)
    ubicacion = Column(Geometry(geometry_type='POINT', srid=4326))
    
    imagen_url = Column(String(500))
    aforo_maximo = Column(Integer)
    estado = Column(String(20), default='PENDIENTE')
    activo = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'nombre': self.nombre,
            'descripcion': self.descripcion,
            'categoria': self.categoria,
            'direccion': self.direccion,
            'latitud': self.latitud,
            'longitud': self.longitud,
            'imagen_url': self.imagen_url,
            'aforo_maximo': self.aforo_maximo,
            'estado': self.estado,
            'activo': self.activo
        }

class Establecimiento(db.Model):
    __tablename__ = 'establecimientos'
    
    id = Column(Integer, primary_key=True)
    nombre = Column(String(200), nullable=False)
    nit = Column(String(20), unique=True)
    descripcion = Column(Text)
    categoria = Column(String(30), nullable=False)
    propietario_id = Column(Integer, ForeignKey('usuarios.id'))
    direccion = Column(String(300))
    latitud = Column(Float, nullable=False)
    longitud = Column(Float, nullable=False)
    ubicacion = Column(Geometry(geometry_type='POINT', srid=4326)) 
    imagen_url = Column(String(500))
    aforo_maximo = Column(Integer)
    aforo_actual = Column(Integer, default=0)
    telefono = Column(String(20))
    horario_atencion = Column(String(200))
    estado = Column(String(20), default='PENDIENTE')
    activo = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'nombre': self.nombre,
            'nit': self.nit,
            'descripcion': self.descripcion,
            'categoria': self.categoria,
            'direccion': self.direccion,
            'latitud': self.latitud,
            'longitud': self.longitud,
            'imagen_url': self.imagen_url,
            'aforo_maximo': self.aforo_maximo,
            'aforo_actual': self.aforo_actual,
            'telefono': self.telefono,
            'horario_atencion': self.horario_atencion,
            'estado': self.estado,
            'activo': self.activo
        }

class ConfigAlerta(db.Model):
    __tablename__ = 'config_alertas'
    
    id = Column(Integer, primary_key=True)
    usuario_id = Column(Integer, ForeignKey('usuarios.id'), nullable=False)
    radio_metros = Column(Integer, default=1000)
    tipos_lugares = Column(String(200))
    tipos_establecimientos = Column(String(200))
    notificar_promociones = Column(Boolean, default=True)
    notificar_eventos = Column(Boolean, default=True)
    activo = Column(Boolean, default=True)
    created_at = Column(DateTime, default=datetime.utcnow)
    updated_at = Column(DateTime, default=datetime.utcnow, onupdate=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'usuario_id': self.usuario_id,
            'radio_metros': self.radio_metros,
            'tipos_lugares': self.tipos_lugares,
            'tipos_establecimientos': self.tipos_establecimientos,
            'notificar_promociones': self.notificar_promociones,
            'notificar_eventos': self.notificar_eventos,
            'activo': self.activo
        }

class Notificacion(db.Model):
    __tablename__ = 'notificaciones'
    
    id = Column(Integer, primary_key=True)
    usuario_id = Column(Integer, ForeignKey('usuarios.id'), nullable=False)
    tipo = Column(String(30), nullable=False)
    titulo = Column(String(200), nullable=False)
    mensaje = Column(Text, nullable=False)
    referencia_id = Column(Integer)
    referencia_tipo = Column(String(50))
    leida = Column(Boolean, default=False)
    fecha_lectura = Column(DateTime)
    accion_url = Column(String(500))
    imagen_url = Column(String(500))
    origen = Column(String(20), default='FLASK')
    created_at = Column(DateTime, default=datetime.utcnow)
    
    def to_dict(self):
        return {
            'id': self.id,
            'tipo': self.tipo,
            'titulo': self.titulo,
            'mensaje': self.mensaje,
            'referencia_id': self.referencia_id,
            'referencia_tipo': self.referencia_tipo,
            'leida': self.leida,
            'origen': self.origen,
            'created_at': self.created_at.isoformat() if self.created_at else None
        }

class Usuario(db.Model):
    __tablename__ = 'usuarios'
    
    id = Column(Integer, primary_key=True)
    email = Column(String(100), unique=True, nullable=False)
    nombre = Column(String(100), nullable=False)
    rol = Column(String(20), nullable=False)
    puntos_acumulados = Column(Integer, default=0)
    activo = Column(Boolean, default=True)    
    config_alerta = relationship('ConfigAlerta', backref='usuario', uselist=False)