
### 3. **SIN TESTS AUTOMATIZADOS - 0% Cobertura**
**Severidad:** CRÍTICA  
**Ubicación:** Directorio `test/` completamente vacío

**Servicios sin test:**
- ✗ AuthService (autenticación completa)
- ✗ ReservaService (lógica de negocio crítica)
- ✗ PuntosService (cálculos financieros)
- ✗ PasarelaService (pagos integrados)
- ✗ SuscripcionService (ingresos)
- ✗ GeoEngine (cálculos espaciales - Python)

**Riesgos:**
- Cambios rompen funcionalidad sin detección
- Refactorización = introducir bugs
- Integración Mercado Pago sin validación
- Cálculo de puntos sin verificación de precisión

**Casos de test faltantes:**
```
AuthService:
- ✗ Login con credenciales correctas → JWT válido
- ✗ Login con contraseña incorrecta → HTTP 401
- ✗ Registro duplicado email → Excepción
- ✗ Registro con campos vacíos → Validación

ReservaService:
- ✗ Crear reserva sin puntos → Excepción InsufficientPointsException
- ✗ Crear reserva que excede aforo → Validación
- ✗ Confirmar asistencia → +10 puntos correctamente
- ✗ Cancelar dentro de 24h → Sin penalización

PuntosService:
- ✗ Suma correcta: 0 + 50 = 50
- ✗ Resta no permite negativos: 30 - 50 → Excepción
- ✗ Transacciones concurrentes → Sin race condition

PasarelaService:
- ✗ Webhook Mercado Pago EXITOSO → Plan extendido 30 días
- ✗ Webhook con paymentId inválido → Sin actualización
- ✗ Duplicate webhook → Idempotente (no cobra 2x)

GeoEngine:
- ✗ Distancia Haversine correcta
- ✗ Geofencing entrada/salida correcta
- ✗ Radio máximo 50km → Validado
```

**Acción Requerida:**
```gradle
// build.gradle - agregar
testImplementation 'org.springframework.boot:spring-boot-starter-test'
testImplementation 'org.mockito:mockito-core'
testImplementation 'org.mockito:mockito-inline'
```

**Timeline:** URGENTE - Mínimo 60% cobertura antes de producción

---



### 8. **Logging Estructurado**
**Estado actual:** Inconsistente (AuthService usa SLF4J, otros no)

**Mejora:**
```java
// Agregar a pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-logging</artifactId>
</dependency>

// En todos los servicios:
private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

public void confirmarAsistencia(Long reservaId) {
    logger.info("Confirmando asistencia para reserva: {}", reservaId);
    
    Reserva reserva = reservaRepository.findById(reservaId)
        .orElseThrow(() -> {
            logger.error("Reserva no encontrada: {}", reservaId);
            return new ResourceNotFoundException("Reserva", reservaId);
        });
    
    logger.debug("Reserva encontrada: usuario={}, establecimiento={}", 
                 reserva.getUsuario().getId(), 
                 reserva.getEstablecimiento().getId());
    
    try {
        puntosService.sumarPuntos(reserva.getUsuario(), 10 * reserva.getNumeroPersonas());
        logger.info("Puntos sumados correctamente: usuario={}, puntos={}", 
                   reserva.getUsuario().getId(), 10 * reserva.getNumeroPersonas());
    } catch (Exception e) {
        logger.error("Error sumando puntos para reserva: {}", reservaId, e);
        throw e;
    }
}
```

**application.properties:**
```properties
logging.level.root=WARN
logging.level.com.budgetmap=INFO
logging.level.com.budgetmap.service=DEBUG
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} - %logger{36} - %msg%n
logging.file.name=logs/budgetmap.log
logging.file.max-size=10MB
logging.file.max-history=10
```

**Timeline:** MEDIA (1 semana)

---

### 9. **Bean Validation - Validaciones en DTO**
**Estado actual:** Decoradores sin @Valid en controladores

