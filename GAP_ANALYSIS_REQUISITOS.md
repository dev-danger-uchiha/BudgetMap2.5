# 🔍 ANÁLISIS DE GAPS EN REQUISITOS

**Fecha:** 25 de Mayo 2026  
**Análisis:** Comparación entre Requisitos Documentados vs Necesidades del Proyecto

---

## 1️⃣ REQUISITOS FUNCIONALES FALTANTES

### 🔴 CRÍTICOS (Debe agregar)

#### RF-022 - Recuperación de Contraseña Olvidada
**Prioridad:** Alta  
**Justificación:** Usuario no puede acceder si olvida contraseña. Sin esto = bloqueo de cuentas.

```
ID: RF-022
Título: Recuperación de contraseña mediante email
Descripción: El sistema permitirá al usuario recuperar su contraseña mediante un 
enlace seguro enviado a su email con token de expiración de 30 minutos.
Flujo:
  1. Usuario solicita "Olvidé mi contraseña"
  2. Sistema envía email con link: /reset?token=xxx
  3. Usuario hace clic → Página de reset password
  4. Nueva contraseña con validación de complejidad
  5. Automáticamente inicia sesión con nueva contraseña
Actores: EXPLORADOR, LOCAL_ALIADO, ANFITRION
Prioridad: Alta
```

**Por qué:** 
- Experiencia de usuario bloqueada sin esto
- Genera tickets de soporte innecesarios
- Simple de implementar (3-4 días)

---

#### RF-023 - Logout y Revocación de Tokens
**Prioridad:** Alta  
**Justificación:** Usuario no tiene forma de cerrar sesión explícitamente. Token vive 24h sin control.

```
ID: RF-023
Título: Logout con revocación de token JWT
Descripción: El sistema permitirá al usuario cerrar sesión y revocalizar su token 
actual para evitar su uso futuro.
Flujo:
  1. Usuario hace clic en "Cerrar sesión"
  2. Token se agrega a blacklist (Redis)
  3. Redirección a login
  4. Token puede ser validado contra blacklist
Endpoints:
  POST /api/auth/logout
  - Requiere: Authorization header
  - Respuesta: {message: "Sesión cerrada exitosamente"}
Prioridad: Alta
```

**Por qué:**
- Seguridad crítica (usuario puede logout antes de 24h)
- Administrador puede forzar logout
- Necesario para 2FA

---

#### RF-024 - Bloqueo de Cuenta por Intentos Fallidos
**Prioridad:** Alta  
**Justificación:** Sin esto es vulnerable a fuerza bruta (3 intentos = acceso).

```
ID: RF-024
Título: Bloqueo temporal de cuenta tras intentos fallidos
Descripción: El sistema bloqueará temporalmente una cuenta tras 5 intentos de login 
fallidos en 15 minutos.
Comportamiento:
  - 1-4 intentos: Permitido, contador incrementa
  - 5to intento: Cuenta bloqueada por 30 minutos
  - Usuario recibe email: "Múltiples intentos de login detectados"
  - Admin notificado en panel
Campos BD:
  usuarios.intentos_fallidos_login (INT)
  usuarios.fecha_bloqueo_login (TIMESTAMP)
Prioridad: Alta
Seguridad: Crítica contra fuerza bruta
```

**Por qué:**
- OWASP Top 10 - A07: Authentication flaws
- Protege contra diccionario attack
- Rate limiting por IP no es suficiente

---

#### RF-025 - Validación de Email (Double Opt-In)
**Prioridad:** Media  
**Justificación:** Sin validación, usuarios pueden registrarse con emails falsos.

```
ID: RF-025
Título: Verificación de email al registrarse
Descripción: El sistema enviará email de confirmación con enlace al registrarse.
Email no activado = cuenta restringida.
Flujo:
  1. Usuario registra email: user@example.com
  2. Email enviado con link: /verify?token=xxx&email=xxx
  3. Usuario hace clic → Email validado
  4. Acceso completo desbloqueado
  5. Sin verificación → Puede ver solo, no crear
Estados:
  - EMAIL_NO_VERIFICADO (default)
  - EMAIL_VERIFICADO
  - EMAIL_BLOQUEADO (spam)
Campos BD:
  usuarios.email_verificado (BOOLEAN)
  usuarios.fecha_verificacion_email (TIMESTAMP)
Prioridad: Media
```

