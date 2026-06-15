-- ======================================================
-- SCRIPT DE BASE DE DATOS: BUDGETMAP (VERSIÓN FINAL + MODELO DE NEGOCIO)
-- Autores: DEV UCHIHA
-- Stack: Spring Boot + Flask + MySQL 8.0
-- ======================================================

DROP DATABASE IF EXISTS budgetmap;
CREATE DATABASE IF NOT EXISTS budgetmap 
    CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE budgetmap;

-- 1. Tablas independientes

-- budgetmap.planes_suscripcion definition
CREATE TABLE "planes_suscripcion" (
  "id" int NOT NULL AUTO_INCREMENT,
  "nombre" varchar(50) NOT NULL,
  "tipo_publico" enum('ALIADO', 'EXPLORADOR') NOT NULL,
  "precio_mensual" decimal(38, 2) NOT NULL,
  "permite_promos_ilimitadas" tinyint(1) DEFAULT '0',
  "permite_estadisticas_avanzadas" tinyint(1) DEFAULT '0',
  "acceso_anticipado_ofertas" tinyint(1) DEFAULT '0',
  "sin_anuncios" tinyint(1) DEFAULT '0',
  "activo" tinyint(1) DEFAULT '1',
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  UNIQUE KEY "nombre" ("nombre")
);

-- budgetmap.tokens_revocados definition
CREATE TABLE "tokens_revocados" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "token" varchar(512) NOT NULL,
  "fecha_expiracion" datetime(6) NOT NULL,
  "created_at" datetime(6) NOT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "UK_svutquepwyiy8h1lyb0cacxa9" ("token")
);

-- 2. Tablas base dependientes de planes

-- budgetmap.usuarios definition
CREATE TABLE "usuarios" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "plan_id" int DEFAULT NULL,
  "email" varchar(255) NOT NULL,
  "password" varchar(255) NOT NULL,
  "rol" varchar(50) NOT NULL,
  "nombre" varchar(255) NOT NULL,
  "apellido" varchar(255) DEFAULT NULL,
  "telefono" varchar(255) DEFAULT NULL,
  "avatar_url" varchar(1000) DEFAULT NULL,
  "puntos_acumulados" int DEFAULT '0',
  "fecha_fin_suscripcion" datetime DEFAULT NULL,
  "activo" tinyint(1) DEFAULT '1',
  "email_verificado" tinyint(1) DEFAULT '0',
  "cuenta_bloqueada" tinyint(1) NOT NULL DEFAULT '0',
  "intentos_fallidos" int NOT NULL DEFAULT '0',
  "fecha_desbloqueo" datetime DEFAULT NULL,
  "ultimo_acceso" datetime DEFAULT NULL,
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  "updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  UNIQUE KEY "email" ("email"),
  KEY "idx_email" ("email"),
  KEY "idx_rol" ("rol"),
  KEY "plan_id" ("plan_id"),
  CONSTRAINT "usuarios_ibfk_1" FOREIGN KEY ("plan_id") REFERENCES "planes_suscripcion" ("id")
);

-- 3. Entidades principales espaciales

-- budgetmap.lugares definition
CREATE TABLE "lugares" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "nombre" varchar(255) NOT NULL,
  "categoria" enum('PARQUE', 'MUSEO', 'SITIO_TURISTICO', 'BIBLIOTECA', 'OTRO') NOT NULL,
  "descripcion" text,
  "direccion" varchar(255) DEFAULT NULL,
  "latitud" double NOT NULL,
  "longitud" double NOT NULL,
  "ubicacion" point NOT NULL /*!80003 SRID 4326 */,
  "imagen_url" varchar(1000) DEFAULT NULL,
  "aforo_maximo" int DEFAULT NULL,
  "destacado" tinyint(1) DEFAULT '0',
  "estado" enum('PENDIENTE', 'APROBADO', 'RECHAZADO') DEFAULT 'PENDIENTE',
  "activo" tinyint(1) DEFAULT '1',
  "verificado" tinyint(1) NOT NULL DEFAULT '0',
  "moderador_id" bigint DEFAULT NULL,
  "fecha_aprobacion" datetime DEFAULT NULL,
  "motivo_rechazo" varchar(255) DEFAULT NULL,
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  "updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  SPATIAL KEY "idx_ubicacion_lugar" ("ubicacion"),
  KEY "moderador_id" ("moderador_id"),
  CONSTRAINT "lugares_ibfk_1" FOREIGN KEY ("moderador_id") REFERENCES "usuarios" ("id")
);

