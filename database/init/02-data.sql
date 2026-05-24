-- ======================================================
-- SCRIPT DE INSERCIÓN MASIVA: BUDGETMAP (50 REGISTROS POR TABLA)
-- Contraseña universal: BudgetMap2026
-- ======================================================

USE budgetmap;

SET @pwd = '$2a$12$aMFbZoljD1yS/5rKlTSTyeavdIQcVF4j79Dh7m5zHmA1yoshNAefK';

-- -----------------------------------------------------
-- 1. planes_suscripcion (50 registros)
-- -----------------------------------------------------
INSERT INTO planes_suscripcion (nombre, tipo_publico, precio_mensual, permite_promos_ilimitadas, permite_estadisticas_avanzadas, acceso_anticipado_ofertas, sin_anuncios, activo) VALUES
('Básico E1', 'EXPLORADOR', 0.00, 0, 0, 0, 0, 1), ('Pro E1', 'EXPLORADOR', 4900.00, 0, 0, 1, 1, 1), ('Básico A1', 'ALIADO', 0.00, 0, 0, 0, 0, 1), ('Pro A1', 'ALIADO', 29900.00, 1, 1, 0, 0, 1), ('VIP E1', 'EXPLORADOR', 9900.00, 0, 0, 1, 1, 1),
('Básico E2', 'EXPLORADOR', 0.00, 0, 0, 0, 0, 1), ('Pro E2', 'EXPLORADOR', 4900.00, 0, 0, 1, 1, 1), ('Básico A2', 'ALIADO', 0.00, 0, 0, 0, 0, 1), ('Pro A2', 'ALIADO', 29900.00, 1, 1, 0, 0, 1), ('VIP E2', 'EXPLORADOR', 9900.00, 0, 0, 1, 1, 1),
('Básico E3', 'EXPLORADOR', 0.00, 0, 0, 0, 0, 1), ('Pro E3', 'EXPLORADOR', 4900.00, 0, 0, 1, 1, 1), ('Básico A3', 'ALIADO', 0.00, 0, 0, 0, 0, 1), ('Pro A3', 'ALIADO', 29900.00, 1, 1, 0, 0, 1), ('VIP E3', 'EXPLORADOR', 9900.00, 0, 0, 1, 1, 1),
('Básico E4', 'EXPLORADOR', 0.00, 0, 0, 0, 0, 1), ('Pro E4', 'EXPLORADOR', 4900.00, 0, 0, 1, 1, 1), ('Básico A4', 'ALIADO', 0.00, 0, 0, 0, 0, 1), ('Pro A4', 'ALIADO', 29900.00, 1, 1, 0, 0, 1), ('VIP E4', 'EXPLORADOR', 9900.00, 0, 0, 1, 1, 1),
('Básico E5', 'EXPLORADOR', 0.00, 0, 0, 0, 0, 1), ('Pro E5', 'EXPLORADOR', 4900.00, 0, 0, 1, 1, 1), ('Básico A5', 'ALIADO', 0.00, 0, 0, 0, 0, 1), ('Pro A5', 'ALIADO', 29900.00, 1, 1, 0, 0, 1), ('VIP E5', 'EXPLORADOR', 9900.00, 0, 0, 1, 1, 1),
('Básico E6', 'EXPLORADOR', 0.00, 0, 0, 0, 0, 1), ('Pro E6', 'EXPLORADOR', 4900.00, 0, 0, 1, 1, 1), ('Básico A6', 'ALIADO', 0.00, 0, 0, 0, 0, 1), ('Pro A6', 'ALIADO', 29900.00, 1, 1, 0, 0, 1), ('VIP E6', 'EXPLORADOR', 9900.00, 0, 0, 1, 1, 1),
('Básico E7', 'EXPLORADOR', 0.00, 0, 0, 0, 0, 1), ('Pro E7', 'EXPLORADOR', 4900.00, 0, 0, 1, 1, 1), ('Básico A7', 'ALIADO', 0.00, 0, 0, 0, 0, 1), ('Pro A7', 'ALIADO', 29900.00, 1, 1, 0, 0, 1), ('VIP E7', 'EXPLORADOR', 9900.00, 0, 0, 1, 1, 1),
('Básico E8', 'EXPLORADOR', 0.00, 0, 0, 0, 0, 1), ('Pro E8', 'EXPLORADOR', 4900.00, 0, 0, 1, 1, 1), ('Básico A8', 'ALIADO', 0.00, 0, 0, 0, 0, 1), ('Pro A8', 'ALIADO', 29900.00, 1, 1, 0, 0, 1), ('VIP E8', 'EXPLORADOR', 9900.00, 0, 0, 1, 1, 1),
('Básico E9', 'EXPLORADOR', 0.00, 0, 0, 0, 0, 1), ('Pro E9', 'EXPLORADOR', 4900.00, 0, 0, 1, 1, 1), ('Básico A9', 'ALIADO', 0.00, 0, 0, 0, 0, 1), ('Pro A9', 'ALIADO', 29900.00, 1, 1, 0, 0, 1), ('VIP E9', 'EXPLORADOR', 9900.00, 0, 0, 1, 1, 1),
('Básico E10', 'EXPLORADOR', 0.00, 0, 0, 0, 0, 1), ('Pro E10', 'EXPLORADOR', 4900.00, 0, 0, 1, 1, 1), ('Básico A10', 'ALIADO', 0.00, 0, 0, 0, 0, 1), ('Pro A10', 'ALIADO', 29900.00, 1, 1, 0, 0, 1), ('VIP E10', 'EXPLORADOR', 9900.00, 0, 0, 1, 1, 1);

-- -----------------------------------------------------
-- 2. usuarios (50 registros: 10 Admins/Mods, 20 Aliados/Anfitriones, 20 Exploradores)
-- -----------------------------------------------------
INSERT INTO usuarios (email, password, nombre, apellido, telefono, rol, plan_id, puntos_acumulados, activo, email_verificado) VALUES
('admin1@bd.com', @pwd, 'Admin1', 'Super', '3000000001', 'ADMINISTRADOR', 1, 0, 1, 1), ('admin2@bd.com', @pwd, 'Admin2', 'Super', '3000000002', 'ADMINISTRADOR', 1, 0, 1, 1), ('mod1@bd.com', @pwd, 'Mod1', 'Control', '3000000003', 'MODERADOR', 1, 0, 1, 1), ('mod2@bd.com', @pwd, 'Mod2', 'Control', '3000000004', 'MODERADOR', 1, 0, 1, 1), ('mod3@bd.com', @pwd, 'Mod3', 'Control', '3000000005', 'MODERADOR', 1, 0, 1, 1),
('mod4@bd.com', @pwd, 'Mod4', 'Control', '3000000006', 'MODERADOR', 1, 0, 1, 1), ('mod5@bd.com', @pwd, 'Mod5', 'Control', '3000000007', 'MODERADOR', 1, 0, 1, 1), ('mod6@bd.com', @pwd, 'Mod6', 'Control', '3000000008', 'MODERADOR', 1, 0, 1, 1), ('mod7@bd.com', @pwd, 'Mod7', 'Control', '3000000009', 'MODERADOR', 1, 0, 1, 1), ('mod8@bd.com', @pwd, 'Mod8', 'Control', '3000000010', 'MODERADOR', 1, 0, 1, 1),
('aliado1@bd.com', @pwd, 'Aliado1', 'Local', '3000000011', 'LOCAL_ALIADO', 3, 0, 1, 1), ('aliado2@bd.com', @pwd, 'Aliado2', 'Local', '3000000012', 'LOCAL_ALIADO', 4, 0, 1, 1), ('aliado3@bd.com', @pwd, 'Aliado3', 'Local', '3000000013', 'LOCAL_ALIADO', 3, 0, 1, 1), ('aliado4@bd.com', @pwd, 'Aliado4', 'Local', '3000000014', 'LOCAL_ALIADO', 4, 0, 1, 1), ('aliado5@bd.com', @pwd, 'Aliado5', 'Local', '3000000015', 'LOCAL_ALIADO', 3, 0, 1, 1),
('aliado6@bd.com', @pwd, 'Aliado6', 'Local', '3000000016', 'LOCAL_ALIADO', 4, 0, 1, 1), ('aliado7@bd.com', @pwd, 'Aliado7', 'Local', '3000000017', 'LOCAL_ALIADO', 3, 0, 1, 1), ('aliado8@bd.com', @pwd, 'Aliado8', 'Local', '3000000018', 'LOCAL_ALIADO', 4, 0, 1, 1), ('aliado9@bd.com', @pwd, 'Aliado9', 'Local', '3000000019', 'LOCAL_ALIADO', 3, 0, 1, 1), ('aliado10@bd.com', @pwd, 'Aliado10', 'Local', '3000000020', 'LOCAL_ALIADO', 4, 0, 1, 1),
('anfitrion1@bd.com', @pwd, 'Anfitrion1', 'Evt', '3000000021', 'ANFITRION', 3, 0, 1, 1), ('anfitrion2@bd.com', @pwd, 'Anfitrion2', 'Evt', '3000000022', 'ANFITRION', 4, 0, 1, 1), ('anfitrion3@bd.com', @pwd, 'Anfitrion3', 'Evt', '3000000023', 'ANFITRION', 3, 0, 1, 1), ('anfitrion4@bd.com', @pwd, 'Anfitrion4', 'Evt', '3000000024', 'ANFITRION', 4, 0, 1, 1), ('anfitrion5@bd.com', @pwd, 'Anfitrion5', 'Evt', '3000000025', 'ANFITRION', 3, 0, 1, 1),
('anfitrion6@bd.com', @pwd, 'Anfitrion6', 'Evt', '3000000026', 'ANFITRION', 4, 0, 1, 1), ('anfitrion7@bd.com', @pwd, 'Anfitrion7', 'Evt', '3000000027', 'ANFITRION', 3, 0, 1, 1), ('anfitrion8@bd.com', @pwd, 'Anfitrion8', 'Evt', '3000000028', 'ANFITRION', 4, 0, 1, 1), ('anfitrion9@bd.com', @pwd, 'Anfitrion9', 'Evt', '3000000029', 'ANFITRION', 3, 0, 1, 1), ('anfitrion10@bd.com', @pwd, 'Anfitrion10', 'Evt', '3000000030', 'ANFITRION', 4, 0, 1, 1),
('exp1@bd.com', @pwd, 'Exp1', 'User', '3000000031', 'EXPLORADOR', 1, 50, 1, 1), ('exp2@bd.com', @pwd, 'Exp2', 'User', '3000000032', 'EXPLORADOR', 2, 100, 1, 1), ('exp3@bd.com', @pwd, 'Exp3', 'User', '3000000033', 'EXPLORADOR', 1, 20, 1, 1), ('exp4@bd.com', @pwd, 'Exp4', 'User', '3000000034', 'EXPLORADOR', 2, 30, 1, 1), ('exp5@bd.com', @pwd, 'Exp5', 'User', '3000000035', 'EXPLORADOR', 1, 40, 1, 1),
('exp6@bd.com', @pwd, 'Exp6', 'User', '3000000036', 'EXPLORADOR', 2, 10, 1, 1), ('exp7@bd.com', @pwd, 'Exp7', 'User', '3000000037', 'EXPLORADOR', 1, 80, 1, 1), ('exp8@bd.com', @pwd, 'Exp8', 'User', '3000000038', 'EXPLORADOR', 2, 90, 1, 1), ('exp9@bd.com', @pwd, 'Exp9', 'User', '3000000039', 'EXPLORADOR', 1, 15, 1, 1), ('exp10@bd.com', @pwd, 'Exp10', 'User', '3000000040', 'EXPLORADOR', 2, 25, 1, 1),
('exp11@bd.com', @pwd, 'Exp11', 'User', '3000000041', 'EXPLORADOR', 1, 5, 1, 1), ('exp12@bd.com', @pwd, 'Exp12', 'User', '3000000042', 'EXPLORADOR', 2, 500, 1, 1), ('exp13@bd.com', @pwd, 'Exp13', 'User', '3000000043', 'EXPLORADOR', 1, 300, 1, 1), ('exp14@bd.com', @pwd, 'Exp14', 'User', '3000000044', 'EXPLORADOR', 2, 200, 1, 1), ('exp15@bd.com', @pwd, 'Exp15', 'User', '3000000045', 'EXPLORADOR', 1, 100, 1, 1),
('exp16@bd.com', @pwd, 'Exp16', 'User', '3000000046', 'EXPLORADOR', 2, 0, 1, 1), ('exp17@bd.com', @pwd, 'Exp17', 'User', '3000000047', 'EXPLORADOR', 1, 0, 1, 1), ('exp18@bd.com', @pwd, 'Exp18', 'User', '3000000048', 'EXPLORADOR', 2, 10, 1, 1), ('exp19@bd.com', @pwd, 'Exp19', 'User', '3000000049', 'EXPLORADOR', 1, 0, 1, 1), ('exp20@bd.com', @pwd, 'Exp20', 'User', '3000000050', 'EXPLORADOR', 2, 50, 1, 1);

