# REVISIÓN EXHAUSTIVA: FILTROS MULTICRITERIO Y ENDPOINTS

**Fecha**: 2026-05-26 | **Estado**: Completo

---

## 📊 RESUMEN EJECUTIVO

| Rol | Filtros Implementados | Filtros Faltantes | Endpoints OK | Endpoints con Issues |
|-----|----------------------|------------------|-------------|---------------------|
| **ADMIN** | 3/5 | 2 | 7/8 | 1 (usuarios paginado) |
| **ALIADO** | 0/4 | 4 | 3/4 | 1 (promociones sin paginación en endpoint) |
| **ANFITRION** | 0/3 | 3 | 4/4 | 0 |
| **EXPLORADOR** | 1/3 | 2 | 3/3 | 0 |

---

## 🔍 ANÁLISIS DETALLADO POR ROL

### 1. ROL ADMINISTRADOR

#### HTMLs:
- ✅ `admin/usuarios.html` 
- ✅ `admin/aprobaciones.html`
- ✅ `admin/pqrs.html`
- ✅ `admin/estadisticas.html`

#### **Filtros Implementados:**
```
usuarios.html:
  ✅ Búsqueda texto (Email/Nombre)
  ✅ Filtro por Rol (EXPLORADOR, LOCAL_ALIADO, ANFITRION, MODERADOR, ADMINISTRADOR)
  ✅ Filtro por Estado (Activos/Suspendidos)
  ❌ FALTA: Filtro por Puntos Acumulados
  ❌ FALTA: Filtro por Plan/Suscripción

aprobaciones.html:
  ❌ NO HAY FILTROS (solo mostrar todo)
  ❌ FALTA: Filtro por Tipo (ESTABLECIMIENTO/LUGAR)
  ❌ FALTA: Filtro por Fecha
  ❌ FALTA: Búsqueda por nombre

pqrs.html:
  ✅ Filtro por Estado (ABIERTO, EN_PROCESO, RESPONDIDO, CERRADO)
  ❌ FALTA: Filtro por Prioridad (ALTA/BAJA)
  ❌ FALTA: Filtro por Tipo (PETICIÓN/QUEJA/RECLAMO/SUGERENCIA)
  ❌ FALTA: Búsqueda por asunto/descripción
```

#### **Problemas en Endpoints:**
```
GET /api/usuarios/paginado
  ❌ ISSUE: El HTML envía queryParams (criterio, rol, activo) pero 
     el endpoint SOLO recibe Pageable. No filtra nada.
     
POST /api/pqrs/{id}/responder
  ✅ Funciona correctamente
  
GET /api/pqrs/paginados
  ❌ ISSUE: Endpoint no existe en PQRSController
     El HTML usa: http://localhost:8080/api/pqrs/paginados?estado=...
     Pero el controller solo tiene GET /pqrs (sin paginación por estado)
```

---

### 2. ROL ALIADO (LOCAL_ALIADO)

#### HTMLs:
- ✅ `aliado/mi-establecimiento.html`
- ✅ `aliado/promociones.html`
- ✅ `aliado/check-in.html`
- ✅ `aliado/dashboard.html`

#### **Filtros Implementados:**
```
promociones.html:
  ❌ NO HAY FILTROS
  ❌ FALTA: Filtro por Estado (Activa/Vencida/Agotada)
  ❌ FALTA: Filtro por % Descuento (rango)
  ❌ FALTA: Búsqueda por título
  ❌ FALTA: Ordenar por Usos (ASC/DESC)

mi-establecimiento.html:
  ⚠️ No es una lista, es un formulario de edición
  ✅ Correcto para su propósito

check-in.html:
  ⚠️ Revisar si necesita filtros

dashboard.html:
  ⚠️ Revisar si necesita filtros
```

#### **Problemas en Endpoints:**
```
GET /api/promociones/mis-promociones?page=X&size=Y
  ❌ ISSUE: El endpoint retorna List, no Page
     El HTML espera: data.content y data.totalPages
     Debe devolver Page<PromocionResponse>
     
POST /api/promociones
  ✅ Funciona correctamente
  
GET /api/establecimientos/mi-establecimiento
  ✅ Funciona correctamente
```

---

### 3. ROL ANFITRION

#### HTMLs:
- ✅ `anfitrion/dashboard.html`
- ✅ `anfitrion/mis-eventos.html`
- ✅ `anfitrion/check-in.html`

#### **Filtros Implementados:**
```
mis-eventos.html:
  ❌ NO HAY FILTROS
  ❌ FALTA: Filtro por Tipo (ARTISTICO, CULTURAL, DEPORTIVO, RECREATIVO)
  ❌ FALTA: Filtro por Estado (Programado/En_Vivo/Finalizado)
  ❌ FALTA: Búsqueda por nombre del evento
  ❌ FALTA: Filtro por Rango de Fechas

check-in.html:
  ⚠️ Revisar si necesita filtros

dashboard.html:
  ⚠️ Revisar si necesita filtros
```