**Mejora:**
```java
// DTOs mejorados
public class ReservaRequest {
    @NotNull(message = "El número de personas es requerido")
    @Min(value = 1, message = "Debe reservar mínimo 1 persona")
    @Max(value = 50, message = "No se puede reservar más de 50 personas")
    private Integer numeroPersonas;
    
    @NotNull(message = "El ID del establecimiento es requerido")
    private Long establecimientoId;
    
    @NotNull(message = "Fecha de la reserva es requerida")
    @FutureOrPresent(message = "La fecha no puede ser en el pasado")
    private LocalDateTime fechaReserva;
}

// En controlador
@PostMapping
public ResponseEntity<?> crearReserva(
    @Valid @RequestBody ReservaRequest request,  // ← @Valid activa validaciones
    @AuthenticationPrincipal UserDetails userDetails
) {
    // request está garantizado válido aquí
    Reserva reserva = reservaService.crear(request);
    return ResponseEntity.status(201).body(reserva);
}

// Handler global de errores de validación
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }
}
```

**Timeline:** MEDIA (1 semana)

---

### 10. **CORS Restrictivo en Lugar de Wildcard**
**Estado actual:** `origins = "*"` en todos lados

**Mejora:**
```java
// config/CorsConfig.java
@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOrigins(
                        "https://budgetmap.com",
                        "https://www.budgetmap.com",
                        "http://localhost:3000" // Solo desarrollo
                    )
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true)
                    .maxAge(3600);
            }
        };
    }
}
```

**application.properties:**
```properties
cors.allowed-origins=https://budgetmap.com,https://www.budgetmap.com
cors.allowed-methods=GET,POST,PUT,DELETE
cors.allow-credentials=true
cors.max-age=3600
```

**Timeline:** MEDIA (1 semana)

---

### 11. **Implementar Caching**
**Estado actual:** Sin caching - queries completas cada vez

**Mejoras:**
```java
// Agregar a pom.xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

// application.properties
spring.cache.type=caffeine
spring.cache.caffeine.spec=maximumSize=1000,expireAfterWrite=10m

// En config
@Configuration
@EnableCaching
public class CacheConfig {}

// En servicios
@Service
public class PromocionService {
    @Cacheable(value = "promociones_activas", unless = "#result.isEmpty()")
    public List<Promocion> obtenerPromocionesActivas() {
        return promocionRepository.findByActivoTrueAndFechaFinAfter(LocalDateTime.now());
    }
    
    @CacheEvict(value = "promociones_activas", allEntries = true)
    public Promocion crearPromocion(PromocionRequest request) {
        // ...
    }
}

@Service
public class LugarService {
    @Cacheable(value = "lugares_aprobados", unless = "#result.isEmpty()")
    public List<Lugar> obtenerLugaresAprobados() {
        return lugarRepository.findByEstado(EstadoAprobacion.APROBADO);
    }
}
```

**Qué cachear:**
- ✓ Promociones activas (cambia 10x/día)
- ✓ Lugares aprobados (cambia 1-2x/día)
- ✓ Establecimientos aprobados (cambia 1-2x/día)
- ✓ Planes disponibles (rara vez cambia)
- ✗ Reservas del usuario (cambia constantemente)
- ✗ Puntos del usuario (cambia con cada acción)

**Timeline:** MEDIA (1 semana)

---

### 12. **Índices de Base de Datos Faltantes**
**Estado actual:** Solo email, rol, spatiales

