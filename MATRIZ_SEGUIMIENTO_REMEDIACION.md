# 📅 MATRIZ DE SEGUIMIENTO DE AUDITORIA
## BudgetMap - Roadmap de Remediación 2026

---

## SPRINT PLANNING - REMEDIACIÓN

**Fecha de Inicio:** 5 de Junio, 2026  
**Objetivo:** Resolver todos los problemas críticos (P0) en 2 semanas  
**Equipo:** 3 desarrolladores + 1 QA + 1 Arquitecto

---

## FASE 1: CRÍTICOS (Semana 1 - 5-11 de Junio)

### Sprint 1.1 | Del 5 al 7 de Junio (Lunes-Miércoles)

**Tema:** Data Integrity & Concurrency

| ID | Problema | Responsable | Archivo | Esfuerzo | Estado | Criterio Aceptación | Bloqueante |
|---|---|---|---|---|---|---|---|
| **P0.1** | Transacción Cupones | dev-1 (Java Senior) | CuponService.java | 4h | ⏳ Por iniciar | ✅ Tests pasan (3 escenarios) | NO |
| **P0.2** | Race Condition Aforo | dev-1 (Java Senior) | ReservaService.java | 6h | ⏳ Por iniciar | ✅ Concurrency test con 15 threads | SÍ (crítico) |
| **QA.1** | Tests Integración P0.1 + P0.2 | qa-1 | test/ | 4h | ⏳ Por iniciar | ✅ Coverage >80%, load test 100 req/s | - |

**Detalles P0.1:**
```
Línea de tiempo:
06/05 09:00 - Dev inicia
06/05 12:00 - Código rediseñado + unit tests
06/05 14:00 - Code review (Arquitecto)
06/05 16:00 - Integration tests
06/06 09:00 - Merge a rama QA
06/06 10:00 - QA verifica
06/06 17:00 - Listo para Producción
```

**Detalles P0.2:**
```
Línea de tiempo:
06/05 09:00 - Dev estudia pessimistic locking
06/05 11:00 - Implementa SELECT FOR UPDATE
06/05 14:00 - Tests de concurrencia (ExecutorService)
06/06 09:00 - Benchmark con jMeter (100 usuarios)
06/06 14:00 - Code review
06/06 16:00 - Merge QA
06/07 10:00 - Production ready
```

---

### Sprint 1.2 | Del 7 al 9 de Junio (Jueves-Viernes + Lunes)

**Tema:** Security & Authentication

| ID | Problema | Responsable | Archivo | Esfuerzo | Estado | Criterio Aceptación | Bloqueante |
|---|---|---|---|---|---|---|---|
| **P0.3** | Bloqueo Incompleto | dev-2 (Backend) | UserDetailsImpl.java + AuthController.java | 3h | ⏳ Por iniciar | ✅ 5 intentos = bloqueo 15 min | NO |
| **P0.4** | JWT Algorithm | dev-3 (Python) | budgetmap-geo/auth.py | 5h | ⏳ Por iniciar | ✅ Rechaza "none", "RS256" | SÍ (seguridad) |
| **QA.2** | Penetration Test P0.3 + P0.4 | qa-1 | - | 4h | ⏳ Por iniciar | ✅ 0 vulnerabilidades OWASP A07 | - |

**Detalles P0.3:**
```
Checklist:
- [ ] UserDetailsImpl.isAccountNonLocked() retorna false si bloqueada
- [ ] AuthController registra intentos fallidos
- [ ] Bloqueo automático después de 5 intentos
- [ ] Desbloqueo automático después de 15 min
- [ ] Mensaje de error diferente: "Bloqueada" vs "Credenciales"
- [ ] Test E2E: login x 6 → 403 FORBIDDEN en sexto intento
- [ ] Test: Wait 16 min → login exitoso
```

