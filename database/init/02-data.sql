USE budgetmap;

-- Contraseña universal: BudgetMap2026
SET @pwd = '$2a$12$aMFbZoljD1yS/5rKlTSTyeavdIQcVF4j79Dh7m5zHmA1yoshNAefK';

-- =================================================================
-- 1. USUARIOS (Con variabilidad de estado "activo")
-- =================================================================
INSERT INTO usuarios (email, password, nombre, rol, email_verificado, activo) VALUES 
-- Admin (ID 1) y Mods (ID 2, 3) -> Siempre activos
('admin@budgetmap.com', @pwd, 'Admin Principal', 'ADMINISTRADOR', 1, 1),
('mod1@budgetmap.com', @pwd, 'Moderador Uno', 'MODERADOR', 1, 1),
('mod2@budgetmap.com', @pwd, 'Moderador Dos', 'MODERADOR', 1, 1),

-- Aliados (IDs 4 al 13) -> El 12 y 13 están INACTIVOS
('aliado1@budgetmap.com', @pwd, 'Aliado 1', 'LOCAL_ALIADO', 1, 1),
('aliado2@budgetmap.com', @pwd, 'Aliado 2', 'LOCAL_ALIADO', 1, 1),
('aliado3@budgetmap.com', @pwd, 'Aliado 3', 'LOCAL_ALIADO', 1, 1),
('aliado4@budgetmap.com', @pwd, 'Aliado 4', 'LOCAL_ALIADO', 1, 1),
('aliado5@budgetmap.com', @pwd, 'Aliado 5', 'LOCAL_ALIADO', 1, 1),
('aliado6@budgetmap.com', @pwd, 'Aliado 6', 'LOCAL_ALIADO', 1, 1),
('aliado7@budgetmap.com', @pwd, 'Aliado 7', 'LOCAL_ALIADO', 1, 1),
('aliado8@budgetmap.com', @pwd, 'Aliado 8', 'LOCAL_ALIADO', 1, 1),
('aliado9@budgetmap.com', @pwd, 'Aliado 9 (Inactivo)', 'LOCAL_ALIADO', 1, 0),
('aliado10@budgetmap.com', @pwd, 'Aliado 10 (Inactivo)', 'LOCAL_ALIADO', 1, 0),

-- Anfitriones (IDs 14 al 23) -> El 22 y 23 están INACTIVOS
('anfitrion1@budgetmap.com', @pwd, 'Anfitrión 1', 'ANFITRION', 1, 1),
('anfitrion2@budgetmap.com', @pwd, 'Anfitrión 2', 'ANFITRION', 1, 1),
('anfitrion3@budgetmap.com', @pwd, 'Anfitrión 3', 'ANFITRION', 1, 1),
('anfitrion4@budgetmap.com', @pwd, 'Anfitrión 4', 'ANFITRION', 1, 1),
('anfitrion5@budgetmap.com', @pwd, 'Anfitrión 5', 'ANFITRION', 1, 1),
('anfitrion6@budgetmap.com', @pwd, 'Anfitrión 6', 'ANFITRION', 1, 1),
('anfitrion7@budgetmap.com', @pwd, 'Anfitrión 7', 'ANFITRION', 1, 1),
('anfitrion8@budgetmap.com', @pwd, 'Anfitrión 8', 'ANFITRION', 1, 1),
('anfitrion9@budgetmap.com', @pwd, 'Anfitrión 9 (Inactivo)', 'ANFITRION', 1, 0),
('anfitrion10@budgetmap.com', @pwd, 'Anfitrión 10 (Inactivo)', 'ANFITRION', 1, 0),

-- Exploradores (IDs 24 al 43) -> Del 40 al 43 están INACTIVOS
('exp1@budgetmap.com', @pwd, 'Explorador 1', 'EXPLORADOR', 1, 1),
('exp2@budgetmap.com', @pwd, 'Explorador 2', 'EXPLORADOR', 1, 1),
('exp3@budgetmap.com', @pwd, 'Explorador 3', 'EXPLORADOR', 1, 1),
('exp4@budgetmap.com', @pwd, 'Explorador 4', 'EXPLORADOR', 1, 1),
('exp5@budgetmap.com', @pwd, 'Explorador 5', 'EXPLORADOR', 1, 1),
('exp6@budgetmap.com', @pwd, 'Explorador 6', 'EXPLORADOR', 1, 1),
('exp7@budgetmap.com', @pwd, 'Explorador 7', 'EXPLORADOR', 1, 1),
('exp8@budgetmap.com', @pwd, 'Explorador 8', 'EXPLORADOR', 1, 1),
('exp9@budgetmap.com', @pwd, 'Explorador 9', 'EXPLORADOR', 1, 1),
('exp10@budgetmap.com', @pwd, 'Explorador 10', 'EXPLORADOR', 1, 1),
('exp11@budgetmap.com', @pwd, 'Explorador 11', 'EXPLORADOR', 1, 1),
('exp12@budgetmap.com', @pwd, 'Explorador 12', 'EXPLORADOR', 1, 1),
('exp13@budgetmap.com', @pwd, 'Explorador 13', 'EXPLORADOR', 1, 1),
('exp14@budgetmap.com', @pwd, 'Explorador 14', 'EXPLORADOR', 1, 1),
('exp15@budgetmap.com', @pwd, 'Explorador 15', 'EXPLORADOR', 1, 1),
('exp16@budgetmap.com', @pwd, 'Explorador 16', 'EXPLORADOR', 1, 1),
('exp17@budgetmap.com', @pwd, 'Explorador 17 (Inactivo)', 'EXPLORADOR', 1, 0),
('exp18@budgetmap.com', @pwd, 'Explorador 18 (Inactivo)', 'EXPLORADOR', 1, 0),
('exp19@budgetmap.com', @pwd, 'Explorador 19 (Inactivo)', 'EXPLORADOR', 1, 0),
('exp20@budgetmap.com', @pwd, 'Explorador 20 (Inactivo)', 'EXPLORADOR', 1, 0);

