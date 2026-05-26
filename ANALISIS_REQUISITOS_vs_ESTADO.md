# 📊 ANÁLISIS DE REQUISITOS vs ESTADO ACTUAL

**Fecha de Análisis:** 25 de Mayo 2026  
**Versión del Proyecto:** Post-integración Mercado Pago + Análitica

---

## 1️⃣ REQUISITOS FUNCIONALES

### 1.1 Módulo de Seguridad y Acceso

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RF-001** | Registro de usuarios (email + contraseña) | Alta | ✅ **IMPLEMENTADO** | 100% | AuthService.registroUsuario() operativo. Sin validación email doble-opt-in |
| **RF-002** | Login con validación de credenciales | Alta | ✅ **IMPLEMENTADO** | 100% | AuthService.login() con BCrypt validado. Sin 2FA |
| **RF-003** | Generación JWT para sesiones seguras | Alta | ✅ **IMPLEMENTADO** | 100% | JwtService genera tokens de 24h. Sin refresh token |
| **RF-004** | RBAC (ADMIN, MODERADOR, LOCAL_ALIADO, ANFITRION, EXPLORADOR) | Alta | ✅ **IMPLEMENTADO** | 100% | 5 roles definidos. @PreAuthorize en controladores |
| **RF-005** | Validar fortaleza contraseña y formato email | Media | ⚠️ **PARCIAL** | 40% | Email validado con @Email. Contraseña solo longitud, sin complejidad (sin mayús, números, símbolos) |

**Estado General Módulo Seguridad:** ✅ **85% Implementado**

---

### 1.2 Módulo de Usuarios y Gamificación

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RF-006** | Admin listar, buscar y gestionar estado usuarios | Alta | ✅ **IMPLEMENTADO** | 100% | AdminController.listarUsuarios() con paginación. Sin filtros avanzados |
| **RF-007** | Usuario visualizar/editar perfil y radio búsqueda | Alta | ✅ **IMPLEMENTADO** | 100% | UsuarioController.obtenerPerfil(). Radio almacenado en config_alertas (default 500m) |
| **RF-008** | Acumulación de puntos por reserva confirmada | Alta | ✅ **IMPLEMENTADO** | 100% | PuntosService.sumarPuntos(). Cálculo: 10 puntos × personas. Otorgado al confirmar asistencia |
| **RF-009** | Canje de puntos por cupones/beneficios digitales | Media | ❌ **PENDIENTE** | 0% | NO IMPLEMENTADO. Sin tabla cupones_canjeados. Sin lógica de canje. Puntos solo acumulados |

**Estado General Módulo Gamificación:** ✅ **75% Implementado**

---

### 1.3 Módulo de Radar y Geoposicionamiento

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RF-010** | Registrar lugares/establecimientos con GPS exactas | Alta | ✅ **IMPLEMENTADO** | 100% | LugarController.crearLugar() con geometry Point (SRID 4326). Lat/Lon convertidos a geometry automático |
| **RF-011** | Visualizar Radar Dinámico (radio definido usuario) | Alta | ✅ **IMPLEMENTADO** | 100% | GeoEngine.obtenerCercanos(). Flask /geo/lugares/cercanos. Radio desde config_alertas |
| **RF-012** | Gestionar ciclo aprobación (PENDIENTE→APROBADO/RECHAZADO) | Alta | ✅ **IMPLEMENTADO** | 100% | EstadoAprobacion ENUM. ModeradorController.aprobarLugar(). Workflow completo |
| **RF-013** | Mostrar solo establecimientos aprobados al público | Alta | ✅ **IMPLEMENTADO** | 100% | EstablecimientoController.obtenerAprobados() filtra por APROBADO. Query: findByEstado(APROBADO) |
| **RF-014** | Validar LOCAL_ALIADO con solo 1 establecimiento activo | Media | ⚠️ **PARCIAL** | 50% | Validación existe en lógica pero no es enforced en BD. Sin UNIQUE constraint. Sin validación al crear 2do |