-- budgetmap.establecimientos definition
CREATE TABLE "establecimientos" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "propietario_id" bigint NOT NULL,
  "nit" varchar(255) DEFAULT NULL,
  "nombre" varchar(255) NOT NULL,
  "categoria" enum('RESTAURANTE', 'PANADERIA', 'BAR', 'TIENDA', 'SUPERMERCADO', 'FARMACIA', 'HOTEL', 'GIMNASIO', 'OTRO') NOT NULL,
  "descripcion" text,
  "direccion" varchar(255) DEFAULT NULL,
  "telefono" varchar(255) DEFAULT NULL,
  "horario_atencion" varchar(255) DEFAULT NULL,
  "latitud" double NOT NULL,
  "longitud" double NOT NULL,
  "ubicacion" point NOT NULL /*!80003 SRID 4326 */,
  "imagen_url" varchar(1000) DEFAULT NULL,
  "rut_pdf_url" varchar(500) DEFAULT NULL,
  "aforo_maximo" int DEFAULT NULL,
  "aforo_actual" int DEFAULT '0',
  "reservas_habilitadas" tinyint(1) DEFAULT '0',
  "destacado" tinyint(1) DEFAULT '0',
  "pin_destacado" tinyint(1) DEFAULT '0',
  "color_pin" varchar(20) DEFAULT 'NORMAL',
  "estado" enum('PENDIENTE', 'APROBADO', 'RECHAZADO') DEFAULT 'PENDIENTE',
  "activo" tinyint(1) DEFAULT '1',
  "verificado" tinyint(1) NOT NULL DEFAULT '0',
  "moderador_id" bigint DEFAULT NULL,
  "fecha_aprobacion" datetime DEFAULT NULL,
  "motivo_rechazo" varchar(1000) DEFAULT NULL,
  "fin_publicidad" datetime DEFAULT NULL,
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  "updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  UNIQUE KEY "nit" ("nit"),
  SPATIAL KEY "idx_ubicacion_estab" ("ubicacion"),
  KEY "propietario_id" ("propietario_id"),
  KEY "moderador_id" ("moderador_id"),
  CONSTRAINT "establecimientos_ibfk_1" FOREIGN KEY ("propietario_id") REFERENCES "usuarios" ("id"),
  CONSTRAINT "establecimientos_ibfk_2" FOREIGN KEY ("moderador_id") REFERENCES "usuarios" ("id")
);

-- 4. Entidades transaccionales y temporales

-- budgetmap.eventos definition
CREATE TABLE "eventos" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "creador_id" bigint NOT NULL,
  "lugar_id" bigint DEFAULT NULL,
  "establecimiento_id" bigint DEFAULT NULL,
  "nombre" varchar(255) NOT NULL,
  "tipo_evento" enum('ARTISTICO', 'CULTURAL', 'DEPORTIVO', 'VETERINARIO', 'RECREATIVO') NOT NULL,
  "descripcion" text,
  "fecha_inicio" date NOT NULL,
  "fecha_fin" date DEFAULT NULL,
  "hora_inicio" time NOT NULL,
  "hora_fin" time DEFAULT NULL,
  "precio" decimal(10, 2) DEFAULT '0.00',
  "aforo_maximo" int DEFAULT NULL,
  "aforo_actual" int DEFAULT '0',
  "imagen_url" varchar(1000) DEFAULT NULL,
  "destacado" tinyint(1) DEFAULT '0',
  "estado" enum('PENDIENTE', 'APROBADO', 'RECHAZADO', 'EN_REVISION') NOT NULL,
  "activo" tinyint(1) DEFAULT '1',
  "verificado" tinyint(1) NOT NULL DEFAULT '0',
  "moderador_id" bigint DEFAULT NULL,
  "fecha_aprobacion" datetime DEFAULT NULL,
  "motivo_rechazo" varchar(500) DEFAULT NULL,
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  "updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  KEY "lugar_id" ("lugar_id"),
  KEY "establecimiento_id" ("establecimiento_id"),
  KEY "creador_id" ("creador_id"),
  KEY "moderador_id" ("moderador_id"),
  CONSTRAINT "eventos_ibfk_1" FOREIGN KEY ("lugar_id") REFERENCES "lugares" ("id"),
  CONSTRAINT "eventos_ibfk_2" FOREIGN KEY ("establecimiento_id") REFERENCES "establecimientos" ("id"),
  CONSTRAINT "eventos_ibfk_3" FOREIGN KEY ("creador_id") REFERENCES "usuarios" ("id"),
  CONSTRAINT "eventos_ibfk_4" FOREIGN KEY ("moderador_id") REFERENCES "usuarios" ("id")
);