-- -----------------------------------------------------
-- 3. transacciones (50 registros)
-- -----------------------------------------------------
INSERT INTO transacciones (usuario_id, tipo, monto, metodo_pago, referencia_pago, estado) VALUES
(11, 'COMPRA_PLAN', 29900.00, 'PSE', 'REF-001', 'EXITOSO'), (12, 'COMPRA_PLAN', 29900.00, 'TDC', 'REF-002', 'EXITOSO'), (13, 'COMPRA_PUNTOS', 10000.00, 'NEQUI', 'REF-003', 'EXITOSO'), (14, 'PAGO_ADS', 15000.00, 'PSE', 'REF-004', 'PENDIENTE'), (15, 'COMISION_RESERVA', 500.00, 'INTERNO', 'REF-005', 'EXITOSO'),
(16, 'COMPRA_PLAN', 29900.00, 'PSE', 'REF-006', 'EXITOSO'), (17, 'COMPRA_PLAN', 29900.00, 'TDC', 'REF-007', 'EXITOSO'), (18, 'COMPRA_PUNTOS', 10000.00, 'NEQUI', 'REF-008', 'EXITOSO'), (19, 'PAGO_ADS', 15000.00, 'PSE', 'REF-009', 'PENDIENTE'), (20, 'COMISION_RESERVA', 500.00, 'INTERNO', 'REF-010', 'EXITOSO'),
(21, 'COMPRA_PLAN', 29900.00, 'PSE', 'REF-011', 'EXITOSO'), (22, 'COMPRA_PLAN', 29900.00, 'TDC', 'REF-012', 'EXITOSO'), (23, 'COMPRA_PUNTOS', 10000.00, 'NEQUI', 'REF-013', 'EXITOSO'), (24, 'PAGO_ADS', 15000.00, 'PSE', 'REF-014', 'PENDIENTE'), (25, 'COMISION_RESERVA', 500.00, 'INTERNO', 'REF-015', 'EXITOSO'),
(26, 'COMPRA_PLAN', 29900.00, 'PSE', 'REF-016', 'EXITOSO'), (27, 'COMPRA_PLAN', 29900.00, 'TDC', 'REF-017', 'EXITOSO'), (28, 'COMPRA_PUNTOS', 10000.00, 'NEQUI', 'REF-018', 'EXITOSO'), (29, 'PAGO_ADS', 15000.00, 'PSE', 'REF-019', 'PENDIENTE'), (30, 'COMISION_RESERVA', 500.00, 'INTERNO', 'REF-020', 'EXITOSO'),
(31, 'COMPRA_PLAN', 4900.00, 'PSE', 'REF-021', 'EXITOSO'), (32, 'COMPRA_PLAN', 4900.00, 'TDC', 'REF-022', 'EXITOSO'), (33, 'COMPRA_PUNTOS', 5000.00, 'NEQUI', 'REF-023', 'EXITOSO'), (34, 'COMPRA_PLAN', 4900.00, 'PSE', 'REF-024', 'PENDIENTE'), (35, 'COMPRA_PLAN', 4900.00, 'INTERNO', 'REF-025', 'EXITOSO'),
(36, 'COMPRA_PLAN', 4900.00, 'PSE', 'REF-026', 'EXITOSO'), (37, 'COMPRA_PLAN', 4900.00, 'TDC', 'REF-027', 'EXITOSO'), (38, 'COMPRA_PUNTOS', 5000.00, 'NEQUI', 'REF-028', 'EXITOSO'), (39, 'COMPRA_PLAN', 4900.00, 'PSE', 'REF-029', 'PENDIENTE'), (40, 'COMPRA_PLAN', 4900.00, 'INTERNO', 'REF-030', 'EXITOSO'),
(41, 'COMPRA_PLAN', 4900.00, 'PSE', 'REF-031', 'EXITOSO'), (42, 'COMPRA_PLAN', 4900.00, 'TDC', 'REF-032', 'EXITOSO'), (43, 'COMPRA_PUNTOS', 5000.00, 'NEQUI', 'REF-033', 'EXITOSO'), (44, 'COMPRA_PLAN', 4900.00, 'PSE', 'REF-034', 'PENDIENTE'), (45, 'COMPRA_PLAN', 4900.00, 'INTERNO', 'REF-035', 'EXITOSO'),
(46, 'COMPRA_PLAN', 4900.00, 'PSE', 'REF-036', 'EXITOSO'), (47, 'COMPRA_PLAN', 4900.00, 'TDC', 'REF-037', 'EXITOSO'), (48, 'COMPRA_PUNTOS', 5000.00, 'NEQUI', 'REF-038', 'EXITOSO'), (49, 'COMPRA_PLAN', 4900.00, 'PSE', 'REF-039', 'PENDIENTE'), (50, 'COMPRA_PLAN', 4900.00, 'INTERNO', 'REF-040', 'EXITOSO'),
(11, 'COMISION_RESERVA', 1000.00, 'INTERNO', 'REF-041', 'EXITOSO'), (12, 'COMISION_RESERVA', 1000.00, 'INTERNO', 'REF-042', 'EXITOSO'), (13, 'COMISION_RESERVA', 1000.00, 'INTERNO', 'REF-043', 'EXITOSO'), (14, 'COMISION_RESERVA', 1000.00, 'INTERNO', 'REF-044', 'EXITOSO'), (15, 'COMISION_RESERVA', 1000.00, 'INTERNO', 'REF-045', 'EXITOSO'),
(16, 'COMISION_RESERVA', 1000.00, 'INTERNO', 'REF-046', 'EXITOSO'), (17, 'COMISION_RESERVA', 1000.00, 'INTERNO', 'REF-047', 'EXITOSO'), (18, 'COMISION_RESERVA', 1000.00, 'INTERNO', 'REF-048', 'EXITOSO'), (19, 'COMISION_RESERVA', 1000.00, 'INTERNO', 'REF-049', 'EXITOSO'), (20, 'COMISION_RESERVA', 1000.00, 'INTERNO', 'REF-050', 'EXITOSO');