-- =================================================================
-- 2. LUGARES (Públicos) - 10 Registros
-- Tienen estados PENDIENTE, APROBADO (por Admin o Mod) y RECHAZADO
-- =================================================================
INSERT INTO lugares (nombre, descripcion, categoria, direccion, latitud, longitud, ubicacion, aforo_maximo, estado, moderador_id, fecha_aprobacion, motivo_rechazo, activo) VALUES 
('Parque Simón Bolívar', 'Gran parque metropolitano', 'PARQUE', 'Calle 63 y 53', 4.658, -74.093, ST_GeomFromText('POINT(-74.093 4.658)', 4326), 10000, 'APROBADO', 1, NOW(), NULL, 1),
('Museo del Oro', 'Colección de orfebrería', 'MUSEO', 'Carrera 6 # 15-88', 4.601, -74.071, ST_GeomFromText('POINT(-74.071 4.601)', 4326), 500, 'APROBADO', 2, NOW(), NULL, 1),
('Cerro Monserrate', 'Mirador turístico', 'SITIO_TURISTICO', 'Carrera 2 Este # 21-48', 4.605, -74.055, ST_GeomFromText('POINT(-74.055 4.605)', 4326), 2000, 'APROBADO', 3, NOW(), NULL, 1),
('Biblioteca Virgilio Barco', 'Biblioteca pública', 'BIBLIOTECA', 'Avenida 50 # 20-50', 4.656, -74.088, ST_GeomFromText('POINT(-74.088 4.656)', 4326), 800, 'APROBADO', 1, NOW(), NULL, 1),
('Jardín Botánico', 'Flora colombiana', 'PARQUE', 'Avenida 63 # 68-95', 4.668, -74.099, ST_GeomFromText('POINT(-74.099 4.668)', 4326), 3000, 'PENDIENTE', NULL, NULL, NULL, 1),
('Museo Nacional', 'Historia de Colombia', 'MUSEO', 'Carrera 7 # 28-66', 4.615, -74.068, ST_GeomFromText('POINT(-74.068 4.615)', 4326), 1000, 'PENDIENTE', NULL, NULL, NULL, 1),
('Planetario Distrital', 'Ciencia y astronomía', 'SITIO_TURISTICO', 'Calle 26B # 5-93', 4.612, -74.067, ST_GeomFromText('POINT(-74.067 4.612)', 4326), 400, 'RECHAZADO', 2, NULL, 'Faltan fotos claras del exterior.', 0),
('Parque de los Novios', 'Lagos y zonas verdes', 'PARQUE', 'Calle 63 # 45-10', 4.659, -74.082, ST_GeomFromText('POINT(-74.082 4.659)', 4326), 2000, 'RECHAZADO', 1, NULL, 'Coordenadas incorrectas, apunta al mar.', 1),
('Maloka', 'Museo interactivo', 'MUSEO', 'Carrera 68D # 24A-51', 4.642, -74.101, ST_GeomFromText('POINT(-74.101 4.642)', 4326), 1500, 'APROBADO', 3, NOW(), NULL, 0), -- Aprobado pero inactivo temporalmente
('Plaza de Bolívar', 'Centro histórico', 'SITIO_TURISTICO', 'Carrera 7 # 11-10', 4.598, -74.076, ST_GeomFromText('POINT(-74.076 4.598)', 4326), 5000, 'PENDIENTE', NULL, NULL, NULL, 1);