-- budgetmap.promociones definition
CREATE TABLE "promociones" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "establecimiento_id" bigint DEFAULT NULL,
  "evento_id" bigint DEFAULT NULL,
  "titulo" varchar(255) NOT NULL,
  "descripcion" text,
  "codigo_cupon" varchar(255) DEFAULT NULL,
  "descuento_porcentaje" int DEFAULT '0',
  "descuento_valor" decimal(10, 2) DEFAULT '0.00',
  "precio_especial" decimal(10, 2) DEFAULT '0.00',
  "fecha_inicio" date NOT NULL,
  "fecha_fin" date NOT NULL,
  "usos_maximos" int DEFAULT NULL,
  "usos_actuales" int DEFAULT '0',
  "solo_pro" tinyint(1) DEFAULT '0',
  "imagen_url" varchar(1000) DEFAULT NULL,
  "activo" tinyint(1) DEFAULT '1',
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  "updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  KEY "establecimiento_id" ("establecimiento_id"),
  KEY "evento_id" ("evento_id"),
  CONSTRAINT "promociones_ibfk_1" FOREIGN KEY ("establecimiento_id") REFERENCES "establecimientos" ("id"),
  CONSTRAINT "promociones_ibfk_2" FOREIGN KEY ("evento_id") REFERENCES "eventos" ("id")
);

-- budgetmap.reservas definition
CREATE TABLE "reservas" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "usuario_id" bigint NOT NULL,
  "evento_id" bigint DEFAULT NULL,
  "establecimiento_id" bigint DEFAULT NULL,
  "lugar_id" bigint DEFAULT NULL,
  "promocion_id" bigint DEFAULT NULL,
  "codigo_reserva" varchar(255) NOT NULL,
  "fecha_reserva" date NOT NULL,
  "hora_inicio" time DEFAULT NULL,
  "hora_fin" time DEFAULT NULL,
  "numero_personas" int DEFAULT '1',
  "notas" text,
  "estado" enum('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA', 'REDIMIDA') DEFAULT 'PENDIENTE',
  "puntos_otorgados" int DEFAULT '0',
  "comision_cobrada" decimal(10, 2) DEFAULT '0.00',
  "motivo_cancelacion" varchar(500) DEFAULT NULL,
  "fecha_validacion" datetime DEFAULT NULL,
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  "updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  UNIQUE KEY "codigo_reserva" ("codigo_reserva"),
  KEY "usuario_id" ("usuario_id"),
  KEY "evento_id" ("evento_id"),
  KEY "establecimiento_id" ("establecimiento_id"),
  KEY "lugar_id" ("lugar_id"),
  KEY "promocion_id" ("promocion_id"),
  CONSTRAINT "reservas_ibfk_1" FOREIGN KEY ("usuario_id") REFERENCES "usuarios" ("id"),
  CONSTRAINT "reservas_ibfk_2" FOREIGN KEY ("evento_id") REFERENCES "eventos" ("id"),
  CONSTRAINT "reservas_ibfk_3" FOREIGN KEY ("establecimiento_id") REFERENCES "establecimientos" ("id"),
  CONSTRAINT "reservas_ibfk_4" FOREIGN KEY ("lugar_id") REFERENCES "lugares" ("id"),
  CONSTRAINT "reservas_ibfk_5" FOREIGN KEY ("promocion_id") REFERENCES "promociones" ("id")
);

-- 5. Interacción y operaciones secundarias

-- budgetmap.cupones_redimidos definition
CREATE TABLE "cupones_redimidos" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "usuario_id" bigint NOT NULL,
  "establecimiento_id" bigint NOT NULL,
  "codigo_unico" varchar(20) NOT NULL,
  "titulo_descuento" varchar(255) NOT NULL,
  "puntos_gastados" int NOT NULL,
  "usado" tinyint(1) NOT NULL,
  "fecha_redencion" datetime(6) DEFAULT NULL,
  "fecha_expiracion" datetime(6) DEFAULT NULL,
  PRIMARY KEY ("id"),
  UNIQUE KEY "UK_ipckg9a8c7xsna78ubhxd7df4" ("codigo_unico"),
  KEY "FK9e577ako84ls5wn28obac41q9" ("establecimiento_id"),
  KEY "FKfqdohhqicy17r1qw5kdk0hf34" ("usuario_id"),
  CONSTRAINT "FK9e577ako84ls5wn28obac41q9" FOREIGN KEY ("establecimiento_id") REFERENCES "establecimientos" ("id"),
  CONSTRAINT "FKfqdohhqicy17r1qw5kdk0hf34" FOREIGN KEY ("usuario_id") REFERENCES "usuarios" ("id")
);