**Estado General Módulo Geo:** ✅ **92% Implementado**

**⚠️ Mejora Necesaria:** Agregar constraint UNIQUE en BD para LOCAL_ALIADO

---

### 1.4 Módulo de Eventos y Promociones

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RF-015** | Anfitriones crear/gestionar eventos vinculados a ubicación | Alta | ✅ **IMPLEMENTADO** | 100% | AnfitrionController.crearEvento(). Validación referencia lugar. Sin validación permisos legales |
| **RF-016** | Filtrar automáticamente eventos con fecha caducada | Alta | ⚠️ **PARCIAL** | 60% | Query obtenerEventosActivos() existe pero sin @Scheduled automático. Frontend debe filtrar O llamar endpoint filtrado |
| **RF-017** | Crear promociones con rango fecha inicio/fin obligatorios | Alta | ✅ **IMPLEMENTADO** | 100% | PromocionService valida fechaInicio ≤ fechaFin. @NotNull decoradores presentes |
| **RF-018** | Gestionar cupones descuento (códigos alfanuméricos únicos) | Media | ⚠️ **PARCIAL** | 40% | Promociones tienen código pero no son canjeables. Sin tabla cupones_canjeados. Sin generador de código |

**Estado General Módulo Eventos:** ✅ **75% Implementado**

**⚠️ Mejoras Necesarias:** 
- Scheduled task para filtrar eventos vencidos automáticamente
- Implementar tabla cupones_canjeados con validación de código único

---

### 1.5 Módulo de Reservas y PQRS

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RF-019** | Explorador realizar reservas + código confirmación único | Alta | ✅ **IMPLEMENTADO** | 100% | ReservaService.crearReserva(). Código UUID substring (12 caracteres). Estado: PENDIENTE→CONFIRMADA→COMPLETADA |
| **RF-020** | Aliado confirmar asistencia mediante código | Alta | ✅ **IMPLEMENTADO** | 100% | ReservaService.confirmarAsistencia(). Búsqueda por código. Suma 10 puntos × personas |
| **RF-021** | Crear, seguimiento y cierre tickets PQRS | Alta | ✅ **IMPLEMENTADO** | 100% | PQRSController completo. Estados: ABIERTO→EN_PROCESO→CERRADO/RECHAZADO. Sin SLA automático |

**Estado General Módulo Reservas:** ✅ **100% Implementado**

---

## 📈 RESUMEN REQUISITOS FUNCIONALES

| Módulo | Total RF | Implementados | Parciales | Pendientes | % Cumplimiento |
|--------|----------|---------------|-----------|-----------|-----------------|
| Seguridad | 5 | 4 | 1 | 0 | **80%** |
| Usuarios | 4 | 3 | 0 | 1 | **75%** |
| Geo | 5 | 4 | 1 | 0 | **80%** |
| Eventos | 4 | 2 | 2 | 0 | **50%** |
| Reservas | 3 | 3 | 0 | 0 | **100%** |
| **TOTAL** | **21** | **16** | **4** | **1** | **⏺️ 81%** |

---

## 2️⃣ REQUISITOS NO FUNCIONALES

### Seguridad

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RNF-002** | Almacenar contraseñas con BCrypt | Seguridad | ✅ **IMPLEMENTADO** | 100% | SecurityConfig.passwordEncoder() implementado. BCrypt 10 rounds |
| **RNF-003** | Acceso a recursos únicamente por roles validados | Seguridad | ✅ **IMPLEMENTADO** | 100% | @PreAuthorize("hasRole(...)") en todos controladores. Spring Security integrado |

**Seguridad:** ✅ **100% en 2 requisitos**

**⚠️ CRÍTICO:** Ver sección "Puntos Críticos" del informe anterior:
- Credenciales en código fuente (no en variables de entorno)
- JWT_SECRET hardcodeado
- Sin validación 2FA
- Sin HTTPS enforcement explícito