-- =================================================================
-- 3. ESTABLECIMIENTOS (10 locales)
-- Combinación de estados y validadores
-- =================================================================
INSERT INTO establecimientos (nombre, nit, categoria, propietario_id, direccion, latitud, longitud, ubicacion, aforo_maximo, estado, moderador_id, fecha_aprobacion, motivo_rechazo, activo) VALUES 
('Restaurante El Buen Sabor', '800111111-1', 'RESTAURANTE', 4, 'Calle 10 # 5-20', 4.598, -74.076, ST_GeomFromText('POINT(-74.076 4.598)', 4326), 50, 'APROBADO', 1, NOW(), NULL, 1),
('Panadería Central', '800222222-2', 'PANADERIA', 5, 'Carrera 15 # 72-10', 4.656, -74.059, ST_GeomFromText('POINT(-74.059 4.656)', 4326), 20, 'APROBADO', 2, NOW(), NULL, 1),
('Bar La Noche', '800333333-3', 'BAR', 6, 'Calle 85 # 11-50', 4.667, -74.053, ST_GeomFromText('POINT(-74.053 4.667)', 4326), 100, 'APROBADO', 3, NOW(), NULL, 1),
('Tienda La Esquina', '800444444-4', 'TIENDA', 7, 'Carrera 7 # 45-22', 4.631, -74.065, ST_GeomFromText('POINT(-74.065 4.631)', 4326), 15, 'APROBADO', 1, NOW(), NULL, 1),
('Supermercado Express', '800555555-5', 'SUPERMERCADO', 8, 'Avenida 19 # 120-30', 4.700, -74.040, ST_GeomFromText('POINT(-74.040 4.700)', 4326), 200, 'PENDIENTE', NULL, NULL, NULL, 1),
('Farmacia Salud', '800666666-6', 'FARMACIA', 9, 'Calle 53 # 24-15', 4.641, -74.080, ST_GeomFromText('POINT(-74.080 4.641)', 4326), 10, 'PENDIENTE', NULL, NULL, NULL, 1),
('Hotel Plaza', '800777777-7', 'HOTEL', 10, 'Carrera 10 # 26-21', 4.612, -74.069, ST_GeomFromText('POINT(-74.069 4.612)', 4326), 150, 'RECHAZADO', 2, NULL, 'El NIT no coincide con el registro en cámara de comercio.', 1),
('Gimnasio Fit', '800888888-8', 'GIMNASIO', 11, 'Calle 116 # 15-40', 4.695, -74.042, ST_GeomFromText('POINT(-74.042 4.695)', 4326), 80, 'RECHAZADO', 3, NULL, 'Falta dirección exacta.', 0),
('Restaurante Gourmet (Inactivo)', '800999999-9', 'RESTAURANTE', 12, 'Carrera 9 # 70-15', 4.654, -74.056, ST_GeomFromText('POINT(-74.056 4.654)', 4326), 60, 'APROBADO', 1, NOW(), NULL, 0),
('Café del Parque (Inactivo)', '800000000-0', 'OTRO', 13, 'Calle 93 # 12-20', 4.676, -74.048, ST_GeomFromText('POINT(-74.048 4.676)', 4326), 30, 'PENDIENTE', NULL, NULL, NULL, 0);

-- =================================================================
-- 4. EVENTOS (20 eventos)
-- Algunos inactivos
-- =================================================================
INSERT INTO eventos (nombre, tipo_evento, creador_id, lugar_id, fecha_inicio, hora_inicio, aforo_maximo, activo) VALUES 
('Concierto Rock', 'ARTISTICO', 14, 1, DATE_ADD(CURDATE(), INTERVAL 5 DAY), '19:00:00', 300, 1),
('Exposición de Arte', 'CULTURAL', 14, 2, DATE_ADD(CURDATE(), INTERVAL 10 DAY), '10:00:00', 100, 1),
('Maratón 5K', 'DEPORTIVO', 15, 3, DATE_ADD(CURDATE(), INTERVAL 15 DAY), '07:00:00', 500, 1),
('Jornada de Adopción', 'VETERINARIO', 15, 4, DATE_ADD(CURDATE(), INTERVAL 20 DAY), '09:00:00', 50, 1),
('Feria Gastronómica', 'CULTURAL', 16, 5, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '12:00:00', 400, 1),
('Obra de Teatro', 'ARTISTICO', 16, 6, DATE_ADD(CURDATE(), INTERVAL 8 DAY), '20:00:00', 150, 1),
('Torneo de Fútbol', 'DEPORTIVO', 17, 1, DATE_ADD(CURDATE(), INTERVAL 12 DAY), '08:00:00', 200, 1),
('Campamento Recreativo', 'RECREATIVO', 17, 3, DATE_ADD(CURDATE(), INTERVAL 25 DAY), '06:00:00', 80, 1),
('Cine al Parque', 'ARTISTICO', 18, 1, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '18:30:00', 250, 1),
('Taller de Pintura', 'CULTURAL', 18, 4, DATE_ADD(CURDATE(), INTERVAL 7 DAY), '15:00:00', 30, 1),
('Clase Yoga (Inactivo)', 'DEPORTIVO', 19, 1, DATE_ADD(CURDATE(), INTERVAL 14 DAY), '07:30:00', 120, 0),
('Campaña Vacunación', 'VETERINARIO', 19, 1, DATE_ADD(CURDATE(), INTERVAL 4 DAY), '09:00:00', 200, 1),
('Festival Cerveza', 'CULTURAL', 20, 1, DATE_ADD(CURDATE(), INTERVAL 21 DAY), '16:00:00', 600, 1),
('Stand-up Comedy', 'ARTISTICO', 20, 2, DATE_ADD(CURDATE(), INTERVAL 9 DAY), '21:00:00', 100, 1),
('Carrera Ciclismo', 'DEPORTIVO', 21, 3, DATE_ADD(CURDATE(), INTERVAL 16 DAY), '06:00:00', 300, 1),
('Picnic Familiar', 'RECREATIVO', 21, 1, DATE_ADD(CURDATE(), INTERVAL 6 DAY), '11:00:00', 150, 1),
('Noche Jazz (Inactivo)', 'ARTISTICO', 22, 2, DATE_ADD(CURDATE(), INTERVAL 11 DAY), '20:30:00', 80, 0),
('Feria Libro (Inactivo)', 'CULTURAL', 22, 4, DATE_ADD(CURDATE(), INTERVAL 18 DAY), '10:00:00', 800, 0),
('Torneo Tenis', 'DEPORTIVO', 23, 1, DATE_ADD(CURDATE(), INTERVAL 22 DAY), '09:00:00', 100, 1),
('Charla Animal (Inactivo)', 'VETERINARIO', 23, 4, DATE_ADD(CURDATE(), INTERVAL 13 DAY), '14:00:00', 60, 0);