-- budgetmap.transacciones definition
CREATE TABLE "transacciones" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "usuario_id" bigint NOT NULL,
  "referencia_pago" varchar(255) DEFAULT NULL,
  "tipo" enum('COMPRA_PLAN', 'COMPRA_PUNTOS', 'COMISION_RESERVA', 'PAGO_ADS') NOT NULL,
  "monto" decimal(38, 2) NOT NULL,
  "metodo_pago" varchar(50) DEFAULT NULL,
  "estado" enum('PENDIENTE', 'EXITOSO', 'FALLIDO', 'REEMBOLSADO') DEFAULT 'PENDIENTE',
  "fecha_transaccion" datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  UNIQUE KEY "referencia_pago" ("referencia_pago"),
  KEY "usuario_id" ("usuario_id"),
  CONSTRAINT "transacciones_ibfk_1" FOREIGN KEY ("usuario_id") REFERENCES "usuarios" ("id")
);

-- budgetmap.pqrs definition
CREATE TABLE "pqrs" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "usuario_id" bigint NOT NULL,
  "codigo_ticket" varchar(255) NOT NULL,
  "tipo" enum('PETICION', 'QUEJA', 'RECLAMO', 'SUGERENCIA') NOT NULL,
  "asunto" varchar(255) NOT NULL,
  "descripcion" text NOT NULL,
  "adjuntos" varchar(1000) DEFAULT NULL,
  "prioridad" varchar(10) DEFAULT NULL,
  "estado" enum('ABIERTO', 'EN_PROCESO', 'RESPONDIDO', 'CERRADO') DEFAULT 'ABIERTO',
  "respuesta" text,
  "moderador_asignado_id" bigint DEFAULT NULL,
  "fecha_respuesta" datetime DEFAULT NULL,
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  "updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  UNIQUE KEY "codigo_ticket" ("codigo_ticket"),
  KEY "usuario_id" ("usuario_id"),
  KEY "moderador_asignado_id" ("moderador_asignado_id"),
  CONSTRAINT "pqrs_ibfk_1" FOREIGN KEY ("usuario_id") REFERENCES "usuarios" ("id"),
  CONSTRAINT "pqrs_ibfk_2" FOREIGN KEY ("moderador_asignado_id") REFERENCES "usuarios" ("id")
);

-- 6. Analíticas, Notificaciones y Configuración

-- budgetmap.analiticas_locales definition
CREATE TABLE "analiticas_locales" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "establecimiento_id" bigint NOT NULL,
  "fecha" date NOT NULL,
  "vistas_mapa" int DEFAULT '0',
  "clics_perfil" int DEFAULT '0',
  "cupones_vistos" int DEFAULT '0',
  "exploradores_cercanos_promedio" int DEFAULT '0',
  PRIMARY KEY ("id"),
  UNIQUE KEY "idx_estab_fecha" ("establecimiento_id", "fecha"),
  CONSTRAINT "analiticas_locales_ibfk_1" FOREIGN KEY ("establecimiento_id") REFERENCES "establecimientos" ("id")
);

-- budgetmap.notificaciones definition
CREATE TABLE "notificaciones" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "usuario_id" bigint NOT NULL,
  "referencia_id" bigint DEFAULT NULL,
  "referencia_tipo" varchar(50) DEFAULT NULL,
  "tipo" enum('RESERVA_CONFIRMADA', 'RESERVA_CANCELADA', 'ALERTA_PROXIMIDAD', 'PROMOCION_NUEVA', 'EVENTO_RECORDATORIO', 'PQRS_RESPUESTA', 'SISTEMA') NOT NULL,
  "titulo" varchar(255) NOT NULL,
  "mensaje" text NOT NULL,
  "origen" varchar(20) DEFAULT NULL,
  "accion_url" varchar(500) DEFAULT NULL,
  "imagen_url" varchar(500) DEFAULT NULL,
  "leida" tinyint(1) DEFAULT '0',
  "fecha_lectura" datetime DEFAULT NULL,
  "created_at" datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  KEY "usuario_id" ("usuario_id"),
  CONSTRAINT "notificaciones_ibfk_1" FOREIGN KEY ("usuario_id") REFERENCES "usuarios" ("id")
);

-- budgetmap.config_alertas definition
CREATE TABLE "config_alertas" (
  "id" bigint NOT NULL AUTO_INCREMENT,
  "usuario_id" bigint NOT NULL,
  "radio_metros" int DEFAULT '500',
  "notificar_promociones" tinyint(1) DEFAULT '1',
  "notificar_eventos" tinyint(1) DEFAULT '1',
  "activo" tinyint(1) DEFAULT '1',
  "updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY ("id"),
  UNIQUE KEY "usuario_id" ("usuario_id"),
  CONSTRAINT "config_alertas_ibfk_1" FOREIGN KEY ("usuario_id") REFERENCES "usuarios" ("id")
);