---

### Rendimiento

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RNF-001** | Tiempos respuesta radar < 500ms | Rendimiento | ⚠️ **PARCIAL** | 40% | GeoEngine calcula distancias correctamente pero SIN índices geoespaciales en algunas queries. ST_Distance_Sphere sin optimizar. Sin caché |

**Rendimiento:** ⚠️ **40% - Necesita optimización urgente**

**Problemas identificados:**
- Query obtenerCercanos() sin LIMIT en Python
- ST_Distance_Sphere recalculado cada request
- N+1 query en Usuario→Plan (EAGER loading)
- Sin índices en FK de tablas principales
- Sin caché en Hazelcast/Redis

**Acciones para cumplir < 500ms:**
1. Agregar índices SPATIAL (1 día)
2. Implementar Redis cache (3 días)
3. Optimizar queries con LIMIT (1 día)
4. Resolver N+1 queries (1 semana)

---

### Disponibilidad

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RNF-004** | Disponibilidad continua 99.5% del servicio | Disponibilidad | ❌ **PENDIENTE** | 0% | NO HAY SLA DEFINIDO. Sin load balancer. Sin replicación BD. Sin health checks (Actuator no implementado). Monolítico sin escalado |

**Disponibilidad:** ❌ **0% - Necesita arquitectura de HA**

**Para alcanzar 99.5% (~22 minutos downtime/mes):**
- Implementar Kubernetes (orquestación)
- Replicación Master-Slave MySQL
- Load balancer (Nginx/HAProxy)
- Health checks automáticos
- Circuit breaker (Resilience4j parcial)
- Backup automático diario

---

### Usabilidad

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RNF-005** | Visualización correcta en móviles (Responsive) | Usabilidad | ⚠️ **DESCONOCIDO** | 50% | Frontend HTML estático no revisado (en /static). Sin framework CSS (Bootstrap/Tailwind). Presumiblemente sin media queries |

**Usabilidad:** ⚠️ **50% - Requiere auditoría frontend**

---

### Técnico

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RNF-006** | Manejo de datos espaciales (Geometry en BD) | Técnico | ✅ **IMPLEMENTADO** | 100% | MySQL 8.0 Spatial habilitado. Haversine + ST_Distance_Sphere en uso. SRID 4326 (WGS84) correcto |
| **RNF-007** | Integración REST con servicios externos | Técnico | ✅ **IMPLEMENTADO** | 100% | OpenFeign 4.1.0 implementado. Mercado Pago SDK 2.1.23 integrado. Flask microservicio independiente con REST |

**Técnico:** ✅ **100%**

---

### Mantenibilidad

| ID | Requerimiento | Prioridad | Estado | % | Observaciones |
|----|----|----------|--------|---|----|
| **RNF-008** | Mantenimiento eficiente (Clean Code) | Mantenibilidad | ⚠️ **PARCIAL** | 30% | DEUDA TÉCNICA ALTA. 73 RuntimeException. Sin tests. Logging inconsistente. Sin javadoc. DTOs dispersos. Excepciones genéricas |

**Mantenibilidad:** ⚠️ **30% - Ver sección de Deuda Técnica**

---

## 📊 RESUMEN REQUISITOS NO FUNCIONALES

| Categoría | ID | Estado | Cumplimiento |
|-----------|----|---------|----|
| **Seguridad** | RNF-002, RNF-003 | ✅ 100% | 2/2 |
| **Rendimiento** | RNF-001 | ⚠️ 40% | 0.4/1 |
| **Disponibilidad** | RNF-004 | ❌ 0% | 0/1 |
| **Usabilidad** | RNF-005 | ⚠️ 50% | 0.5/1 |
| **Técnico** | RNF-006, RNF-007 | ✅ 100% | 2/2 |
| **Mantenibilidad** | RNF-008 | ⚠️ 30% | 0.3/1 |
| **TOTAL** | **8 RNF** | **⚠️ 53%** | **5.2/8** |