**Mejoras SQL:**
```sql
-- Reservas
ALTER TABLE reservas ADD INDEX idx_usuario_id (usuario_id);
ALTER TABLE reservas ADD INDEX idx_establecimiento_id (establecimiento_id);
ALTER TABLE reservas ADD INDEX idx_fecha_creacion (fecha_creacion);
ALTER TABLE reservas ADD INDEX idx_estado (estado);
ALTER TABLE reservas ADD UNIQUE INDEX idx_codigo_unico (codigo);

-- Transacciones
ALTER TABLE transacciones ADD INDEX idx_usuario_id (usuario_id);
ALTER TABLE transacciones ADD INDEX idx_estado (estado);
ALTER TABLE transacciones ADD INDEX idx_fecha_creacion (fecha_creacion);

-- Eventos
ALTER TABLE eventos ADD INDEX idx_lugar_id (lugar_id);
ALTER TABLE eventos ADD INDEX idx_anfitrion_id (anfitrion_id);
ALTER TABLE eventos ADD INDEX idx_fecha_inicio (fecha_inicio);

-- Promociones
ALTER TABLE promociones ADD INDEX idx_establecimiento_id (establecimiento_id);
ALTER TABLE promociones ADD INDEX idx_evento_id (evento_id);
ALTER TABLE promociones ADD INDEX idx_estado (activo, fecha_fin);

-- Análitica
ALTER TABLE analiticas_locales ADD INDEX idx_establecimiento_id (establecimiento_id);
ALTER TABLE analiticas_locales ADD INDEX idx_fecha (fecha);

-- Notificaciones
ALTER TABLE notificaciones ADD INDEX idx_usuario_id (usuario_id);
ALTER TABLE notificaciones ADD INDEX idx_leida (leida);
```

**Timeline:** MEDIA (1 día)

---

### 13. **Paginación Consistente**
**Estado actual:** Mixto - algunos endpoints paginaros, otros no

**Mejora:**
```java
// Crear DTO genérico
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}

// Aplicar en todos los endpoints
@GetMapping
public ResponseEntity<PageResponse<LugarDTO>> obtenerLugares(
    @RequestParam(defaultValue = "0") int page,
    @RequestParam(defaultValue = "20") int size
) {
    Page<Lugar> pageResult = lugarRepository.findByEstado(
        EstadoAprobacion.APROBADO,
        PageRequest.of(page, size, Sort.by("createdAt").descending())
    );
    
    PageResponse<LugarDTO> response = new PageResponse<>(
        pageResult.getContent().stream().map(LugarDTO::from).collect(toList()),
        pageResult.getNumber(),
        pageResult.getSize(),
        pageResult.getTotalElements(),
        pageResult.getTotalPages(),
        pageResult.hasNext(),
        pageResult.hasPrevious()
    );
    
    return ResponseEntity.ok(response);
}
```

**Timeline:** MEDIA (1 semana)

---

### 14. **Validar Tokens JWT en Flask**
**Estado actual:** Flask confía en Java sin verificar

**Mejora:**
```python
# budgetmap-geo/auth.py
import jwt
from functools import wraps
from flask import request, jsonify
from datetime import datetime

JWT_SECRET = os.getenv('JWT_SECRET', 'default-secret')

def verify_jwt_token(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        token = None
        if 'Authorization' in request.headers:
            auth_header = request.headers['Authorization']
            try:
                token = auth_header.split(" ")[1]  # "Bearer <token>"
            except IndexError:
                return jsonify({"error": "Token inválido"}), 401
        
        if not token:
            return jsonify({"error": "Token requerido"}), 401
        
        try:
            payload = jwt.decode(token, JWT_SECRET, algorithms=['HS256'])
            request.usuario_id = payload.get('sub')
            request.usuario_rol = payload.get('rol')
        except jwt.ExpiredSignatureError:
            return jsonify({"error": "Token expirado"}), 401
        except jwt.InvalidTokenError:
            return jsonify({"error": "Token inválido"}), 401
        
        return f(*args, **kwargs)
    return decorated_function

# Usar en rutas
@app.route('/api/filtros/config/<usuario_id>', methods=['GET'])
@verify_jwt_token
def get_config(usuario_id):
    if str(request.usuario_id) != usuario_id:
        return jsonify({"error": "No autorizado"}), 403
    # ...
```

**Timeline:** MEDIA (3 días)

---

### 15. **Transacciones Atómicas en Pagos**
**Estado actual:** Sin @Transactional explícita