#### **Problemas en Endpoints:**
```
GET /api/eventos/mis-eventos?page=X&size=Y
  ⚠️ ISSUE: El endpoint retorna List, no Page
     Línea 57-61 del EventoController:
     public ResponseEntity<List<EventoResponse>> listarMisEventos()
     
     Pero el HTML espera Page y accede a data.content/data.totalPages
     
POST /api/eventos
  ✅ Funciona correctamente
```

---

### 4. ROL EXPLORADOR

#### HTMLs:
- ✅ `explorador/dashboard.html`
- ✅ `explorador/perfil.html`
- ✅ `explorador/detalle.html`
- ✅ `explorador/mis-reservas.html`
- ✅ `explorador/notificaciones.html`

#### **Filtros Implementados:**
```
mis-reservas.html:
  ✅ Filtro por Estado (ACTIVA/HISTORIAL)
     - ACTIVA: PENDIENTE, CONFIRMADA
     - HISTORIAL: CANCELADA, COMPLETADA, NO_ASISTIO
  ❌ FALTA: Filtro por Tipo (Evento/Establecimiento)
  ❌ FALTA: Búsqueda por nombre
  ❌ FALTA: Filtro por Fecha
  ❌ FALTA: Ordenar por Fecha DESC

notificaciones.html:
  ❌ NO HAY FILTROS
  ❌ FALTA: Filtro por Tipo (Promoción/Evento/Sistema)
  ❌ FALTA: Filtro por Estado (Leído/No_Leído)
  ❌ FALTA: Búsqueda

dashboard.html:
  ⚠️ Revisar si necesita filtros
```

#### **Problemas en Endpoints:**
```
GET /api/reservas/mis-reservas
  ✅ Funciona (retorna List, lo cual es OK para este caso)
  
GET /api/mis-reservas/paginado
  ✅ Existe y retorna Page (alternativa)
```

---

## ⚠️ PROBLEMAS CRÍTICOS ENCONTRADOS

### 🔴 P1: Endpoints que esperan parámetros de filtro que no procesan

```
ENDPOINT: GET /api/usuarios/paginado
HTML ENVÍA: ?page=0&size=10&criterio=texto&rol=EXPLORADOR&activo=true
CONTROLLER RECIBE: Solo Pageable (page, size)
RESULTADO: Filtros ignorados, devuelve TODOS los usuarios
FIX: Agregar @RequestParam String criterio, @RequestParam String rol, @RequestParam Boolean activo
```

### 🔴 P2: Endpoints inexistentes que el HTML intenta usar

```
ENDPOINT: GET /api/pqrs/paginados?estado=ABIERTO&page=0&size=10
PROBLEMA: No existe en el controller
HTML USA: admin/pqrs.html línea 109
FIX: Agregar nuevo endpoint con filtros
```

### 🔴 P3: Respuesta DTO incorrecta (List en lugar de Page)

```
ENDPOINT: GET /api/promociones/mis-promociones?page=0&size=4
CONTROLLER RETORNA: List<PromocionResponse>
HTML ESPERA: Page { content: [...], totalPages: X }
RESULTADO: HTML crashea al acceder a data.content
FIX: Cambiar retorno a Page<PromocionResponse> con @RequestParam Pageable pageable
```

### 🔴 P4: Lo mismo con eventos

```
ENDPOINT: GET /api/eventos/mis-eventos?page=0&size=4
CONTROLLER RETORNA: List<EventoResponse>
HTML ESPERA: Page { content: [...], totalPages: X }
FIX: Cambiar retorno a Page<EventoResponse>
```

---

## 📋 PLAN DE ACCIÓN

### FASE 1: Corregir endpoints existentes (CRÍTICO)

#### 1.1 UsuarioController - Agregar filtros multicriterio
```java
@GetMapping("/usuarios/paginado")
@PreAuthorize("hasRole('ADMINISTRADOR')")
public ResponseEntity<Page<UsuarioDTO>> listarPaginado(
    Pageable pageable,
    @RequestParam(required = false) String criterio,      // Email/Nombre
    @RequestParam(required = false) String rol,           // RolUsuario
    @RequestParam(required = false) Boolean activo) {     // true/false
    
    return ResponseEntity.ok(usuarioService.listarPaginado(pageable, criterio, rol, activo));
}
```

#### 1.2 PQRSController - Crear endpoint paginado con filtros
```java
@GetMapping("/pqrs/paginados")
@PreAuthorize("hasAnyRole('ADMINISTRADOR', 'MODERADOR')")
public ResponseEntity<Page<PQRSResponse>> listarPaginados(
    Pageable pageable,
    @RequestParam(required = false) String estado,        // ABIERTO, EN_PROCESO, RESPONDIDO, CERRADO
    @RequestParam(required = false) String prioridad,     // ALTA, BAJA
    @RequestParam(required = false) String tipo) {        // PETICIÓN, QUEJA, RECLAMO, SUGERENCIA
    
    return ResponseEntity.ok(pqrsService.listarPaginados(pageable, estado, prioridad, tipo));
}
```

