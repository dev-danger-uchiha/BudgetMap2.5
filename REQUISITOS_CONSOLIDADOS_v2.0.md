# REQUERIMIENTOS FUNCIONALES Y NO FUNCIONALES - BudgetMap v2.0

## 1.5.1 REQUERIMIENTOS FUNCIONALES

### 1.5.1.1 Módulo de Seguridad y Acceso

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RF-001 | El software permitirá el registro de nuevos usuarios mediante correo electrónico y contraseña. | Alta |
| RF-002 | El software permitirá el inicio de sesión mediante la validación de credenciales. | Alta |
| RF-003 | El software permitirá la generación de un token JWT para el manejo de sesiones seguras. | Alta |
| RF-004 | El software permitirá el control de acceso basado en roles (ADMIN, MODERADOR, LOCAL_ALIADO, ANFITRION, EXPLORADOR). | Alta |
| RF-005 | El software permitirá validar la fortaleza de la contraseña y el formato del correo ingresado. | Media |
| RF-022 | **[NUEVO]** El software permitirá la recuperación de contraseña olvidada mediante un enlace seguro por email con expiración de 30 minutos. | Alta |
| RF-023 | **[NUEVO]** El software permitirá el logout de usuarios con revocación de token JWT mediante blacklist. | Alta |
| RF-024 | **[NUEVO]** El software permitirá bloquear temporalmente una cuenta tras 5 intentos fallidos de login en 15 minutos, con notificación al usuario y administrador. | Alta |

---

### 1.5.1.2 Módulo de Usuarios y Gamificación

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RF-006 | El sistema permitirá al administrador listar, buscar y gestionar el estado de los usuarios. | Alta |
| RF-007 | El sistema permitirá al usuario visualizar y editar su perfil y radio de búsqueda preferido. | Alta |
| RF-008 | El sistema permitirá la acumulación de Tokens de puntos por cada reserva confirmada (10 puntos × número de personas). | Alta |
| RF-009 | El sistema permitirá el canje de puntos acumulados por cupones o beneficios digitales con código único y expiración. | Media |
| RF-031 | **[NUEVO]** El sistema permitirá visualizar el historial completo de puntos ganados y gastados con auditoría de transacciones. | Media |
| RF-032 | **[NUEVO]** El sistema permitirá guardar búsquedas y filtros personalizados para acceso rápido. | Baja |

---

### 1.5.1.3 Módulo de Radar y Geoposicionamiento

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RF-010 | El software permitirá registrar lugares y establecimientos con coordenadas GPS exactas (Geometry SRID 4326). | Alta |
| RF-011 | El software permitirá visualizar un Radar Dinámico de sitios en un radio definido por el usuario. | Alta |
| RF-012 | El software permitirá gestionar el ciclo de aprobación de nuevos establecimientos y lugares (PENDIENTE → APROBADO/RECHAZADO). | Alta |
| RF-013 | El software permitirá mostrar únicamente los establecimientos aprobados al público general. | Alta |
| RF-014 | El software permitirá validar que un LOCAL_ALIADO posea solo un establecimiento activo mediante constraint en base de datos. | Media |
| RF-026 | **[NUEVO]** El software permitirá búsqueda avanzada de establecimientos con múltiples filtros (categoría, precio, horario, calificación, servicios, distancia). | Media |

---

### 1.5.1.4 Módulo de Eventos y Promociones

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RF-015 | El sistema permitirá a los anfitriones crear y gestionar eventos vinculados a una ubicación teniendo en cuenta las políticas y permisos legales. | Alta |
| RF-016 | El sistema permitirá filtrar automáticamente y ocultar los eventos cuya fecha haya caducado mediante @Scheduled task. | Alta |
| RF-017 | El sistema permitirá crear promociones con rangos de fecha de inicio y fin obligatorios. | Alta |
| RF-018 | El sistema permitirá gestionar cupones de descuento mediante códigos alfanuméricos únicos y validación de uso. | Media |
| RF-033 | **[NUEVO]** El sistema permitirá guardar filtros de búsqueda personalizados para acceso rápido. | Baja |
| RF-034 | **[NUEVO]** El sistema permitirá un programa de referrals con bonificación de 50 puntos al usuario referidor y referido. | Baja |

---

### 1.5.1.5 Módulo de Reservas y PQRS

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RF-019 | El software permitirá al Explorador realizar reservas y generar un código de confirmación único (UUID). | Alta |
| RF-020 | El software permitirá al Aliado confirmar la asistencia del usuario mediante dicho código y otorgar puntos. | Alta |
| RF-021 | El software permitirá la creación, seguimiento y cierre de tickets de PQRS (Peticiones, Quejas, Reclamos, Sugerencias). | Alta |
| RF-035 | **[NUEVO]** El sistema permitirá búsqueda de historial de reservas con filtros avanzados (estado, establecimiento, fecha, ordenamiento). | Media |
| RF-036 | **[NUEVO]** El sistema permitirá cancelación de reservas con políticas de reembolso de puntos según días de anticipación (7+: 100%, 3-7: 75%, 1-3: 50%, <1: 0%). | Media |
| RF-037 | **[NUEVO]** El sistema permitirá notificación automática de confirmación de reserva y recordatorio 24 horas antes de la fecha. | Media |

---