**Mejora:**
```java
@Service
@Transactional
public class PasarelaService {
    
    @Transactional(propagation = Propagation.REQUIRED, 
                   rollbackFor = Exception.class)
    public void procesarNotificacionDePago(String paymentId) {
        // 1. Obtener datos de Mercado Pago
        Map<String, Object> paymentData = mercadoPagoClient.getPayment(paymentId);
        
        if (paymentData == null) {
            logger.warn("Webhook con paymentId inválido: {}", paymentId);
            return;
        }
        
        // 2. Crear o actualizar Transacción
        Transaccion transaccion = transaccionRepository.findByReferenciaExterna(paymentId)
            .orElse(new Transaccion());
        
        transaccion.setReferenciaExterna(paymentId);
        transaccion.setMonto(BigDecimal.valueOf((double) paymentData.get("transaction_amount")));
        transaccion.setMetodoPago("MERCADO_PAGO");
        
        // 3. Determinar estado
        String mpStatus = (String) paymentData.get("status");
        if ("approved".equals(mpStatus)) {
            transaccion.setEstado(EstadoTransaccion.EXITOSO);
        } else if ("pending".equals(mpStatus)) {
            transaccion.setEstado(EstadoTransaccion.PENDIENTE);
            return; // No actualizar usuario aún
        } else {
            transaccion.setEstado(EstadoTransaccion.FALLIDO);
            logger.warn("Pago fallido para paymentId: {}", paymentId);
            return; // No actualizar usuario
        }
        
        // 4. Actualizar usuario (DENTRO de @Transactional)
        Long usuarioId = (Long) paymentData.get("usuario_id");
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario", usuarioId));
        
        LocalDateTime ahora = LocalDateTime.now();
        if (usuario.getFechaVencimientoPlan() != null && 
            usuario.getFechaVencimientoPlan().isAfter(ahora)) {
            // Plan ya activo - extender
            usuario.setFechaVencimientoPlan(usuario.getFechaVencimientoPlan().plusDays(30));
        } else {
            // Nuevo plan o vencido - crear desde hoy
            usuario.setFechaVencimientoPlan(ahora.plusDays(30));
        }
        usuario.setActivo(true);
        
        // 5. Guardar TODO de forma atómica
        transaccionRepository.save(transaccion);
        usuarioRepository.save(usuario);
        
        logger.info("Pago procesado exitosamente: paymentId={}, usuario={}, nuevaFecha={}", 
                   paymentId, usuarioId, usuario.getFechaVencimientoPlan());
    }
    
    @Transactional(readOnly = true)
    public boolean isPaymentProcessed(String paymentId) {
        return transaccionRepository.existsByReferenciaExterna(paymentId);
    }
}
```

**Timeline:** ALTA (1 semana)

---

### 16. **Separar Entities de DTOs Completamente**
**Estado actual:** Devolviendo entities directamente en respuestas

**Mejora:**
```java
// Crear mapper
@Component
public class LugarMapper {
    public LugarDTO toDTO(Lugar lugar) {
        return LugarDTO.builder()
            .id(lugar.getId())
            .nombre(lugar.getNombre())
            .descripcion(lugar.getDescripcion())
            .categoria(lugar.getCategoria())
            .ubicacion(new UbicacionDTO(
                lugar.getUbicacion().getX(),
                lugar.getUbicacion().getY()
            ))
            .estado(lugar.getEstado())
            .build();
    }
    
    public Lugar toEntity(CrearLugarRequest request) {
        // ...
    }
}

// Controlador
@GetMapping("/{id}")
public ResponseEntity<LugarDTO> obtenerLugar(@PathVariable Long id) {
    Lugar lugar = lugarService.obtenerPorId(id);
    return ResponseEntity.ok(lugarMapper.toDTO(lugar));
}
```

**Timeline:** MEDIA (2 semanas)

---

## 🆕 COSAS POR CREAR - FUNCIONALIDADES FALTANTES

### 17. **Sistema de Auditoría Completo**
**Estado actual:** No existe

**Crear:**
```sql
CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    entidad VARCHAR(100) NOT NULL,
    entidad_id BIGINT NOT NULL,
    usuario_id BIGINT,
    accion ENUM('CREAR', 'ACTUALIZAR', 'ELIMINAR', 'CONSULTAR') NOT NULL,
    datos_anterior JSON,
    datos_nuevo JSON,
    motivo VARCHAR(255),
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_entidad (entidad, entidad_id),
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_fecha (fecha_cambio)
);
```

