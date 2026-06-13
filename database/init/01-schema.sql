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

-- budgetmap.usuarios definition

CREATE TABLE "usuarios" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"email" varchar(255) NOT NULL,
"password" varchar(255) NOT NULL,
"nombre" varchar(255) NOT NULL,
"apellido" varchar(255) DEFAULT NULL,
"telefono" varchar(255) DEFAULT NULL,
"rol" varchar(50) NOT NULL,
"plan_id" int DEFAULT NULL,
"fecha_fin_suscripcion" datetime DEFAULT NULL,
"puntos_acumulados" int DEFAULT '0',
"activo" tinyint(1) DEFAULT '1',
"email_verificado" tinyint(1) DEFAULT '0',
"ultimo_acceso" datetime DEFAULT NULL,
"created_at" datetime DEFAULT CURRENT_TIMESTAMP,
"updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON
UPDATE
    CURRENT_TIMESTAMP,
    "avatar_url" varchar(1000) DEFAULT NULL,
    "cuenta_bloqueada" tinyint(1) NOT NULL DEFAULT '0',
    "intentos_fallidos" int NOT NULL DEFAULT '0',
    "fecha_desbloqueo" datetime DEFAULT NULL,
    PRIMARY KEY ("id"),
    UNIQUE KEY "email" ("email"),
    KEY "idx_email" ("email"),
    KEY "idx_rol" ("rol"),
    KEY "plan_id" ("plan_id"),
    CONSTRAINT "usuarios_ibfk_1" FOREIGN KEY ("plan_id") REFERENCES "planes_suscripcion" ("id")
);


-- budgetmap.lugares definition

CREATE TABLE "lugares" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"nombre" varchar(255) NOT NULL,
"descripcion" text,
"categoria" enum('PARQUE', 'MUSEO', 'SITIO_TURISTICO', 'BIBLIOTECA', 'OTRO') NOT NULL,
"direccion" varchar(255) DEFAULT NULL,
"latitud" double NOT NULL,
"longitud" double NOT NULL,
"ubicacion" point NOT NULL /*!80003 SRID 4326 */
,
"imagen_url" varchar(1000) DEFAULT NULL,
"aforo_maximo" int DEFAULT NULL,
"estado" enum('PENDIENTE', 'APROBADO', 'RECHAZADO') DEFAULT 'PENDIENTE',
"moderador_id" bigint DEFAULT NULL,
"fecha_aprobacion" datetime DEFAULT NULL,
"motivo_rechazo" varchar(255) DEFAULT NULL,
"destacado" tinyint(1) DEFAULT '0',
"activo" tinyint(1) DEFAULT '1',
"created_at" datetime DEFAULT CURRENT_TIMESTAMP,
"updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON
UPDATE
    CURRENT_TIMESTAMP,
    "verificado" tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY ("id"),
    SPATIAL KEY "idx_ubicacion_lugar" ("ubicacion"),
    KEY "moderador_id" ("moderador_id"),
    CONSTRAINT "lugares_ibfk_1" FOREIGN KEY ("moderador_id") REFERENCES "usuarios" ("id")
);

-- budgetmap.establecimientos definition

