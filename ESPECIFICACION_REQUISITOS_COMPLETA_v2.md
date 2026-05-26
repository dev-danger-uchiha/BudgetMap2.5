# 📋 ESPECIFICACIÓN DE REQUISITOS - BudgetMap v2.0
**Versión Actualizada con Nuevos Requisitos**  
**Fecha:** 25 de Mayo 2026  
**Estado:** Completo (30 Funcionales + 17 No Funcionales)

---

# ÍNDICE
1. [Requisitos Funcionales (30 RF)](#requisitos-funcionales)
2. [Requisitos No Funcionales (17 RNF)](#requisitos-no-funcionales)
3. [Matriz de Prioridades](#matriz-de-prioridades)
4. [Timeline Recomendado](#timeline-recomendado)
5. [Dependencias entre Requisitos](#dependencias-entre-requisitos)

---

# REQUISITOS FUNCIONALES

## 1.1 Módulo de Seguridad y Acceso (8 RF)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RF-001** | Registro de usuarios mediante correo electrónico y contraseña | Alta | ✅ Implementado | - |
| **RF-002** | Inicio de sesión mediante validación de credenciales | Alta | ✅ Implementado | - |
| **RF-003** | Generación de token JWT para manejo de sesiones seguras | Alta | ✅ Implementado | - |
| **RF-004** | Control de acceso basado en roles (ADMIN, MODERADOR, LOCAL_ALIADO, ANFITRION, EXPLORADOR) | Alta | ✅ Implementado | - |
| **RF-005** | Validación de fortaleza de contraseña y formato de correo | Media | ⚠️ Parcial | 2 días |
| **RF-022** | **[NUEVO]** Recuperación de contraseña olvidada mediante enlace por email | Alta | ❌ Pendiente | 1 semana |
| **RF-023** | **[NUEVO]** Logout con revocación de token JWT (blacklist) | Alta | ❌ Pendiente | 3 días |
| **RF-024** | **[NUEVO]** Bloqueo de cuenta tras 5 intentos fallidos en 15 minutos | Alta | ❌ Pendiente | 3 días |

### Detalles RF-022: Recuperación de Contraseña
```
FLUJO:
1. Usuario hace clic en "¿Olvidó su contraseña?"
2. Ingresa email → Email enviado con link válido 30 minutos
3. Link: /reset-password?token=xyz&email=user@example.com
4. Usuario ingresa nueva contraseña (validada con RF-005)
5. Contraseña actualizada → Automáticamente loguea
6. Email de confirmación enviado

CAMPOS BD:
- usuarios.password_reset_token (VARCHAR 255)
- usuarios.password_reset_expiry (TIMESTAMP)

SEGURIDAD:
- Token con cifrado aleatorio (UUID)
- Expiración: 30 minutos
- Una sola redención por token
- Email validado (case-insensitive)

ENDPOINT:
POST /api/auth/forgot-password
  { "email": "user@example.com" }
  Response: { "message": "Email enviado" }

POST /api/auth/reset-password
  { "token": "xyz", "newPassword": "NewPass@123" }
  Response: { "access_token": "...", "expires_in": 86400 }
```

### Detalles RF-023: Logout y Revocación
```
FLUJO:
1. Usuario hace clic en "Cerrar sesión"
2. Token agregado a blacklist (Redis)
3. Redirección a /login
4. Validación posterior rechaza token en blacklist

CAMPOS BD:
- jwt_blacklist (tabla o Redis)
  - token_jti (ID único del token)
  - usuario_id
  - fecha_revocacion
  - fecha_expiracion (limpiar automáticamente)

ENDPOINT:
POST /api/auth/logout
  Headers: Authorization: Bearer <token>
  Response: { "message": "Sesión cerrada exitosamente" }

VALIDACIÓN EN GATEWAY:
- Antes de procesar request, validar token contra blacklist
- Si en blacklist → Return 401 Unauthorized
```

### Detalles RF-024: Bloqueo por Intentos Fallidos
```
FLUJO:
1. Usuario intenta login con contraseña incorrecta
2. Contador incrementa (Redis o BD)
3. Si contador >= 5 en 15 minutos:
   - Cuenta bloqueada por 30 minutos
   - Email enviado: "Múltiples intentos de login detectados"
   - Admin notificado en dashboard
4. Usuario puede intentar después de 30 minutos

CAMPOS BD:
- usuarios.intentos_login_fallidos (INT, default 0)
- usuarios.fecha_bloqueo_temporal (TIMESTAMP)
- usuarios.locked_until (TIMESTAMP)

LÓGICA:
if (loginFallido) {
    intentos++;
    if (intentos >= 5) {
        locked_until = now() + 30 minutos;
        enviarEmail("Intento de acceso no autorizado");
        notificarAdmin();
    }
    if (intentos >= 10 en 24h) {
        solicitar verificación 2FA;
    }
}
if (loginExitoso) {
    intentos = 0;  // Reset contador
}

RESPONSE AL USUARIO:
- Intento 1-4: { "error": "Credenciales inválidas" }
- Intento 5+: { 
    "error": "Cuenta bloqueada temporalmente", 
    "retry_after": 1800,  // segundos
    "message": "Tu cuenta se desbloqueará en 30 minutos"
  }
```

---

## 1.2 Módulo de Usuarios y Gamificación (6 RF)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RF-006** | Admin listar, buscar y gestionar estado de usuarios | Alta | ✅ Implementado | - |
| **RF-007** | Usuario visualizar y editar perfil y radio de búsqueda preferido | Alta | ✅ Implementado | - |
| **RF-008** | Sistema permite acumulación de puntos por reserva confirmada | Alta | ✅ Implementado | - |
| **RF-009** | Canje de puntos acumulados por cupones o beneficios digitales | Media | ❌ Pendiente | 2 semanas |
| **RF-031** | **[NUEVO]** Historial de puntos con auditoría completa | Media | ❌ Pendiente | 3 días |
| **RF-032** | **[NUEVO]** Guardar búsquedas y filtros para acceso rápido | Baja | ❌ Pendiente | 3 días |

### Detalles RF-009: Canje de Puntos
```
ESTRUCTURA DE CUPONES:
Tabla: cupones
  - id (BIGINT PK)
  - codigo (VARCHAR 20, UNIQUE) - Ej: PARK2024ABC
  - usuario_id (BIGINT FK)
  - valor_descuento (DECIMAL 10,2) - En COP
  - tipo_descuento (ENUM: PORCENTAJE, MONTO_FIJO)
  - porcentaje (INT) - Si tipo_descuento = PORCENTAJE
  - establecimiento_id (BIGINT FK, nullable) - null = válido en todos
  - minimo_compra (DECIMAL 10,2) - Monto mínimo
  - estado (ENUM: ACTIVO, USADO, EXPIRADO)
  - puntos_requeridos (INT) - Puntos canjeados
  - fecha_creacion (TIMESTAMP)
  - fecha_expiracion (TIMESTAMP)
  - fecha_uso (TIMESTAMP, nullable)

FLUJO CANJE:
1. Usuario ve: "Tienes 150 puntos"
2. Click en "Canjear"
3. Lista de opciones:
   - 50 puntos → $5,000 descuento
   - 100 puntos → $12,000 descuento
   - 150 puntos → $18,000 descuento
4. Usuario selecciona opción
5. Validar: usuario.puntos >= puntos_requeridos
6. Generar código único: PARK2024ABC
7. Restar puntos: usuario.puntos -= 100
8. Crear cupón: estado = ACTIVO
9. Email: "Cupón generado: PARK2024ABC (válido 30 días)"
10. Usuario muestra cupón en establecimiento (QR o texto)

FLUJO USO CUPÓN:
1. Explorador llega a establecimiento
2. En checkout: "Tengo código de descuento"
3. Ingresa: PARK2024ABC
4. Sistema valida:
   - Código existe
   - No está usado
   - No está expirado
   - Establecimiento es válido
   - Monto mínimo cumplido
5. Aplica descuento automáticamente
6. Marca cupón: estado = USADO, fecha_uso = now()
7. Notificación al LOCAL_ALIADO: "Cupón usado: descuento $12,000"

ENDPOINTS:
GET /api/puntos/saldo
  Response: { "puntos": 150, "cupones_activos": 2 }

GET /api/cupones/opciones
  Response: [
    { "puntos": 50, "valor_descuento": 5000, "duracion_dias": 30 },
    { "puntos": 100, "valor_descuento": 12000, "duracion_dias": 30 },
    { "puntos": 150, "valor_descuento": 18000, "duracion_dias": 30 }
  ]

POST /api/cupones/canjear
  { "puntos": 100 }
  Response: { "codigo": "PARK2024ABC", "valor": 12000, "expira": "2024-06-25" }

GET /api/cupones/mis-cupones
  Response: [
    {
      "codigo": "PARK2024ABC",
      "valor": 12000,
      "estado": "ACTIVO",
      "fecha_expiracion": "2024-06-25"
    }
  ]

POST /api/reservas/aplicar-cupon
  { "codigo": "PARK2024ABC" }
  Response: { "descuento_aplicado": 12000, "total_final": 38000 }
```

### Detalles RF-031: Historial de Puntos
```
Tabla: historial_puntos
  - id (BIGINT PK)
  - usuario_id (BIGINT FK)
  - tipo (ENUM: GANADO, GASTADO, AJUSTE, REVERSO)
  - cantidad (INT) - Positivo o negativo
  - descripcion (VARCHAR 255) - "Reserva confirmada en XYZ", "Cupón canjeado"
  - referencia_id (BIGINT) - ID de reserva, cupón, etc.
  - referencia_tipo (VARCHAR 50) - "RESERVA", "CUPON", etc.
  - saldo_anterior (INT)
  - saldo_posterior (INT)
  - fecha (TIMESTAMP, default now())
  - admin_id (BIGINT, nullable) - Si es ajuste manual

ENDPOINT:
GET /api/puntos/historial?page=0&size=20&tipo=GANADO
Response: [
  {
    "fecha": "2024-05-20 14:30",
    "tipo": "GANADO",
    "cantidad": 30,
    "descripcion": "Reserva confirmada en Restaurante El Corral (3 personas)",
    "saldo_posterior": 180
  }
]
```

---

## 1.3 Módulo de Radar y Geoposicionamiento (6 RF)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RF-010** | Registrar lugares y establecimientos con coordenadas GPS exactas | Alta | ✅ Implementado | - |
| **RF-011** | Visualizar Radar Dinámico de sitios en radio definido por usuario | Alta | ✅ Implementado | - |
| **RF-012** | Gestionar ciclo de aprobación de nuevos establecimientos y lugares | Alta | ✅ Implementado | - |
| **RF-013** | Mostrar únicamente establecimientos aprobados al público general | Alta | ✅ Implementado | - |
| **RF-014** | Validar que LOCAL_ALIADO posea solo un establecimiento activo | Media | ⚠️ Parcial | 1 día |
| **RF-026** | **[NUEVO]** Búsqueda avanzada con múltiples filtros | Media | ❌ Pendiente | 1 semana |

### Detalles RF-014: Mejora de Validación LOCAL_ALIADO
```
CAMBIOS REQUERIDOS:

BD:
ALTER TABLE establecimientos 
  ADD CONSTRAINT unique_aliado_activo 
  UNIQUE (usuario_id, estado) 
  WHERE estado = 'APROBADO';

LÓGICA EN SERVICIO:
public void crearEstablecimiento(EstablecimientoRequest request, Usuario aliado) {
    // Validar que no tenga otro establecimiento APROBADO
    int activos = establecimientoRepository.countByUsuarioIdAndEstado(
        aliado.getId(), 
        EstadoAprobacion.APROBADO
    );
    
    if (activos > 0) {
        throw new InvalidOperationException(
            "Ya tienes un establecimiento activo. " +
            "Debes cerrar el anterior antes de crear uno nuevo."
        );
    }
    
    // Crear nuevo establecimiento (estado PENDIENTE)
    Establecimiento est = new Establecimiento();
    est.setUsuario(aliado);
    est.setEstado(EstadoAprobacion.PENDIENTE);
    // ...
}

ENDPOINT CON VALIDACIÓN:
POST /api/establecimientos
  { "nombre": "Café Colombiano", "ubicacion": {...} }
  
  Si aliado ya tiene uno activo:
  Response 409 Conflict: {
    "error": "Ya tienes un establecimiento activo",
    "establecimiento_actual": "Café Viejo",
    "opciones": [
      "Cerrar establecimiento actual",
      "Transferir a otro usuario"
    ]
  }
```

### Detalles RF-026: Búsqueda Avanzada
```
PARÁMETROS DE FILTRADO:
GET /api/establecimientos/busqueda?
  &categoria=RESTAURANTE,CAFE
  &precioMin=20000
  &precioMax=80000
  &abierto=true
  &calificacionMin=4.0
  &distanciaMax=5
  &servicios=WIFI,ESTACIONAMIENTO,PATIO
  &ordenar=distancia|calificacion|relevancia
  &page=0
  &size=20

ENUMS DISPONIBLES:
Categoría: RESTAURANTE, CAFE, BAR, PANADERIA, HELADERIA, PIZZERIA, SUSHI, VEGANO, CHINO
Servicios: WIFI, ESTACIONAMIENTO, PATIO, AIRE_ACONDICIONADO, MASCOTAS, ACCESIBLE
Ordenar: DISTANCIA, CALIFICACION, PRECIO, RELEVANCIA

RESPONSE:
{
  "total": 45,
  "page": 0,
  "size": 20,
  "establecimientos": [
    {
      "id": 123,
      "nombre": "Restaurante La Carne",
      "categoria": "RESTAURANTE",
      "calificacion": 4.5,
      "numero_reviews": 124,
      "distancia_km": 0.8,
      "rango_precios": "MEDIO",
      "precioPromedio": 45000,
      "servicios": ["WIFI", "ESTACIONAMIENTO"],
      "horario": {
        "abierto_ahora": true,
        "hora_apertura": "11:00",
        "hora_cierre": "22:00"
      },
      "promocion_activa": "20% descuento hoy",
      "ubicacion": { "lat": 4.72160, "lon": -74.04160 }
    }
  ]
}

ÍNDICES REQUERIDOS:
CREATE INDEX idx_categoria ON establecimientos(categoria);
CREATE INDEX idx_precio ON establecimientos(precio_promedio);
CREATE INDEX idx_ubicacion ON establecimientos(ubicacion SPATIAL);
CREATE INDEX idx_estado_categoria ON establecimientos(estado, categoria);
```

---

## 1.4 Módulo de Eventos y Promociones (6 RF)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RF-015** | Anfitriones crear y gestionar eventos vinculados a ubicación | Alta | ✅ Implementado | - |
| **RF-016** | Filtrar automáticamente y ocultar eventos cuya fecha haya caducado | Alta | ⚠️ Parcial | 1 día |
| **RF-017** | Crear promociones con rangos de fecha inicio y fin obligatorios | Alta | ✅ Implementado | - |
| **RF-018** | Gestionar cupones de descuento mediante códigos alfanuméricos únicos | Media | ⚠️ Parcial | Ver RF-009 |
| **RF-033** | **[NUEVO]** Guardar filtros de búsqueda para acceso rápido | Baja | ❌ Pendiente | 3 días |
| **RF-034** | **[NUEVO]** Programa de referrals con bonificación de puntos | Baja | ❌ Pendiente | 1 semana |

### Detalles RF-016: Filtrar Eventos Vencidos Automáticamente
```
MEJORA ACTUAL:
El endpoint obtenerEventosActivos() existe pero sin automatización.

CAMBIOS:
1. Agregar @Scheduled task que ejecute cada hora:

@Component
public class EventosScheduler {
    @Scheduled(fixedRate = 3600000) // Cada hora
    public void limpiarEventosVencidos() {
        List<Evento> vencidos = eventoRepository.findByFechaFinBefore(LocalDateTime.now());
        vencidos.forEach(e -> {
            e.setActivo(false);
            e.setMotivoCierre("FECHA_EXPIRADA");
            logger.info("Evento vencido archivado: {}", e.getId());
        });
        eventoRepository.saveAll(vencidos);
    }
}

2. Query optimizada:
@Query("SELECT e FROM Evento e WHERE e.activo = true AND e.fechaFin > CURRENT_TIMESTAMP")
List<Evento> findActivos();

3. Validación en creación:
if (fechaFin.isBefore(LocalDateTime.now().plusMinutes(30))) {
    throw new InvalidDateException("El evento debe estar vigente mínimo 30 minutos");
}
```

### Detalles RF-034: Programa de Referrals
```
TABLA NUEVA:
referrals (
  id BIGINT PK,
  usuario_referidor_id BIGINT FK,
  usuario_referido_id BIGINT FK,
  codigo_referral VARCHAR 20 UNIQUE, -- REFER5A4C
  puntos_bonus INT default 50,
  estado ENUM: PENDIENTE, COMPLETADO, CANCELADO,
  fecha_referencia TIMESTAMP,
  fecha_completacion TIMESTAMP,
  FOREIGN KEY (usuario_referidor_id) REFERENCES usuarios(id),
  FOREIGN KEY (usuario_referido_id) REFERENCES usuarios(id)
)

FLUJO:
1. Usuario A hace clic en "Referir amigo"
2. Genera código único: REFER5A4C (6-8 caracteres)
3. Comparte link: budgetmap.com/?ref=REFER5A4C
4. Usuario B se registra con link
5. Ambos ganan 50 puntos:
   - Usuario A: 50 puntos (referidor)
   - Usuario B: 50 puntos (nuevo usuario)
6. Estado = COMPLETADO

ENDPOINT:
GET /api/referrals/mi-codigo
Response: {
  "codigo": "REFER5A4C",
  "link_compartible": "https://budgetmap.com/?ref=REFER5A4C",
  "referidos_totales": 12,
  "referidos_completados": 8,
  "puntos_obtenidos": 400
}

POST /api/auth/registro?ref=REFER5A4C
{
  "email": "nuevo@example.com",
  "contraseña": "Pass@123"
}
Response: {
  "usuario": {...},
  "bonus": "50 puntos de bienvenida por referral"
}
```

---

## 1.5 Módulo de Reservas y PQRS (7 RF)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RF-019** | Explorador realizar reservas y generar código confirmación único | Alta | ✅ Implementado | - |
| **RF-020** | Aliado confirmar asistencia mediante código | Alta | ✅ Implementado | - |
| **RF-021** | Crear, seguimiento y cierre de tickets PQRS | Alta | ✅ Implementado | - |
| **RF-035** | **[NUEVO]** Búsqueda de historial de reservas con filtros avanzados | Media | ❌ Pendiente | 3 días |
| **RF-036** | **[NUEVO]** Cancelación de reservas con políticas y reembolso de puntos | Media | ❌ Pendiente | 1 semana |
| **RF-037** | **[NUEVO]** Notificación de confirmación y recordatorio 24h antes | Media | ❌ Pendiente | Ver RF-027 |
| **RF-038** | **[NUEVO]** Reseñas y ratings de establecimientos | Media | ❌ Pendiente | 1.5 semanas |

### Detalles RF-035: Búsqueda de Reservas
```
ENDPOINT:
GET /api/reservas/historial?
  &estado=COMPLETADA,CANCELADA
  &establecimiento=123
  &fechaDesde=2024-01-01
  &fechaHasta=2024-05-25
  &ordenar=fecha_desc
  &page=0
  &size=20

RESPONSE:
{
  "total": 45,
  "reservas": [
    {
      "id": 1001,
      "establecimiento": "Restaurante XYZ",
      "fecha": "2024-05-15 19:30",
      "personas": 4,
      "codigo": "ABC123DEF",
      "estado": "COMPLETADA",
      "puntos_ganados": 40,
      "tiene_resena": false,
      "puede_resenenar": true,
      "acciones": ["Ver detalles", "Escribir reseña"]
    }
  ]
}

FILTROS:
- Estado: PENDIENTE, CONFIRMADA, COMPLETADA, CANCELADA
- Rango de fechas
- Establecimiento específico
- Ordenar por: fecha, estado
```

### Detalles RF-036: Cancelación con Políticas
```
ESTRUCTURA:
politica_cancelacion (
  id BIGINT PK,
  dias_antes INT, -- Cancelación 24h antes = 1 día
  reembolso_porcentaje INT, -- 100 = reembolso total, 50 = mitad
  descripcion VARCHAR 255
)

Predefinidas:
- 7+ días antes: 100% puntos reembolsados
- 3-7 días antes: 75% puntos reembolsados
- 1-3 días antes: 50% puntos reembolsados
- < 1 día antes: 0% puntos reembolsados (no-show)

FLUJO CANCELACIÓN:
1. Usuario hace clic en "Cancelar" en reserva
2. Sistema calcula política según horas antes
3. Muestra: "Cancelar ahora: recuperarás 30 de 40 puntos"
4. Usuario confirma
5. Reserva → estado CANCELADA
6. Puntos reembolsados automáticamente
7. Notificación al LOCAL_ALIADO: "Reserva cancelada: XYZ"

ENDPOINT:
DELETE /api/reservas/123
Response: {
  "mensaje": "Reserva cancelada",
  "puntos_reembolsados": 30,
  "nueva_cantidad_puntos": 150
}
```

### Detalles RF-038: Reseñas y Ratings
```
TABLA NUEVA:
resenas (
  id BIGINT PK,
  usuario_id BIGINT FK,
  establecimiento_id BIGINT FK,
  calificacion INT (1-5),
  titulo VARCHAR 100,
  texto VARCHAR 500,
  fotos JSON (array de URLs),
  respuesta_aliado VARCHAR 500 nullable,
  estado ENUM: PENDIENTE_MODERACION, PUBLICADA, RECHAZADA,
  fecha_creacion TIMESTAMP,
  fecha_respuesta TIMESTAMP nullable,
  fecha_ultimaEdicion TIMESTAMP,
  UNIQUE(usuario_id, establecimiento_id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  FOREIGN KEY (establecimiento_id) REFERENCES establecimientos(id)
)

FLUJO:
1. Usuario completa reserva
2. 1 día después → Notificación: "¿Cómo fue tu experiencia?"
3. Click → Formulario:
   - Calificación (1-5 estrellas)
   - Título: "Excelente servicio"
   - Texto: Opinión (max 500 caracteres)
   - Fotos: Hasta 3 imágenes
4. Validación:
   - Min 10 caracteres en texto
   - Máximo 1 reseña por usuario/establecimiento
   - Solo después de completar reserva
5. Guardada con estado PENDIENTE_MODERACION
6. Admin moderador revisa (spam/ofensivo)
7. Si OK → estado PUBLICADA
8. Email al LOCAL_ALIADO: "Nueva reseña: 5 estrellas"

MODERACIÓN:
- Admin puede rechazar/editar
- Palabras clave prohibidas (spam, insultos)
- Validación de imágenes (no ofensivas)

RESPUESTA DEL ALIADO:
- LOCAL_ALIADO puede responder reseña
- Visible en perfil establecimiento
- Notificación al usuario: "El restaurante respondió tu reseña"

ENDPOINT:
POST /api/resenas
{
  "establecimiento_id": 123,
  "calificacion": 5,
  "titulo": "Excelente comida",
  "texto": "El servicio fue muy rápido y amable, la comida deliciosa.",
  "fotos": ["url_foto1", "url_foto2"]
}

GET /api/establecimientos/123/resenas?page=0&size=10&orden=reciente
Response: {
  "calificacion_promedio": 4.5,
  "numero_resenas": 24,
  "resenas": [
    {
      "autor": "Juan Pérez",
      "calificacion": 5,
      "fecha": "2024-05-20",
      "titulo": "Excelente comida",
      "texto": "El servicio fue muy rápido...",
      "fotos": ["url1", "url2"],
      "respuesta_aliado": "¡Gracias por tu confianza!",
      "ayudó": 8
    }
  ]
}
```

---

## 1.6 Módulo de Perfil y Cuenta de Usuario (3 RF - NUEVOS)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RF-025** | **[NUEVO]** Validación de email con double opt-in | Media | ❌ Pendiente | 2 días |
| **RF-029** | **[NUEVO]** Derecho al olvido - Eliminar cuenta y datos personales | Media | ❌ Pendiente | 3 días |
| **RF-030** | **[NUEVO]** Guardar establecimientos como favoritos | Baja | ❌ Pendiente | 3 días |

### Detalles RF-025: Email Verification
```
TABLA NUEVA:
email_verification (
  id BIGINT PK,
  usuario_id BIGINT FK,
  email VARCHAR 255,
  token VARCHAR 255 UNIQUE,
  estado ENUM: PENDIENTE, VERIFICADO, EXPIRADO,
  fecha_creacion TIMESTAMP,
  fecha_verificacion TIMESTAMP nullable,
  fecha_expiracion TIMESTAMP
)

CAMPOS USUARIO:
usuarios.email_verificado (BOOLEAN default false)
usuarios.fecha_verificacion_email (TIMESTAMP)

FLUJO REGISTRO:
1. Usuario registra email: user@example.com
2. Email enviado con link: /verify-email?token=xyz
3. Token válido 24 horas
4. Usuario hace clic → Email verificado
5. Si no verifica en 24h:
   - Email expirado
   - Opción: "Reenviar email de confirmación"

RESTRICCIÓN DE CUENTA:
Si email_verificado = false:
- Puede: Visualizar, búsqueda, ver detalles
- NO puede: Crear reserva, crear evento, recibir notificaciones

ENDPOINT:
POST /api/auth/registro
{
  "email": "user@example.com",
  "contraseña": "Pass@123"
}
Response: 201 Created
  "message": "Registro exitoso. Verifica tu email para activar cuenta"

GET /api/auth/verify-email?token=xyz
Response: 200
  "message": "Email verificado exitosamente. Puedes hacer login"

POST /api/auth/resend-verification-email
{
  "email": "user@example.com"
}
```

### Detalles RF-029: Eliminar Cuenta (GDPR)
```
FLUJO:
1. Usuario: Configuración → "Eliminar mi cuenta"
2. Advertencia: "Esto es permanente. Se eliminarán:"
   - Datos personales (nombre, email, teléfono)
   - Historial de reservas (anónimizado)
   - Reseñas (firmadas "Usuario Anónimo")
   - Puntos (se pierden)
   - Favoritos
3. Usuario confirma vía email (seguridad)
4. Cuenta → estado ELIMINADA_PENDIENTE
5. Período de espera: 30 días
   - Usuario puede hacer login (recuperar)
   - Datos no accesibles públicamente
6. Tras 30 días:
   - Datos anónimizados completamente
   - Estado: ELIMINADA_PERMANENTE
   - Email: "Tu cuenta ha sido eliminada permanentemente"

CAMPOS BD:
usuarios.estado (ENUM: ACTIVO, ELIMINADA_PENDIENTE, ELIMINADA_PERMANENTE)
usuarios.fecha_eliminacion_solicitada (TIMESTAMP)
usuarios.fecha_eliminacion_final (TIMESTAMP)

ANÓNIMIZACIÓN:
- nombre → "Usuario [ID]"
- email → null
- teléfono → null
- documento → null
- ubicación → null
- fotos → eliminadas
- reservas → usuario_id = null, usuario_nombre = "Usuario Anónimo"
- reseñas → usuario_id = null, autor = "Usuario Anónimo"
- puntos → 0

ENDPOINT:
POST /api/usuarios/solicitar-eliminacion
Response: {
  "message": "Solicitud enviada. Verifica tu email.",
  "periodo_espera_dias": 30
}

GET /api/usuarios/cancelar-eliminacion
Response: {
  "message": "Cuenta recuperada. Cuenta activa nuevamente."
}
```

### Detalles RF-030: Favoritos
```
TABLA NUEVA:
favoritos (
  usuario_id BIGINT,
  establecimiento_id BIGINT,
  fecha_creacion TIMESTAMP,
  PRIMARY KEY (usuario_id, establecimiento_id),
  FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
  FOREIGN KEY (establecimiento_id) REFERENCES establecimientos(id)
)

FUNCIONALIDAD:
- Click en corazón en perfil establecimiento → Guardar
- Vista "Mis Favoritos" con lista de establecimientos guardados
- Notificación: "Tu favorito tiene nueva promoción"
- Acceso rápido en radar

ENDPOINT:
POST /api/favoritos/123
{ }
Response: 201 Created
  "message": "Agregado a favoritos"

DELETE /api/favoritos/123
Response: 204 No Content

GET /api/favoritos?page=0&size=20
Response: {
  "total": 15,
  "favoritos": [
    {
      "id": 123,
      "nombre": "Restaurante XYZ",
      "calificacion": 4.5,
      "distancia_km": 2.3,
      "ubicacion": {...}
    }
  ]
}

GET /api/establecimientos/123/en-favoritos
Response: { "favorito": true }
```

---

## RESUMEN REQUISITOS FUNCIONALES

```
MÓDULO                          RF TOTAL    IMPLEMENTADOS    PENDIENTES
Seguridad y Acceso                8              4                4
Usuarios y Gamificación            6              3                3
Radar y Geo                        6              4                2
Eventos y Promociones              6              2                4
Reservas y PQRS                    7              3                4
Perfil y Cuenta (NUEVO)            3              0                3
TOTALES                           30             16               20
```

---

# REQUISITOS NO FUNCIONALES

## 2.1 Seguridad (5 RNF)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RNF-002** | Almacenamiento de contraseñas mediante cifrado BCrypt | Seguridad | ✅ Implementado | - |
| **RNF-003** | Acceso a recursos únicamente mediante validación de roles | Seguridad | ✅ Implementado | - |
| **RNF-009** | **[NUEVO]** Encriptación de datos sensibles (PII, tarjetas) | Seguridad | ❌ Pendiente | 1 semana |
| **RNF-010** | **[NUEVO]** HTTPS/TLS obligatorio con redirección automática | Seguridad | ❌ Pendiente | 3 días |
| **RNF-012** | **[NUEVO]** Cumplimiento OWASP Top 10 2023 | Seguridad | ⚠️ Parcial | 4 semanas |

### Detalles RNF-009: Encriptación de Datos Sensibles
```
CAMPOS A ENCRIPTAR:
- usuarios.telefono (PII)
- usuarios.numero_documento (PII)
- usuarios.direccion (PII)
- transacciones.referencia_tarjeta (PCI-DSS)
- reservas.notas_especiales (PII)

IMPLEMENTACIÓN:
Usar Hibernate Lifecycle Listeners:

@Entity
public class Usuario {
    @Encrypted
    private String telefono;
    
    @Encrypted
    private String numeroDocumento;
}

@Component
public class EncryptionListener {
    @PrePersist
    @PreUpdate
    public void encriptarCampos(Object entity) {
        // Encriptar campos marcados con @Encrypted usando AES-256
    }
    
    @PostLoad
    public void desencriptarCampos(Object entity) {
        // Desencriptar automáticamente en memoria
    }
}

CLAVE MAESTRA:
- Almacenada en variable de entorno: ENCRYPTION_KEY
- Rotación: anual mínimo
- Backup: almacenado en Key Management Service (AWS KMS)

CUMPLIMIENTO:
- PCI-DSS: Tarjetas encriptadas
- GDPR: Datos personales protegidos
- LOPA Colombia: PII asegurado
```

### Detalles RNF-010: HTTPS/TLS Enforcement
```
CONFIGURACIÓN:

application.properties:
server.ssl.enabled=true
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12

# Redirección HTTP → HTTPS
server.http2.enabled=true
server.ssl.protocol=TLSv1.2
server.ssl.enabled-protocols=TLSv1.2,TLSv1.3

HEADERS SEGURIDAD:
@Configuration
public class SecurityHeadersConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers()
                .contentSecurityPolicy("default-src 'self'")
                .and()
                .xssProtection()
                .and()
                .frameOptions().DENY
                .and()
                .httpStrictTransportSecurity()
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                    .preload(true);
        return http.build();
    }
}

CERTIFICADO SSL:
- Let's Encrypt (gratuito, renovación automática)
- Certificado válido por 90 días
- Renovación 30 días antes del vencimiento
- Comando: certbot renew --nginx

VALIDACIÓN:
- HTTPS obligatorio en producción
- HTTP redirige a HTTPS automáticamente:
  
@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .requiresChannel()
            .anyRequest()
            .requiresSecure();  // Força HTTPS
    return http.build();
}

TEST:
curl -I https://api.budgetmap.com  # Debe ser 200 OK
curl -I http://api.budgetmap.com   # Debe redirigir 301 a HTTPS
```

### Detalles RNF-012: OWASP Top 10 Compliance
```
A01 - BROKEN ACCESS CONTROL
✓ Implementado: @PreAuthorize valida roles
Mejoras:
  - Validar usuario_id en paths (no asumir)
  - Endpoint: POST /api/usuarios/{id}/roles
    - Validar: requester es ADMIN

A02 - CRYPTOGRAPHIC FAILURES
⚠️ Parcial: BCrypt implementado, falta HTTPS + encriptación en reposo
Acciones:
  ✓ RNF-010 (HTTPS)
  ✓ RNF-009 (Encriptación)

A03 - INJECTION
⚠️ Parcial: Usando Hibernate ORM (seguro de SQL Injection)
Mejoras:
  - Validar input en @RequestParam
  - Usar @Validated con @Valid
  - Ejemplo vulnerable a arreglar:
    @Query(value = "SELECT * FROM usuarios WHERE nombre LIKE :nombre", nativeQuery = true)
    // Cambiar a:
    @Query("SELECT u FROM Usuario u WHERE UPPER(u.nombre) LIKE UPPER(CONCAT('%', :nombre, '%'))")

A04 - INSECURE DESIGN
- Agregar threat modeling
- STRIDE analysis (Spoofing, Tampering, Repudiation, Information Disclosure, DoS, Elevation of Privilege)
- Security requirements en cada sprint

A05 - SECURITY MISCONFIGURATION
Acciones:
  - Variables de entorno para todos los secrets
  - Deshabilitar console.log en producción
  - spring.jpa.show-sql=false en producción
  - Remover stack traces de respuestas HTTP

A06 - VULNERABLE COMPONENTS
- Auditar dependencias: mvn dependency-check
- Actualizar Spring Boot a 3.2.0+ (parches de seguridad)
- Implementar renovación de dependencias automática

A07 - AUTHENTICATION FLAWS
✓ RNF-023 (Logout)
✓ RNF-024 (Bloqueo)
✓ RF-022 (Reset password)
Agregar:
  - 2FA TOTP (Google Authenticator)
  - Sesiones limitadas (no 24h indefinidos)
  - Cookies HttpOnly + Secure

A08 - SOFTWARE AND DATA INTEGRITY FAILURES
- Validar integridad de webhooks (Mercado Pago)
- Firmar requests con HMAC-SHA256
- Verificar Content-Type headers

A09 - LOGGING AND MONITORING FAILURES
✓ RNF-011 (Auditoría)
Agregar:
  - Alertas en tiempo real
  - Detección de anomalías

A10 - USING COMPONENTS WITH KNOWN VULNERABILITIES
- Implementar SBOM (Software Bill of Materials)
- Auditoría de dependencias mensual
```

---

## 2.2 Rendimiento (3 RNF)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RNF-001** | Tiempos de respuesta en radar inferiores a 500ms | Rendimiento | ⚠️ Parcial | 2 semanas |
| **RNF-017** | **[NUEVO]** Estrategia de caching distribuido (Redis) | Rendimiento | ❌ Pendiente | 1 semana |
| **RNF-018** | **[NUEVO]** Load testing y optimización de queries | Rendimiento | ❌ Pendiente | 1 semana |

### Detalles RNF-017: Caching Strategy
```
IMPLEMENTAR REDIS:

Dependencias:
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>

Configuración:
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.timeout=60000
spring.cache.type=redis

CACHÉ LAYER:

@Service
@EnableCaching
public class PromocionService {
    
    @Cacheable(
        value = "promociones_activas",
        unless = "#result.isEmpty()",
        cacheManager = "cacheManager"
    )
    public List<Promocion> obtenerActivas() {
        return promocionRepository.findByActivoAndFechaFinAfter(true, LocalDateTime.now());
    }
    
    @CacheEvict(value = "promociones_activas", allEntries = true)
    public Promocion crear(PromocionRequest request) {
        // ...
    }
}

QUÉ CACHEAR:
- Promociones activas (10 min TTL)
- Lugares aprobados (30 min TTL)
- Establecimientos aprobados (30 min TTL)
- Planes disponibles (1h TTL)
- Configuración global (12h TTL)

NO CACHEAR:
- Reservas del usuario (real-time)
- Puntos del usuario (real-time)
- Ubicación del usuario (real-time)
- Datos personales (seguridad)

MONITOREO:
redis-cli INFO stats
  - hit_ratio debe ser > 80%
  - memory_used < 1GB
```

### Detalles RNF-018: Load Testing
```
HERRAMIENTAS:
- JMeter: Tests de carga
- Gatling: Tests de estrés
- New Relic: Monitoreo en producción

TESTS RECOMENDADOS:

1. Radar cercano (frecuente):
   - 1000 usuarios simultáneos
   - Query: GET /api/geo/lugares/cercanos
   - Target: < 500ms p95
   - Load: 100 req/s

2. Búsqueda avanzada:
   - 500 usuarios
   - Query: GET /api/establecimientos/busqueda?...
   - Target: < 1000ms p95
   - Load: 50 req/s

3. Creación de reservas:
   - 100 usuarios (transaccional)
   - Query: POST /api/reservas
   - Target: < 2000ms
   - Load: 10 req/s

OPTIMIZACIONES POST-TEST:
- Agregar índices donde falta
- Implementar particionamiento si necesario
- Aumentar pool de conexiones BD
- Configurar read replicas
```

---

## 2.3 Disponibilidad (2 RNF - NUEVOS)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RNF-004** | Disponibilidad continua del 99.5% del servicio | Disponibilidad | ❌ Pendiente | 4 semanas |
| **RNF-019** | **[NUEVO]** Backup automático y Disaster Recovery | Disponibilidad | ❌ Pendiente | 1 semana |

### Detalles RNF-004: 99.5% Availability (HA Setup)
```
REQUISITOS PARA ALCANZAR 99.5%:
(Máximo 22 minutos de downtime/mes)

ARQUITECTURA:

┌─ Load Balancer (Nginx, AWS ELB)
│
├─ API Instance 1 (Spring Boot)
├─ API Instance 2 (Spring Boot)
└─ API Instance 3 (Spring Boot)
    │
    ├─ MySQL Master
    └─ MySQL Replica (read-only)
    │
    └─ Redis Cluster (3 nodos)

COMPONENTES:

1. Load Balancer:
   - Nginx/HAProxy con SSL termination
   - Health checks cada 10 segundos
   - Auto-remove instance si falla

2. Replicación MySQL:
   - Master-Slave replication (binlog)
   - Failover automático (MHA)
   - Backup diario (mysqldump)

3. Kubernetes (orquestación):
   - Orchestrate containers
   - Auto-scaling basado en CPU (50-80%)
   - Rolling updates sin downtime
   - Self-healing (reinicia pods fallidos)

CONFIGURACIÓN:

kubernetes/deployment.yaml:
apiVersion: apps/v1
kind: Deployment
metadata:
  name: budgetmap-api
spec:
  replicas: 3
  strategy:
    type: RollingUpdate
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 0  # Zero downtime deployment
  template:
    containers:
    - name: api
      livenessProbe:
        httpGet:
          path: /actuator/health/live
          port: 8080
        initialDelaySeconds: 30
        periodSeconds: 10
      readinessProbe:
        httpGet:
          path: /actuator/health/ready
          port: 8080
        initialDelaySeconds: 5
        periodSeconds: 5

MONITORING:
- Prometheus + Grafana
- Alertas: CPU > 80%, memory > 85%, error rate > 1%
- Latency tracking: p50, p95, p99
- Uptime SLA: Verificación horaria

SLA GUARANTEE:
- 99.5% uptime = 22 minutos downtime/mes
- Si no cumple: reembolso 10% de suscripción
```

### Detalles RNF-019: Backup y Disaster Recovery
```
ESTRATEGIA DE BACKUP:

Daily Backups:
- Hora: 02:00 AM (baja carga)
- Tipo: Mysqldump + xtrabackup
- Destino: AWS S3
- Retención: 30 días

Retención de Datos:
- Último 7 días: Diarios
- Último 30 días: Semanales
- Último 1 año: Mensuales
- 7 años: Archivado (legal/compliance)

PLAN DE RECUPERACIÓN:

RTO: Recovery Time Objective = 4 horas
RPO: Recovery Point Objective = 1 hora

Escenario: Fallo total de BD
1. Detectar problema (2 min)
2. Promover replica como master (5 min)
3. Restaurar data de backup si necesario (30 min)
4. Verificación (15 min)
5. Redirigir tráfico (5 min)
TOTAL: ~1 hora

Escenario: Fallo de aplicación
1. Health check detecta fallo (10 seg)
2. Load balancer remueve instancia (5 seg)
3. Kubernetes spawn nueva instancia (30 seg)
4. Nueva instancia lista para tráfico (1 min)
TOTAL: ~2 minutos

TEST MONTHLY:
- Simular fallo de BD
- Simular fallo de instancia
- Probar restauración desde backup
- Documentar tiempo real vs RTO/RPO

SCRIPT DE BACKUP:
#!/bin/bash
BACKUP_DIR="/backups/mysql"
DATE=$(date +%Y%m%d_%H%M%S)

# Backup
mysqldump -u root -p$DB_PASSWORD --all-databases \
  | gzip > $BACKUP_DIR/budgetmap_$DATE.sql.gz

# Upload to S3
aws s3 cp $BACKUP_DIR/budgetmap_$DATE.sql.gz \
  s3://budgetmap-backups/mysql/

# Cleanup local (keep 7 days)
find $BACKUP_DIR -name "*.gz" -mtime +7 -delete
```

---

## 2.4 Usabilidad (1 RNF)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RNF-005** | Visualización correcta en dispositivos móviles (Responsive) | Usabilidad | ⚠️ Desconocido | 1-2 sem |

### Detalles RNF-005: Mobile Responsive
```
AUDITORÍA REQUERIDA:
1. Pruebas en dispositivos reales:
   - iPhone 12, 14, 15
   - Samsung Galaxy S20, S22, S24
   - Tablets (iPad)

2. Validación de viewport:
   <meta name="viewport" content="width=device-width, initial-scale=1.0">

3. Breakpoints CSS:
   - Mobile: < 600px
   - Tablet: 600-1024px
   - Desktop: > 1024px

4. Elementos responsive:
   - Mapa debe ocupar 100% ancho
   - Botones mínimo 44x44px (accesibilidad)
   - Textos legibles (min 16px en mobile)
   - Touch-friendly (no hover obligatorio)

5. Performance móvil:
   - Tiempo carga: < 3 seg en 4G
   - Minificar CSS/JS
   - Imágenes optimizadas (webp)
   - Lazy loading de imágenes

TOOLS:
- Google Lighthouse (auditoría)
- PageSpeed Insights
- WebPageTest
- Browserstack (testing en devices)
```

---

## 2.5 Técnico (3 RNF)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RNF-006** | Manejo de datos espaciales en BD (Geometry) | Técnico | ✅ Implementado | - |
| **RNF-007** | Integración con servicios externos mediante REST | Técnico | ✅ Implementado | - |
| **RNF-020** | **[NUEVO]** Versionado de API para cambios sin ruptura | Técnico | ❌ Pendiente | 3 días |

### Detalles RNF-020: API Versioning
```
ESTRATEGIA: URL Path Versioning

ENDPOINTS:
GET /api/v1/establecimientos/123  (deprecated)
GET /api/v2/establecimientos/123  (actual)
GET /api/v3/establecimientos/123  (futura)

SOPORTE:
- Versión actual (v2): Full support
- Versión anterior (v1): Bug fixes only
- Versión anterior -1 (antes v1): Deprecated pero funcional

CAMBIOS EN V2 vs V1:
v1 Response:
{
  "id": 123,
  "nombre": "Restaurante XYZ",
  "ubicacion": {
    "latitud": 4.72,
    "longitud": -74.04
  }
}

v2 Response (mejorado):
{
  "id": 123,
  "nombre": "Restaurante XYZ",
  "ubicacion": {
    "lat": 4.72,
    "lon": -74.04,
    "direccion": "Calle 50 #10-15"
  },
  "rating": 4.5,
  "numero_resenas": 45
}

IMPLEMENTACIÓN:

@RestController
@RequestMapping("/api/v2/establecimientos")
public class EstablecimientoControllerV2 {
    // Nueva versión con campos adicionales
}

@RestController
@RequestMapping("/api/v1/establecimientos")
public class EstablecimientoControllerV1 {
    // Versión legacy, será deprecated
}

DEPRECATION HEADER:
// En respuesta de v1:
response.setHeader("Deprecation", "true");
response.setHeader("Sunset", "Sun, 25 May 2025 23:59:59 GMT");
response.setHeader("Link", "</api/v2/establecimientos>; rel=\"successor-version\"");

MIGRACIÓN:
- Documentar cambios en cada versión
- Proporcionar guía de migración
- Soporte mínimo 1 año para versión anterior
```

---

## 2.6 Mantenibilidad (3 RNF)

| ID | Requerimiento | Prioridad | Estado | Estimado |
|----|----|----------|--------|----------|
| **RNF-008** | Mantenimiento eficiente siguiendo estándares Clean Code | Mantenibilidad | ⚠️ Parcial | 4 semanas |
| **RNF-011** | **[NUEVO]** Sistema de auditoría y logging completo | Mantenibilidad | ❌ Pendiente | 2 semanas |
| **RNF-021** | **[NUEVO]** Documentación de API con Swagger/OpenAPI | Mantenibilidad | ❌ Pendiente | 3 días |

### Detalles RNF-011: Auditoría y Logging
```
TABLA DE AUDITORÍA:

CREATE TABLE auditoria (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  entidad VARCHAR(100) NOT NULL,
  entidad_id BIGINT NOT NULL,
  usuario_id BIGINT,
  accion ENUM('CREAR', 'ACTUALIZAR', 'ELIMINAR', 'CONSULTAR') NOT NULL,
  datos_anterior JSON,
  datos_nuevo JSON,
  motivo VARCHAR(255),
  ip_origen VARCHAR(45),
  user_agent VARCHAR(500),
  fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_entidad (entidad, entidad_id),
  INDEX idx_usuario (usuario_id),
  INDEX idx_fecha (fecha_cambio),
  INDEX idx_accion (accion)
);

EVENTOS AUDITADOS:

1. AUTENTICACIÓN:
   - Login exitoso: usuario, IP, dispositivo
   - Login fallido: email, IP, número intentos
   - Logout: usuario, timestamp
   - Cambio de contraseña: usuario, por quién
   - Reset de contraseña: usuario, token

2. APROBACIONES:
   - Crear establecimiento: usuario, datos
   - Aprobar establecimiento: moderador, ID, motivo
   - Rechazar establecimiento: moderador, ID, motivo de rechazo

3. TRANSACCIONES:
   - Crear reserva: usuario, establecimiento, personas
   - Cancelar reserva: usuario, motivo, reembolso
   - Procesar pago: transacción ID, monto, status
   - Reembolso: transacción ID, motivo

4. DATOS SENSIBLES:
   - Acceso a email de usuario
   - Acceso a teléfono de usuario
   - Acceso a documento de usuario

IMPLEMENTACIÓN CON ASPECT:

@Aspect
@Component
public class AuditingAspect {
    
    @Around("@annotation(com.budgetmap.annotation.Auditable)")
    public Object auditOperation(ProceedingJoinPoint pjp) throws Throwable {
        // Capturar parámetros ANTES
        Object[] args = pjp.getArgs();
        
        // Ejecutar método
        Object result = pjp.proceed();
        
        // Registrar en auditoría DESPUÉS
        registrarAuditoria(pjp.getSignature(), args, result);
        
        return result;
    }
    
    private void registrarAuditoria(...) {
        // Guardar en tabla auditoria con timestamp
    }
}

USO:
@Service
public class EstablecimientoService {
    
    @Auditable(entidad = "Establecimiento", accion = "CREAR")
    public Establecimiento crear(EstablecimientoRequest request, Usuario usuario) {
        // Lógica...
    }
}

LOGS ESTRUCTURADOS:

Usar SLF4J + Logback + JSON:

{
  "timestamp": "2024-05-25T10:30:45.123Z",
  "level": "INFO",
  "logger": "com.budgetmap.service.ReservaService",
  "message": "Reserva creada exitosamente",
  "usuario_id": 456,
  "reserva_id": 789,
  "establecimiento_id": 123,
  "numero_personas": 4,
  "duracion_ms": 245
}

RETENCIÓN:
- 1 año en BD operacional
- Archivado a S3 tras 1 año
- 7 años de retención (legal)
```

### Detalles RNF-021: Swagger/OpenAPI
```
SETUP:

pom.xml:
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.0.2</version>
</dependency>

application.properties:
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true

DOCUMENTACIÓN DE ENDPOINTS:

@RestController
@RequestMapping("/api/v2/reservas")
@Tag(name = "Reservas", description = "Gestión de reservas de usuarios")
public class ReservaController {
    
    @PostMapping
    @Operation(
        summary = "Crear nueva reserva",
        description = "Permite a un explorador crear una reserva en un establecimiento"
    )
    @ApiResponse(
        responseCode = "201",
        description = "Reserva creada exitosamente",
        content = @Content(schema = @Schema(implementation = ReservaDTO.class))
    )
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    @ApiResponse(responseCode = "409", description = "Aforo lleno")
    public ResponseEntity<?> crear(
        @Valid @RequestBody ReservaRequest request,
        @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Implementación...
    }
}

ACCESO:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs
- ReDoc: http://localhost:8080/webjars/redoc/index.html

BENEFICIOS:
- Documentación actualizada automáticamente
- Interfaz interactiva para probar endpoints
- Clientes pueden generar SDK
- Reducir tickets de "cómo usar API"
```

---

## RESUMEN REQUISITOS NO FUNCIONALES

```
CATEGORÍA                    RNF TOTAL    IMPLEMENTADOS    PARCIALES    PENDIENTES
Seguridad                       5              2                1              2
Rendimiento                     3              0                1              2
Disponibilidad                  2              0                0              2
Usabilidad                      1              0                1              0
Técnico                         3              2                0              1
Mantenibilidad                  3              0                0              3
TOTALES                        17              4                3             10
```

---

# MATRIZ DE PRIORIDADES

## Por Severidad y Timeline

```
🔴 CRÍTICA - Sprint 1 (1-2 semanas):
├─ RF-022 (Password reset) → 1 sem
├─ RF-023 (Logout) → 3 días
├─ RF-024 (Bloqueo intentos) → 3 días
├─ RF-025 (Email verify) → 2 días
├─ RNF-009 (Encriptación) → 1 sem
├─ RNF-010 (HTTPS) → 3 días
└─ RNF-012 (OWASP Top 10) → 4 sem

🟡 ALTA - Sprint 2-3 (3-4 semanas):
├─ RF-009 (Canje puntos) → 2 sem
├─ RF-026 (Búsqueda avanzada) → 1 sem
├─ RF-029 (Eliminar cuenta GDPR) → 3 días
├─ RF-038 (Reseñas) → 1.5 sem
├─ RNF-001 (Radar < 500ms) → 2 sem
├─ RNF-004 (99.5% HA) → 4 sem
├─ RNF-011 (Auditoría) → 2 sem
└─ RNF-019 (Backup/DR) → 1 sem

🟢 MEDIA - Sprint 4+ (2-3 semanas):
├─ RF-027 (Notificaciones) → 2 sem
├─ RF-030 (Favoritos) → 3 días
├─ RF-031 (Historial puntos) → 3 días
├─ RF-034 (Referral program) → 1 sem
├─ RF-035 (Búsqueda reservas) → 3 días
├─ RF-036 (Cancelación políticas) → 1 sem
├─ RNF-017 (Redis cache) → 1 sem
├─ RNF-018 (Load testing) → 1 sem
├─ RNF-020 (API versioning) → 3 días
└─ RNF-021 (Swagger) → 3 días

🔵 BAJA - Post-MVP:
├─ RF-032 (Filtros guardados) → 3 días
└─ RNF-005 (Mobile responsive) → 1-2 sem
```

---

# TIMELINE RECOMENDADO

## Fase 1: MVP Seguro (5-6 semanas)
```
Semana 1-2: Seguridad crítica
  ✓ RF-022, RF-023, RF-024, RF-025
  ✓ RNF-009, RNF-010
  ✓ Correcciones urgentes de OWASP A01, A02, A03

Semana 2-3: Funcionalidades core
  ✓ RF-009 (Canje puntos)
  ✓ RF-026 (Búsqueda avanzada)
  ✓ RF-029 (Eliminar cuenta GDPR)

Semana 3-4: Calidad y performance
  ✓ RNF-001 (Radar < 500ms)
  ✓ RNF-011 (Auditoría)
  ✓ Tests unitarios (mínimo 60%)

Semana 4-6: HA y operacional
  ✓ RNF-004 (99.5% HA)
  ✓ RNF-019 (Backup/DR)
  ✓ RNF-021 (Swagger)

TOTAL MVP: 5-6 semanas
STATUS: Listo para Producción Segura
```

## Fase 2: Feature Completeness (3-4 semanas)
```
Semana 7-8: UX mejorado
  ✓ RF-027 (Notificaciones)
  ✓ RF-038 (Reseñas)
  ✓ RF-030 (Favoritos)

Semana 8-9: Performance avanzado
  ✓ RNF-017 (Redis)
  ✓ RNF-018 (Load testing)
  ✓ RNF-020 (API versioning)

Semana 9-10: Polish
  ✓ RF-031, RF-034, RF-035, RF-036
  ✓ RNF-005 (Mobile responsive)
  ✓ Documentación completa

TOTAL FEATURES: 3-4 semanas
STATUS: Feature Complete
```

## Fase 3: Post-Launch (Ongoing)
```
- Monitoreo en producción
- Optimizaciones basadas en datos reales
- Soporte técnico
- Actualizaciones de seguridad
```

---

# DEPENDENCIAS ENTRE REQUISITOS

```
DEPENDENCIAS CRÍTICAS:

RF-022 ← RF-005  (Validación contraseña fuerte)
RF-023 ← RNF-010 (HTTPS para tokens seguros)
RF-024 ← RNF-011 (Logging de intentos fallidos)
RF-025 ← RNF-017 (Caching de emails verificados)
RF-029 ← RNF-011 (Auditoría de eliminación)

RNF-001 ← RNF-017 (Caching para performance)
RNF-004 ← RNF-019 (Backup para HA)
RNF-009 ← RNF-010 (Encriptación en tránsito + reposo)
RNF-012 ← RNF-009, RNF-010 (Encriptación para OWASP)

RF-027 (Notificaciones) ← RF-023 (Logout afecta FCM tokens)
RF-038 (Reseñas) ← RF-020 (Confirmar asistencia para reseñar)
```

---

**Documento Generado:** 25 de Mayo 2026  
**Versión:** 2.0 (Actualizado con 30 RF + 17 RNF)  
**Status:** Listo para implementación