-- -----------------------------------------------------
-- 4. lugares (50 registros: Parques, Museos, etc.)
-- -----------------------------------------------------
INSERT INTO lugares (nombre, descripcion, categoria, direccion, latitud, longitud, ubicacion, imagen_url, aforo_maximo, estado, moderador_id, destacado, activo) VALUES
('Parque Central 1', 'Hermoso parque', 'PARQUE', 'Cra 1 # 1-1', 4.6097, -74.0817, ST_GeomFromText('POINT(-74.0817 4.6097)', 4326), 'https://picsum.photos/seed/lug1/800/600', 500, 'APROBADO', 3, 1, 1),
('Museo Nacional 2', 'Historia local', 'MUSEO', 'Cra 2 # 2-2', 4.6197, -74.0717, ST_GeomFromText('POINT(-74.0717 4.6197)', 4326), 'https://picsum.photos/seed/lug2/800/600', 200, 'APROBADO', 3, 1, 1),
('Sitio Turístico 3', 'Vista hermosa', 'SITIO_TURISTICO', 'Cra 3 # 3-3', 4.6297, -74.0617, ST_GeomFromText('POINT(-74.0617 4.6297)', 4326), 'https://picsum.photos/seed/lug3/800/600', 300, 'APROBADO', 3, 0, 1),
('Biblioteca Pública 4', 'Lectura libre', 'BIBLIOTECA', 'Cra 4 # 4-4', 4.6397, -74.0517, ST_GeomFromText('POINT(-74.0517 4.6397)', 4326), 'https://picsum.photos/seed/lug4/800/600', 100, 'APROBADO', 3, 0, 1),
('Plaza Mayor 5', 'Punto de encuentro', 'OTRO', 'Cra 5 # 5-5', 4.6497, -74.0417, ST_GeomFromText('POINT(-74.0417 4.6497)', 4326), 'https://picsum.photos/seed/lug5/800/600', 1000, 'PENDIENTE', NULL, 0, 1),
('Parque Central 6', 'Hermoso parque', 'PARQUE', 'Cra 6 # 6-6', 4.6597, -74.0317, ST_GeomFromText('POINT(-74.0317 4.6597)', 4326), 'https://picsum.photos/seed/lug6/800/600', 500, 'APROBADO', 3, 1, 1),
('Museo Nacional 7', 'Historia local', 'MUSEO', 'Cra 7 # 7-7', 4.6697, -74.0217, ST_GeomFromText('POINT(-74.0217 4.6697)', 4326), 'https://picsum.photos/seed/lug7/800/600', 200, 'APROBADO', 3, 0, 1),
('Sitio Turístico 8', 'Vista hermosa', 'SITIO_TURISTICO', 'Cra 8 # 8-8', 4.6797, -74.0117, ST_GeomFromText('POINT(-74.0117 4.6797)', 4326), 'https://picsum.photos/seed/lug8/800/600', 300, 'APROBADO', 3, 0, 1),
('Biblioteca Pública 9', 'Lectura libre', 'BIBLIOTECA', 'Cra 9 # 9-9', 4.6897, -74.0017, ST_GeomFromText('POINT(-74.0017 4.6897)', 4326), 'https://picsum.photos/seed/lug9/800/600', 100, 'APROBADO', 3, 0, 1),
('Plaza Mayor 10', 'Punto de encuentro', 'OTRO', 'Cra 10 # 10-10', 4.6997, -73.9917, ST_GeomFromText('POINT(-73.9917 4.6997)', 4326), 'https://picsum.photos/seed/lug10/800/600', 1000, 'PENDIENTE', NULL, 0, 1),
('Parque Central 11', 'Hermoso parque', 'PARQUE', 'Cra 11 # 11-11', 4.6097, -74.0817, ST_GeomFromText('POINT(-74.0817 4.6097)', 4326), 'https://picsum.photos/seed/lug11/800/600', 500, 'APROBADO', 3, 1, 1),
('Museo Nacional 12', 'Historia local', 'MUSEO', 'Cra 12 # 12-12', 4.6197, -74.0717, ST_GeomFromText('POINT(-74.0717 4.6197)', 4326), 'https://picsum.photos/seed/lug12/800/600', 200, 'APROBADO', 3, 1, 1),
('Sitio Turístico 13', 'Vista hermosa', 'SITIO_TURISTICO', 'Cra 13 # 13-13', 4.6297, -74.0617, ST_GeomFromText('POINT(-74.0617 4.6297)', 4326), 'https://picsum.photos/seed/lug13/800/600', 300, 'APROBADO', 3, 0, 1),
('Biblioteca Pública 14', 'Lectura libre', 'BIBLIOTECA', 'Cra 14 # 14-14', 4.6397, -74.0517, ST_GeomFromText('POINT(-74.0517 4.6397)', 4326), 'https://picsum.photos/seed/lug14/800/600', 100, 'APROBADO', 3, 0, 1),
('Plaza Mayor 15', 'Punto de encuentro', 'OTRO', 'Cra 15 # 15-15', 4.6497, -74.0417, ST_GeomFromText('POINT(-74.0417 4.6497)', 4326), 'https://picsum.photos/seed/lug15/800/600', 1000, 'PENDIENTE', NULL, 0, 1),
('Parque Central 16', 'Hermoso parque', 'PARQUE', 'Cra 16 # 16-16', 4.6597, -74.0317, ST_GeomFromText('POINT(-74.0317 4.6597)', 4326), 'https://picsum.photos/seed/lug16/800/600', 500, 'APROBADO', 3, 1, 1),
('Museo Nacional 17', 'Historia local', 'MUSEO', 'Cra 17 # 17-17', 4.6697, -74.0217, ST_GeomFromText('POINT(-74.0217 4.6697)', 4326), 'https://picsum.photos/seed/lug17/800/600', 200, 'APROBADO', 3, 0, 1),
('Sitio Turístico 18', 'Vista hermosa', 'SITIO_TURISTICO', 'Cra 18 # 18-18', 4.6797, -74.0117, ST_GeomFromText('POINT(-74.0117 4.6797)', 4326), 'https://picsum.photos/seed/lug18/800/600', 300, 'APROBADO', 3, 0, 1),
('Biblioteca Pública 19', 'Lectura libre', 'BIBLIOTECA', 'Cra 19 # 19-19', 4.6897, -74.0017, ST_GeomFromText('POINT(-74.0017 4.6897)', 4326), 'https://picsum.photos/seed/lug19/800/600', 100, 'APROBADO', 3, 0, 1),
('Plaza Mayor 20', 'Punto de encuentro', 'OTRO', 'Cra 20 # 20-20', 4.6997, -73.9917, ST_GeomFromText('POINT(-73.9917 4.6997)', 4326), 'https://picsum.photos/seed/lug20/800/600', 1000, 'PENDIENTE', NULL, 0, 1),
('Parque Central 21', 'Hermoso parque', 'PARQUE', 'Cra 21 # 21-21', 4.6097, -74.0817, ST_GeomFromText('POINT(-74.0817 4.6097)', 4326), 'https://picsum.photos/seed/lug21/800/600', 500, 'APROBADO', 3, 1, 1),
('Museo Nacional 22', 'Historia local', 'MUSEO', 'Cra 22 # 22-22', 4.6197, -74.0717, ST_GeomFromText('POINT(-74.0717 4.6197)', 4326), 'https://picsum.photos/seed/lug22/800/600', 200, 'APROBADO', 3, 1, 1),
('Sitio Turístico 23', 'Vista hermosa', 'SITIO_TURISTICO', 'Cra 23 # 23-23', 4.6297, -74.0617, ST_GeomFromText('POINT(-74.0617 4.6297)', 4326), 'https://picsum.photos/seed/lug23/800/600', 300, 'APROBADO', 3, 0, 1),
('Biblioteca Pública 24', 'Lectura libre', 'BIBLIOTECA', 'Cra 24 # 24-24', 4.6397, -74.0517, ST_GeomFromText('POINT(-74.0517 4.6397)', 4326), 'https://picsum.photos/seed/lug24/800/600', 100, 'APROBADO', 3, 0, 1),
('Plaza Mayor 25', 'Punto de encuentro', 'OTRO', 'Cra 25 # 25-25', 4.6497, -74.0417, ST_GeomFromText('POINT(-74.0417 4.6497)', 4326), 'https://picsum.photos/seed/lug25/800/600', 1000, 'PENDIENTE', NULL, 0, 1),
('Parque Central 26', 'Hermoso parque', 'PARQUE', 'Cra 26 # 26-26', 4.6597, -74.0317, ST_GeomFromText('POINT(-74.0317 4.6597)', 4326), 'https://picsum.photos/seed/lug26/800/600', 500, 'APROBADO', 3, 1, 1),
('Museo Nacional 27', 'Historia local', 'MUSEO', 'Cra 27 # 27-27', 4.6697, -74.0217, ST_GeomFromText('POINT(-74.0217 4.6697)', 4326), 'https://picsum.photos/seed/lug27/800/600', 200, 'APROBADO', 3, 0, 1),
('Sitio Turístico 28', 'Vista hermosa', 'SITIO_TURISTICO', 'Cra 28 # 28-28', 4.6797, -74.0117, ST_GeomFromText('POINT(-74.0117 4.6797)', 4326), 'https://picsum.photos/seed/lug28/800/600', 300, 'APROBADO', 3, 0, 1),
('Biblioteca Pública 29', 'Lectura libre', 'BIBLIOTECA', 'Cra 29 # 29-29', 4.6897, -74.0017, ST_GeomFromText('POINT(-74.0017 4.6897)', 4326), 'https://picsum.photos/seed/lug29/800/600', 100, 'APROBADO', 3, 0, 1),
('Plaza Mayor 30', 'Punto de encuentro', 'OTRO', 'Cra 30 # 30-30', 4.6997, -73.9917, ST_GeomFromText('POINT(-73.9917 4.6997)', 4326), 'https://picsum.photos/seed/lug30/800/600', 1000, 'PENDIENTE', NULL, 0, 1),
('Parque Central 31', 'Hermoso parque', 'PARQUE', 'Cra 31 # 31-31', 4.6097, -74.0817, ST_GeomFromText('POINT(-74.0817 4.6097)', 4326), 'https://picsum.photos/seed/lug31/800/600', 500, 'APROBADO', 3, 1, 1),
('Museo Nacional 32', 'Historia local', 'MUSEO', 'Cra 32 # 32-32', 4.6197, -74.0717, ST_GeomFromText('POINT(-74.0717 4.6197)', 4326), 'https://picsum.photos/seed/lug32/800/600', 200, 'APROBADO', 3, 1, 1),
('Sitio Turístico 33', 'Vista hermosa', 'SITIO_TURISTICO', 'Cra 33 # 33-33', 4.6297, -74.0617, ST_GeomFromText('POINT(-74.0617 4.6297)', 4326), 'https://picsum.photos/seed/lug33/800/600', 300, 'APROBADO', 3, 0, 1),
('Biblioteca Pública 34', 'Lectura libre', 'BIBLIOTECA', 'Cra 34 # 34-34', 4.6397, -74.0517, ST_GeomFromText('POINT(-74.0517 4.6397)', 4326), 'https://picsum.photos/seed/lug34/800/600', 100, 'APROBADO', 3, 0, 1),
('Plaza Mayor 35', 'Punto de encuentro', 'OTRO', 'Cra 35 # 35-35', 4.6497, -74.0417, ST_GeomFromText('POINT(-74.0417 4.6497)', 4326), 'https://picsum.photos/seed/lug35/800/600', 1000, 'PENDIENTE', NULL, 0, 1),
('Parque Central 36', 'Hermoso parque', 'PARQUE', 'Cra 36 # 36-36', 4.6597, -74.0317, ST_GeomFromText('POINT(-74.0317 4.6597)', 4326), 'https://picsum.photos/seed/lug36/800/600', 500, 'APROBADO', 3, 1, 1),
('Museo Nacional 37', 'Historia local', 'MUSEO', 'Cra 37 # 37-37', 4.6697, -74.0217, ST_GeomFromText('POINT(-74.0217 4.6697)', 4326), 'https://picsum.photos/seed/lug37/800/600', 200, 'APROBADO', 3, 0, 1),
('Sitio Turístico 38', 'Vista hermosa', 'SITIO_TURISTICO', 'Cra 38 # 38-38', 4.6797, -74.0117, ST_GeomFromText('POINT(-74.0117 4.6797)', 4326), 'https://picsum.photos/seed/lug38/800/600', 300, 'APROBADO', 3, 0, 1),
('Biblioteca Pública 39', 'Lectura libre', 'BIBLIOTECA', 'Cra 39 # 39-39', 4.6897, -74.0017, ST_GeomFromText('POINT(-74.0017 4.6897)', 4326), 'https://picsum.photos/seed/lug39/800/600', 100, 'APROBADO', 3, 0, 1),
('Plaza Mayor 40', 'Punto de encuentro', 'OTRO', 'Cra 40 # 40-40', 4.6997, -73.9917, ST_GeomFromText('POINT(-73.9917 4.6997)', 4326), 'https://picsum.photos/seed/lug40/800/600', 1000, 'PENDIENTE', NULL, 0, 1),
('Parque Central 41', 'Hermoso parque', 'PARQUE', 'Cra 41 # 41-41', 4.6097, -74.0817, ST_GeomFromText('POINT(-74.0817 4.6097)', 4326), 'https://picsum.photos/seed/lug41/800/600', 500, 'APROBADO', 3, 1, 1),
('Museo Nacional 42', 'Historia local', 'MUSEO', 'Cra 42 # 42-42', 4.6197, -74.0717, ST_GeomFromText('POINT(-74.0717 4.6197)', 4326), 'https://picsum.photos/seed/lug42/800/600', 200, 'APROBADO', 3, 1, 1),
('Sitio Turístico 43', 'Vista hermosa', 'SITIO_TURISTICO', 'Cra 43 # 43-43', 4.6297, -74.0617, ST_GeomFromText('POINT(-74.0617 4.6297)', 4326), 'https://picsum.photos/seed/lug43/800/600', 300, 'APROBADO', 3, 0, 1),
('Biblioteca Pública 44', 'Lectura libre', 'BIBLIOTECA', 'Cra 44 # 44-44', 4.6397, -74.0517, ST_GeomFromText('POINT(-74.0517 4.6397)', 4326), 'https://picsum.photos/seed/lug44/800/600', 100, 'APROBADO', 3, 0, 1),
('Plaza Mayor 45', 'Punto de encuentro', 'OTRO', 'Cra 45 # 45-45', 4.6497, -74.0417, ST_GeomFromText('POINT(-74.0417 4.6497)', 4326), 'https://picsum.photos/seed/lug45/800/600', 1000, 'PENDIENTE', NULL, 0, 1),
('Parque Central 46', 'Hermoso parque', 'PARQUE', 'Cra 46 # 46-46', 4.6597, -74.0317, ST_GeomFromText('POINT(-74.0317 4.6597)', 4326), 'https://picsum.photos/seed/lug46/800/600', 500, 'APROBADO', 3, 1, 1),
('Museo Nacional 47', 'Historia local', 'MUSEO', 'Cra 47 # 47-47', 4.6697, -74.0217, ST_GeomFromText('POINT(-74.0217 4.6697)', 4326), 'https://picsum.photos/seed/lug47/800/600', 200, 'APROBADO', 3, 0, 1),
('Sitio Turístico 48', 'Vista hermosa', 'SITIO_TURISTICO', 'Cra 48 # 48-48', 4.6797, -74.0117, ST_GeomFromText('POINT(-74.0117 4.6797)', 4326), 'https://picsum.photos/seed/lug48/800/600', 300, 'APROBADO', 3, 0, 1),
('Biblioteca Pública 49', 'Lectura libre', 'BIBLIOTECA', 'Cra 49 # 49-49', 4.6897, -74.0017, ST_GeomFromText('POINT(-74.0017 4.6897)', 4326), 'https://picsum.photos/seed/lug49/800/600', 100, 'APROBADO', 3, 0, 1),
('Plaza Mayor 50', 'Punto de encuentro', 'OTRO', 'Cra 50 # 50-50', 4.6997, -73.9917, ST_GeomFromText('POINT(-73.9917 4.6997)', 4326), 'https://picsum.photos/seed/lug50/800/600', 1000, 'PENDIENTE', NULL, 0, 1);