CREATE TABLE "establecimientos" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"nombre" varchar(255) NOT NULL,
"nit" varchar(255) DEFAULT NULL,
"descripcion" text,
"categoria" enum('RESTAURANTE', 'PANADERIA', 'BAR', 'TIENDA', 'SUPERMERCADO', 'FARMACIA', 'HOTEL', 'GIMNASIO', 'OTRO') NOT NULL,
"propietario_id" bigint NOT NULL,
"direccion" varchar(255) DEFAULT NULL,
"latitud" double NOT NULL,
"longitud" double NOT NULL,
"ubicacion" point NOT NULL /*!80003 SRID 4326 */,
"imagen_url" varchar(1000) DEFAULT NULL,
"aforo_maximo" int DEFAULT NULL,
"aforo_actual" int DEFAULT '0',
"telefono" varchar(255) DEFAULT NULL,
"horario_atencion" varchar(255) DEFAULT NULL,
"reservas_habilitadas" tinyint(1) DEFAULT '0',
"estado" enum('PENDIENTE', 'APROBADO', 'RECHAZADO') DEFAULT 'PENDIENTE',
"moderador_id" bigint DEFAULT NULL,
"fecha_aprobacion" datetime DEFAULT NULL,
"motivo_rechazo" varchar(1000) DEFAULT NULL,
"pin_destacado" tinyint(1) DEFAULT '0',
"color_pin" varchar(20) DEFAULT 'NORMAL',
"fin_publicidad" datetime DEFAULT NULL,
"destacado" tinyint(1) DEFAULT '0',
"activo" tinyint(1) DEFAULT '1',
"created_at" datetime DEFAULT CURRENT_TIMESTAMP,
"updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON
UPDATE
    CURRENT_TIMESTAMP,
    "rut_pdf_url" varchar(500) DEFAULT NULL,
    "verificado" tinyint(1) NOT NULL DEFAULT '0',
    PRIMARY KEY ("id"),
    UNIQUE KEY "nit" ("nit"),
    SPATIAL KEY "idx_ubicacion_estab" ("ubicacion"),
    KEY "propietario_id" ("propietario_id"),
    KEY "moderador_id" ("moderador_id"),
    CONSTRAINT "establecimientos_ibfk_1" FOREIGN KEY ("propietario_id") REFERENCES "usuarios" ("id"),
    CONSTRAINT "establecimientos_ibfk_2" FOREIGN KEY ("moderador_id") REFERENCES "usuarios" ("id")
);

-- budgetmap.eventos definition

CREATE TABLE "eventos" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"nombre" varchar(255) NOT NULL,
"descripcion" text,
"tipo_evento" enum('ARTISTICO', 'CULTURAL', 'DEPORTIVO', 'VETERINARIO', 'RECREATIVO') NOT NULL,
"lugar_id" bigint DEFAULT NULL,
"establecimiento_id" bigint DEFAULT NULL,
"creador_id" bigint NOT NULL,
"fecha_inicio" date NOT NULL,
"fecha_fin" date DEFAULT NULL,
"hora_inicio" time NOT NULL,
"hora_fin" time DEFAULT NULL,
"aforo_maximo" int DEFAULT NULL,
"aforo_actual" int DEFAULT '0',
"precio" decimal(10, 2) DEFAULT '0.00',
"imagen_url" varchar(1000) DEFAULT NULL,
"activo" tinyint(1) DEFAULT '1',
"destacado" tinyint(1) DEFAULT '0',
"created_at" datetime DEFAULT CURRENT_TIMESTAMP,
"updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON
UPDATE
    CURRENT_TIMESTAMP,
    "estado" enum('PENDIENTE', 'APROBADO', 'RECHAZADO', 'EN_REVISION') NOT NULL,
    "moderador_id" bigint DEFAULT NULL,
    "fecha_aprobacion" datetime DEFAULT NULL,
    "motivo_rechazo" varchar(500) DEFAULT NULL,
    "verificado" tinyint(1) NOT NULL DEFAULT '0',
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
"titulo" varchar(255) NOT NULL,
"descripcion" text,
"establecimiento_id" bigint DEFAULT NULL,
"evento_id" bigint DEFAULT NULL,
"descuento_porcentaje" int DEFAULT '0',
"descuento_valor" decimal(10, 2) DEFAULT '0.00',
"precio_especial" decimal(10, 2) DEFAULT '0.00',
"fecha_inicio" date NOT NULL,
"fecha_fin" date NOT NULL,
"codigo_cupon" varchar(255) DEFAULT NULL,
"usos_maximos" int DEFAULT NULL,
"usos_actuales" int DEFAULT '0',
"solo_pro" tinyint(1) DEFAULT '0',
"imagen_url" varchar(1000) DEFAULT NULL,
"activo" tinyint(1) DEFAULT '1',
"created_at" datetime DEFAULT CURRENT_TIMESTAMP,
"updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON
UPDATE
    CURRENT_TIMESTAMP,
    PRIMARY KEY ("id"),
    KEY "establecimiento_id" ("establecimiento_id"),
    KEY "evento_id" ("evento_id"),
    CONSTRAINT "promociones_ibfk_1" FOREIGN KEY ("establecimiento_id") REFERENCES "establecimientos" ("id"),
    CONSTRAINT "promociones_ibfk_2" FOREIGN KEY ("evento_id") REFERENCES "eventos" ("id")
);