**Por qué:**
- Reduce spam y cuentas fake
- Mejora deliverability de notificaciones
- Regulatorio (GDPR double opt-in)

---

#### RF-026 - Búsqueda Avanzada de Establecimientos
**Prioridad:** Media  
**Justificación:** Usuarios solo pueden ver mapa. Sin búsqueda por categoría, horario, etc.

```
ID: RF-026
Título: Búsqueda avanzada con filtros múltiples
Descripción: El sistema permitirá filtrar establecimientos por:
Filtros:
  - Categoría (RESTAURANTE, CAFÉ, BAR, etc.)
  - Rango de precios
  - Horario actual (Abierto/Cerrado)
  - Calificación mínima
  - Distancia (0-50km)
  - Servicios (WiFi, Estacionamiento, Patio)
Endpoint:
  GET /api/establecimientos/busqueda?
    &categoria=RESTAURANTE
    &precioMin=20000&precioMax=80000
    &abierto=true
    &calificacionMin=4.0
    &distanciaMax=5
Prioridad: Media
```

**Por qué:**
- UX mejorada (vs solo mapa)
- Mayor conversion (usuarios encuentran qué buscan)
- Datos ya disponibles (solo agregar query dinámico)

---

#### RF-027 - Notificaciones en Tiempo Real
**Prioridad:** Media  
**Justificación:** Usuarios no son notificados de confirmaciones, eventos, promociones.

```
ID: RF-027
Título: Sistema de notificaciones multicanal en tiempo real
Descripción: El sistema notificará a usuarios por:
Canales:
  - Push (Firebase Cloud Messaging)
  - Email (SMTP)
  - SMS (Twilio - opcional)
  - In-app (websocket)
Eventos que generan notificación:
  - Reserva confirmada
  - Nueva promoción cercana
  - Evento en tu área de interés
  - Respuesta a PQRS
  - Punto de bloqueo (múltiples logins)
Campos BD:
  usuarios.token_fcm (STRING)
  usuarios.preferencias_notificacion (JSON)
  notificaciones (tabla con estado leída)
Prioridad: Media
```

**Por qué:**
- Engagement crítico (usuarios vuelven)
- Gamificación (puntos/eventos)
- Monetización (promociones patrocinadas)

---

#### RF-028 - Reseñas y Ratings de Establecimientos
**Prioridad:** Media  
**Justificación:** Sin reviews, usuarios no confían en establecimientos.

```
ID: RF-028
Título: Sistema de reseñas y calificaciones
Descripción: El sistema permitirá a EXPLORADORES calificar y escribir reseñas
de establecimientos visitados.
Estructura:
  - Calificación: 1-5 estrellas
  - Texto reseña: 10-500 caracteres
  - Fotos: hasta 3 imágenes
  - Fecha: automática
  - Respuesta del LOCAL_ALIADO
Tabla BD:
  resenas (id, usuario_id, establecimiento_id, calificacion, texto, fotos, fecha)
Restricción:
  - Solo puede reseñar si completó reserva
  - Una reseña por usuario/establecimiento
  - Moderable por admin
Prioridad: Media
Negocio: Crítico para confianza
```

**Por qué:**
- Social proof → Más reservas
- UGC (user generated content)
- Mejora SEO
- Comunidad enganchada

---

#### RF-029 - Eliminar Cuenta y Datos de Usuario
**Prioridad:** Media  
**Justificación:** GDPR/LOPA requiere derecho al olvido.