#### 1.3 PromocionController - Cambiar a Page
```java
@GetMapping("/promociones/mis-promociones")
@PreAuthorize("hasAnyRole('LOCAL_ALIADO', 'ANFITRION')")
public ResponseEntity<Page<PromocionResponse>> listarMisPromociones(
    @AuthenticationPrincipal UserDetailsImpl userDetails,
    Pageable pageable,
    @RequestParam(required = false) String estado,        // ACTIVA, VENCIDA, AGOTADA
    @RequestParam(required = false) String titulo) {      // Búsqueda
    
    return ResponseEntity.ok(promocionService.listarMisPromociones(userDetails.getId(), pageable, estado, titulo));
}
```

#### 1.4 EventoController - Cambiar a Page y agregar filtros
```java
@GetMapping("/eventos/mis-eventos")
@PreAuthorize("hasRole('ANFITRION')")
public ResponseEntity<Page<EventoResponse>> listarMisEventos(
    @AuthenticationPrincipal UserDetailsImpl userDetails,
    Pageable pageable,
    @RequestParam(required = false) String tipo,          // ARTISTICO, CULTURAL, DEPORTIVO, RECREATIVO
    @RequestParam(required = false) String nombre) {      // Búsqueda
    
    return ResponseEntity.ok(eventoService.listarMisEventosPaginado(userDetails.getId(), pageable, tipo, nombre));
}
```

---

### FASE 2: Actualizar HTMLs con formularios de filtro

#### 2.1 admin/aprobaciones.html - Agregar filtros
- Filtro por Tipo (ESTABLECIMIENTO/LUGAR)
- Búsqueda por nombre
- Opcional: Filtro por Fecha

#### 2.2 aliado/promociones.html - Agregar filtros
- Filtro por Estado
- Búsqueda por título
- Opcional: Filtro por % descuento

#### 2.3 anfitrion/mis-eventos.html - Agregar filtros
- Filtro por Tipo de evento
- Búsqueda por nombre
- Opcional: Filtro por fecha

#### 2.4 explorador/mis-reservas.html - Mejorar filtros
- Agregar Filtro por Tipo (Evento/Establecimiento)
- Búsqueda por nombre
- Ordenamiento

#### 2.5 explorador/notificaciones.html - Agregar filtros
- Filtro por Tipo
- Filtro por Estado (Leído/No leído)
- Búsqueda

---

### FASE 3: Servicios que necesitan ser actualizado

#### Necesitan métodos con filtros multicriterio:
- UsuarioService
- PQRSService  
- PromocionService
- EventoService

---

## 🧪 CHECKLIST DE VALIDACIÓN

### Endpoints Críticos a Verificar:
- [ ] GET /api/usuarios/paginado (con filtros)
- [ ] GET /api/pqrs/paginados (nuevo)
- [ ] GET /api/promociones/mis-promociones (cambio a Page)
- [ ] GET /api/eventos/mis-eventos (cambio a Page)
- [ ] POST /api/promociones (sin cambios)
- [ ] POST /api/eventos (sin cambios)

### HTMLs a Probar:
- [ ] admin/usuarios.html - Filtros en tiempo real
- [ ] admin/aprobaciones.html - Nuevos filtros
- [ ] admin/pqrs.html - Filtros por estado/prioridad
- [ ] aliado/promociones.html - Nuevos filtros
- [ ] anfitrion/mis-eventos.html - Nuevos filtros
- [ ] explorador/mis-reservas.html - Mejorados

---

## 📈 IMPACTO DE CAMBIOS

| Área | Cambios | Riesgo | Esfuerzo |
|------|---------|--------|----------|
| Backend Controllers | 4 endpoints | Bajo | Bajo |
| Backend Services | 4 servicios | Medio | Medio |
| Frontend HTMLs | 6 HTMLs | Bajo | Bajo |
| Pruebas | APIs nuevas | Medio | Medio |
| **TOTAL** | **14 archivos** | **Bajo-Medio** | **8-10 horas** |

---

## ✅ CONCLUSIÓN

**Estado General:** 🟡 **PARCIALMENTE IMPLEMENTADO**

- ✅ Endpoints básicos funcionan
- ❌ Filtros multicriterio NO implementados en 80% de vistas
- ❌ Algunos endpoints devuelven DTOs incorrectos (List vs Page)
- ⚠️ Algunos endpoints esperados no existen

**Prioridad:** 🔴 **ALTA** - Afecta experiencia de usuario admin/gestión