**Detalles P0.4:**
```
Checklist:
- [ ] verify_jwt_token() valida header['alg'] == 'HS256'
- [ ] Rechaza token sin firma
- [ ] Rechaza token con alg="none"
- [ ] Rechaza token con alg="RS256"
- [ ] Valida campos requeridos: exp, user_id
- [ ] Test: 10 intentos de "none algo" → todos rechazados
- [ ] Logging de intentos rechazados
```

---

### Sprint 1.3 | Del 10 al 11 de Junio (Martes-Miércoles)

**Tema:** Infrastructure & Scalability

| ID | Problema | Responsable | Archivo | Esfuerzo | Estado | Criterio Aceptación | Bloqueante |
|---|---|---|---|---|---|---|---|
| **P0.5** | Rate Limiting Redis | dev-3 (Python + DevOps) | app.py, requirements.txt, docker-compose.yml | 8h | ⏳ Por iniciar | ✅ Render/production <3 dynos | SÍ (performance) |
| **P0.6** | Remover Password Hardcoded | dev-2 | UsuarioService.java | 2h | ⏳ Por iniciar | ✅ Admin password desde env var | NO |
| **QA.3** | Load Test Completo (P0) | qa-1 | jMeter scripts | 6h | ⏳ Por iniciar | ✅ P95 latencia <200ms, 500 usuarios | - |

---

## FASE 2: ALTOS (Semana 2 - 12-18 de Junio)

### Sprint 2.1 | Del 12 al 14 de Junio

**Tema:** Database Optimization & Query Refactoring

| ID | Problema | Responsable | Archivo | Esfuerzo | Deadline | Estado |
|---|---|---|---|---|---|---|
| **P1.1** | Filtrado en memoria → BD | dev-1 | UsuarioService.java + repository | 8h | 13-jun | ⏳ |
| **P1.2** | Scheduled cada minuto → cada hora | dev-1 | EventoService.java | 3h | 12-jun | ⏳ |
| **P1.3** | Caché invalidación cruzada | dev-2 | EstablecimientoService + PromocionService | 4h | 14-jun | ⏳ |

**Métricas de éxito P1.1:**
```
ANTES:
- Usuarios: 100K
- findAll().filter() = 500 MB en memoria
- Tiempo respuesta: 2500 ms (timeout)

DESPUÉS (QueryDSL):
- findByRolAndActivoAndNombre(rol, true, nombre, pageable)
- Query directa a BD: 50 ms
- Memoria: 5 MB
```

---

### Sprint 2.2 | Del 15 al 18 de Junio

**Tema:** Testing & Auditoría

| ID | Problema | Responsable | Archivo | Esfuerzo | Deadline | Estado |
|---|---|---|---|---|---|---|
| **P1.4** | Tabla auditoría + triggers | dev-3 (SQL) | database/migrations | 7h | 16-jun | ⏳ |
| **P1.5** | Email verificación flow | dev-2 | AuthService + email templates | 6h | 17-jun | ⏳ |
| **Test.1** | 60% cobertura unitaria | qa-1 | src/test/java + src/test/python | 14h | 18-jun | ⏳ |

---

## MATRIZ DE RESPONSABILIDADES

```
┌─────────────────────────────────────────────────────────────┐
│                    EQUIPO BudgetMap QA                      │
├─────────────────────────────────────────────────────────────┤
│ Rol                 │ Nombre      │ Experiencia │ Disponibilidad
├─────────────────────────────────────────────────────────────┤
│ Arquitecto          │ [Asignar]   │ 10+ años    │ 100% (coord.)
│ Dev Java Senior     │ dev-1       │ 8 años      │ 100% (P0.1+2)
│ Dev Backend         │ dev-2       │ 5 años      │ 100% (P0.3+6)
│ Dev Python/DevOps   │ dev-3       │ 6 años      │ 100% (P0.4+5)
│ QA Lead             │ qa-1        │ 7 años      │ 100% (testing)
│ Product Owner       │ [Asignar]   │ -           │ 50% (blockers)
└─────────────────────────────────────────────────────────────┘

Dev-1 (Java): JIRA stories: P0.1, P0.2, P1.1, P1.2
Dev-2 (Backend): JIRA stories: P0.3, P0.6, P1.3, P1.5
Dev-3 (Python): JIRA stories: P0.4, P0.5, P1.4
QA-1: Testing coordinado, penetration tests, load tests
```