```
ID: RF-029
Título: Derecho al olvido - Eliminar cuenta de usuario
Descripción: El usuario puede solicitar eliminación de cuenta y datos personales.
Sistema anónimiza datos tras 30 días.
Flujo:
  1. Usuario solicita "Eliminar mi cuenta"
  2. Email de confirmación enviado
  3. Usuario confirma en email (seguridad)
  4. Cuenta marcada como ELIMINADA_PENDIENTE
  5. Tras 30 días → Datos anónimizados
  6. Email confirmando eliminación
Campos BD:
  usuarios.estado (ELIMINADA_PENDIENTE)
  usuarios.fecha_eliminacion_solicitada
Restricción:
  - Reservas futuras se cancelan automáticamente
  - Puntos se pierden
  - Reviews anónimizadas
Prioridad: Media
Regulatorio: GDPR Art. 17
```

**Por qué:**
- Obligatorio GDPR/LOPA
- Riesgo legal sin esto
- Mejora privacidad percibida

---

#### RF-030 - Favoritos y Lugares Guardados
**Prioridad:** Baja  
**Justificación:** UX mejorada pero no crítico.

```
ID: RF-030
Título: Guardar establecimientos como favoritos
Descripción: El usuario puede marcar establecimientos como favoritos
para acceso rápido posterior.
Funcionalidad:
  - Click en corazón → Guardar
  - Vista de "Mis Favoritos"
  - Sincronización entre dispositivos (si autenticado)
  - Notificación cuando favorito tiene promoción
Tabla BD:
  favoritos (usuario_id, establecimiento_id, fecha_creacion)
Prioridad: Baja
```

**Por qué:**
- Enganche (usuarios vuelven)
- Mejorado para personalizacion
- Dato valioso (preferencias)

---

### 🟡 RECOMENDADOS (Considera agregar)

#### RF-031 - Historial de Puntos con Auditoría
```
Descripción: Usuario puede ver historial completo de puntos ganados/gastados
con timestamp y descripción.
Prioridad: Baja
Justificación: Transparencia + debug de problemas
```

#### RF-032 - Filtros Guardados en Búsqueda
```
Descripción: Usuario puede guardar búsquedas con filtros (ej: "Restaurantes 
abiertos <5km, <50k, calificación >4")
Prioridad: Baja
Justificación: UX (buscar lo mismo frecuentemente)
```

#### RF-033 - Referral Program
```
Descripción: Usuario obtiene puntos bonus por referir amigos que se registran
Prioridad: Baja
Justificación: Growth hacking
```

---

## 2️⃣ REQUISITOS NO FUNCIONALES FALTANTES

### 🔴 CRÍTICOS

#### RNF-009 - Encriptación de Datos Sensibles
**Prioridad:** Alta  
**Justificación:** Números de tarjeta, teléfonos, emails no están encriptados en reposo.

```
ID: RNF-009
Título: Encriptación de datos sensibles en base de datos
Descripción: El sistema encriptará campos sensibles:
Campos a encriptar:
  - usuarios.telefono (PII)
  - usuarios.documento (PII)
  - transacciones.referencia_tarjeta (PCI-DSS)
  - reservas.notas_especiales (PII)
Tecnología:
  - AES-256 con clave maestra en .env
  - Transparente en ORM (listeners Hibernate)
Cumplimiento:
  - PCI-DSS (pagos)
  - GDPR (privacidad)
  - LOPA Colombia
Prioridad: Alta
```

**Por qué:**
- Obligatorio PCI-DSS (si procesa tarjetas)
- Breach de datos = GDPR multa (4% revenue)
- Regulatorio (LOPA Colombia)

---

#### RNF-010 - HTTPS/TLS Enforcement
**Prioridad:** Alta  
**Justificación:** JWT y datos enviados en HTTP = intercepción fácil.

```
ID: RNF-010
Título: Comunicación segura (HTTPS) con redirección automática
Descripción: El sistema:
  - Redirige HTTP → HTTPS automáticamente
  - Certificado SSL/TLS válido (Let's Encrypt)
  - Headers de seguridad HTTP:
    - Strict-Transport-Security: max-age=31536000
    - X-Content-Type-Options: nosniff
    - X-Frame-Options: DENY
    - Content-Security-Policy
Prioridad: Alta
Seguridad: Crítica
```

