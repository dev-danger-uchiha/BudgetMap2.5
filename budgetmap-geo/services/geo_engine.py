import math
from typing import List, Dict, Tuple, Optional
from sqlalchemy import func, text
from sqlalchemy.exc import SQLAlchemyError
from app import db
from models.geo_models import Lugar, Establecimiento
import logging

logger = logging.getLogger(__name__)


class GeoEngine:
    RADIO_TIERRA_KM = 6371
    MAX_RADIO_KM = 50
    
    @staticmethod
    def _validar_coordenadas(lat: float, lon: float) -> None:
        if not isinstance(lat, (int, float)) or not isinstance(lon, (int, float)):
            raise ValueError("Las coordenadas deben ser numeros")
        
        if not (-90 <= lat <= 90):
            raise ValueError(f"Latitud invalida: {lat}. Debe estar entre -90 y 90")
        
        if not (-180 <= lon <= 180):
            raise ValueError(f"Longitud invalida: {lon}. Debe estar entre -180 y 180")
    
    @staticmethod
    def _validar_radio(radio_km: float) -> float:
        if not isinstance(radio_km, (int, float)) or radio_km <= 0:
            raise ValueError("El radio debe ser un numero positivo")
        
        if radio_km > GeoEngine.MAX_RADIO_KM:
            logger.warning(f"Radio {radio_km}km excede el maximo permitido. Usando {GeoEngine.MAX_RADIO_KM}km")
            return GeoEngine.MAX_RADIO_KM
        
        return float(radio_km)

    @staticmethod
    def haversine_distance(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
        GeoEngine._validar_coordenadas(lat1, lon1)
        GeoEngine._validar_coordenadas(lat2, lon2)
        
        lat1, lon1, lat2, lon2 = map(math.radians, [lat1, lon1, lat2, lon2])
        
        dlat = lat2 - lat1
        dlon = lon2 - lon1
        
        a = math.sin(dlat/2)**2 + math.cos(lat1) * math.cos(lat2) * math.sin(dlon/2)**2
        c = 2 * math.asin(math.sqrt(a))
        
        return GeoEngine.RADIO_TIERRA_KM * c
    
    @staticmethod
    def calcular_distancia_metros(lat1: float, lon1: float, lat2: float, lon2: float) -> float:
        """Calcula la distancia en metros entre dos puntos"""
        return GeoEngine.haversine_distance(lat1, lon1, lat2, lon2) * 1000
    
    @staticmethod
    def esta_dentro_radio(lat_usuario: float, lon_usuario: float, 
                          lat_punto: float, lon_punto: float, 
                          radio_metros: float) -> bool:
        """Verifica si un punto esta dentro del radio especificado"""
        distancia = GeoEngine.calcular_distancia_metros(
            lat_usuario, lon_usuario, lat_punto, lon_punto
        )
        return distancia <= radio_metros
    
    @staticmethod
    def crear_circulo_geofence(lat: float, lon: float, radio_metros: float) -> Dict:
        """Crea un geofence circular alrededor de un punto"""
        GeoEngine._validar_coordenadas(lat, lon)
        return {
            'tipo': 'circulo',
            'centro': {'lat': lat, 'lon': lon},
            'radio_metros': radio_metros
        }
    
    @classmethod
    def buscar_lugares_cercanos(cls, lat: float, lon: float, radio_km: float = 5.0,
                                categoria: Optional[str] = None) -> List[Dict]:

        try:
            cls._validar_coordenadas(lat, lon)
            radio_km = cls._validar_radio(radio_km)
            
            radio_metros = radio_km * 1000
            
            punto_origen = func.ST_GeomFromText(
                func.concat('POINT(', func.cast(lon, db.String), ' ', func.cast(lat, db.String), ')'),
                4326
            )

            query = db.session.query(
                Lugar,
                func.ST_Distance_Sphere(Lugar.ubicacion, punto_origen).label('distancia_metros')
            ).filter(
                Lugar.estado == 'APROBADO',
                Lugar.activo == True,
                func.ST_Distance_Sphere(Lugar.ubicacion, punto_origen) <= radio_metros
            )

            if categoria:
                categorias_permitidas = ['PARQUE', 'MUSEO', 'SITIO_TURISTICO', 'BIBLIOTECA', 'OTRO']
                if categoria.upper() in categorias_permitidas:
                    query = query.filter(Lugar.categoria == categoria.upper())
                else:
                    logger.warning(f"Categoria no permitida ignorada: {categoria}")

            query = query.order_by('distancia_metros')
            
            query = query.limit(100)
            
            resultados = []
            for lugar, distancia_m in query.all():
                lugar_dict = lugar.to_dict()
                lugar_dict['distancia_km'] = round(distancia_m / 1000, 2)
                lugar_dict.pop('ubicacion', None)  # Remover objeto espacial
                resultados.append(lugar_dict)
            
            logger.info(f"Busqueda de lugares: lat={lat}, lon={lon}, radio={radio_km}km, resultados={len(resultados)}")
            
            return resultados
            
        except SQLAlchemyError as e:
            logger.error(f"Error de base de datos en buscar_lugares_cercanos: {str(e)}")
            raise Exception("Error al consultar la base de datos")
        except Exception as e:
            logger.error(f"Error inesperado en buscar_lugares_cercanos: {str(e)}")
            raise
    
    @classmethod
    def buscar_establecimientos_cercanos(cls, lat: float, lon: float, radio_km: float = 5.0,
                                            categoria: Optional[str] = None) -> List[Dict]:

        try:
            cls._validar_coordenadas(lat, lon)
            radio_km = cls._validar_radio(radio_km)
            
            radio_metros = radio_km * 1000

            punto_origen = func.ST_GeomFromText(
                func.concat('POINT(', func.cast(lon, db.String), ' ', func.cast(lat, db.String), ')'),
                4326
            )
            
            query = db.session.query(
                Establecimiento,
                func.ST_Distance_Sphere(Establecimiento.ubicacion, punto_origen).label('distancia_metros')
            ).filter(
                Establecimiento.estado == 'APROBADO',
                Establecimiento.activo == True,
                func.ST_Distance_Sphere(Establecimiento.ubicacion, punto_origen) <= radio_metros
            )
            
            if categoria:
                categorias_permitidas = [
                    'RESTAURANTE', 'PANADERIA', 'BAR', 'TIENDA', 
                    'SUPERMERCADO', 'FARMACIA', 'HOTEL', 'GIMNASIO', 'OTRO'
                ]
                if categoria.upper() in categorias_permitidas:
                    query = query.filter(Establecimiento.categoria == categoria.upper())
                else:
                    logger.warning(f"Categoria no permitida ignorada: {categoria}")
            
            query = query.order_by('distancia_metros').limit(100)
            
            resultados = []
            for est, distancia_m in query.all():
                est_dict = est.to_dict()
                est_dict['distancia_km'] = round(distancia_m / 1000, 2)
                est_dict.pop('ubicacion', None)
                resultados.append(est_dict)
            
            logger.info(f"Busqueda de establecimientos: lat={lat}, lon={lon}, radio={radio_km}km, resultados={len(resultados)}")
            
            return resultados
            
        except SQLAlchemyError as e:
            logger.error(f"Error de base de datos: {str(e)}")
            raise Exception("Error al consultar la base de datos")
        except Exception as e:
            logger.error(f"Error inesperado: {str(e)}")
            raise
    
    @classmethod
    def buscar_todo_cercano(cls, lat: float, lon: float, radio_km: float = 5.0) -> Dict:
        cls._validar_coordenadas(lat, lon)
        radio_km = cls._validar_radio(radio_km)
        
        return {
            'lugares': cls.buscar_lugares_cercanos(lat, lon, radio_km),
            'establecimientos': cls.buscar_establecimientos_cercanos(lat, lon, radio_km),
            'centro_busqueda': {'lat': lat, 'lon': lon},
            'radio_km': radio_km
        }
    
    @staticmethod
    def verificar_geofence_entrada(lat_usuario: float, lon_usuario: float,
                                   lat_centro: float, lon_centro: float,
                                   radio_metros: float, estaba_dentro: bool) -> Dict:
        """Verifica si un usuario entro o salio de un geofence"""
        GeoEngine._validar_coordenadas(lat_usuario, lon_usuario)
        GeoEngine._validar_coordenadas(lat_centro, lon_centro)
        
        esta_dentro = GeoEngine.esta_dentro_radio(
            lat_usuario, lon_usuario, lat_centro, lon_centro, radio_metros
        )
        
        resultado = {
            'esta_dentro': esta_dentro,
            'evento': None,
            'distancia_metros': round(GeoEngine.calcular_distancia_metros(
                lat_usuario, lon_usuario, lat_centro, lon_centro
            ), 2)
        }
        
        if not estaba_dentro and esta_dentro:
            resultado['evento'] = 'ENTRADA'
        elif estaba_dentro and not esta_dentro:
            resultado['evento'] = 'SALIDA'
        
        return resultado
    
    @staticmethod
    def calcular_bounding_box(lat: float, lon: float, radio_km: float) -> Dict:
        GeoEngine._validar_coordenadas(lat, lon)
        radio_km = GeoEngine._validar_radio(radio_km)
        delta_lat = radio_km / 111.0
        delta_lon = radio_km / (111.0 * math.cos(math.radians(lat)))
        
        return {
            'min_lat': lat - delta_lat,
            'max_lat': lat + delta_lat,
            'min_lon': lon - delta_lon,
            'max_lon': lon + delta_lon
        }
    
    @staticmethod
    def interpolar_posicion(lat1: float, lon1: float, lat2: float, lon2: float, 
                            fraccion: float) -> Tuple[float, float]:
        GeoEngine._validar_coordenadas(lat1, lon1)
        GeoEngine._validar_coordenadas(lat2, lon2)
        
        if not (0 <= fraccion <= 1):
            raise ValueError("La fraccion debe estar entre 0 y 1")
        
        lat = lat1 + (lat2 - lat1) * fraccion
        lon = lon1 + (lon2 - lon1) * fraccion
        return lat, lon