-- =================================================================
-- 5. PROMOCIONES (50 promos, 5 por cada Establecimiento)
-- =================================================================
INSERT INTO promociones (titulo, establecimiento_id, descuento_porcentaje, fecha_inicio, fecha_fin, codigo_cupon, activo) VALUES 
-- Establecimiento 1
('Martes 2x1', 1, 50, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'MARTES50', 1),
('Postre Gratis', 1, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'POSTRE10', 1),
('Desayuno 15% OFF', 1, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'DESAYUNO15', 1),
('Cena Romántica', 1, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 40 DAY), 'CENA20', 1),
('Promo Vencida', 1, 100, DATE_SUB(CURDATE(), INTERVAL 10 DAY), DATE_SUB(CURDATE(), INTERVAL 1 DAY), 'VENCIDA', 0),
-- Establecimiento 2
('Pan Caliente 20%', 2, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'PAN20', 1),
('Docena a 10', 2, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'DOCENA15', 1),
('Café + Croissant', 2, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'CAFE25', 1),
('Torta Familiar', 2, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'TORTA10', 1),
('Desc. Inactivo', 2, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 180 DAY), 'INACTIVO', 0),
-- Establecimiento 3
('Hora Loca', 3, 30, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'HORA30', 1),
('Cerveza 3x2', 3, 33, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'CERV3X2', 1),
('Cover Gratis M', 3, 100, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 21 DAY), 'COVERM', 1),
('Coctel Bienvenida', 3, 50, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'COCTEL50', 1),
('Viernes Shots (Off)', 3, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 'SHOTS20', 0),
-- Establecimiento 4
('Snacks al 10%', 4, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'SNACK10', 1),
('Gaseosa Familiar', 4, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'GAS15', 1),
('Combo Cine', 4, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'CINE20', 1),
('Desc. Fin de Mes', 4, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 2 DAY), 'FINMES25', 1),
('Lácteos 5% (Off)', 4, 5, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'LACT5', 0),
-- Establecimiento 5
('Carnes 20%', 5, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 7 DAY), 'CARNE20', 1),
('Frutas Temporada', 5, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY), 'FRUTA15', 1),
('Día Mercado', 5, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'MERCADO10', 1),
('Aseo Hogar', 5, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'ASEO25', 1),
('Mascotas (Inactivo)', 5, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 'MASC15', 0),
-- Establecimiento 6
('Vitaminas 20%', 6, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'VITA20', 1),
('Cuidado Piel', 6, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'PIEL15', 1),
('Bebés 10%', 6, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 45 DAY), 'BEBE10', 1),
('Primeros Aux', 6, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 'AUX25', 1),
('Adulto Mayor (Off)', 6, 30, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 365 DAY), 'MAYOR30', 0),
-- Establecimiento 7
('Fin Semana', 7, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 90 DAY), 'FINDE20', 1),
('Noche Bodas', 7, 30, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 180 DAY), 'BODAS30', 1),
('Larga Estadía', 7, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 120 DAY), 'LARGA25', 1),
('Desayuno', 7, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'DESA15', 1),
('Reserva (Inactiva)', 7, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 'ANTI10', 0),
-- Establecimiento 8
('Matrícula Gratis', 8, 100, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'MATRI100', 1),
('Plan Anual 30%', 8, 30, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'ANUAL30', 1),
('Entrenador', 8, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 45 DAY), 'PERSO15', 1),
('Suplementos 10%', 8, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 60 DAY), 'SUPLE10', 1),
('Amigo (Off)', 8, 50, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'AMIGO50', 0),
-- Establecimiento 9
('Degustación', 9, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 30 DAY), 'DEGUS15', 1),
('Vino Casa', 9, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 15 DAY), 'VINO20', 1),
('Ejecutivo', 9, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 90 DAY), 'EJEC10', 1),
('Aniversario', 9, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 180 DAY), 'ANIV25', 1),
('Privados (Off)', 9, 30, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 120 DAY), 'PRIV30', 0),
-- Establecimiento 10
('Café Frío 2x1', 10, 50, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 10 DAY), 'FRIO50', 1),
('Combo Merienda', 10, 15, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 20 DAY), 'MERIENDA15', 1),
('Termo', 10, 10, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 365 DAY), 'TERMO10', 1),
('Pasteles 20%', 10, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 5 DAY), 'PASTEL20', 1),
('Frecuente (Off)', 10, 25, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 90 DAY), 'FREC25', 0);