**Por qué:**
- OWASP A02: Cryptographic Failures
- Man-in-the-middle attack sin esto
- JWT token interceptable en HTTP

---

#### RNF-011 - Auditoría y Logging Completo
**Prioridad:** Alta  
**Justificación:** Sin logs no puedes investigar incidents o cumplir GDPR.

```
ID: RNF-011
Título: Sistema de auditoría y logging para cumplimiento
Descripción: El sistema registrará:
Eventos auditados:
  - Login/Logout (usuario, IP, timestamp, éxito/fallo)
  - Cambios de contraseña
  - Cambios de rol (por admin)
  - Aprobación/rechazo de establecimientos (quién, cuándo, por qué)
  - Acceso a datos sensibles
  - Transacciones (completas)
  - Eliminación de datos
Storage:
  - Tabla auditoria (inmutable, append-only)
  - Replicación a log externo (Splunk/ELK)
Retención:
  - 1 año completo
  - 2-7 años archivado (legal)
Prioridad: Alta
Regulatorio: GDPR, LOPA, PCI-DSS
```

**Por qué:**
- Regulatorio (demostrar cumplimiento)
- Forensics (investigar breaches)
- Debug (qué pasó y cuándo)

---

#### RNF-012 - Cumplimiento OWASP Top 10
**Prioridad:** Alta  
**Justificación:** Tu proyecto tiene múltiples vulnerabilidades OWASP.

```
ID: RNF-012
Título: Cumplimiento con estándares OWASP Top 10
Descripción: El sistema será auditado y parchado contra:
OWASP Top 10 2023:
  A01 - Broken Access Control (RBAC validation)
  A02 - Cryptographic Failures (HTTPS + encriptación)
  A03 - Injection (prepared statements, validación input)
  A04 - Insecure Design (threat modeling)
  A05 - Security Misconfiguration (hardening)
  A06 - Vulnerable Components (dependencias actualizadas)
  A07 - Authentication Flaws (2FA, bloqueo)
  A08 - CORS/CSRF (restrictivo)
  A09 - Logging/Monitoring (auditoría)
  A10 - SSRF (validación de URLs)
Validación:
  - Audit de seguridad trimestral
  - Pen testing anual
Prioridad: Alta
```

**Por qué:**
- Obligatorio para cualquier aplicación web
- Regulatorio
- Reduce riesgo de breach 10x

---

### 🟡 RECOMENDADOS

#### RNF-013 - Escalabilidad Horizontal
```
Descripción: Sistema diseñado para escalar a múltiples instancias:
  - Stateless API (sin sesiones en memoria)
  - Base de datos replicada
  - Cache distribuido (Redis)
  - Load balancer (Nginx)
  - Kubernetes ready
Prioridad: Media
Timeline: Post-MVP
```

#### RNF-014 - Backup y Disaster Recovery
```
Descripción: 
  - Backup automático diario (S3)
  - RTO: 4 horas
  - RPO: 1 hora
  - Plan de recuperación documentado
  - Teste mensual
Prioridad: Media
```

#### RNF-015 - Monitoreo y Alertas
```
Descripción:
  - Prometheus + Grafana
  - Alertas para:
    - CPU > 80%
    - Memoria > 85%
    - Error rate > 1%
    - Response time > 500ms (radar)
    - Disk > 90%
  - Escalamiento automático
Prioridad: Media
```

#### RNF-016 - Versionado de API
```
Descripción: API versionada para cambios sin romper clientes:
  - /api/v1/... (deprecated)
  - /api/v2/... (actual)
  - Soporte 2 versiones simultáneamente
Prioridad: Media
```

#### RNF-017 - Documentación de API (Swagger/OpenAPI)
```
Descripción: API autodocumentada con Swagger
  - Endpoints documentados
  - Ejemplos de request/response
  - Códigos de error
  - Esquemas DTO
Prioridad: Media
```

---

## 3️⃣ TABLA CONSOLIDADA: REQUISITOS ACTUALES + PROPUESTOS