### 1.5.1.6 Módulo de Perfil y Cuenta de Usuario (NUEVO)

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RF-025 | **[NUEVO]** El software permitirá validación de email mediante double opt-in con enlace de confirmación válido 24 horas. | Media |
| RF-029 | **[NUEVO]** El software permitirá la eliminación de cuenta (derecho al olvido GDPR) con período de espera de 30 días y anónimización de datos. | Media |
| RF-030 | **[NUEVO]** El software permitirá guardar establecimientos como favoritos con notificación de nuevas promociones. | Baja |

---

### 1.5.1.7 Módulo de Reseñas y Calificaciones (NUEVO)

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RF-038 | **[NUEVO]** El sistema permitirá a usuarios que completaron reservas escribir reseñas con calificación (1-5 estrellas), texto, fotos y respuesta del LOCAL_ALIADO. | Media |

---

## 1.5.2 REQUERIMIENTOS NO FUNCIONALES

### 1.5.2.1 Seguridad

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RNF-002 | El software permitirá el almacenamiento de contraseñas mediante el cifrado BCrypt. | Seguridad |
| RNF-003 | El software permitirá el acceso a recursos únicamente mediante la validación de roles. | Seguridad |
| RNF-009 | **[NUEVO]** El software permitirá la encriptación de datos sensibles en reposo (PII, tarjetas, documentos) mediante AES-256. | Seguridad |
| RNF-010 | **[NUEVO]** El software permitirá comunicación segura mediante HTTPS/TLS obligatorio con redirección automática de HTTP y headers de seguridad. | Seguridad |
| RNF-012 | **[NUEVO]** El software cumplirá con estándares OWASP Top 10 2023 incluyendo validación, inyección, autenticación y criptografía. | Seguridad |

---

### 1.5.2.2 Rendimiento

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RNF-001 | El software permitirá tiempos de respuesta en el radar inferiores a 500ms en p95. | Rendimiento |
| RNF-017 | **[NUEVO]** El software permitirá estrategia de caching distribuido con Redis para promociones, lugares y establecimientos (10-30 min TTL). | Rendimiento |
| RNF-018 | **[NUEVO]** El software permitirá validación de performance mediante load testing con herramientas como JMeter/Gatling en diferentes escenarios. | Rendimiento |

---

### 1.5.2.3 Disponibilidad

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RNF-004 | El software permitirá una disponibilidad continua del servicio del 99.5% (máximo 22 minutos downtime/mes) con load balancer, replicación MySQL y Kubernetes. | Disponibilidad |
| RNF-019 | **[NUEVO]** El software permitirá backup automático diario con retención de 30 días operacionales y 7 años archivado, con RTO 4 horas y RPO 1 hora. | Disponibilidad |

---

### 1.5.2.4 Usabilidad

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RNF-005 | El software permitirá una visualización correcta en dispositivos móviles (Responsive) con breakpoints para mobile (<600px), tablet (600-1024px) y desktop (>1024px). | Usabilidad |

---

### 1.5.2.5 Técnico

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RNF-006 | El software permitirá el manejo de datos espaciales en la base de datos (Geometry con SRID 4326). | Técnico |
| RNF-007 | El software permitirá la integración con servicios externos mediante una arquitectura REST (OpenFeign, Mercado Pago SDK). | Técnico |
| RNF-020 | **[NUEVO]** El software permitirá versionado de API para cambios sin ruptura de clientes (/api/v1, /api/v2) con soporte mínimo 1 año por versión. | Técnico |

---

### 1.5.2.6 Mantenibilidad

| ID | Requerimiento | Prioridad |
|----|----|----------|
| RNF-008 | El software permitirá un mantenimiento eficiente siguiendo estándares de Clean Code, con excepciones personalizadas, logging estructurado y tests unitarios (60%+ cobertura). | Mantenibilidad |
| RNF-011 | **[NUEVO]** El software permitirá sistema de auditoría completo registrando login, cambios de datos, aprobaciones, transacciones con retención 1 año operacional + 7 años archivado. | Mantenibilidad |
| RNF-021 | **[NUEVO]** El software permitirá documentación automática de API mediante Swagger/OpenAPI con interfaz interactiva en /swagger-ui.html. | Mantenibilidad |

---

## RESUMEN GENERAL

### Requisitos Funcionales
- **Total RF:** 30 (incluye 9 nuevos)
- **Implementados:** 16 (53%)
- **Parciales:** 4 (13%)
- **Pendientes:** 10 (33%)

### Requisitos No Funcionales
- **Total RNF:** 17 (incluye 9 nuevos)
- **Implementados:** 4 (24%)
- **Parciales:** 3 (18%)
- **Pendientes:** 10 (59%)

### Cumplimiento Global
- **RF Cumplidas:** 81% (16/20 originales)
- **RNF Cumplidas:** 53% (4/8 originales)
- **Nuevos Requisitos Agregados:** 18 (9 RF + 9 RNF)

---

## TIMELINE ESTIMADO

| Fase | Duración | RF | RNF | Status |
|------|----------|----|----|--------|
| **Fase 1: MVP Seguro** | 5-6 semanas | 15 | 12 | 🟡 Producción Base |
| **Fase 2: Completo** | +3-4 semanas | 30 | 17 | ✅ Producción Robusta |
| **TOTAL** | **8-10 semanas** | **30** | **17** | **✅ READY** |