-- budgetmap.reservas definition

CREATE TABLE "reservas" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"codigo_reserva" varchar(255) NOT NULL,
"usuario_id" bigint NOT NULL,
"evento_id" bigint DEFAULT NULL,
"establecimiento_id" bigint DEFAULT NULL,
"lugar_id" bigint DEFAULT NULL,
"promocion_id" bigint DEFAULT NULL,
"fecha_reserva" date NOT NULL,
"numero_personas" int DEFAULT '1',
"estado" enum('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA', 'REDIMIDA') DEFAULT 'PENDIENTE',
"puntos_otorgados" int DEFAULT '0',
"comision_cobrada" decimal(10, 2) DEFAULT '0.00',
"created_at" datetime DEFAULT CURRENT_TIMESTAMP,
"hora_inicio" time DEFAULT NULL,
"hora_fin" time DEFAULT NULL,
"fecha_validacion" datetime DEFAULT NULL,
"notas" text,
"motivo_cancelacion" varchar(500) DEFAULT NULL,
"updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON
UPDATE
    CURRENT_TIMESTAMP,
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


-- budgetmap.pqrs definition

CREATE TABLE "pqrs" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"codigo_ticket" varchar(255) NOT NULL,
"usuario_id" bigint NOT NULL,
"tipo" enum('PETICION', 'QUEJA', 'RECLAMO', 'SUGERENCIA') NOT NULL,
"asunto" varchar(255) NOT NULL,
"descripcion" text NOT NULL,
"estado" enum('ABIERTO', 'EN_PROCESO', 'RESPONDIDO', 'CERRADO') DEFAULT 'ABIERTO',
"prioridad" varchar(10) DEFAULT NULL,
"moderador_asignado_id" bigint DEFAULT NULL,
"respuesta" text,
"fecha_respuesta" datetime DEFAULT NULL,
"created_at" datetime DEFAULT CURRENT_TIMESTAMP,
"adjuntos" varchar(1000) DEFAULT NULL,
"updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON
UPDATE
    CURRENT_TIMESTAMP,
    PRIMARY KEY ("id"),
    UNIQUE KEY "codigo_ticket" ("codigo_ticket"),
    KEY "usuario_id" ("usuario_id"),
    KEY "moderador_asignado_id" ("moderador_asignado_id"),
    CONSTRAINT "pqrs_ibfk_1" FOREIGN KEY ("usuario_id") REFERENCES "usuarios" ("id"),
    CONSTRAINT "pqrs_ibfk_2" FOREIGN KEY ("moderador_asignado_id") REFERENCES "usuarios" ("id")
);


-- budgetmap.cupones_redimidos definition

CREATE TABLE "cupones_redimidos" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"codigo_unico" varchar(20) NOT NULL,
"fecha_expiracion" datetime(6) DEFAULT NULL,
"fecha_redencion" datetime(6) DEFAULT NULL,
"puntos_gastados" int NOT NULL,
"titulo_descuento" varchar(255) NOT NULL,
"usado" tinyint(1) NOT NULL,
"establecimiento_id" bigint NOT NULL,
"usuario_id" bigint NOT NULL,
PRIMARY KEY ("id"),
UNIQUE KEY "UK_ipckg9a8c7xsna78ubhxd7df4" ("codigo_unico"),
KEY "FK9e577ako84ls5wn28obac41q9" ("establecimiento_id"),
KEY "FKfqdohhqicy17r1qw5kdk0hf34" ("usuario_id"),
CONSTRAINT "FK9e577ako84ls5wn28obac41q9" FOREIGN KEY ("establecimiento_id") REFERENCES "establecimientos" ("id"),
CONSTRAINT "FKfqdohhqicy17r1qw5kdk0hf34" FOREIGN KEY ("usuario_id") REFERENCES "usuarios" ("id")
);

