-- ======================================================
-- SCRIPT DE BASE DE DATOS: BUDGETMAP (VERSIÓN FINAL)
-- Autores: DEV UCHIHA
-- Stack: Spring Boot + Flask + MySQL 8.0
-- ======================================================

DROP DATABASE IF EXISTS budgetmap;
CREATE DATABASE IF NOT EXISTS budgetmap 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE budgetmap;

-- -----------------------------------------------------
-- 1. MÓDULO DE USUARIOS
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Ampliado para soportar BCrypt (60+ chars)
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255),
    telefono VARCHAR(255),
    rol ENUM('ADMINISTRADOR', 'MODERADOR', 'LOCAL_ALIADO', 'ANFITRION', 'EXPLORADOR') NOT NULL,
    puntos_acumulados INT DEFAULT 0,
    activo BOOLEAN DEFAULT TRUE,
    email_verificado BOOLEAN DEFAULT FALSE,
    ultimo_acceso DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_rol (rol)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 2. MÓDULO LUGARES
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS lugares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    categoria ENUM('PARQUE', 'MUSEO', 'SITIO_TURISTICO', 'BIBLIOTECA', 'OTRO') NOT NULL,
    direccion VARCHAR(255),
    latitud DECIMAL(10, 8),
    longitud DECIMAL(11, 8),
    ubicacion POINT NOT NULL SRID 4326,
    imagen_url VARCHAR(1000),
    aforo_maximo INT,
    estado ENUM('PENDIENTE', 'APROBADO', 'RECHAZADO') DEFAULT 'PENDIENTE',
    moderador_id BIGINT,
    fecha_aprobacion DATETIME,
    motivo_rechazo VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    SPATIAL INDEX idx_ubicacion_lugar (ubicacion),
    FOREIGN KEY (moderador_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 3. MÓDULO ESTABLECIMIENTOS
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS establecimientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    nit VARCHAR(255) UNIQUE,
    descripcion TEXT,
    categoria ENUM('RESTAURANTE','PANADERIA','BAR','TIENDA','SUPERMERCADO','FARMACIA','HOTEL','GIMNASIO','OTRO') NOT NULL,
    propietario_id BIGINT NOT NULL,
    direccion VARCHAR(255),
    latitud DOUBLE NOT NULL,
    longitud DOUBLE NOT NULL,
    ubicacion POINT NOT NULL SRID 4326,
    imagen_url VARCHAR(1000),
    aforo_maximo INT,
    aforo_actual INT DEFAULT 0,
    telefono VARCHAR(255),
    horario_atencion VARCHAR(255),
    estado ENUM('PENDIENTE','APROBADO','RECHAZADO') DEFAULT 'PENDIENTE',
    moderador_id BIGINT,
    fecha_aprobacion DATETIME,
    motivo_rechazo VARCHAR(1000),
    activo BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    SPATIAL INDEX idx_ubicacion_estab (ubicacion),
    FOREIGN KEY (propietario_id) REFERENCES usuarios(id),
    FOREIGN KEY (moderador_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 4. MÓDULO EVENTOS
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS eventos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    descripcion TEXT,
    tipo_evento ENUM('ARTISTICO','CULTURAL','DEPORTIVO','VETERINARIO','RECREATIVO') NOT NULL,
    lugar_id BIGINT,
    establecimiento_id BIGINT,
    creador_id BIGINT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    hora_inicio TIME NOT NULL,
    hora_fin TIME,
    aforo_maximo INT,
    aforo_actual INT DEFAULT 0,
    precio DECIMAL(10,2) DEFAULT 0.00,
    imagen_url VARCHAR(1000),
    activo BOOLEAN DEFAULT TRUE,
    destacado BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (lugar_id) REFERENCES lugares(id),
    FOREIGN KEY (establecimiento_id) REFERENCES establecimientos(id),
    FOREIGN KEY (creador_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 5. MÓDULO RESERVAS
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_reserva VARCHAR(255) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    evento_id BIGINT NULL,
    establecimiento_id BIGINT NULL,
    lugar_id BIGINT NULL,
    fecha_reserva DATETIME NOT NULL,
    numero_personas INT DEFAULT 1,
    estado ENUM('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA') DEFAULT 'PENDIENTE',
    puntos_otorgados INT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (evento_id) REFERENCES eventos(id),
    FOREIGN KEY (establecimiento_id) REFERENCES establecimientos(id),
    FOREIGN KEY (lugar_id) REFERENCES lugares(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 6. MÓDULO PROMOCIONES
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS promociones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(255) NOT NULL,
    descripcion TEXT,
    establecimiento_id BIGINT,
    evento_id BIGINT,
    descuento_porcentaje INT DEFAULT 0,
    descuento_valor DECIMAL(10,2) DEFAULT 0.00,
    precio_especial DECIMAL(10,2) DEFAULT 0.00,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    codigo_cupon VARCHAR(255),
    usos_maximos INT,
    usos_actuales INT DEFAULT 0,
    imagen_url VARCHAR(1000),
    activo BOOLEAN DEFAULT TRUE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (establecimiento_id) REFERENCES establecimientos(id),
    FOREIGN KEY (evento_id) REFERENCES eventos(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 7. MÓDULO DE SOPORTE
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS pqrs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo_ticket VARCHAR(255) NOT NULL UNIQUE,
    usuario_id BIGINT NOT NULL,
    tipo ENUM('PETICION', 'QUEJA', 'RECLAMO', 'SUGERENCIA') NOT NULL,
    asunto VARCHAR(255) NOT NULL,
    descripcion TEXT NOT NULL,
    estado ENUM('ABIERTO', 'EN_PROCESO', 'RESPONDIDO', 'CERRADO') DEFAULT 'ABIERTO',
    prioridad ENUM('BAJA', 'MEDIA', 'ALTA') DEFAULT 'MEDIA',
    moderador_asignado_id BIGINT,
    respuesta TEXT,
    fecha_respuesta DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (moderador_asignado_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 8. MÓDULO DE NOTIFICACIONES
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS notificaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo ENUM('RESERVA_CONFIRMADA', 'RESERVA_CANCELADA', 'ALERTA_PROXIMIDAD', 'PROMOCION_NUEVA', 'EVENTO_RECORDATORIO', 'PQRS_RESPUESTA', 'SISTEMA') NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    mensaje TEXT NOT NULL,
    referencia_id BIGINT NULL,
    referencia_tipo ENUM('RESERVA', 'EVENTO', 'ESTABLECIMIENTO', 'PQRS') NULL,
    leida BOOLEAN DEFAULT FALSE,
    origen ENUM('SPRING', 'FLASK') DEFAULT 'SPRING',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- 9. MÓDULO DE ALERTAS
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS config_alertas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    radio_metros INT DEFAULT 500,
    notificar_promociones BOOLEAN DEFAULT TRUE,
    notificar_eventos BOOLEAN DEFAULT TRUE,
    activo BOOLEAN DEFAULT TRUE,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
) ENGINE=InnoDB;