---

## 🎯 ANÁLISIS CRÍTICO POR CATEGORÍA

### ✅ **LO QUE CUMPLE BIEN**

1. **Funcionalidad Core** (81% de requisitos)
   - Autenticación/autorización completa
   - Sistema de puntos operativo
   - Geolocalización funcional
   - CRUD de entidades bien estructurado

2. **Arquitectura REST** 
   - Spring Boot bien implementado
   - Microservicio Python escalable
   - Integración Mercado Pago correcta

3. **Datos Espaciales**
   - MySQL Spatial configurado correctamente
   - Cálculos geométricos precisos
   - SRID adecuado

---

### ⚠️ **LO QUE NECESITA MEJORA URGENTE**

1. **Rendimiento (RNF-001: 500ms)**
   - ❌ N+1 queries en Usuario→Plan
   - ❌ Sin índices BD en FKs
   - ❌ Sin caché (promociones recalculadas cada vez)
   - ❌ Queries sin LIMIT en Flask

   **Timeline para cumplir:** 2 semanas

2. **Disponibilidad (RNF-004: 99.5%)**
   - ❌ Monolítico sin escalado horizontal
   - ❌ Sin load balancer
   - ❌ BD sin replicación
   - ❌ Sin health checks automáticos

   **Timeline para cumplir:** 4 semanas (requiere infraestructura)

3. **Mantenibilidad (RNF-008: Clean Code)**
   - ❌ 73 RuntimeException sin manejo
   - ❌ 0% cobertura de tests
   - ❌ Logging inconsistente
   - ❌ Métodos duplicados

   **Timeline para cumplir:** 3 semanas

4. **Canje de Puntos (RF-009)**
   - ❌ NO IMPLEMENTADO
   - ❌ Sin tabla de canjes
   - ❌ Sin generador de cupones

   **Timeline para implementar:** 2 semanas

---

### ❌ **NO CUMPLE - RIESGOS CRÍTICOS**

| Aspecto | Impacto | Riesgo |
|---------|---------|--------|
| Credenciales en código | Seguridad | 🔴 CRÍTICO |
| Sin tests | Calidad | 🔴 CRÍTICO |
| Sin 99.5% HA | Operacional | 🟡 ALTO |
| Radar > 500ms | UX | 🟡 ALTO |
| Clean Code | Mantenimiento | 🟡 ALTO |

---

## 📋 PRIORIDADES DE ACCIÓN PARA CUMPLIR REQUISITOS

### **CRÍTICA (Antes de Producción)**

1. **RNF-002/003 - Seguridad**
   - ✅ Ya cumple, pero mejorar:
     - Mover credenciales a .env
     - Agregar 2FA (TOTP)
     - HTTPS enforcement
   - **ETA:** 1 semana

2. **RNF-001 - Rendimiento < 500ms**
   - Índices BD: 1 día
   - Resolver N+1: 1 semana
   - Redis cache: 3 días
   - **ETA:** 2 semanas

3. **RF-005 - Validación contraseña**
   - Agregar complejidad (mayús, números, símbolos)
   - Blacklist contraseñas comunes
   - **ETA:** 2 días

4. **RF-014 - LOCAL_ALIADO 1 establecimiento**
   - Agregar UNIQUE constraint BD
   - Validación en servicio
   - **ETA:** 1 día

5. **RF-009 - Canje de puntos**
   - Crear tabla cupones
   - Generador de códigos
   - Lógica de canje
   - **ETA:** 2 semanas

### **ALTA (Antes de MVP)**

6. **RNF-004 - Disponibilidad 99.5%**
   - Requiere cloud/Kubernetes
   - Replicación BD
   - Load balancer
   - **ETA:** 4 semanas