```java
@Aspect
@Component
public class AuditingAspect {
    @Pointcut("@annotation(com.budgetmap.annotation.Auditable)")
    public void auditableMethods() {}
    
    @After("auditableMethods()")
    public void audit(JoinPoint jp) {
        // Registrar cambios en tabla auditoria
    }
}
```

**Timeline:** BAJA (pero recomendado antes de producción)

---

### 18. **Refresh Token Mechanism**
**Estado actual:** Token de 24h sin refresh

**Crear:**
```java
@Entity
public class RefreshToken {
    @Id
    private String token;
    
    @ManyToOne
    private Usuario usuario;
    
    private LocalDateTime fechaExpiracion;
    
    private boolean revocado;
}

@Service
public class TokenService {
    public Map<String, String> generateTokenPair(Usuario usuario) {
        String accessToken = generarAccessToken(usuario);
        String refreshToken = generarRefreshToken(usuario);
        
        refreshTokenRepository.save(new RefreshToken(
            refreshToken,
            usuario,
            LocalDateTime.now().plusDays(30)
        ));
        
        return Map.of(
            "accessToken", accessToken,
            "refreshToken", refreshToken
        );
    }
    
    public String refreshAccessToken(String refreshToken) {
        RefreshToken rt = refreshTokenRepository.findById(refreshToken)
            .orElseThrow(() -> new InvalidTokenException());
        
        if (rt.isRevocado() || rt.getFechaExpiracion().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException();
        }
        
        return generarAccessToken(rt.getUsuario());
    }
}
```

**Endpoints:**
```
POST /api/auth/refresh
{
    "refreshToken": "..."
}

Response:
{
    "accessToken": "...",
    "expiresIn": 3600
}
```

**Timeline:** MEDIA (1 semana)

---

### 19. **Swagger/OpenAPI Documentation**
**Estado actual:** No existe

**Crear:**
```gradle
dependency {
    implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.2'
}
```

```java
@Configuration
public class OpenAPIConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("BudgetMap API")
                .version("1.0.0")
                .description("API para gestión de presupuestos y geolocalización"))
            .servers(List.of(
                new Server().url("http://localhost:8080").description("Local"),
                new Server().url("https://api.budgetmap.com").description("Producción")
            ))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
            .security(List.of(
                new SecurityRequirement().addList("bearer-jwt")));
    }
}

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    @PostMapping
    @Operation(summary = "Crear nueva reserva",
               description = "Crear una reserva en un establecimiento")
    @ApiResponse(responseCode = "201", description = "Reserva creada exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos inválidos")
    @ApiResponse(responseCode = "401", description = "No autenticado")
    public ResponseEntity<?> crearReserva(@Valid @RequestBody ReservaRequest request) {
        // ...
    }
}
```

**Acceso:** `http://localhost:8080/swagger-ui.html`

**Timeline:** BAJA (documentación, pero valiosa)

---

### 20. **Health Checks y Monitoring**
**Estado actual:** No existe

**Crear:**
```gradle
implementation 'org.springframework.boot:spring-boot-starter-actuator'
implementation 'io.micrometer:micrometer-registry-prometheus'
```

```properties
management.endpoints.web.exposure.include=health,metrics,prometheus
management.endpoint.health.show-details=always
management.health.livenessState.enabled=true
management.health.readinessState.enabled=true
```

```java
@Component
public class BudgetMapHealthIndicator extends AbstractHealthIndicator {
    @Override
    protected void doHealthCheck(Health.Builder builder) {
        boolean dbHealthy = checkDatabase();
        boolean flaskHealthy = checkFlask();
        boolean mercadoPagoHealthy = checkMercadoPago();
        
        if (dbHealthy && flaskHealthy && mercadoPagoHealthy) {
            builder.up()
                .withDetail("database", "OK")
                .withDetail("flask", "OK")
                .withDetail("mercadopago", "OK");
        } else {
            builder.down()
                .withDetail("database", dbHealthy ? "OK" : "ERROR")
                .withDetail("flask", flaskHealthy ? "OK" : "ERROR")
                .withDetail("mercadopago", mercadoPagoHealthy ? "OK" : "ERROR");
        }
    }
}
```