---

## HITOS CRÍTICOS (Go/No-Go)

```
📍 HITO 1: 7 de Junio (Viernes)
   Condición: P0.1 + P0.2 mergeados a main
   Criterio: Todos los tests pasan
   Responsable: dev-1 + qa-1
   
📍 HITO 2: 9 de Junio (Domingo) — HARD DEADLINE
   Condición: P0 completos excepto P0.5
   Criterio: 0 nuevas vulnerabilidades OWASP
   Responsable: Arquitecto
   Acción si NO: Retrasar deployment a 16-junio
   
📍 HITO 3: 11 de Junio (Martes) — PRODUCTION READY
   Condición: P0.1-P0.6 todos cerrados + QA aprobado
   Criterio: Load test 500 usuarios, P95 <200ms
   Responsable: Arquitecto + PO
   Acción: Deployment a staging → production
```

---

## CRITERIOS DE SALIDA (Definition of Done)

### Para cada problema P0 resuelto:

**Código:**
- [ ] Implementación completada según especificación
- [ ] Code review aprobado por Arquitecto
- [ ] Cambios documentados en PR (descripción + diagrama si es necesario)
- [ ] Sin warnings de compiler (Java) / linter (Python)

**Testing:**
- [ ] Unit tests con cobertura >80%
- [ ] Integration tests: caso exitoso + 2-3 casos error
- [ ] Tests de concurrencia (si aplica)
- [ ] Penetration test realizado (si es seguridad)

**Performance:**
- [ ] Latencia <200ms en local (macOS 2023+)
- [ ] Load test: 100 req/s sin error
- [ ] Memory usage estable (sin memory leak)

**Documentación:**
- [ ] Código comentado (si lógica no obvia)
- [ ] README actualizado con cambios
- [ ] CHANGELOG.md entrada nueva

**Deployment:**
- [ ] Merge a rama `main` aprobado
- [ ] Tag de versión (v1.0.1-P0-fixes)
- [ ] Deployment a staging éxitoso
- [ ] Monitoreo configurado (alertas)

---

## ESTIMACIÓN DE ESFUERZO DETALLADA

```
FASE 1 (Semana 1):
┌─────────────────────────────────────────────────────┐
│ Dev-1 (Java):   P0.1 (4h) + P0.2 (6h) = 10h        │
│ Dev-2 (Backend): P0.3 (3h) + P0.6 (2h) = 5h        │
│ Dev-3 (Python):  P0.4 (5h) + P0.5 (8h) = 13h       │
│ QA-1:            Testing (4h+4h+6h) = 14h          │
│ TOTAL:           42 horas (52.5% de 2 semanas)     │
└─────────────────────────────────────────────────────┘

FASE 2 (Semana 2):
┌─────────────────────────────────────────────────────┐
│ Dev-1: P1.1 (8h) + P1.2 (3h) = 11h                 │
│ Dev-2: P1.3 (4h) + P1.5 (6h) = 10h                 │
│ Dev-3: P1.4 (7h) = 7h                              │
│ QA-1:  Tests (14h) = 14h                           │
│ TOTAL: 42 horas (52.5% de 2 semanas)               │
└─────────────────────────────────────────────────────┘

CARGA POR PERSONA:
├─ dev-1: 21 horas (37.5% sprints)
├─ dev-2: 15 horas (26.8% sprints)
├─ dev-3: 20 horas (35.7% sprints)
└─ qa-1:  28 horas (50% sprints) ← CUELLO DE BOTELLA

CONCLUSIÓN: QA es el recurso limitante. Considerar
            third-party penetration testing si es necesario.
```

---

## NOTIFICACIÓN A STAKEHOLDERS