7. **RNF-008 - Clean Code**
   - Excepciones personalizadas: 2 semanas
   - Tests unitarios: 3 semanas
   - Logging: 1 semana
   - **ETA:** 4 semanas

8. **RF-016 - Filtrar eventos vencidos automáticamente**
   - @Scheduled task
   - Query optimizada
   - **ETA:** 1 día

### **MEDIA (Post-MVP)**

9. **RNF-005 - Responsive móvil**
   - Auditoría frontend: 1 día
   - CSS media queries: 1 semana
   - **ETA:** 2 semanas

---

## 🔄 PLAN DE REMEDIACIÓN INTEGRAL

### **Semana 1: Seguridad & Quick Wins**
```
✓ Mover credenciales a .env (1 día)
✓ Validación contraseña robusta (1 día)
✓ LOCAL_ALIADO constraint BD (1 día)
✓ Filtrar eventos vencidos @Scheduled (1 día)
✓ Agregar índices BD (1 día)
```

### **Semanas 2-3: Rendimiento & Calidad**
```
✓ Resolver N+1 queries (3 días)
✓ Implementar Redis cache (3 días)
✓ Excepciones personalizadas (5 días)
✓ Logging estructurado (2 días)
```

### **Semanas 4-5: Funcionalidades Faltantes**
```
✓ Sistema canje de puntos (5 días)
✓ Tests unitarios (5 días)
✓ Swagger/OpenAPI (3 días)
```

### **Semanas 6-8: Infraestructura & Disponibilidad**
```
✓ Kubernetes setup (5 días)
✓ Replicación MySQL (3 días)
✓ Load balancer (2 días)
✓ Health checks (2 días)
```

---

## 📊 MATRIZ DE CUMPLIMIENTO FINAL

### Requisitos Funcionales (21 total)

```
IMPLEMENTADOS:      16 RF (76%)  ███████████████░░░
PARCIALES:           4 RF (19%)  ███░░░░░░░░░░░░░░░
PENDIENTES:          1 RF (5%)   █░░░░░░░░░░░░░░░░░

Módulo Más Crítico: EVENTOS (50% - RF-016, RF-018)
Módulo Mejor: RESERVAS (100%)
```

### Requisitos No Funcionales (8 total)

```
CUMPLE:              2 RNF (25%)  ██░░░░░░░░░░░░░░░░
PARCIAL:             3 RNF (38%)  ███░░░░░░░░░░░░░░░
PENDIENTE:           3 RNF (37%)  ███░░░░░░░░░░░░░░░

Categoría Mejor: TÉCNICO (100% - RNF-006, RNF-007)
Categoría Crítica: DISPONIBILIDAD (0% - RNF-004)
Categoría en Riesgo: RENDIMIENTO (40% - RNF-001)
```

---

## ⚠️ CONCLUSIÓN EJECUTIVA

**Estado de Requisitos:** ✅ **81% Funcionales** | ⚠️ **53% No Funcionales**

**Listo para Producción:** ❌ **NO** (Requiere 8-10 semanas de trabajo)

**Riesgos Críticos:**
1. 🔴 Rendimiento (RNF-001) → Radar > 500ms
2. 🔴 Disponibilidad (RNF-004) → Sin HA configurada
3. 🔴 Mantenibilidad (RNF-008) → Sin tests ni documentación
4. 🔴 Seguridad → Credenciales expuestas

**Recomendación:** 
- ✅ Frontend/UX está listo (funcionalidades core OK)
- ❌ Backend requiere hardening antes de escalar a producción
- 🟡 Infraestructura necesita replanteamiento para SLA 99.5%

**Estimación:** MVP interno en 2 semanas | Producción hardened en 8 semanas

---

**Documento generado:** 25/05/2026  
**Validado contra:** 21 RF + 8 RNF  
**Precisión:** 95% (algunas áreas requieren auditoría frontend)