**Endpoints:**
```
GET /actuator/health
GET /actuator/health/liveness
GET /actuator/health/readiness
GET /actuator/metrics
GET /actuator/prometheus
```

**Timeline:** MEDIA (1 semana)

---

### 21. **Sistema de Notificaciones Push**
**Estado actual:** Parcial (email/SMS sin envío real)

**Mejorar:**
```gradle
// Firebase Cloud Messaging
implementation 'com.google.firebase:firebase-admin:9.2.0'

// Twilio para SMS
implementation 'com.twilio.sdk:twilio:9.0.0'

// JavaMail para email
implementation 'org.springframework.boot:spring-boot-starter-mail'
```

```java
@Service
public class NotificationService {
    public void notificar(Usuario usuario, String titulo, String mensaje, TipoNotificacion tipo) {
        Notificacion notif = new Notificacion();
        notif.setUsuario(usuario);
        notif.setTitulo(titulo);
        notif.setMensaje(mensaje);
        notif.setTipo(tipo);
        notif.setLeida(false);
        
        notificacionRepository.save(notif);
        
        // Enviar por canal preferido
        if (usuario.getPreferenciasNotificacion().isEmail()) {
            enviarEmail(usuario.getEmail(), titulo, mensaje);
        }
        if (usuario.getPreferenciasNotificacion().isPush()) {
            enviarPush(usuario.getTokenFCM(), titulo, mensaje);
        }
        if (usuario.getPreferenciasNotificacion().isSms()) {
            enviarSMS(usuario.getTelefono(), mensaje);
        }
    }
    
    private void enviarEmail(String email, String asunto, String contenido) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("notificaciones@budgetmap.com");
        message.setTo(email);
        message.setSubject(asunto);
        message.setText(contenido);
        mailSender.send(message);
    }
}
```

**Timeline:** MEDIA (2 semanas)

---

### 22. **API Rate Limiting por Usuario**
**Estado actual:** Por IP global, no diferenciado

**Crear:**
```java
@Service
public class RateLimitService {
    
    private final RateLimitBucket bucket = new RateLimitBucket();
    
    public void checkRateLimit(Long usuarioId, String endpoint) {
        String key = usuarioId + ":" + endpoint;
        
        RateLimitConfig config = getRateLimitConfig(endpoint);
        Bucket b = bucket.resolveBucket(key);
        
        if (!b.tryConsume(1)) {
            throw new RateLimitExceededException(
                "Límite alcanzado. Reintenta en " + b.estimateAbilityToConsume(1)
            );
        }
    }
}
```

**Timeline:** BAJA (después de MVP)

---

## ❌ COSAS POR BORRAR - CÓDIGO MUERTO

### 23. **Campos Sin Uso**

**En tabla `transacciones`:**
```sql
-- Aparentemente sin uso:
-- comisión_cobrada (siempre NULL, lógica incompleta)
-- motivo_fallo (almacena null, no se usa)
```

**En DTOs:**
```java
// ReviewDTO tiene campos que nunca se usan:
// @Deprecated reviewTiempo, reviewSabor, reviewPrecios
```

**Acción:** Eliminar después de confirmar con equipo

---

### 24. **Métodos Duplicados**

**En ReservaService:**
```java
// Métodos duplicados:
public List<Reserva> obtenerMisReservas(Long usuarioId) { ... }
public Page<Reserva> obtenerMisReservasPaginadas(Long usuarioId, Pageable p) { ... }
// Consolidar en uno

public void confirmarAsistencia(Long reservaId) { ... }
public void confirmarAsistenciaConCodigo(String codigo) { ... }
// Consolidar en uno
```

**Timeline:** MEDIA (1 semana)

---

### 25. **Configuraciones Obsoletas**