**Correo a:** PO, CTO, CEO  
**Asunto:** 🔴 AUDITORÍA CRÍTICA - 5 Vulnerabilidades Detectadas  
**Prioridad:** 🔴 URGENTE

```
Estimados Stakeholders,

Se completó una auditoría exhaustiva del proyecto BudgetMap bajo 
normas ISO 25010. Se detectaron 5 problemas CRÍTICOS que afectan:

1. Data Integrity (pérdida de datos potencial)
2. Business Logic (overbooking de eventos)
3. Seguridad (elusión de autenticación)
4. Escalabilidad (DDoS sin defensa)
5. Compliance (GDPR/LGPD incumplimiento)

RECOMENDACIÓN: No liberar a producción con usuarios reales 
hasta 11 de junio.

PLAN DE ACCIÓN:
- Semana 1 (5-11 jun): Resolver 6 P0
- Semana 2 (12-18 jun): Refactorización + Testing
- Semana 3 (19-23 jun): Production deployment

Detalles: 
- Documento principal: AUDITORIA_EXHAUSTIVA_ISO25010_v1.md
- Correcciones técnicas: CORRECCIONES_TECNICAS_DETALLADAS_P0.md
- Tracking: Esta matriz

Solicito aprobación de:
☐ Retrasar launch 2 semanas (nuevo target: 23 junio)
☐ Asignar 4 personas full-time al team
☐ $10K budget si necesita 3rd-party pen testing

Próxima reunión: Mañana 9 AM (coordinación).

---
Arquitecto de Software
BudgetMap Team
```

---

## DASHBOARD DE PROGRESO (Actualizar Diariamente)

```
SEMANA 1 (5-11 de Junio):

Lunes 5 de Junio:
  P0.1 Transacción Cupones ████░░░░░░ 40%
  P0.2 Race Condition ██░░░░░░░░ 20%
  P0.3 Bloqueo Cuenta ░░░░░░░░░░ 0%
  P0.4 JWT Algorithm ░░░░░░░░░░ 0%
  P0.5 Rate Limiting ░░░░░░░░░░ 0%
  P0.6 Password Hardcoded ░░░░░░░░░░ 0%

Martes 6 de Junio:
  P0.1 Transacción Cupones █████████░ 90% ← Ready for QA
  P0.2 Race Condition ████░░░░░░ 40%
  P0.3 Bloqueo Cuenta ████░░░░░░ 40%
  P0.4 JWT Algorithm ██░░░░░░░░ 20%
  P0.5 Rate Limiting ░░░░░░░░░░ 0% ← Waiting dev-3
  P0.6 Password Hardcoded ░░░░░░░░░░ 0%

... (continuar cada día)
```

---

## MÉTRICAS DE ÉXITO FINALES

**Antes de Auditoria:**
```
Funcionalidad:       75%
Fiabilidad:          60%
Performance:         50%
Seguridad:           55%
PROMEDIO:            67% 🔴
```

**Objetivo Después (11 de Junio):**
```
Funcionalidad:       85% (+10)
Fiabilidad:          85% (+25) ← Focus aquí
Performance:         75% (+25) ← Focus aquí
Seguridad:           88% (+33) ← Focus aquí
PROMEDIO:            83% ✅
```

**Objetivo Después (18 de Junio):**
```
Funcionalidad:       90%
Fiabilidad:          92%
Performance:         88%
Seguridad:           95%
PROMEDIO:            91% ✅✅ PRODUCTION READY
```

---

## APROBACIONES

```
Nombre                  Rol             Fecha       Firma
─────────────────────────────────────────────────────────
_________________       Arquitecto      _____       ___
_________________       Dev Lead        _____       ___
_________________       QA Lead         _____       ___
_________________       Product Owner   _____       ___
_________________       CTO             _____       ___
```

---

**Documento de seguimiento generado:** 4 de Junio, 2026  
**Próxima actualización:** Diaria a las 17:00  
**Responsable:** Arquitecto de Software

---

*Fin de la Matriz de Seguimiento.*