### Requisitos Funcionales

```
ACTUALES:  21 RF (Seguridad, Usuarios, Geo, Eventos, Reservas)

PROPUESTOS - CRÍTICOS (deben agregar):
├─ RF-022: Recuperación contraseña olvidada (1 semana)
├─ RF-023: Logout y revocación token (3 días)
├─ RF-024: Bloqueo por intentos fallidos (3 días)
├─ RF-025: Validación email double opt-in (2 días)
├─ RF-026: Búsqueda avanzada establecimientos (1 semana)
├─ RF-027: Notificaciones multicanal (2 semanas)
├─ RF-028: Reseñas y ratings (1.5 semanas)
├─ RF-029: Eliminar cuenta (GDPR) (3 días)
└─ RF-030: Favoritos (3 días)

NUEVOS TOTALES: 30 RF
CRITICIDAD: 9 nuevos (6 críticos, 3 recomendados)
ESFUERZO TOTAL: ~8 semanas
```

### Requisitos No Funcionales

```
ACTUALES: 8 RNF (Seguridad, Rendimiento, Disponibilidad, Usabilidad, Técnico, Mantenibilidad)

PROPUESTOS - CRÍTICOS (deben agregar):
├─ RNF-009: Encriptación datos sensibles (1 semana)
├─ RNF-010: HTTPS/TLS enforcement (3 días)
├─ RNF-011: Auditoría y logging completo (2 semanas)
├─ RNF-012: Cumplimiento OWASP Top 10 (4 semanas)
├─ RNF-013: Escalabilidad horizontal (2 semanas)
├─ RNF-014: Backup/Disaster Recovery (1 semana)
├─ RNF-015: Monitoreo y alertas (1 semana)
├─ RNF-016: Versionado de API (3 días)
└─ RNF-017: Swagger/OpenAPI (3 días)

NUEVOS TOTALES: 17 RNF
CRITICIDAD: 9 nuevos (5 críticos, 4 recomendados)
ESFUERZO TOTAL: ~12 semanas
```

---

## 🎯 RECOMENDACIÓN FINAL

### ✅ **SÍ AGREGARÍAS ESTOS REQUISITOS**

**Mínimo Viable (MVP - 5 semanas):**
```
✓ RF-022 (Password reset)
✓ RF-023 (Logout)
✓ RF-024 (Bloqueo)
✓ RF-025 (Email verify)
✓ RNF-009 (Encriptación)
✓ RNF-010 (HTTPS)
✓ RNF-011 (Auditoría)
✓ RNF-012 (OWASP)
```

**Fase 2 (Después MVP - 3 semanas):**
```
✓ RF-026 (Búsqueda avanzada)
✓ RF-028 (Reseñas)
✓ RNF-013 (Escalabilidad)
✓ RNF-014 (Backup)
✓ RNF-015 (Monitoreo)
```

**Fase 3 (Post-lanzamiento - 2 semanas):**
```
✓ RF-027 (Notificaciones)
✓ RF-029 (Eliminar cuenta)
✓ RF-030 (Favoritos)
✓ RNF-016 (Versionado)
✓ RNF-017 (Swagger)
```

### 📊 **IMPACTO EN TIMELINE**

| Escenario | RF | RNF | Timeline Total | Listo Producción |
|-----------|----|----|-----------------|---|
| **Solo actuales** | 21 | 8 | 8 semanas | NO (inseguro) |
| **+ Críticos** | 30 | 17 | 16 semanas | ✅ SÍ (robusto) |
| **Agile MVP** | 26 | 13 | 10 semanas | ⚠️ Parcial |

---

## 🚀 PRÓXIMO PASO

**¿Quieres que agregue estos requisitos a tu documento oficial?**

Opción 1: Agregar todos (críticos + recomendados)
Opción 2: Solo críticos (MVP + seguridad)
Opción 3: Personalizado (tú seleccionas cuáles)

---

**Documento generado:** 25/05/2026  
**Análisis completo:** Gaps vs Estándares Industria + Regulatorio