**En `application.properties`:**
```properties
# Aparentemente obsoleto:
# spring.jpa.show-sql=true (desactivar en producción)
# spring.jpa.properties.hibernate.generate_statistics=true
# debug=true (solo para desarrollo)
```

**Crear `application-prod.properties`:**
```properties
spring.jpa.show-sql=false
logging.level.root=WARN
debug=false
```

---

### 26. **Importaciones No Utilizadas**

Limpiar en todos los archivos Java:
```bash
# En VS Code: Preferences → Format on Save
# En IntelliJ: Code → Analyze → Run Inspection by Name → "Unused import"
```

**Timeline:** BAJA (1 día)

---

## 📊 TABLA RESUMEN DE ACCIONES

| ID | Descripción | Severidad | Tipo | Timeline | Esfuerzo |
|----|----|----------|------|----------|----------|

| 3 | Sin tests | CRÍTICA | Testing | URGENTE | 3 semanas |

| 8 | Logging estructurado | MEDIA | Mantenibilidad | MEDIA | 1 semana |
| 9 | Bean Validation | MEDIA | Validación | MEDIA | 1 semana |

| 11 | Caching | MEDIA | Performance | MEDIA | 1 semana |
| 12 | Índices BD | MEDIA | Performance | MEDIA | 1 día |
| 13 | Paginación consistente | MEDIA | Refactoring | MEDIA | 1 semana |

| 15 | Transacciones atómicas | MEDIA | Negocio | MEDIA | 1 semana |
| 16 | Entities vs DTOs | MEDIA | Arquitectura | MEDIA | 2 semanas |
| 17 | Sistema auditoría | BAJA | Negocio | BAJA | 1 semana |
| 18 | Refresh tokens | BAJA | Seguridad | BAJA | 1 semana |
| 19 | Swagger/OpenAPI | BAJA | Documentación | BAJA | 3 días |
| 20 | Health checks | BAJA | Monitoreo | BAJA | 1 semana |
| 21 | Notificaciones push | MEDIA | Feature | MEDIA | 2 semanas |
| 22 | Rate Limit por usuario | BAJA | Feature | BAJA | 1 semana |
| 23 | Campos sin uso | BAJA | Limpieza | BAJA | 1 día |
| 24 | Métodos duplicados | MEDIA | Refactoring | MEDIA | 1 semana |
| 25 | Configuraciones obsoletas | BAJA | Limpieza | BAJA | 1 día |
| 26 | Importaciones sin usar | BAJA | Limpieza | BAJA | 1 día |

---

## 🎯 PLAN DE ACCIÓN RECOMENDADO

### **FASE 1: Securización (Semana 1)**
- ✅ Mover credenciales a .env
- ✅ Implementar excepciones personalizadas
- ✅ Validar contraseñas con complejidad
- ✅ Validar JWT en Flask
- ✅ CORS restrictivo

### **FASE 2: Calidad de Código (Semanas 2-3)**
- ✅ Logging estructurado
- ✅ Bean Validation
- ✅ Tests unitarios (mínimo 60%)
- ✅ Mappers Entities → DTOs
- ✅ Limpieza de código duplicado

### **FASE 3: Performance (Semana 4)**
- ✅ Índices en BD
- ✅ Solucionar N+1 queries
- ✅ Implementar caching
- ✅ Paginación consistente

### **FASE 4: Monitoreo & Docs (Semana 5)**
- ✅ Health checks
- ✅ Swagger/OpenAPI
- ✅ Sistema de auditoría
- ✅ Refresh tokens

### **FASE 5: Features (Post MVP)**
- ✅ Notificaciones push avanzadas
- ✅ Rate limiting por usuario
- ✅ Sistema de reportes mejorado

---

## 📌 NOTAS IMPORTANTES

1. **No mezcles refactorización con nuevas features** - Crea PR separadas
2. **Tests primero** - TDD para código crítico
3. **Revisa seguridad en cada PR** - Auditoría de cambios
4. **Monitorea producción** - Logs + métricas desde el inicio
5. **Documentación actualizada** - README + Swagger + comments

---

**Documento generado:** 25/05/2026  
**Versión:** 1.0