-- -----------------------------------------------------
-- 5. establecimientos (50 registros)
-- -----------------------------------------------------
INSERT INTO establecimientos (nombre, nit, descripcion, categoria, propietario_id, direccion, latitud, longitud, ubicacion, imagen_url, aforo_maximo, aforo_actual, telefono, estado, moderador_id, pin_destacado, color_pin, destacado, activo) VALUES
('Restaurante A1', 'NIT-001', 'Deliciosa comida', 'RESTAURANTE', 11, 'Calle 1', 4.60, -74.08, ST_GeomFromText('POINT(-74.08 4.60)', 4326), 'https://picsum.photos/seed/est1/800/600', 50, 0, '1234567', 'APROBADO', 3, 1, 'DORADO', 1, 1),
('Panaderia A2', 'NIT-002', 'Pan fresco', 'PANADERIA', 12, 'Calle 2', 4.61, -74.07, ST_GeomFromText('POINT(-74.07 4.61)', 4326), 'https://picsum.photos/seed/est2/800/600', 30, 0, '1234568', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Bar A3', 'NIT-003', 'Tragos', 'BAR', 13, 'Calle 3', 4.62, -74.06, ST_GeomFromText('POINT(-74.06 4.62)', 4326), 'https://picsum.photos/seed/est3/800/600', 100, 0, '1234569', 'APROBADO', 3, 1, 'ROJO_URGENTE', 1, 1),
('Tienda A4', 'NIT-004', 'Abarrotes', 'TIENDA', 14, 'Calle 4', 4.63, -74.05, ST_GeomFromText('POINT(-74.05 4.63)', 4326), 'https://picsum.photos/seed/est4/800/600', 20, 0, '1234570', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Supermercado A5', 'NIT-005', 'Todo', 'SUPERMERCADO', 15, 'Calle 5', 4.64, -74.04, ST_GeomFromText('POINT(-74.04 4.64)', 4326), 'https://picsum.photos/seed/est5/800/600', 500, 0, '1234571', 'PENDIENTE', NULL, 0, 'NORMAL', 0, 1),
('Farmacia A6', 'NIT-006', 'Drogas', 'FARMACIA', 16, 'Calle 6', 4.65, -74.03, ST_GeomFromText('POINT(-74.03 4.65)', 4326), 'https://picsum.photos/seed/est6/800/600', 15, 0, '1234572', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Hotel A7', 'NIT-007', 'Dormir', 'HOTEL', 17, 'Calle 7', 4.66, -74.02, ST_GeomFromText('POINT(-74.02 4.66)', 4326), 'https://picsum.photos/seed/est7/800/600', 200, 0, '1234573', 'APROBADO', 3, 1, 'DORADO', 1, 1),
('Gimnasio A8', 'NIT-008', 'Pesas', 'GIMNASIO', 18, 'Calle 8', 4.67, -74.01, ST_GeomFromText('POINT(-74.01 4.67)', 4326), 'https://picsum.photos/seed/est8/800/600', 80, 0, '1234574', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Otro A9', 'NIT-009', 'Varios', 'OTRO', 19, 'Calle 9', 4.68, -74.00, ST_GeomFromText('POINT(-74.00 4.68)', 4326), 'https://picsum.photos/seed/est9/800/600', 40, 0, '1234575', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Restaurante A10', 'NIT-010', 'Asados', 'RESTAURANTE', 20, 'Calle 10', 4.69, -73.99, ST_GeomFromText('POINT(-73.99 4.69)', 4326), 'https://picsum.photos/seed/est10/800/600', 60, 0, '1234576', 'PENDIENTE', NULL, 0, 'NORMAL', 0, 1),
('Restaurante A11', 'NIT-011', 'Comida rápida', 'RESTAURANTE', 11, 'Calle 11', 4.60, -74.08, ST_GeomFromText('POINT(-74.08 4.60)', 4326), 'https://picsum.photos/seed/est11/800/600', 50, 0, '1234577', 'APROBADO', 3, 1, 'DORADO', 1, 1),
('Panaderia A12', 'NIT-012', 'Postres', 'PANADERIA', 12, 'Calle 12', 4.61, -74.07, ST_GeomFromText('POINT(-74.07 4.61)', 4326), 'https://picsum.photos/seed/est12/800/600', 30, 0, '1234578', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Bar A13', 'NIT-013', 'Cervezas', 'BAR', 13, 'Calle 13', 4.62, -74.06, ST_GeomFromText('POINT(-74.06 4.62)', 4326), 'https://picsum.photos/seed/est13/800/600', 100, 0, '1234579', 'APROBADO', 3, 1, 'ROJO_URGENTE', 1, 1),
('Tienda A14', 'NIT-014', 'Básicos', 'TIENDA', 14, 'Calle 14', 4.63, -74.05, ST_GeomFromText('POINT(-74.05 4.63)', 4326), 'https://picsum.photos/seed/est14/800/600', 20, 0, '1234580', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Supermercado A15', 'NIT-015', 'Mercado', 'SUPERMERCADO', 15, 'Calle 15', 4.64, -74.04, ST_GeomFromText('POINT(-74.04 4.64)', 4326), 'https://picsum.photos/seed/est15/800/600', 500, 0, '1234581', 'PENDIENTE', NULL, 0, 'NORMAL', 0, 1),
('Farmacia A16', 'NIT-016', 'Salud', 'FARMACIA', 16, 'Calle 16', 4.65, -74.03, ST_GeomFromText('POINT(-74.03 4.65)', 4326), 'https://picsum.photos/seed/est16/800/600', 15, 0, '1234582', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Hotel A17', 'NIT-017', 'Descanso', 'HOTEL', 17, 'Calle 17', 4.66, -74.02, ST_GeomFromText('POINT(-74.02 4.66)', 4326), 'https://picsum.photos/seed/est17/800/600', 200, 0, '1234583', 'APROBADO', 3, 1, 'DORADO', 1, 1),
('Gimnasio A18', 'NIT-018', 'Fitness', 'GIMNASIO', 18, 'Calle 18', 4.67, -74.01, ST_GeomFromText('POINT(-74.01 4.67)', 4326), 'https://picsum.photos/seed/est18/800/600', 80, 0, '1234584', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Otro A19', 'NIT-019', 'General', 'OTRO', 19, 'Calle 19', 4.68, -74.00, ST_GeomFromText('POINT(-74.00 4.68)', 4326), 'https://picsum.photos/seed/est19/800/600', 40, 0, '1234585', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Restaurante A20', 'NIT-020', 'Pizzas', 'RESTAURANTE', 20, 'Calle 20', 4.69, -73.99, ST_GeomFromText('POINT(-73.99 4.69)', 4326), 'https://picsum.photos/seed/est20/800/600', 60, 0, '1234586', 'PENDIENTE', NULL, 0, 'NORMAL', 0, 1),
('Restaurante A21', 'NIT-021', 'Gourmet', 'RESTAURANTE', 11, 'Calle 21', 4.60, -74.08, ST_GeomFromText('POINT(-74.08 4.60)', 4326), 'https://picsum.photos/seed/est21/800/600', 50, 0, '1234587', 'APROBADO', 3, 1, 'DORADO', 1, 1),
('Panaderia A22', 'NIT-022', 'Dulces', 'PANADERIA', 12, 'Calle 22', 4.61, -74.07, ST_GeomFromText('POINT(-74.07 4.61)', 4326), 'https://picsum.photos/seed/est22/800/600', 30, 0, '1234588', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Bar A23', 'NIT-023', 'Cócteles', 'BAR', 13, 'Calle 23', 4.62, -74.06, ST_GeomFromText('POINT(-74.06 4.62)', 4326), 'https://picsum.photos/seed/est23/800/600', 100, 0, '1234589', 'APROBADO', 3, 1, 'ROJO_URGENTE', 1, 1),
('Tienda A24', 'NIT-024', 'Snacks', 'TIENDA', 14, 'Calle 24', 4.63, -74.05, ST_GeomFromText('POINT(-74.05 4.63)', 4326), 'https://picsum.photos/seed/est24/800/600', 20, 0, '1234590', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Supermercado A25', 'NIT-025', 'Verduras', 'SUPERMERCADO', 15, 'Calle 25', 4.64, -74.04, ST_GeomFromText('POINT(-74.04 4.64)', 4326), 'https://picsum.photos/seed/est25/800/600', 500, 0, '1234591', 'PENDIENTE', NULL, 0, 'NORMAL', 0, 1),
('Farmacia A26', 'NIT-026', 'Insumos', 'FARMACIA', 16, 'Calle 26', 4.65, -74.03, ST_GeomFromText('POINT(-74.03 4.65)', 4326), 'https://picsum.photos/seed/est26/800/600', 15, 0, '1234592', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Hotel A27', 'NIT-027', 'Turismo', 'HOTEL', 17, 'Calle 27', 4.66, -74.02, ST_GeomFromText('POINT(-74.02 4.66)', 4326), 'https://picsum.photos/seed/est27/800/600', 200, 0, '1234593', 'APROBADO', 3, 1, 'DORADO', 1, 1),
('Gimnasio A28', 'NIT-028', 'Cardio', 'GIMNASIO', 18, 'Calle 28', 4.67, -74.01, ST_GeomFromText('POINT(-74.01 4.67)', 4326), 'https://picsum.photos/seed/est28/800/600', 80, 0, '1234594', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Otro A29', 'NIT-029', 'Servicios', 'OTRO', 19, 'Calle 29', 4.68, -74.00, ST_GeomFromText('POINT(-74.00 4.68)', 4326), 'https://picsum.photos/seed/est29/800/600', 40, 0, '1234595', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Restaurante A30', 'NIT-030', 'Sushi', 'RESTAURANTE', 20, 'Calle 30', 4.69, -73.99, ST_GeomFromText('POINT(-73.99 4.69)', 4326), 'https://picsum.photos/seed/est30/800/600', 60, 0, '1234596', 'PENDIENTE', NULL, 0, 'NORMAL', 0, 1),
('Restaurante A31', 'NIT-031', 'Mexicana', 'RESTAURANTE', 11, 'Calle 31', 4.60, -74.08, ST_GeomFromText('POINT(-74.08 4.60)', 4326), 'https://picsum.photos/seed/est31/800/600', 50, 0, '1234597', 'APROBADO', 3, 1, 'DORADO', 1, 1),
('Panaderia A32', 'NIT-032', 'Tortas', 'PANADERIA', 12, 'Calle 32', 4.61, -74.07, ST_GeomFromText('POINT(-74.07 4.61)', 4326), 'https://picsum.photos/seed/est32/800/600', 30, 0, '1234598', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Bar A33', 'NIT-033', 'Música', 'BAR', 13, 'Calle 33', 4.62, -74.06, ST_GeomFromText('POINT(-74.06 4.62)', 4326), 'https://picsum.photos/seed/est33/800/600', 100, 0, '1234599', 'APROBADO', 3, 1, 'ROJO_URGENTE', 1, 1),
('Tienda A34', 'NIT-034', 'Ropa', 'TIENDA', 14, 'Calle 34', 4.63, -74.05, ST_GeomFromText('POINT(-74.05 4.63)', 4326), 'https://picsum.photos/seed/est34/800/600', 20, 0, '1234600', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Supermercado A35', 'NIT-035', 'Lácteos', 'SUPERMERCADO', 15, 'Calle 35', 4.64, -74.04, ST_GeomFromText('POINT(-74.04 4.64)', 4326), 'https://picsum.photos/seed/est35/800/600', 500, 0, '1234601', 'PENDIENTE', NULL, 0, 'NORMAL', 0, 1),
('Farmacia A36', 'NIT-036', 'Cuidado', 'FARMACIA', 16, 'Calle 36', 4.65, -74.03, ST_GeomFromText('POINT(-74.03 4.65)', 4326), 'https://picsum.photos/seed/est36/800/600', 15, 0, '1234602', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Hotel A37', 'NIT-037', 'Spa', 'HOTEL', 17, 'Calle 37', 4.66, -74.02, ST_GeomFromText('POINT(-74.02 4.66)', 4326), 'https://picsum.photos/seed/est37/800/600', 200, 0, '1234603', 'APROBADO', 3, 1, 'DORADO', 1, 1),
('Gimnasio A38', 'NIT-038', 'Crossfit', 'GIMNASIO', 18, 'Calle 38', 4.67, -74.01, ST_GeomFromText('POINT(-74.01 4.67)', 4326), 'https://picsum.photos/seed/est38/800/600', 80, 0, '1234604', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Otro A39', 'NIT-039', 'Misc', 'OTRO', 19, 'Calle 39', 4.68, -74.00, ST_GeomFromText('POINT(-74.00 4.68)', 4326), 'https://picsum.photos/seed/est39/800/600', 40, 0, '1234605', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Restaurante A40', 'NIT-040', 'Tacos', 'RESTAURANTE', 20, 'Calle 40', 4.69, -73.99, ST_GeomFromText('POINT(-73.99 4.69)', 4326), 'https://picsum.photos/seed/est40/800/600', 60, 0, '1234606', 'PENDIENTE', NULL, 0, 'NORMAL', 0, 1),
('Restaurante A41', 'NIT-041', 'Carnes', 'RESTAURANTE', 11, 'Calle 41', 4.60, -74.08, ST_GeomFromText('POINT(-74.08 4.60)', 4326), 'https://picsum.photos/seed/est41/800/600', 50, 0, '1234607', 'APROBADO', 3, 1, 'DORADO', 1, 1),
('Panaderia A42', 'NIT-042', 'Galletas', 'PANADERIA', 12, 'Calle 42', 4.61, -74.07, ST_GeomFromText('POINT(-74.07 4.61)', 4326), 'https://picsum.photos/seed/est42/800/600', 30, 0, '1234608', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Bar A43', 'NIT-043', 'Tapas', 'BAR', 13, 'Calle 43', 4.62, -74.06, ST_GeomFromText('POINT(-74.06 4.62)', 4326), 'https://picsum.photos/seed/est43/800/600', 100, 0, '1234609', 'APROBADO', 3, 1, 'ROJO_URGENTE', 1, 1),
('Tienda A44', 'NIT-044', 'Frutas', 'TIENDA', 14, 'Calle 44', 4.63, -74.05, ST_GeomFromText('POINT(-74.05 4.63)', 4326), 'https://picsum.photos/seed/est44/800/600', 20, 0, '1234610', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Supermercado A45', 'NIT-045', 'Bebidas', 'SUPERMERCADO', 15, 'Calle 45', 4.64, -74.04, ST_GeomFromText('POINT(-74.04 4.64)', 4326), 'https://picsum.photos/seed/est45/800/600', 500, 0, '1234611', 'PENDIENTE', NULL, 0, 'NORMAL', 0, 1),
('Farmacia A46', 'NIT-046', 'Vitaminas', 'FARMACIA', 16, 'Calle 46', 4.65, -74.03, ST_GeomFromText('POINT(-74.03 4.65)', 4326), 'https://picsum.photos/seed/est46/800/600', 15, 0, '1234612', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Hotel A47', 'NIT-047', 'Suite', 'HOTEL', 17, 'Calle 47', 4.66, -74.02, ST_GeomFromText('POINT(-74.02 4.66)', 4326), 'https://picsum.photos/seed/est47/800/600', 200, 0, '1234613', 'APROBADO', 3, 1, 'DORADO', 1, 1),
('Gimnasio A48', 'NIT-048', 'Zumba', 'GIMNASIO', 18, 'Calle 48', 4.67, -74.01, ST_GeomFromText('POINT(-74.01 4.67)', 4326), 'https://picsum.photos/seed/est48/800/600', 80, 0, '1234614', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Otro A49', 'NIT-049', 'Papeleria', 'OTRO', 19, 'Calle 49', 4.68, -74.00, ST_GeomFromText('POINT(-74.00 4.68)', 4326), 'https://picsum.photos/seed/est49/800/600', 40, 0, '1234615', 'APROBADO', 3, 0, 'NORMAL', 0, 1),
('Restaurante A50', 'NIT-050', 'Postres', 'RESTAURANTE', 20, 'Calle 50', 4.69, -73.99, ST_GeomFromText('POINT(-73.99 4.69)', 4326), 'https://picsum.photos/seed/est50/800/600', 60, 0, '1234616', 'PENDIENTE', NULL, 0, 'NORMAL', 0, 1);

-- -----------------------------------------------------
-- 6. eventos (50 registros: Mitad en Lugares, Mitad en Establecimientos)
-- -----------------------------------------------------
INSERT INTO eventos (nombre, descripcion, tipo_evento, lugar_id, establecimiento_id, creador_id, fecha_inicio, fecha_fin, hora_inicio, hora_fin, aforo_maximo, aforo_actual, precio, imagen_url, activo, destacado) VALUES
('Concierto 1', 'Música', 'ARTISTICO', 1, NULL, 21, '2026-06-01', '2026-06-01', '18:00', '22:00', 500, 0, 0.00, 'https://picsum.photos/seed/ev1/800/600', 1, 1),
('Taller 2', 'Cultura', 'CULTURAL', 2, NULL, 22, '2026-06-02', '2026-06-02', '10:00', '12:00', 50, 0, 15000.00, 'https://picsum.photos/seed/ev2/800/600', 1, 0),
('Torneo 3', 'Deporte', 'DEPORTIVO', 3, NULL, 23, '2026-06-03', '2026-06-03', '08:00', '18:00', 200, 0, 0.00, 'https://picsum.photos/seed/ev3/800/600', 1, 1),
('Jornada 4', 'Mascotas', 'VETERINARIO', 4, NULL, 24, '2026-06-04', '2026-06-04', '09:00', '14:00', 100, 0, 0.00, 'https://picsum.photos/seed/ev4/800/600', 1, 0),
('Juegos 5', 'Recreativo', 'RECREATIVO', 5, NULL, 25, '2026-06-05', '2026-06-05', '14:00', '17:00', 300, 0, 5000.00, 'https://picsum.photos/seed/ev5/800/600', 1, 1),
('Concierto 6', 'Música', 'ARTISTICO', 6, NULL, 26, '2026-06-06', '2026-06-06', '18:00', '22:00', 500, 0, 0.00, 'https://picsum.photos/seed/ev6/800/600', 1, 1),
('Taller 7', 'Cultura', 'CULTURAL', 7, NULL, 27, '2026-06-07', '2026-06-07', '10:00', '12:00', 50, 0, 15000.00, 'https://picsum.photos/seed/ev7/800/600', 1, 0),
('Torneo 8', 'Deporte', 'DEPORTIVO', 8, NULL, 28, '2026-06-08', '2026-06-08', '08:00', '18:00', 200, 0, 0.00, 'https://picsum.photos/seed/ev8/800/600', 1, 1),
('Jornada 9', 'Mascotas', 'VETERINARIO', 9, NULL, 29, '2026-06-09', '2026-06-09', '09:00', '14:00', 100, 0, 0.00, 'https://picsum.photos/seed/ev9/800/600', 1, 0),
('Juegos 10', 'Recreativo', 'RECREATIVO', 10, NULL, 30, '2026-06-10', '2026-06-10', '14:00', '17:00', 300, 0, 5000.00, 'https://picsum.photos/seed/ev10/800/600', 1, 1),
('Fiesta 11', 'Club', 'ARTISTICO', NULL, 11, 21, '2026-06-11', '2026-06-12', '21:00', '03:00', 150, 0, 20000.00, 'https://picsum.photos/seed/ev11/800/600', 1, 1),
('Cata 12', 'Vinos', 'CULTURAL', NULL, 12, 22, '2026-06-12', '2026-06-12', '19:00', '21:00', 30, 0, 50000.00, 'https://picsum.photos/seed/ev12/800/600', 1, 0),
('Clase 13', 'Baile', 'DEPORTIVO', NULL, 13, 23, '2026-06-13', '2026-06-13', '08:00', '09:00', 20, 0, 0.00, 'https://picsum.photos/seed/ev13/800/600', 1, 0),
('Standup 14', 'Comedia', 'ARTISTICO', NULL, 14, 24, '2026-06-14', '2026-06-14', '20:00', '22:00', 80, 0, 10000.00, 'https://picsum.photos/seed/ev14/800/600', 1, 1),
('Karaoke 15', 'Canto', 'RECREATIVO', NULL, 15, 25, '2026-06-15', '2026-06-15', '19:00', '23:00', 50, 0, 0.00, 'https://picsum.photos/seed/ev15/800/600', 1, 1),
('Fiesta 16', 'Club', 'ARTISTICO', NULL, 16, 26, '2026-06-16', '2026-06-17', '21:00', '03:00', 150, 0, 20000.00, 'https://picsum.photos/seed/ev16/800/600', 1, 1),
('Cata 17', 'Vinos', 'CULTURAL', NULL, 17, 27, '2026-06-17', '2026-06-17', '19:00', '21:00', 30, 0, 50000.00, 'https://picsum.photos/seed/ev17/800/600', 1, 0),
('Clase 18', 'Baile', 'DEPORTIVO', NULL, 18, 28, '2026-06-18', '2026-06-18', '08:00', '09:00', 20, 0, 0.00, 'https://picsum.photos/seed/ev18/800/600', 1, 0),
('Standup 19', 'Comedia', 'ARTISTICO', NULL, 19, 29, '2026-06-19', '2026-06-19', '20:00', '22:00', 80, 0, 10000.00, 'https://picsum.photos/seed/ev19/800/600', 1, 1),
('Karaoke 20', 'Canto', 'RECREATIVO', NULL, 20, 30, '2026-06-20', '2026-06-20', '19:00', '23:00', 50, 0, 0.00, 'https://picsum.photos/seed/ev20/800/600', 1, 1),
('Concierto 21', 'Música', 'ARTISTICO', 11, NULL, 21, '2026-06-21', '2026-06-21', '18:00', '22:00', 500, 0, 0.00, 'https://picsum.photos/seed/ev21/800/600', 1, 1),
('Taller 22', 'Cultura', 'CULTURAL', 12, NULL, 22, '2026-06-22', '2026-06-22', '10:00', '12:00', 50, 0, 15000.00, 'https://picsum.photos/seed/ev22/800/600', 1, 0),
('Torneo 23', 'Deporte', 'DEPORTIVO', 13, NULL, 23, '2026-06-23', '2026-06-23', '08:00', '18:00', 200, 0, 0.00, 'https://picsum.photos/seed/ev23/800/600', 1, 1),
('Jornada 24', 'Mascotas', 'VETERINARIO', 14, NULL, 24, '2026-06-24', '2026-06-24', '09:00', '14:00', 100, 0, 0.00, 'https://picsum.photos/seed/ev24/800/600', 1, 0),
('Juegos 25', 'Recreativo', 'RECREATIVO', 15, NULL, 25, '2026-06-25', '2026-06-25', '14:00', '17:00', 300, 0, 5000.00, 'https://picsum.photos/seed/ev25/800/600', 1, 1),
('Concierto 26', 'Música', 'ARTISTICO', 16, NULL, 26, '2026-06-26', '2026-06-26', '18:00', '22:00', 500, 0, 0.00, 'https://picsum.photos/seed/ev26/800/600', 1, 1),
('Taller 27', 'Cultura', 'CULTURAL', 17, NULL, 27, '2026-06-27', '2026-06-27', '10:00', '12:00', 50, 0, 15000.00, 'https://picsum.photos/seed/ev27/800/600', 1, 0),
('Torneo 28', 'Deporte', 'DEPORTIVO', 18, NULL, 28, '2026-06-28', '2026-06-28', '08:00', '18:00', 200, 0, 0.00, 'https://picsum.photos/seed/ev28/800/600', 1, 1),
('Jornada 29', 'Mascotas', 'VETERINARIO', 19, NULL, 29, '2026-06-29', '2026-06-29', '09:00', '14:00', 100, 0, 0.00, 'https://picsum.photos/seed/ev29/800/600', 1, 0),
('Juegos 30', 'Recreativo', 'RECREATIVO', 20, NULL, 30, '2026-06-30', '2026-06-30', '14:00', '17:00', 300, 0, 5000.00, 'https://picsum.photos/seed/ev30/800/600', 1, 1),
('Fiesta 31', 'Club', 'ARTISTICO', NULL, 21, 21, '2026-07-01', '2026-07-02', '21:00', '03:00', 150, 0, 20000.00, 'https://picsum.photos/seed/ev31/800/600', 1, 1),
('Cata 32', 'Vinos', 'CULTURAL', NULL, 22, 22, '2026-07-02', '2026-07-02', '19:00', '21:00', 30, 0, 50000.00, 'https://picsum.photos/seed/ev32/800/600', 1, 0),
('Clase 33', 'Baile', 'DEPORTIVO', NULL, 23, 23, '2026-07-03', '2026-07-03', '08:00', '09:00', 20, 0, 0.00, 'https://picsum.photos/seed/ev33/800/600', 1, 0),
('Standup 34', 'Comedia', 'ARTISTICO', NULL, 24, 24, '2026-07-04', '2026-07-04', '20:00', '22:00', 80, 0, 10000.00, 'https://picsum.photos/seed/ev34/800/600', 1, 1),
('Karaoke 35', 'Canto', 'RECREATIVO', NULL, 25, 25, '2026-07-05', '2026-07-05', '19:00', '23:00', 50, 0, 0.00, 'https://picsum.photos/seed/ev35/800/600', 1, 1),
('Fiesta 36', 'Club', 'ARTISTICO', NULL, 26, 26, '2026-07-06', '2026-07-07', '21:00', '03:00', 150, 0, 20000.00, 'https://picsum.photos/seed/ev36/800/600', 1, 1),
('Cata 37', 'Vinos', 'CULTURAL', NULL, 27, 27, '2026-07-07', '2026-07-07', '19:00', '21:00', 30, 0, 50000.00, 'https://picsum.photos/seed/ev37/800/600', 1, 0),
('Clase 38', 'Baile', 'DEPORTIVO', NULL, 28, 28, '2026-07-08', '2026-07-08', '08:00', '09:00', 20, 0, 0.00, 'https://picsum.photos/seed/ev38/800/600', 1, 0),
('Standup 39', 'Comedia', 'ARTISTICO', NULL, 29, 29, '2026-07-09', '2026-07-09', '20:00', '22:00', 80, 0, 10000.00, 'https://picsum.photos/seed/ev39/800/600', 1, 1),
('Karaoke 40', 'Canto', 'RECREATIVO', NULL, 30, 30, '2026-07-10', '2026-07-10', '19:00', '23:00', 50, 0, 0.00, 'https://picsum.photos/seed/ev40/800/600', 1, 1),
('Fiesta 41', 'Club', 'ARTISTICO', NULL, 31, 21, '2026-07-11', '2026-07-12', '21:00', '03:00', 150, 0, 20000.00, 'https://picsum.photos/seed/ev41/800/600', 1, 1),
('Cata 42', 'Vinos', 'CULTURAL', NULL, 32, 22, '2026-07-12', '2026-07-12', '19:00', '21:00', 30, 0, 50000.00, 'https://picsum.photos/seed/ev42/800/600', 1, 0),
('Clase 43', 'Baile', 'DEPORTIVO', NULL, 33, 23, '2026-07-13', '2026-07-13', '08:00', '09:00', 20, 0, 0.00, 'https://picsum.photos/seed/ev43/800/600', 1, 0),
('Standup 44', 'Comedia', 'ARTISTICO', NULL, 34, 24, '2026-07-14', '2026-07-14', '20:00', '22:00', 80, 0, 10000.00, 'https://picsum.photos/seed/ev44/800/600', 1, 1),
('Karaoke 45', 'Canto', 'RECREATIVO', NULL, 35, 25, '2026-07-15', '2026-07-15', '19:00', '23:00', 50, 0, 0.00, 'https://picsum.photos/seed/ev45/800/600', 1, 1),
('Fiesta 46', 'Club', 'ARTISTICO', NULL, 36, 26, '2026-07-16', '2026-07-17', '21:00', '03:00', 150, 0, 20000.00, 'https://picsum.photos/seed/ev46/800/600', 1, 1),
('Cata 47', 'Vinos', 'CULTURAL', NULL, 37, 27, '2026-07-17', '2026-07-17', '19:00', '21:00', 30, 0, 50000.00, 'https://picsum.photos/seed/ev47/800/600', 1, 0),
('Clase 48', 'Baile', 'DEPORTIVO', NULL, 38, 28, '2026-07-18', '2026-07-18', '08:00', '09:00', 20, 0, 0.00, 'https://picsum.photos/seed/ev48/800/600', 1, 0),
('Standup 49', 'Comedia', 'ARTISTICO', NULL, 39, 29, '2026-07-19', '2026-07-19', '20:00', '22:00', 80, 0, 10000.00, 'https://picsum.photos/seed/ev49/800/600', 1, 1),
('Karaoke 50', 'Canto', 'RECREATIVO', NULL, 40, 30, '2026-07-20', '2026-07-20', '19:00', '23:00', 50, 0, 0.00, 'https://picsum.photos/seed/ev50/800/600', 1, 1);

-- -----------------------------------------------------
-- 7. promociones (50 registros)
-- -----------------------------------------------------
INSERT INTO promociones (titulo, descripcion, establecimiento_id, evento_id, descuento_porcentaje, descuento_valor, precio_especial, fecha_inicio, fecha_fin, codigo_cupon, usos_maximos, usos_actuales, solo_pro, imagen_url, activo) VALUES
('Promo 1', 'Descuento', 1, NULL, 20, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC20', 100, 10, 0, 'https://picsum.photos/seed/p1/800/600', 1),
('Promo 2', 'Descuento', 2, NULL, 10, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC10', 100, 5, 0, 'https://picsum.photos/seed/p2/800/600', 1),
('Promo 3', 'Descuento', 3, NULL, 15, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC15', 50, 20, 1, 'https://picsum.photos/seed/p3/800/600', 1),
('Promo 4', 'Especial', 4, NULL, 0, 5000.00, 0.00, '2026-05-01', '2026-12-31', 'MENOS5', 200, 0, 0, 'https://picsum.photos/seed/p4/800/600', 1),
('Promo 5', 'Especial', 5, NULL, 0, 0.00, 15000.00, '2026-05-01', '2026-12-31', 'FIJO15', 50, 0, 1, 'https://picsum.photos/seed/p5/800/600', 1),
('Promo 6', 'Descuento', 6, NULL, 20, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC20', 100, 10, 0, 'https://picsum.photos/seed/p6/800/600', 1),
('Promo 7', 'Descuento', 7, NULL, 10, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC10', 100, 5, 0, 'https://picsum.photos/seed/p7/800/600', 1),
('Promo 8', 'Descuento', 8, NULL, 15, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC15', 50, 20, 1, 'https://picsum.photos/seed/p8/800/600', 1),
('Promo 9', 'Especial', 9, NULL, 0, 5000.00, 0.00, '2026-05-01', '2026-12-31', 'MENOS5', 200, 0, 0, 'https://picsum.photos/seed/p9/800/600', 1),
('Promo 10', 'Especial', 10, NULL, 0, 0.00, 15000.00, '2026-05-01', '2026-12-31', 'FIJO15', 50, 0, 1, 'https://picsum.photos/seed/p10/800/600', 1),
('Promo 11', 'Descuento', 11, NULL, 20, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC20', 100, 10, 0, 'https://picsum.photos/seed/p11/800/600', 1),
('Promo 12', 'Descuento', 12, NULL, 10, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC10', 100, 5, 0, 'https://picsum.photos/seed/p12/800/600', 1),
('Promo 13', 'Descuento', 13, NULL, 15, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC15', 50, 20, 1, 'https://picsum.photos/seed/p13/800/600', 1),
('Promo 14', 'Especial', 14, NULL, 0, 5000.00, 0.00, '2026-05-01', '2026-12-31', 'MENOS5', 200, 0, 0, 'https://picsum.photos/seed/p14/800/600', 1),
('Promo 15', 'Especial', 15, NULL, 0, 0.00, 15000.00, '2026-05-01', '2026-12-31', 'FIJO15', 50, 0, 1, 'https://picsum.photos/seed/p15/800/600', 1),
('Promo 16', 'Descuento', 16, NULL, 20, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC20', 100, 10, 0, 'https://picsum.photos/seed/p16/800/600', 1),
('Promo 17', 'Descuento', 17, NULL, 10, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC10', 100, 5, 0, 'https://picsum.photos/seed/p17/800/600', 1),
('Promo 18', 'Descuento', 18, NULL, 15, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC15', 50, 20, 1, 'https://picsum.photos/seed/p18/800/600', 1),
('Promo 19', 'Especial', 19, NULL, 0, 5000.00, 0.00, '2026-05-01', '2026-12-31', 'MENOS5', 200, 0, 0, 'https://picsum.photos/seed/p19/800/600', 1),
('Promo 20', 'Especial', 20, NULL, 0, 0.00, 15000.00, '2026-05-01', '2026-12-31', 'FIJO15', 50, 0, 1, 'https://picsum.photos/seed/p20/800/600', 1),
('Promo 21', 'Descuento', 21, NULL, 20, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC20', 100, 10, 0, 'https://picsum.photos/seed/p21/800/600', 1),
('Promo 22', 'Descuento', 22, NULL, 10, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC10', 100, 5, 0, 'https://picsum.photos/seed/p22/800/600', 1),
('Promo 23', 'Descuento', 23, NULL, 15, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC15', 50, 20, 1, 'https://picsum.photos/seed/p23/800/600', 1),
('Promo 24', 'Especial', 24, NULL, 0, 5000.00, 0.00, '2026-05-01', '2026-12-31', 'MENOS5', 200, 0, 0, 'https://picsum.photos/seed/p24/800/600', 1),
('Promo 25', 'Especial', 25, NULL, 0, 0.00, 15000.00, '2026-05-01', '2026-12-31', 'FIJO15', 50, 0, 1, 'https://picsum.photos/seed/p25/800/600', 1),
('Promo 26', 'Descuento', 26, NULL, 20, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC20', 100, 10, 0, 'https://picsum.photos/seed/p26/800/600', 1),
('Promo 27', 'Descuento', 27, NULL, 10, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC10', 100, 5, 0, 'https://picsum.photos/seed/p27/800/600', 1),
('Promo 28', 'Descuento', 28, NULL, 15, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC15', 50, 20, 1, 'https://picsum.photos/seed/p28/800/600', 1),
('Promo 29', 'Especial', 29, NULL, 0, 5000.00, 0.00, '2026-05-01', '2026-12-31', 'MENOS5', 200, 0, 0, 'https://picsum.photos/seed/p29/800/600', 1),
('Promo 30', 'Especial', 30, NULL, 0, 0.00, 15000.00, '2026-05-01', '2026-12-31', 'FIJO15', 50, 0, 1, 'https://picsum.photos/seed/p30/800/600', 1),
('Promo 31', 'Descuento', 31, NULL, 20, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC20', 100, 10, 0, 'https://picsum.photos/seed/p31/800/600', 1),
('Promo 32', 'Descuento', 32, NULL, 10, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC10', 100, 5, 0, 'https://picsum.photos/seed/p32/800/600', 1),
('Promo 33', 'Descuento', 33, NULL, 15, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC15', 50, 20, 1, 'https://picsum.photos/seed/p33/800/600', 1),
('Promo 34', 'Especial', 34, NULL, 0, 5000.00, 0.00, '2026-05-01', '2026-12-31', 'MENOS5', 200, 0, 0, 'https://picsum.photos/seed/p34/800/600', 1),
('Promo 35', 'Especial', 35, NULL, 0, 0.00, 15000.00, '2026-05-01', '2026-12-31', 'FIJO15', 50, 0, 1, 'https://picsum.photos/seed/p35/800/600', 1),
('Promo 36', 'Descuento', 36, NULL, 20, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC20', 100, 10, 0, 'https://picsum.photos/seed/p36/800/600', 1),
('Promo 37', 'Descuento', 37, NULL, 10, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC10', 100, 5, 0, 'https://picsum.photos/seed/p37/800/600', 1),
('Promo 38', 'Descuento', 38, NULL, 15, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC15', 50, 20, 1, 'https://picsum.photos/seed/p38/800/600', 1),
('Promo 39', 'Especial', 39, NULL, 0, 5000.00, 0.00, '2026-05-01', '2026-12-31', 'MENOS5', 200, 0, 0, 'https://picsum.photos/seed/p39/800/600', 1),
('Promo 40', 'Especial', 40, NULL, 0, 0.00, 15000.00, '2026-05-01', '2026-12-31', 'FIJO15', 50, 0, 1, 'https://picsum.photos/seed/p40/800/600', 1),
('Promo 41', 'Descuento', 41, NULL, 20, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC20', 100, 10, 0, 'https://picsum.photos/seed/p41/800/600', 1),
('Promo 42', 'Descuento', 42, NULL, 10, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC10', 100, 5, 0, 'https://picsum.photos/seed/p42/800/600', 1),
('Promo 43', 'Descuento', 43, NULL, 15, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC15', 50, 20, 1, 'https://picsum.photos/seed/p43/800/600', 1),
('Promo 44', 'Especial', 44, NULL, 0, 5000.00, 0.00, '2026-05-01', '2026-12-31', 'MENOS5', 200, 0, 0, 'https://picsum.photos/seed/p44/800/600', 1),
('Promo 45', 'Especial', 45, NULL, 0, 0.00, 15000.00, '2026-05-01', '2026-12-31', 'FIJO15', 50, 0, 1, 'https://picsum.photos/seed/p45/800/600', 1),
('Promo 46', 'Descuento', 46, NULL, 20, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC20', 100, 10, 0, 'https://picsum.photos/seed/p46/800/600', 1),
('Promo 47', 'Descuento', 47, NULL, 10, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC10', 100, 5, 0, 'https://picsum.photos/seed/p47/800/600', 1),
('Promo 48', 'Descuento', 48, NULL, 15, 0.00, 0.00, '2026-05-01', '2026-12-31', 'DESC15', 50, 20, 1, 'https://picsum.photos/seed/p48/800/600', 1),
('Promo 49', 'Especial', 49, NULL, 0, 5000.00, 0.00, '2026-05-01', '2026-12-31', 'MENOS5', 200, 0, 0, 'https://picsum.photos/seed/p49/800/600', 1),
('Promo 50', 'Especial', 50, NULL, 0, 0.00, 15000.00, '2026-05-01', '2026-12-31', 'FIJO15', 50, 0, 1, 'https://picsum.photos/seed/p50/800/600', 1);

-- -----------------------------------------------------
-- 8. reservas (50 registros)
-- -----------------------------------------------------
INSERT INTO reservas (codigo_reserva, usuario_id, evento_id, establecimiento_id, lugar_id, promocion_id, fecha_reserva, numero_personas, estado, puntos_otorgados, comision_cobrada) VALUES
('RES-001', 31, NULL, 1, NULL, 1, '2026-05-20 18:00:00', 2, 'COMPLETADA', 10, 500.00), ('RES-002', 32, NULL, 2, NULL, 2, '2026-05-20 18:00:00', 4, 'PENDIENTE', 0, 0.00), ('RES-003', 33, NULL, 3, NULL, 3, '2026-05-20 18:00:00', 1, 'CONFIRMADA', 0, 0.00), ('RES-004', 34, NULL, 4, NULL, 4, '2026-05-20 18:00:00', 3, 'REDIMIDA', 15, 1000.00), ('RES-005', 35, NULL, 5, NULL, 5, '2026-05-20 18:00:00', 2, 'CANCELADA', 0, 0.00),
('RES-006', 36, NULL, 6, NULL, 6, '2026-05-20 18:00:00', 2, 'COMPLETADA', 10, 500.00), ('RES-007', 37, NULL, 7, NULL, 7, '2026-05-20 18:00:00', 4, 'PENDIENTE', 0, 0.00), ('RES-008', 38, NULL, 8, NULL, 8, '2026-05-20 18:00:00', 1, 'CONFIRMADA', 0, 0.00), ('RES-009', 39, NULL, 9, NULL, 9, '2026-05-20 18:00:00', 3, 'REDIMIDA', 15, 1000.00), ('RES-010', 40, NULL, 10, NULL, 10, '2026-05-20 18:00:00', 2, 'CANCELADA', 0, 0.00),
('RES-011', 41, NULL, 11, NULL, 11, '2026-05-20 18:00:00', 2, 'COMPLETADA', 10, 500.00), ('RES-012', 42, NULL, 12, NULL, 12, '2026-05-20 18:00:00', 4, 'PENDIENTE', 0, 0.00), ('RES-013', 43, NULL, 13, NULL, 13, '2026-05-20 18:00:00', 1, 'CONFIRMADA', 0, 0.00), ('RES-014', 44, NULL, 14, NULL, 14, '2026-05-20 18:00:00', 3, 'REDIMIDA', 15, 1000.00), ('RES-015', 45, NULL, 15, NULL, 15, '2026-05-20 18:00:00', 2, 'CANCELADA', 0, 0.00),
('RES-016', 46, NULL, 16, NULL, 16, '2026-05-20 18:00:00', 2, 'COMPLETADA', 10, 500.00), ('RES-017', 47, NULL, 17, NULL, 17, '2026-05-20 18:00:00', 4, 'PENDIENTE', 0, 0.00), ('RES-018', 48, NULL, 18, NULL, 18, '2026-05-20 18:00:00', 1, 'CONFIRMADA', 0, 0.00), ('RES-019', 49, NULL, 19, NULL, 19, '2026-05-20 18:00:00', 3, 'REDIMIDA', 15, 1000.00), ('RES-020', 50, NULL, 20, NULL, 20, '2026-05-20 18:00:00', 2, 'CANCELADA', 0, 0.00),
('RES-021', 31, NULL, 21, NULL, 21, '2026-05-20 18:00:00', 2, 'COMPLETADA', 10, 500.00), ('RES-022', 32, NULL, 22, NULL, 22, '2026-05-20 18:00:00', 4, 'PENDIENTE', 0, 0.00), ('RES-023', 33, NULL, 23, NULL, 23, '2026-05-20 18:00:00', 1, 'CONFIRMADA', 0, 0.00), ('RES-024', 34, NULL, 24, NULL, 24, '2026-05-20 18:00:00', 3, 'REDIMIDA', 15, 1000.00), ('RES-025', 35, NULL, 25, NULL, 25, '2026-05-20 18:00:00', 2, 'CANCELADA', 0, 0.00),
('RES-026', 36, NULL, 26, NULL, 26, '2026-05-20 18:00:00', 2, 'COMPLETADA', 10, 500.00), ('RES-027', 37, NULL, 27, NULL, 27, '2026-05-20 18:00:00', 4, 'PENDIENTE', 0, 0.00), ('RES-028', 38, NULL, 28, NULL, 28, '2026-05-20 18:00:00', 1, 'CONFIRMADA', 0, 0.00), ('RES-029', 39, NULL, 29, NULL, 29, '2026-05-20 18:00:00', 3, 'REDIMIDA', 15, 1000.00), ('RES-030', 40, NULL, 30, NULL, 30, '2026-05-20 18:00:00', 2, 'CANCELADA', 0, 0.00),
('RES-031', 41, NULL, 31, NULL, 31, '2026-05-20 18:00:00', 2, 'COMPLETADA', 10, 500.00), ('RES-032', 42, NULL, 32, NULL, 32, '2026-05-20 18:00:00', 4, 'PENDIENTE', 0, 0.00), ('RES-033', 43, NULL, 33, NULL, 33, '2026-05-20 18:00:00', 1, 'CONFIRMADA', 0, 0.00), ('RES-034', 44, NULL, 34, NULL, 34, '2026-05-20 18:00:00', 3, 'REDIMIDA', 15, 1000.00), ('RES-035', 45, NULL, 35, NULL, 35, '2026-05-20 18:00:00', 2, 'CANCELADA', 0, 0.00),
('RES-036', 46, NULL, 36, NULL, 36, '2026-05-20 18:00:00', 2, 'COMPLETADA', 10, 500.00), ('RES-037', 47, NULL, 37, NULL, 37, '2026-05-20 18:00:00', 4, 'PENDIENTE', 0, 0.00), ('RES-038', 48, NULL, 38, NULL, 38, '2026-05-20 18:00:00', 1, 'CONFIRMADA', 0, 0.00), ('RES-039', 49, NULL, 39, NULL, 39, '2026-05-20 18:00:00', 3, 'REDIMIDA', 15, 1000.00), ('RES-040', 50, NULL, 40, NULL, 40, '2026-05-20 18:00:00', 2, 'CANCELADA', 0, 0.00),
('RES-041', 31, NULL, 41, NULL, 41, '2026-05-20 18:00:00', 2, 'COMPLETADA', 10, 500.00), ('RES-042', 32, NULL, 42, NULL, 42, '2026-05-20 18:00:00', 4, 'PENDIENTE', 0, 0.00), ('RES-043', 33, NULL, 43, NULL, 43, '2026-05-20 18:00:00', 1, 'CONFIRMADA', 0, 0.00), ('RES-044', 34, NULL, 44, NULL, 44, '2026-05-20 18:00:00', 3, 'REDIMIDA', 15, 1000.00), ('RES-045', 35, NULL, 45, NULL, 45, '2026-05-20 18:00:00', 2, 'CANCELADA', 0, 0.00),
('RES-046', 36, NULL, 46, NULL, 46, '2026-05-20 18:00:00', 2, 'COMPLETADA', 10, 500.00), ('RES-047', 37, NULL, 47, NULL, 47, '2026-05-20 18:00:00', 4, 'PENDIENTE', 0, 0.00), ('RES-048', 38, NULL, 48, NULL, 48, '2026-05-20 18:00:00', 1, 'CONFIRMADA', 0, 0.00), ('RES-049', 39, NULL, 49, NULL, 49, '2026-05-20 18:00:00', 3, 'REDIMIDA', 15, 1000.00), ('RES-050', 40, NULL, 50, NULL, 50, '2026-05-20 18:00:00', 2, 'CANCELADA', 0, 0.00);

-- -----------------------------------------------------
-- 9. analiticas_locales (50 registros)
-- -----------------------------------------------------
INSERT INTO analiticas_locales (establecimiento_id, fecha, clics_perfil, vistas_mapa, cupones_vistos, exploradores_cercanos_promedio) VALUES
(1, '2026-05-23', 12, 45, 5, 20), (2, '2026-05-23', 10, 30, 2, 15), (3, '2026-05-23', 50, 120, 25, 40), (4, '2026-05-23', 8, 20, 1, 10), (5, '2026-05-23', 15, 60, 8, 25),
(6, '2026-05-23', 12, 45, 5, 20), (7, '2026-05-23', 10, 30, 2, 15), (8, '2026-05-23', 50, 120, 25, 40), (9, '2026-05-23', 8, 20, 1, 10), (10, '2026-05-23', 15, 60, 8, 25),
(11, '2026-05-23', 12, 45, 5, 20), (12, '2026-05-23', 10, 30, 2, 15), (13, '2026-05-23', 50, 120, 25, 40), (14, '2026-05-23', 8, 20, 1, 10), (15, '2026-05-23', 15, 60, 8, 25),
(16, '2026-05-23', 12, 45, 5, 20), (17, '2026-05-23', 10, 30, 2, 15), (18, '2026-05-23', 50, 120, 25, 40), (19, '2026-05-23', 8, 20, 1, 10), (20, '2026-05-23', 15, 60, 8, 25),
(21, '2026-05-23', 12, 45, 5, 20), (22, '2026-05-23', 10, 30, 2, 15), (23, '2026-05-23', 50, 120, 25, 40), (24, '2026-05-23', 8, 20, 1, 10), (25, '2026-05-23', 15, 60, 8, 25),
(26, '2026-05-23', 12, 45, 5, 20), (27, '2026-05-23', 10, 30, 2, 15), (28, '2026-05-23', 50, 120, 25, 40), (29, '2026-05-23', 8, 20, 1, 10), (30, '2026-05-23', 15, 60, 8, 25),
(31, '2026-05-23', 12, 45, 5, 20), (32, '2026-05-23', 10, 30, 2, 15), (33, '2026-05-23', 50, 120, 25, 40), (34, '2026-05-23', 8, 20, 1, 10), (35, '2026-05-23', 15, 60, 8, 25),
(36, '2026-05-23', 12, 45, 5, 20), (37, '2026-05-23', 10, 30, 2, 15), (38, '2026-05-23', 50, 120, 25, 40), (39, '2026-05-23', 8, 20, 1, 10), (40, '2026-05-23', 15, 60, 8, 25),
(41, '2026-05-23', 12, 45, 5, 20), (42, '2026-05-23', 10, 30, 2, 15), (43, '2026-05-23', 50, 120, 25, 40), (44, '2026-05-23', 8, 20, 1, 10), (45, '2026-05-23', 15, 60, 8, 25),
(46, '2026-05-23', 12, 45, 5, 20), (47, '2026-05-23', 10, 30, 2, 15), (48, '2026-05-23', 50, 120, 25, 40), (49, '2026-05-23', 8, 20, 1, 10), (50, '2026-05-23', 15, 60, 8, 25);

-- -----------------------------------------------------
-- 10. pqrs (50 registros)
-- -----------------------------------------------------
INSERT INTO pqrs (codigo_ticket, usuario_id, tipo, asunto, descripcion, estado, prioridad, moderador_asignado_id) VALUES
('TK-001', 1, 'PETICION', 'Info', 'Solicito info', 'ABIERTO', 'BAJA', NULL), ('TK-002', 2, 'QUEJA', 'Error', 'Fallo app', 'EN_PROCESO', 'ALTA', 3), ('TK-003', 3, 'RECLAMO', 'Cobro', 'Cobro extra', 'RESPONDIDO', 'ALTA', 4), ('TK-004', 4, 'SUGERENCIA', 'UX', 'Mejorar UI', 'CERRADO', 'BAJA', 3), ('TK-005', 5, 'PETICION', 'Duda', 'Ayuda con pin', 'ABIERTO', 'MEDIA', NULL),
('TK-006', 6, 'PETICION', 'Info', 'Solicito info', 'ABIERTO', 'BAJA', NULL), ('TK-007', 7, 'QUEJA', 'Error', 'Fallo app', 'EN_PROCESO', 'ALTA', 3), ('TK-008', 8, 'RECLAMO', 'Cobro', 'Cobro extra', 'RESPONDIDO', 'ALTA', 4), ('TK-009', 9, 'SUGERENCIA', 'UX', 'Mejorar UI', 'CERRADO', 'BAJA', 3), ('TK-010', 10, 'PETICION', 'Duda', 'Ayuda con pin', 'ABIERTO', 'MEDIA', NULL),
('TK-011', 11, 'PETICION', 'Info', 'Solicito info', 'ABIERTO', 'BAJA', NULL), ('TK-012', 12, 'QUEJA', 'Error', 'Fallo app', 'EN_PROCESO', 'ALTA', 3), ('TK-013', 13, 'RECLAMO', 'Cobro', 'Cobro extra', 'RESPONDIDO', 'ALTA', 4), ('TK-014', 14, 'SUGERENCIA', 'UX', 'Mejorar UI', 'CERRADO', 'BAJA', 3), ('TK-015', 15, 'PETICION', 'Duda', 'Ayuda con pin', 'ABIERTO', 'MEDIA', NULL),
('TK-016', 16, 'PETICION', 'Info', 'Solicito info', 'ABIERTO', 'BAJA', NULL), ('TK-017', 17, 'QUEJA', 'Error', 'Fallo app', 'EN_PROCESO', 'ALTA', 3), ('TK-018', 18, 'RECLAMO', 'Cobro', 'Cobro extra', 'RESPONDIDO', 'ALTA', 4), ('TK-019', 19, 'SUGERENCIA', 'UX', 'Mejorar UI', 'CERRADO', 'BAJA', 3), ('TK-020', 20, 'PETICION', 'Duda', 'Ayuda con pin', 'ABIERTO', 'MEDIA', NULL),
('TK-021', 21, 'PETICION', 'Info', 'Solicito info', 'ABIERTO', 'BAJA', NULL), ('TK-022', 22, 'QUEJA', 'Error', 'Fallo app', 'EN_PROCESO', 'ALTA', 3), ('TK-023', 23, 'RECLAMO', 'Cobro', 'Cobro extra', 'RESPONDIDO', 'ALTA', 4), ('TK-024', 24, 'SUGERENCIA', 'UX', 'Mejorar UI', 'CERRADO', 'BAJA', 3), ('TK-025', 25, 'PETICION', 'Duda', 'Ayuda con pin', 'ABIERTO', 'MEDIA', NULL),
('TK-026', 26, 'PETICION', 'Info', 'Solicito info', 'ABIERTO', 'BAJA', NULL), ('TK-027', 27, 'QUEJA', 'Error', 'Fallo app', 'EN_PROCESO', 'ALTA', 3), ('TK-028', 28, 'RECLAMO', 'Cobro', 'Cobro extra', 'RESPONDIDO', 'ALTA', 4), ('TK-029', 29, 'SUGERENCIA', 'UX', 'Mejorar UI', 'CERRADO', 'BAJA', 3), ('TK-030', 30, 'PETICION', 'Duda', 'Ayuda con pin', 'ABIERTO', 'MEDIA', NULL),
('TK-031', 31, 'PETICION', 'Info', 'Solicito info', 'ABIERTO', 'BAJA', NULL), ('TK-032', 32, 'QUEJA', 'Error', 'Fallo app', 'EN_PROCESO', 'ALTA', 3), ('TK-033', 33, 'RECLAMO', 'Cobro', 'Cobro extra', 'RESPONDIDO', 'ALTA', 4), ('TK-034', 34, 'SUGERENCIA', 'UX', 'Mejorar UI', 'CERRADO', 'BAJA', 3), ('TK-035', 35, 'PETICION', 'Duda', 'Ayuda con pin', 'ABIERTO', 'MEDIA', NULL),
('TK-036', 36, 'PETICION', 'Info', 'Solicito info', 'ABIERTO', 'BAJA', NULL), ('TK-037', 37, 'QUEJA', 'Error', 'Fallo app', 'EN_PROCESO', 'ALTA', 3), ('TK-038', 38, 'RECLAMO', 'Cobro', 'Cobro extra', 'RESPONDIDO', 'ALTA', 4), ('TK-039', 39, 'SUGERENCIA', 'UX', 'Mejorar UI', 'CERRADO', 'BAJA', 3), ('TK-040', 40, 'PETICION', 'Duda', 'Ayuda con pin', 'ABIERTO', 'MEDIA', NULL),
('TK-041', 41, 'PETICION', 'Info', 'Solicito info', 'ABIERTO', 'BAJA', NULL), ('TK-042', 42, 'QUEJA', 'Error', 'Fallo app', 'EN_PROCESO', 'ALTA', 3), ('TK-043', 43, 'RECLAMO', 'Cobro', 'Cobro extra', 'RESPONDIDO', 'ALTA', 4), ('TK-044', 44, 'SUGERENCIA', 'UX', 'Mejorar UI', 'CERRADO', 'BAJA', 3), ('TK-045', 45, 'PETICION', 'Duda', 'Ayuda con pin', 'ABIERTO', 'MEDIA', NULL),
('TK-046', 46, 'PETICION', 'Info', 'Solicito info', 'ABIERTO', 'BAJA', NULL), ('TK-047', 47, 'QUEJA', 'Error', 'Fallo app', 'EN_PROCESO', 'ALTA', 3), ('TK-048', 48, 'RECLAMO', 'Cobro', 'Cobro extra', 'RESPONDIDO', 'ALTA', 4), ('TK-049', 49, 'SUGERENCIA', 'UX', 'Mejorar UI', 'CERRADO', 'BAJA', 3), ('TK-050', 50, 'PETICION', 'Duda', 'Ayuda con pin', 'ABIERTO', 'MEDIA', NULL);

-- -----------------------------------------------------
-- 11. notificaciones (50 registros)
-- -----------------------------------------------------
INSERT INTO notificaciones (usuario_id, tipo, titulo, mensaje, leida, origen) VALUES
(1, 'SISTEMA', 'Bienvenido', 'Hola', 0, 'SPRING'), (2, 'RESERVA_CONFIRMADA', 'Reserva OK', 'Confirmada', 1, 'SPRING'), (3, 'ALERTA_PROXIMIDAD', 'Cerca', 'A 100m', 0, 'FLASK'), (4, 'PROMOCION_NUEVA', 'Promo', 'Nueva promo', 0, 'SPRING'), (5, 'PQRS_RESPUESTA', 'PQRS', 'Respondida', 1, 'SPRING'),
(6, 'SISTEMA', 'Bienvenido', 'Hola', 0, 'SPRING'), (7, 'RESERVA_CONFIRMADA', 'Reserva OK', 'Confirmada', 1, 'SPRING'), (8, 'ALERTA_PROXIMIDAD', 'Cerca', 'A 100m', 0, 'FLASK'), (9, 'PROMOCION_NUEVA', 'Promo', 'Nueva promo', 0, 'SPRING'), (10, 'PQRS_RESPUESTA', 'PQRS', 'Respondida', 1, 'SPRING'),
(11, 'SISTEMA', 'Bienvenido', 'Hola', 0, 'SPRING'), (12, 'RESERVA_CONFIRMADA', 'Reserva OK', 'Confirmada', 1, 'SPRING'), (13, 'ALERTA_PROXIMIDAD', 'Cerca', 'A 100m', 0, 'FLASK'), (14, 'PROMOCION_NUEVA', 'Promo', 'Nueva promo', 0, 'SPRING'), (15, 'PQRS_RESPUESTA', 'PQRS', 'Respondida', 1, 'SPRING'),
(16, 'SISTEMA', 'Bienvenido', 'Hola', 0, 'SPRING'), (17, 'RESERVA_CONFIRMADA', 'Reserva OK', 'Confirmada', 1, 'SPRING'), (18, 'ALERTA_PROXIMIDAD', 'Cerca', 'A 100m', 0, 'FLASK'), (19, 'PROMOCION_NUEVA', 'Promo', 'Nueva promo', 0, 'SPRING'), (20, 'PQRS_RESPUESTA', 'PQRS', 'Respondida', 1, 'SPRING'),
(21, 'SISTEMA', 'Bienvenido', 'Hola', 0, 'SPRING'), (22, 'RESERVA_CONFIRMADA', 'Reserva OK', 'Confirmada', 1, 'SPRING'), (23, 'ALERTA_PROXIMIDAD', 'Cerca', 'A 100m', 0, 'FLASK'), (24, 'PROMOCION_NUEVA', 'Promo', 'Nueva promo', 0, 'SPRING'), (25, 'PQRS_RESPUESTA', 'PQRS', 'Respondida', 1, 'SPRING'),
(26, 'SISTEMA', 'Bienvenido', 'Hola', 0, 'SPRING'), (27, 'RESERVA_CONFIRMADA', 'Reserva OK', 'Confirmada', 1, 'SPRING'), (28, 'ALERTA_PROXIMIDAD', 'Cerca', 'A 100m', 0, 'FLASK'), (29, 'PROMOCION_NUEVA', 'Promo', 'Nueva promo', 0, 'SPRING'), (30, 'PQRS_RESPUESTA', 'PQRS', 'Respondida', 1, 'SPRING'),
(31, 'SISTEMA', 'Bienvenido', 'Hola', 0, 'SPRING'), (32, 'RESERVA_CONFIRMADA', 'Reserva OK', 'Confirmada', 1, 'SPRING'), (33, 'ALERTA_PROXIMIDAD', 'Cerca', 'A 100m', 0, 'FLASK'), (34, 'PROMOCION_NUEVA', 'Promo', 'Nueva promo', 0, 'SPRING'), (35, 'PQRS_RESPUESTA', 'PQRS', 'Respondida', 1, 'SPRING'),
(36, 'SISTEMA', 'Bienvenido', 'Hola', 0, 'SPRING'), (37, 'RESERVA_CONFIRMADA', 'Reserva OK', 'Confirmada', 1, 'SPRING'), (38, 'ALERTA_PROXIMIDAD', 'Cerca', 'A 100m', 0, 'FLASK'), (39, 'PROMOCION_NUEVA', 'Promo', 'Nueva promo', 0, 'SPRING'), (40, 'PQRS_RESPUESTA', 'PQRS', 'Respondida', 1, 'SPRING'),
(41, 'SISTEMA', 'Bienvenido', 'Hola', 0, 'SPRING'), (42, 'RESERVA_CONFIRMADA', 'Reserva OK', 'Confirmada', 1, 'SPRING'), (43, 'ALERTA_PROXIMIDAD', 'Cerca', 'A 100m', 0, 'FLASK'), (44, 'PROMOCION_NUEVA', 'Promo', 'Nueva promo', 0, 'SPRING'), (45, 'PQRS_RESPUESTA', 'PQRS', 'Respondida', 1, 'SPRING'),
(46, 'SISTEMA', 'Bienvenido', 'Hola', 0, 'SPRING'), (47, 'RESERVA_CONFIRMADA', 'Reserva OK', 'Confirmada', 1, 'SPRING'), (48, 'ALERTA_PROXIMIDAD', 'Cerca', 'A 100m', 0, 'FLASK'), (49, 'PROMOCION_NUEVA', 'Promo', 'Nueva promo', 0, 'SPRING'), (50, 'PQRS_RESPUESTA', 'PQRS', 'Respondida', 1, 'SPRING');

-- -----------------------------------------------------
-- 12. config_alertas (50 registros)
-- -----------------------------------------------------
INSERT INTO config_alertas (usuario_id, radio_metros, notificar_promociones, notificar_eventos, activo) VALUES
(1, 500, 1, 1, 1), (2, 1000, 0, 1, 1), (3, 2000, 1, 0, 1), (4, 500, 1, 1, 1), (5, 1000, 0, 0, 0),
(6, 500, 1, 1, 1), (7, 1000, 0, 1, 1), (8, 2000, 1, 0, 1), (9, 500, 1, 1, 1), (10, 1000, 0, 0, 0),
(11, 500, 1, 1, 1), (12, 1000, 0, 1, 1), (13, 2000, 1, 0, 1), (14, 500, 1, 1, 1), (15, 1000, 0, 0, 0),
(16, 500, 1, 1, 1), (17, 1000, 0, 1, 1), (18, 2000, 1, 0, 1), (19, 500, 1, 1, 1), (20, 1000, 0, 0, 0),
(21, 500, 1, 1, 1), (22, 1000, 0, 1, 1), (23, 2000, 1, 0, 1), (24, 500, 1, 1, 1), (25, 1000, 0, 0, 0),
(26, 500, 1, 1, 1), (27, 1000, 0, 1, 1), (28, 2000, 1, 0, 1), (29, 500, 1, 1, 1), (30, 1000, 0, 0, 0),
(31, 500, 1, 1, 1), (32, 1000, 0, 1, 1), (33, 2000, 1, 0, 1), (34, 500, 1, 1, 1), (35, 1000, 0, 0, 0),
(36, 500, 1, 1, 1), (37, 1000, 0, 1, 1), (38, 2000, 1, 0, 1), (39, 500, 1, 1, 1), (40, 1000, 0, 0, 0),
(41, 500, 1, 1, 1), (42, 1000, 0, 1, 1), (43, 2000, 1, 0, 1), (44, 500, 1, 1, 1), (45, 1000, 0, 0, 0),
(46, 500, 1, 1, 1), (47, 1000, 0, 1, 1), (48, 2000, 1, 0, 1), (49, 500, 1, 1, 1), (50, 1000, 0, 0, 0);