-- =================================================================
-- 6. RESERVAS (40 en total)
-- 2 por cada Explorador (IDs 24 al 43). Repartidos en Locales/Lugares aprobados.
-- =================================================================
INSERT INTO reservas (codigo_reserva, usuario_id, establecimiento_id, fecha_reserva, estado) VALUES 
('R0000001', 24, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000002', 24, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'PENDIENTE'),
('R0000003', 25, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000004', 25, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'CANCELADA'),
('R0000005', 26, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000006', 26, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'COMPLETADA'),
('R0000007', 27, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000008', 27, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'PENDIENTE'),
('R0000009', 28, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000010', 28, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'CONFIRMADA'),
('R0000011', 29, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), 'COMPLETADA'),
('R0000012', 29, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'CANCELADA'),
('R0000013', 30, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000014', 30, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'PENDIENTE'),
('R0000015', 31, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000016', 31, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'COMPLETADA'),
('R0000017', 32, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000018', 32, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'PENDIENTE'),
('R0000019', 33, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000020', 33, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'CANCELADA'),
('R0000021', 34, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000022', 34, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'COMPLETADA'),
('R0000023', 35, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000024', 35, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'PENDIENTE'),
('R0000025', 36, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000026', 36, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'CONFIRMADA'),
('R0000027', 37, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), 'COMPLETADA'),
('R0000028', 37, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'CANCELADA'),
('R0000029', 38, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000030', 38, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'PENDIENTE'),
('R0000031', 39, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000032', 39, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'COMPLETADA'),
('R0000033', 40, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000034', 40, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'PENDIENTE'),
('R0000035', 41, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000036', 41, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'CANCELADA'),
('R0000037', 42, 1, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000038', 42, 2, DATE_ADD(NOW(), INTERVAL 2 DAY), 'COMPLETADA'),
('R0000039', 43, 3, DATE_ADD(NOW(), INTERVAL 1 DAY), 'CONFIRMADA'),
('R0000040', 43, 4, DATE_ADD(NOW(), INTERVAL 2 DAY), 'PENDIENTE');

-- =================================================================
-- 7. PQRS (60 en total, 3 por cada Explorador)
-- Asignando moderadores a algunos para simular proceso
-- =================================================================
INSERT INTO pqrs (codigo_ticket, usuario_id, tipo, asunto, descripcion, estado, moderador_asignado_id) VALUES 
('TK-00001', 24, 'PETICION', 'Info evento', 'Quiero saber horarios', 'ABIERTO', NULL),
('TK-00002', 24, 'QUEJA', 'Mala atención', 'Local cerrado', 'EN_PROCESO', 2),
('TK-00003', 24, 'SUGERENCIA', 'Filtros', 'Añadir filtros', 'RESPONDIDO', 3),
('TK-00004', 25, 'PETICION', 'Correo', 'Cambiar correo', 'ABIERTO', NULL),
('TK-00005', 25, 'QUEJA', 'Cobro', 'Cobro doble', 'EN_PROCESO', 2),
('TK-00006', 25, 'RECLAMO', 'Promo', 'Promo inválida', 'CERRADO', 3),
('TK-00007', 26, 'PETICION', 'Baja', 'Eliminar cuenta', 'ABIERTO', NULL),
('TK-00008', 26, 'QUEJA', 'Error app', 'Se cierra sola', 'EN_PROCESO', 2),
('TK-00009', 26, 'SUGERENCIA', 'Modo oscuro', 'Añadan modo oscuro', 'RESPONDIDO', 3),
('TK-00010', 27, 'PETICION', 'Puntos', 'Puntos no sumados', 'ABIERTO', NULL),
('TK-00011', 27, 'QUEJA', 'Mapa', 'Mala ubicación', 'EN_PROCESO', 2),
('TK-00012', 27, 'RECLAMO', 'Reserva', 'Me cancelaron', 'CERRADO', 3),
('TK-00013', 28, 'PETICION', 'Código', 'Código no funciona', 'ABIERTO', NULL),
('TK-00014', 28, 'QUEJA', 'Evento', 'Publicidad falsa', 'EN_PROCESO', 2),
('TK-00015', 28, 'SUGERENCIA', 'Arte', 'Más eventos de arte', 'RESPONDIDO', 3),
('TK-00016', 29, 'PETICION', 'Nombre', 'Cambio de nombre', 'ABIERTO', NULL),
('TK-00017', 29, 'QUEJA', 'Demora', 'Demora en respuesta', 'EN_PROCESO', 2),
('TK-00018', 29, 'RECLAMO', 'Sucio', 'Lugar sucio', 'CERRADO', 3),
('TK-00019', 30, 'PETICION', 'Validar', 'No llega correo', 'ABIERTO', NULL),
('TK-00020', 30, 'QUEJA', 'Error', 'Error 500', 'EN_PROCESO', 2),
('TK-00021', 30, 'SUGERENCIA', 'SMS', 'Avisos SMS', 'RESPONDIDO', 3),
('TK-00022', 31, 'PETICION', 'Historial', 'Ver historial', 'ABIERTO', NULL),
('TK-00023', 31, 'QUEJA', 'Guardia', 'Atención grosera', 'EN_PROCESO', 2),
('TK-00024', 31, 'RECLAMO', 'Comida', 'Comida fría', 'CERRADO', 3),
('TK-00025', 32, 'PETICION', 'Local', 'Añadir local', 'ABIERTO', NULL),
('TK-00026', 32, 'QUEJA', 'Lenta', 'App lenta', 'EN_PROCESO', 2),
('TK-00027', 32, 'SUGERENCIA', 'Fotos', 'Reseñas con fotos', 'RESPONDIDO', 3),
('TK-00028', 33, 'PETICION', 'Clave', 'Recuperar clave', 'ABIERTO', NULL),
('TK-00029', 33, 'QUEJA', 'Propina', 'Cobro oculto', 'EN_PROCESO', 2),
('TK-00030', 33, 'RECLAMO', 'Asiento', 'Asiento ocupado', 'CERRADO', 3),
('TK-00031', 34, 'PETICION', 'Fecha', 'Cambiar fecha', 'ABIERTO', NULL),
('TK-00032', 34, 'QUEJA', 'Ruido', 'Ruido excesivo', 'EN_PROCESO', 2),
('TK-00033', 34, 'SUGERENCIA', 'Vegano', 'Opción vegana', 'RESPONDIDO', 3),
('TK-00034', 35, 'PETICION', 'Foto', 'Eliminar foto', 'ABIERTO', NULL),
('TK-00035', 35, 'QUEJA', 'Baños', 'Baños sucios', 'EN_PROCESO', 2),
('TK-00036', 35, 'RECLAMO', 'Bebida', 'Bebida derramada', 'CERRADO', 3),
('TK-00037', 36, 'PETICION', 'Factura', 'Info facturación', 'ABIERTO', NULL),
('TK-00038', 36, 'QUEJA', 'Aforo', 'No respetan aforo', 'EN_PROCESO', 2),
('TK-00039', 36, 'SUGERENCIA', 'Mañana', 'Eventos matutinos', 'RESPONDIDO', 3),
('TK-00040', 37, 'PETICION', 'Parqueo', 'Duda de parqueo', 'ABIERTO', NULL),
('TK-00041', 37, 'QUEJA', 'Fila', 'Fila muy larga', 'EN_PROCESO', 2),
('TK-00042', 37, 'RECLAMO', 'QR', 'Código no lee', 'CERRADO', 3),
('TK-00043', 38, 'PETICION', 'Patroc.', 'Patrocinar evento', 'ABIERTO', NULL),
('TK-00044', 38, 'QUEJA', 'Señal', 'Mala señal', 'EN_PROCESO', 2),
('TK-00045', 38, 'SUGERENCIA', 'Wifi', 'Wifi gratis', 'RESPONDIDO', 3),
('TK-00046', 39, 'PETICION', 'Bug', 'Reportar bug', 'ABIERTO', NULL),
('TK-00047', 39, 'QUEJA', 'Horario', 'Horario mal', 'EN_PROCESO', 2),
('TK-00048', 39, 'RECLAMO', 'Precio', 'Precio distinto', 'CERRADO', 3),
('TK-00049', 40, 'PETICION', 'Cuenta', 'Ayuda con cuenta', 'ABIERTO', NULL),
('TK-00050', 40, 'QUEJA', 'Seguridad', 'Poca seguridad', 'EN_PROCESO', 2),
('TK-00051', 40, 'SUGERENCIA', 'Alertas', 'Avisos seguridad', 'RESPONDIDO', 3),
('TK-00052', 41, 'PETICION', 'Aliado', 'Ser aliado', 'ABIERTO', NULL),
('TK-00053', 41, 'QUEJA', 'Teléfono', 'No contestan', 'EN_PROCESO', 2),
('TK-00054', 41, 'RECLAMO', 'Comida', 'Comida cruda', 'CERRADO', 3),
('TK-00055', 42, 'PETICION', 'Datos', 'Exportar datos', 'ABIERTO', NULL),
('TK-00056', 42, 'QUEJA', 'Spam', 'Notificaciones', 'EN_PROCESO', 2),
('TK-00057', 42, 'SUGERENCIA', 'Personal.', 'Personalizar alert', 'RESPONDIDO', 3),
('TK-00058', 43, 'PETICION', 'Baja', 'Darse de baja', 'ABIERTO', NULL),
('TK-00059', 43, 'QUEJA', 'Promo', 'Promo engañosa', 'EN_PROCESO', 2),
('TK-00060', 43, 'RECLAMO', 'Trato', 'Mal trato', 'CERRADO', 3);

-- =================================================================
-- 8. NOTIFICACIONES (100 en total, 5 por Explorador)
-- =================================================================
INSERT INTO notificaciones (usuario_id, tipo, titulo, mensaje) VALUES 
(24, 'RESERVA_CONFIRMADA', 'Reserva', 'Reserva confirmada.'), (24, 'ALERTA_PROXIMIDAD', 'Cerca', 'Promo a 100m.'), (24, 'PROMOCION_NUEVA', 'Oferta', 'Nueva oferta.'), (24, 'EVENTO_RECORDATORIO', 'No olvides', 'Concierto mañana.'), (24, 'SISTEMA', 'Aviso', 'Actualización.'),
(25, 'RESERVA_CONFIRMADA', 'Reserva', 'Confirmada.'), (25, 'ALERTA_PROXIMIDAD', 'Cerca', 'Bar cerca.'), (25, 'PROMOCION_NUEVA', 'Oferta', '50% en cine.'), (25, 'EVENTO_RECORDATORIO', 'No olvides', 'Maratón.'), (25, 'PQRS_RESPUESTA', 'Soporte', 'Respuesta queja.'),
(26, 'RESERVA_CONFIRMADA', 'Reserva', 'Te esperamos.'), (26, 'RESERVA_CANCELADA', 'Cancelada', 'Local canceló.'), (26, 'PROMOCION_NUEVA', 'Oferta', 'Descuentos.'), (26, 'EVENTO_RECORDATORIO', 'Recuerda', 'Feria fin de semana.'), (26, 'SISTEMA', 'Hola', 'Bienvenido.'),
(27, 'RESERVA_CONFIRMADA', 'Reserva', 'Mesa asegurada.'), (27, 'ALERTA_PROXIMIDAD', 'Cerca', 'Gimnasio.'), (27, 'PROMOCION_NUEVA', 'Oferta', 'Matrícula gratis.'), (27, 'EVENTO_RECORDATORIO', 'Recuerda', 'Taller.'), (27, 'SISTEMA', 'Mantenimiento', 'App off.'),
(28, 'RESERVA_CONFIRMADA', 'Reserva', 'Nos vemos.'), (28, 'ALERTA_PROXIMIDAD', 'Cerca', '10% desc.'), (28, 'PROMOCION_NUEVA', 'Oferta', 'Rebajas.'), (28, 'EVENTO_RECORDATORIO', 'Torneo', 'Empieza breve.'), (28, 'PQRS_RESPUESTA', 'Petición', 'Aprobada.'),
(29, 'RESERVA_CONFIRMADA', 'Reserva', 'Listo.'), (29, 'ALERTA_PROXIMIDAD', 'Cerca', 'Café.'), (29, 'PROMOCION_NUEVA', 'Oferta', '2x1.'), (29, 'EVENTO_RECORDATORIO', 'Ya casi', 'Mañana.'), (29, 'SISTEMA', 'Puntos', 'Ganaste 50pt.'),
(30, 'RESERVA_CONFIRMADA', 'Reserva', 'Confirmado.'), (30, 'RESERVA_CANCELADA', 'Cancelada', 'Error mesa.'), (30, 'PROMOCION_NUEVA', 'Oferta', 'Mitad precio.'), (30, 'EVENTO_RECORDATORIO', 'QR', 'Lleva QR.'), (30, 'SISTEMA', 'Nivel', 'Plata.'),
(31, 'RESERVA_CONFIRMADA', 'Reserva', 'Listo.'), (31, 'ALERTA_PROXIMIDAD', 'Cerca', 'Museo.'), (31, 'PROMOCION_NUEVA', 'Oferta', 'Gratis.'), (31, 'EVENTO_RECORDATORIO', 'Hoy', 'Feria libro.'), (31, 'PQRS_RESPUESTA', 'Sugerencia', 'Implementada.'),
(32, 'RESERVA_CONFIRMADA', 'Reserva', 'Mesa 2.'), (32, 'ALERTA_PROXIMIDAD', 'Cerca', 'Farmacia.'), (32, 'PROMOCION_NUEVA', 'Oferta', 'Vitaminas 20%.'), (32, 'EVENTO_RECORDATORIO', 'Clase', 'Yoga mañana.'), (32, 'SISTEMA', 'App', 'Nueva versión.'),
(33, 'RESERVA_CONFIRMADA', 'Reserva', 'Esperamos.'), (33, 'ALERTA_PROXIMIDAD', 'Cerca', 'Restaurante.'), (33, 'PROMOCION_NUEVA', 'Oferta', 'Menú rebajado.'), (33, 'EVENTO_RECORDATORIO', 'Picnic', 'Domingo.'), (33, 'SISTEMA', 'Año', 'Cumples 1 año.'),
(34, 'RESERVA_CONFIRMADA', 'Reserva', 'Confirmada.'), (34, 'ALERTA_PROXIMIDAD', 'Cerca', 'Promo a 100m.'), (34, 'PROMOCION_NUEVA', 'Oferta', 'Postre Gratis.'), (34, 'EVENTO_RECORDATORIO', 'Concierto', 'Mañana.'), (34, 'SISTEMA', 'T&C', 'Actualizados.'),
(35, 'RESERVA_CONFIRMADA', 'Reserva', 'Lista.'), (35, 'ALERTA_PROXIMIDAD', 'Cerca', 'Nuevo bar.'), (35, 'PROMOCION_NUEVA', 'Oferta', '50% cine.'), (35, 'EVENTO_RECORDATORIO', 'Maratón', 'Pronto.'), (35, 'PQRS_RESPUESTA', 'Queja', 'Respondida.'),
(36, 'RESERVA_CONFIRMADA', 'Reserva', 'Te esperamos.'), (36, 'RESERVA_CANCELADA', 'Cancelada', 'Canceló local.'), (36, 'PROMOCION_NUEVA', 'Oferta', 'Postres.'), (36, 'EVENTO_RECORDATORIO', 'Feria', 'Fin semana.'), (36, 'SISTEMA', 'Hola', 'Bienvenido.'),
(37, 'RESERVA_CONFIRMADA', 'Reserva', 'Asegurada.'), (37, 'ALERTA_PROXIMIDAD', 'Cerca', 'Gimnasio.'), (37, 'PROMOCION_NUEVA', 'Oferta', 'Matrícula.'), (37, 'EVENTO_RECORDATORIO', 'Taller', 'Falta 1h.'), (37, 'SISTEMA', 'Mantenimiento', 'Medianoche.'),
(38, 'RESERVA_CONFIRMADA', 'Reserva', 'Mañana.'), (38, 'ALERTA_PROXIMIDAD', 'Cerca', '10% desc.'), (38, 'PROMOCION_NUEVA', 'Oferta', 'Mercado.'), (38, 'EVENTO_RECORDATORIO', 'Torneo', 'Breve.'), (38, 'PQRS_RESPUESTA', 'Actualización', 'Aprobada.'),
(39, 'RESERVA_CONFIRMADA', 'Reserva', 'Todo listo.'), (39, 'ALERTA_PROXIMIDAD', 'Cerca', 'Café caliente.'), (39, 'PROMOCION_NUEVA', 'Oferta', '2x1 bebidas.'), (39, 'EVENTO_RECORDATORIO', 'Mañana', 'Evento.'), (39, 'SISTEMA', 'Puntos', '50 puntos.'),
(40, 'RESERVA_CONFIRMADA', 'Reserva', 'Confirmado.'), (40, 'RESERVA_CANCELADA', 'Cancelada', 'Error mesa.'), (40, 'PROMOCION_NUEVA', 'Oferta', 'Mitad precio.'), (40, 'EVENTO_RECORDATORIO', 'Atención', 'Lleva QR.'), (40, 'SISTEMA', 'Nivel', 'Plata.'),
(41, 'RESERVA_CONFIRMADA', 'Reserva', 'Listo.'), (41, 'ALERTA_PROXIMIDAD', 'Cerca', 'Museo.'), (41, 'PROMOCION_NUEVA', 'Oferta', 'Entrada gratis.'), (41, 'EVENTO_RECORDATORIO', 'Hoy', 'Feria libro.'), (41, 'PQRS_RESPUESTA', 'Soporte', 'Implementada.'),
(42, 'RESERVA_CONFIRMADA', 'Reserva', 'Mesa 2.'), (42, 'ALERTA_PROXIMIDAD', 'Cerca', 'Farmacia.'), (42, 'PROMOCION_NUEVA', 'Oferta', 'Vitaminas.'), (42, 'EVENTO_RECORDATORIO', 'Yoga', 'Temprano.'), (42, 'SISTEMA', 'App', 'Nueva versión.'),
(43, 'RESERVA_CONFIRMADA', 'Reserva', 'Esperamos.'), (43, 'ALERTA_PROXIMIDAD', 'Cerca', 'Almuerzo.'), (43, 'PROMOCION_NUEVA', 'Oferta', 'Menú.'), (43, 'EVENTO_RECORDATORIO', 'Picnic', 'Domingo.'), (43, 'SISTEMA', 'Aniversario', '1 año.');