-- budgetmap.tokens_revocados definition

CREATE TABLE "tokens_revocados" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"created_at" datetime(6) NOT NULL,
"fecha_expiracion" datetime(6) NOT NULL,
"token" varchar(512) NOT NULL,
PRIMARY KEY ("id"),
UNIQUE KEY "UK_svutquepwyiy8h1lyb0cacxa9" ("token")
);

-- budgetmap.transacciones definition

CREATE TABLE "transacciones" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"usuario_id" bigint NOT NULL,
"tipo" enum('COMPRA_PLAN', 'COMPRA_PUNTOS', 'COMISION_RESERVA', 'PAGO_ADS') NOT NULL,
"monto" decimal(38, 2) NOT NULL,
"metodo_pago" varchar(50) DEFAULT NULL,
"referencia_pago" varchar(255) DEFAULT NULL,
"estado" enum('PENDIENTE', 'EXITOSO', 'FALLIDO', 'REEMBOLSADO') DEFAULT 'PENDIENTE',
"fecha_transaccion" datetime DEFAULT CURRENT_TIMESTAMP,
PRIMARY KEY ("id"),
UNIQUE KEY "referencia_pago" ("referencia_pago"),
KEY "usuario_id" ("usuario_id"),
CONSTRAINT "transacciones_ibfk_1" FOREIGN KEY ("usuario_id") REFERENCES "usuarios" ("id")
);

-- budgetmap.analiticas_locales definition

CREATE TABLE "analiticas_locales" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"establecimiento_id" bigint NOT NULL,
"fecha" date NOT NULL,
"clics_perfil" int DEFAULT '0',
"vistas_mapa" int DEFAULT '0',
"cupones_vistos" int DEFAULT '0',
"exploradores_cercanos_promedio" int DEFAULT '0',
PRIMARY KEY ("id"),
UNIQUE KEY "idx_estab_fecha" ("establecimiento_id",
"fecha"),
CONSTRAINT "analiticas_locales_ibfk_1" FOREIGN KEY ("establecimiento_id") REFERENCES "establecimientos" ("id")
);

-- budgetmap.notificaciones definition

CREATE TABLE "notificaciones" (
  "id" bigint NOT NULL AUTO_INCREMENT,
"usuario_id" bigint NOT NULL,
"tipo" enum('RESERVA_CONFIRMADA', 'RESERVA_CANCELADA', 'ALERTA_PROXIMIDAD', 'PROMOCION_NUEVA', 'EVENTO_RECORDATORIO', 'PQRS_RESPUESTA', 'SISTEMA') NOT NULL,
"titulo" varchar(255) NOT NULL,
"mensaje" text NOT NULL,
"referencia_id" bigint DEFAULT NULL,
"referencia_tipo" varchar(50) DEFAULT NULL,
"leida" tinyint(1) DEFAULT '0',
"origen" varchar(20) DEFAULT NULL,
"fecha_lectura" datetime DEFAULT NULL,
"accion_url" varchar(500) DEFAULT NULL,
"imagen_url" varchar(500) DEFAULT NULL,
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
"updated_at" datetime DEFAULT CURRENT_TIMESTAMP ON
UPDATE
    CURRENT_TIMESTAMP,
    PRIMARY KEY ("id"),
    UNIQUE KEY "usuario_id" ("usuario_id"),
    CONSTRAINT "config_alertas_ibfk_1" FOREIGN KEY ("usuario_id") REFERENCES "usuarios" ("id")
);
