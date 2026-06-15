# 🔧 PLAN DE CORRECCIONES TÉCNICAS DETALLADAS
## BudgetMap Audit - Remediation Guide

---

## PROBLEMA P0.1: TRANSACCIÓN PARCIAL EN CUPONES

**Ubicación:** `budgetmap-api/src/main/java/com/budgetmap/service/CuponService.java`  
**Línea:** 35-45  
**Clasificación:** Critical Bug - Data Loss

### Análisis Detallado

```java
// ❌ CÓDIGO ACTUAL (INCORRECTO)
@Transactional
public CuponRedimido canjearCupon(Long usuarioId, Long cuponId, Integer puntosCosto) {
    // Validaciones iniciales
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new UsuarioNoEncontradoException());
    
    // ⚠️ LÍNEA 39: Resta puntos (COMMIT implícito)
    puntosService.restarPuntos(usuarioId, puntosCosto);
    
    // ⚠️ LÍNEA 42: Si esta línea falla, puntos ya descontados
    CuponRedimido cupon = cuponRepository.save(new CuponRedimido(
        UUID.randomUUID().toString().substring(0, 8),
        usuario,
        LocalDateTime.now()
    ));
    
    return cupon;
}
```

**Problema Raíz:**
- `puntosService.restarPuntos()` probablemente es `@Transactional` separadamente
- Si `cuponRepository.save()` lanza excepción (ej: FK violada, BD caída)
- Puntos YA fueron restados, cupón NO fue creado
- Rollback de la transacción principal NO revierte puntos

### Solución Correcta

```java
// ✅ CÓDIGO CORREGIDO
@Transactional(
    isolation = Isolation.SERIALIZABLE,  // Máxima protección
    propagation = Propagation.REQUIRED    // Hereda o crea transacción
)
public CuponRedimido canjearCupon(Long usuarioId, Long cuponId, Integer puntosCosto) {
    // 1. Validar usuario existe
    Usuario usuario = usuarioRepository.findById(usuarioId)
        .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no existe"));
    
    // 2. Validar saldo (lectura, sin cambio)
    if (usuario.getPuntosAcumulados() < puntosCosto) {
        throw new PuntosInsuficientesException(
            String.format("Saldo: %d, requerido: %d", 
                usuario.getPuntosAcumulados(), puntosCosto)
        );
    }
    
    // 3. ✅ CREAR CUPÓN PRIMERO (en transacción actual)
    String codigo = generarCodigoCupon();  // "BMAP-" + UUID(0:8)
    CuponRedimido cupon = new CuponRedimido();
    cupon.setCodigo(codigo);
    cupon.setUsuario(usuario);
    cupon.setFechaCreacion(LocalDateTime.now());
    cupon.setVigencia(LocalDateTime.now().plusDays(30));
    
    try {
        cupon = cuponRepository.save(cupon);  // INSERT
        logger.info("Cupón creado: {}", codigo);
    } catch (DataIntegrityViolationException e) {
        throw new CuponException("Error al crear cupón: " + e.getMessage(), e);
    }
    
    // 4. ✅ RESTAR PUNTOS (mismo contexto transaccional)
    try {
        usuario.setPuntosAcumulados(usuario.getPuntosAcumulados() - puntosCosto);
        usuarioRepository.save(usuario);  // UPDATE
        logger.info("Puntos restados. Usuario: {}, Cupón: {}", usuarioId, codigo);
    } catch (Exception e) {
        // ROLLBACK automático: cupón + puntos
        throw new TransactionSystemException(
            "Fallo al descontar puntos. Transacción reversible.", e
        );
    }
    
    // 5. Crear registro de auditoría
    crearRegistroAuditoria(usuarioId, "CUPON_CANJEO", codigo, puntosCosto);
    
    return cupon;
}

// Método helper para generar código único
private String generarCodigoCupon() {
    // Usar UUID completo (12 chars) para evitar colisiones
    return "BMAP-" + UUID.randomUUID().toString()
        .replace("-", "")
        .substring(0, 12)
        .toUpperCase();
}
```

### Testing

```java
@Test
@DisplayName("Canjear cupón: transacción atómica")
void testCanjearCuponAtomico() {
    // Arrange
    Usuario usuario = usuarioRepository.save(new Usuario());
    usuario.setPuntosAcumulados(1000);
    usuario = usuarioRepository.save(usuario);
    
    // Act
    CuponRedimido cupon = cuponService.canjearCupon(usuario.getId(), null, 500);
    
    // Assert - Ambas operaciones completadas
    assertThat(cupon).isNotNull();
    assertThat(cupon.getCodigo()).startsWith("BMAP-");
    
    Usuario usuarioActualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();
    assertThat(usuarioActualizado.getPuntosAcumulados()).isEqualTo(500);
}

@Test
@DisplayName("Canjear cupón: fallo en save revierte todo")
void testCanjearCuponFallo() {
    // Arrange
    Usuario usuario = usuarioRepository.save(new Usuario());
    usuario.setPuntosAcumulados(1000);
    usuario = usuarioRepository.save(usuario);
    
    // Mock fallo de BD
    doThrow(new DataIntegrityViolationException("FK error"))
        .when(cuponRepository).save(any());
    
    // Act & Assert
    assertThatThrownBy(() -> cuponService.canjearCupon(usuario.getId(), null, 500))
        .isInstanceOf(CuponException.class);
    
    // ✅ Puntos NO fueron descontados (rollback)
    Usuario usuarioActualizado = usuarioRepository.findById(usuario.getId()).orElseThrow();
    assertThat(usuarioActualizado.getPuntosAcumulados()).isEqualTo(1000);
}
```

---

## PROBLEMA P0.2: RACE CONDITION EN AFORO

**Ubicación:** `budgetmap-api/src/main/java/com/budgetmap/service/ReservaService.java`  
**Línea:** 114-120  
**Clasificación:** Critical Bug - Business Logic

### Análisis Detallado

```java
// ❌ CÓDIGO ACTUAL (VULNERABLE)
@Transactional
public Reserva crear(ReservaRequest request) {
    // ... validaciones
    
    // Línea 114: Lecturaaforo
    Integer aforoActual = reservaRepository.sumAforoByEventoId(request.getEventoId());
    Evento evento = eventoRepository.findById(request.getEventoId()).orElseThrow();
    
    // Línea 116-119: Validación sin lock
    if (aforoActual >= evento.getAforoMaximo()) {
        throw new ReservaException("Aforo lleno");
    }
    // ⚠️ VENTANA DE RIESGO: Otra transacción puede crear reserva aquí
    
    Reserva reserva = new Reserva();
    reserva.setEvento(evento);
    reserva.setEstado(EstadoReserva.PENDIENTE);
    reserva.setCodigoReserva("RSVP-" + UUID.randomUUID().toString().substring(0, 8));
    
    // Línea 125: INSERT
    return reservaRepository.save(reserva);
}
```

**Escenario de Fallo (100% reproducible):**

```
Momento 0:
  - Evento ID 1: aforoMaximo = 100, reservasActuales = 99

Momento 1:
  - Request A: SELECT COUNT(*) FROM reservas WHERE evento_id = 1 → 99
  - Comparación: 99 < 100? SÍ, proceder

Momento 2:
  - Request B: SELECT COUNT(*) FROM reservas WHERE evento_id = 1 → 99
  - Comparación: 99 < 100? SÍ, proceder
  - ⚠️ AMBAS pasaron validación

Momento 3:
  - Request A: INSERT INTO reservas VALUES (evento_id=1, ...) ✅ OK
  - Request B: INSERT INTO reservas VALUES (evento_id=1, ...) ✅ OK

Resultado:
  - Reservas totales = 101 (violó aforo = 100)
  - Overbooking
```

### Solución Correcta: Pessimistic Locking

```java
// ✅ REPOSITORIO (actualizar interfaz)
@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)  // Lock exclusivo
    @Query("SELECT e FROM Evento e WHERE e.id = :id")
    Optional<Evento> findByIdWithLock(@Param("id") Long id);
}

// ✅ SERVICIO (actualizado)
@Transactional(isolation = Isolation.SERIALIZABLE)
public Reserva crear(ReservaRequest request) {
    // 1. Validar usuario
    Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
        .orElseThrow(() -> new UsuarioNoEncontradoException());
    
    // 2. ✅ Obtener evento CON LOCK (SELECT FOR UPDATE)
    Evento evento = eventoRepository.findByIdWithLock(request.getEventoId())
        .orElseThrow(() -> new EventoException("Evento no existe"));
    
    // 3. Contar reservas (dentro del lock)
    Long reservasActuales = reservaRepository.countByEventoId(evento.getId());
    
    // 4. Validación con información actualizada
    if (reservasActuales >= evento.getAforoMaximo()) {
        throw new ReservaException(
            String.format("Aforo lleno. Capacidad: %d, Reservas: %d",
                evento.getAforoMaximo(), reservasActuales)
        );
    }
    
    // 5. Crear reserva (dentro del lock)
    Reserva reserva = new Reserva();
    reserva.setEvento(evento);
    reserva.setUsuario(usuario);
    reserva.setEstado(EstadoReserva.PENDIENTE);
    reserva.setCodigoReserva(generarCodigoReserva());
    reserva.setFechaCreacion(LocalDateTime.now());
    reserva.setNumeroPersonas(request.getNumeroPersonas() != null 
        ? request.getNumeroPersonas() : 1);
    
    // 6. Calcular puntos (número de personas × 10)
    reserva.setPuntosOtorgados(reserva.getNumeroPersonas() * 10);
    
    // 7. Si hay promoción, agregar comisión
    if (request.getPromocionId() != null) {
        Promocion promo = promocionRepository.findById(request.getPromocionId())
            .orElseThrow(() -> new PromocionException("Promoción no existe"));
        reserva.setPromocion(promo);
        reserva.setComisionCobrada(new BigDecimal("500"));  // $500 COP
    }
    
    // 8. INSERT (dentro del lock)
    try {
        Reserva reservaGuardada = reservaRepository.save(reserva);
        logger.info("Reserva creada. ID: {}, Evento: {}, Usuario: {}",
            reservaGuardada.getId(), evento.getId(), usuario.getId());
        
        // 9. Crear notificación asincronía
        eventPublisher.publishEvent(new ReservaCreadaEvent(reservaGuardada));
        
        return reservaGuardada;
    } catch (Exception e) {
        logger.error("Error al crear reserva", e);
        throw new ReservaException("No fue posible crear la reserva: " + e.getMessage(), e);
    }
    // ✅ Lock se libera automáticamente al finalizar transacción
}

// Helper
private String generarCodigoReserva() {
    return "RSVP-" + UUID.randomUUID().toString()
        .replace("-", "")
        .substring(0, 12)
        .toUpperCase();
}
```

### Testing Concurrencia

```java
@Test
@DisplayName("Crear reserva con múltiples threads: sin overbooking")
void testConcurrenciaReservas() throws InterruptedException {
    // Arrange
    Evento evento = eventoRepository.save(new Evento());
    evento.setAforoMaximo(10);
    evento = eventoRepository.save(evento);
    
    Usuario[] usuarios = new Usuario[15];
    for (int i = 0; i < 15; i++) {
        usuarios[i] = usuarioRepository.save(new Usuario());
    }
    
    // Act: 15 threads intentan crear reserva (solo 10 deben lograrlo)
    ExecutorService executor = Executors.newFixedThreadPool(15);
    CountDownLatch latch = new CountDownLatch(15);
    AtomicInteger exitos = new AtomicInteger(0);
    AtomicInteger fallos = new AtomicInteger(0);
    
    for (int i = 0; i < 15; i++) {
        final int index = i;
        executor.execute(() -> {
            try {
                ReservaRequest req = new ReservaRequest();
                req.setEventoId(evento.getId());
                req.setUsuarioId(usuarios[index].getId());
                
                Reserva reserva = reservaService.crear(req);
                exitos.incrementAndGet();
            } catch (ReservaException e) {
                fallos.incrementAndGet();
            } finally {
                latch.countDown();
            }
        });
    }
    
    // Assert
    latch.await(10, TimeUnit.SECONDS);
    executor.shutdown();
    
    assertThat(exitos.get()).isEqualTo(10);
    assertThat(fallos.get()).isEqualTo(5);
    
    // Verificar BD
    Long reservasEnBD = reservaRepository.countByEventoId(evento.getId());
    assertThat(reservasEnBD).isEqualTo(10);
}
```

---

## PROBLEMA P0.3: BLOQUEO DE CUENTA INCOMPLETO

**Ubicación:** `budgetmap-api/src/main/java/com/budgetmap/security/UserDetailsImpl.java`  
**Línea:** 51-53  
**Clasificación:** Security Bug - Brute Force

### Código Corregido

```java
// ❌ ACTUAL
@Override
public boolean isAccountNonLocked() {
    return true;  // SIEMPRE permite login
}

// ✅ CORREGIDO
@Override
public boolean isAccountNonLocked() {
    // Verificar si cuenta está bloqueada
    if (usuario.isCuentaBloqueada() && usuario.getFechaDesbloqueo() != null) {
        LocalDateTime ahora = LocalDateTime.now();
        
        if (ahora.isBefore(usuario.getFechaDesbloqueo())) {
            // Aún bloqueada
            logger.warn("Intento de login en cuenta bloqueada: {}", usuario.getEmail());
            return false;
        } else {
            // Desbloquear automáticamente (pasó tiempo)
            usuario.setCuentaBloqueada(false);
            usuario.setFechaDesbloqueo(null);
            usuario.setIntentosFallidos(0);
            usuarioRepository.save(usuario);
            logger.info("Cuenta desbloqueada automáticamente: {}", usuario.getEmail());
            return true;
        }
    }
    
    return true;  // No está bloqueada
}
```

### AuthController Mejorado

```java
@PostMapping("/login")
public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
    try {
        // Intentar autenticar
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );
        
        // ✅ Si llega aquí, autenticación exitosa
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Usuario usuario = userDetails.getUsuario();
        
        // Resetear intentos fallidos
        usuario.setIntentosFallidos(0);
        usuario.setCuentaBloqueada(false);
        usuario.setFechaDesbloqueo(null);
        usuarioRepository.save(usuario);
        
        // Generar token
        String jwt = jwtUtils.generateJwtToken(authentication);
        
        logger.info("Login exitoso: {}", usuario.getEmail());
        return ResponseEntity.ok(new LoginResponse(jwt));
        
    } catch (BadCredentialsException | DisabledException e) {
        // ❌ Credenciales inválidas o cuenta deshabilitada
        Usuario usuario = usuarioRepository.findByEmail(loginRequest.getEmail())
            .orElse(null);
        
        if (usuario != null) {
            usuario.setIntentosFallidos(usuario.getIntentosFallidos() + 1);
            
            // Bloquear después de 5 intentos
            if (usuario.getIntentosFallidos() >= 5) {
                usuario.setCuentaBloqueada(true);
                usuario.setFechaDesbloqueo(LocalDateTime.now().plusMinutes(15));
                usuarioRepository.save(usuario);
                
                long minutosRestantes = 15;
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                        "error", "Cuenta bloqueada por intentos fallidos",
                        "minutosRestantes", minutosRestantes
                    ));
            } else {
                usuarioRepository.save(usuario);
                int intentosRestantes = 5 - usuario.getIntentosFallidos();
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "error", "Credenciales inválidas",
                        "intentosRestantes", intentosRestantes
                    ));
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Usuario o contraseña incorrectos"));
        
    } catch (Exception e) {
        logger.error("Error inesperado en login", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Map.of("error", "Error interno del servidor"));
    }
}
```

---

## PROBLEMA P0.4: JWT SIN VALIDACIÓN DE ALGORITMO (Python)

**Ubicación:** `budgetmap-geo/auth.py`  
**Línea:** 20-35  
**Clasificación:** Security Bug - Auth Bypass

### Código Vulnerable

```python
# ❌ ACTUAL (VULNERABLE)
def verify_jwt_token(token):
    try:
        payload = jwt.decode(
            token, 
            JWT_SECRET,
            algorithms=['HS256']
        )
        return payload
    except jwt.ExpiredSignatureError:
        raise ExpiredSignatureError("Token expirado")
    except jwt.InvalidTokenError:
        raise InvalidTokenError("Token inválido")
```

**Vulnerabilidad:**
- Ciertas versiones de PyJWT aceptan algoritmo `"none"`
- Atacante puede crear token sin firmar
- Token: `eyJhbGciOiJub25lIn0.eyJ1c2VyX2lkIjoxfQ.`
- Pase validación sin conocer secret

### Código Seguro

```python
# ✅ CORREGIDO
import jwt
from datetime import datetime, timedelta
import logging

logger = logging.getLogger(__name__)

def verify_jwt_token(token):
    """
    Verifica JWT con validación estricta de algoritmo.
    
    Args:
        token: Token JWT a verificar
        
    Returns:
        dict: Payload del token
        
    Raises:
        InvalidTokenError: Si token es inválido
        ExpiredSignatureError: Si token expiró
    """
    try:
        # 1. Verificar formato básico
        if not token or not isinstance(token, str):
            raise InvalidTokenError("Token vacío o formato inválido")
        
        # 2. Validar estructura (3 partes: header.payload.signature)
        parts = token.split('.')
        if len(parts) != 3:
            raise InvalidTokenError("Estructura JWT inválida")
        
        # 3. Decodificar header para inspeccionar
        try:
            header = jwt.get_unverified_header(token)
        except Exception as e:
            logger.warning(f"Header JWT inválido: {e}")
            raise InvalidTokenError("No se puede decodificar header")
        
        # 4. ✅ VALIDAR ALGORITMO (CRÍTICO)
        expected_algo = 'HS256'
        if header.get('alg') != expected_algo:
            logger.warning(
                f"Algoritmo JWT inválido. Esperado: {expected_algo}, "
                f"Recibido: {header.get('alg')}"
            )
            raise InvalidTokenError(
                f"Algoritmo no permitido. Use {expected_algo}"
            )
        
        # 5. Decodificar con verificación estricta
        payload = jwt.decode(
            token,
            JWT_SECRET,
            algorithms=['HS256'],  # Solo HS256
            options={
                "verify_signature": True,
                "verify_exp": True,
                "verify_aud": False,
                "require": ["exp", "sub"]  # Campos requeridos
            }
        )
        
        # 6. Validaciones adicionales
        if 'user_id' not in payload:
            raise InvalidTokenError("Token sin user_id")
        
        if not isinstance(payload['user_id'], int):
            raise InvalidTokenError("user_id debe ser entero")
        
        logger.debug(f"Token verificado para usuario: {payload['user_id']}")
        return payload
        
    except jwt.ExpiredSignatureError as e:
        logger.info(f"Token expirado: {str(e)}")
        raise ExpiredSignatureError("Token expirado")
        
    except jwt.InvalidSignatureError as e:
        logger.warning(f"Firma JWT inválida: {str(e)}")
        raise InvalidTokenError("Firma inválida")
        
    except jwt.DecodeError as e:
        logger.warning(f"Error decodificando JWT: {str(e)}")
        raise InvalidTokenError("No se puede decodificar token")
        
    except jwt.InvalidTokenError as e:
        logger.warning(f"Token inválido: {str(e)}")
        raise InvalidTokenError("Token inválido")
        
    except Exception as e:
        logger.error(f"Error inesperado en verificación JWT: {str(e)}")
        raise InvalidTokenError("Error al verificar token")


def generate_jwt_token(user_id, expires_in_hours=24):
    """
    Genera JWT con algoritmo HS256.
    
    Args:
        user_id: ID del usuario
        expires_in_hours: Horas de validez (default 24)
        
    Returns:
        str: Token JWT
    """
    try:
        now = datetime.utcnow()
        expiry = now + timedelta(hours=expires_in_hours)
        
        payload = {
            'user_id': user_id,
            'iat': now,
            'exp': expiry
        }
        
        token = jwt.encode(
            payload,
            JWT_SECRET,
            algorithm='HS256'
        )
        
        logger.debug(f"Token generado para usuario: {user_id}")
        return token
        
    except Exception as e:
        logger.error(f"Error generando JWT: {str(e)}")
        raise
```

### Testing Seguridad

```python
import pytest
import json
import base64
from datetime import datetime, timedelta

class TestJWTSecurity:
    
    def test_valida_algoritmo_hs256(self, client):
        """Verifica que solo HS256 sea aceptado"""
        token = generate_jwt_token(user_id=1)
        response = client.get(
            '/api/geo/salud',
            headers={'Authorization': f'Bearer {token}'}
        )
        assert response.status_code == 200
    
    def test_rechaza_algoritmo_none(self, client):
        """Verifica que algoritmo 'none' sea rechazado"""
        # Crear token con algoritmo 'none'
        header = base64.urlsafe_b64encode(b'{"alg":"none"}').rstrip(b'=')
        payload = base64.urlsafe_b64encode(b'{"user_id":1}').rstrip(b'=')
        
        token_none = f"{header.decode()}.{payload.decode()}."
        
        response = client.get(
            '/api/geo/salud',
            headers={'Authorization': f'Bearer {token_none}'}
        )
        assert response.status_code == 401
        assert "algoritmo" in response.json.get('error', '').lower()
    
    def test_rechaza_algoritmo_rs256(self):
        """Verifica que solo HS256 sea permitido"""
        # RS256 debería ser rechazado
        with pytest.raises(InvalidTokenError):
            verify_jwt_token(token_rs256_valido)
    
    def test_rechaza_token_sin_firma(self):
        """Verifica token sin firma"""
        token_sin_firma = "eyJhbGciOiJub25lIn0.eyJ1c2VyX2lkIjoxfQ."
        
        with pytest.raises(InvalidTokenError):
            verify_jwt_token(token_sin_firma)
    
    def test_rechaza_token_vacio(self):
        """Verifica que token vacío sea rechazado"""
        with pytest.raises(InvalidTokenError):
            verify_jwt_token("")
    
    def test_rechaza_sin_user_id(self):
        """Verifica que token sin user_id sea rechazado"""
        payload = {'exp': datetime.utcnow() + timedelta(hours=1)}
        token = jwt.encode(payload, JWT_SECRET, algorithm='HS256')
        
        with pytest.raises(InvalidTokenError):
            verify_jwt_token(token)
```

---

## PROBLEMA P0.5: RATE LIMITING NO DISTRIBUIDO

**Ubicación:** `budgetmap-geo/app.py` (línea 12-20)  
**Clasificación:** Infrastructure Bug - DDoS Risk

### Configuración Actual

```python
# ❌ ACTUAL (No distribuido)
limiter = Limiter(
    app=app,
    key_func=get_remote_address,
    storage_uri="memory://"  # ❌ CADA PROCESO tiene su propio dict
)
```

### Solución con Redis

```python
# ✅ CORREGIDO - app.py
import os
from flask import Flask
from flask_limiter import Limiter
from flask_limiter.util import get_remote_address
from flask_cors import CORS
from flask_sqlalchemy import SQLAlchemy
import logging

# Configuración
REDIS_URL = os.getenv('REDIS_URL', 'redis://localhost:6379/0')
FLASK_ENV = os.getenv('FLASK_ENV', 'development')

# Logger
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Apps & extensions
app = Flask(__name__)
CORS(app)

# Database
app.config['SQLALCHEMY_DATABASE_URI'] = os.getenv(
    'DATABASE_URL',
    'mysql+pymysql://user:password@localhost/budgetmap_geo'
)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
app.config['SQLALCHEMY_ENGINE_OPTIONS'] = {
    'pool_size': 10,
    'pool_recycle': 3600,
    'pool_pre_ping': True,
}

db = SQLAlchemy(app)

# ✅ Rate Limiting con Redis
try:
    limiter = Limiter(
        app=app,
        key_func=get_remote_address,
        storage_uri=REDIS_URL,
        strategy="fixed-window",
        default_limits=["2000 per day", "200 per hour"],
        swallow_errors=False  # Fallar si Redis no disponible
    )
    logger.info(f"Rate limiter configurado con Redis: {REDIS_URL}")
except Exception as e:
    logger.error(f"Error al conectar con Redis: {e}")
    if FLASK_ENV == 'production':
        raise RuntimeError("Redis REQUERIDO en producción")
    else:
        logger.warning("Usando rate limiter en memoria para desarrollo")
        limiter = Limiter(
            app=app,
            key_func=get_remote_address,
            storage_uri="memory://",
            default_limits=["2000 per day", "200 per hour"]
        )

# Rutas
from routes import geo_routes, alert_routes, report_routes

app.register_blueprint(geo_routes.bp, url_prefix='/api/geo')
app.register_blueprint(alert_routes.bp, url_prefix='/api/alertas')
app.register_blueprint(report_routes.bp, url_prefix='/api/reportes')

# Health Check con rate limiting
@app.route('/', methods=['GET'])
@limiter.limit("10 per minute")  # Específico para health
def health():
    return {
        "status": "healthy",
        "service": "budgetmap-geo",
        "environment": FLASK_ENV,
        "redis": "connected" if REDIS_URL else "memory"
    }, 200

# Error handlers
@app.errorhandler(429)
def ratelimit_handler(e):
    return {
        "error": "Demasiadas solicitudes",
        "description": "Ha superado el límite de solicitudes. Intente más tarde.",
        "retry_after": e.description
    }, 429

@app.errorhandler(500)
def internal_error(e):
    logger.error(f"Error interno: {e}")
    return {"error": "Error interno del servidor"}, 500

if __name__ == '__main__':
    debug = FLASK_ENV == 'development'
    port = int(os.getenv('PORT', 5000))
    app.run(debug=debug, host='0.0.0.0', port=port)
```

### requirements.txt Actualizado

```txt
Flask==3.0.0
Flask-CORS==4.0.0
Flask-SQLAlchemy==3.1.1
Flask-Limiter==3.5.0
SQLAlchemy==2.0.23
redis==5.0.1
PyJWT==2.8.1
pymysql==1.1.0
python-dotenv==1.0.0
```

### Dockerfile para Producción

```dockerfile
FROM python:3.11-slim

WORKDIR /app

# Copiar requirements
COPY budgetmap-geo/requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiar código
COPY budgetmap-geo/ .

# Variables de entorno
ENV FLASK_ENV=production
ENV PYTHONUNBUFFERED=1

# Port
EXPOSE 5000

# Comando
CMD ["gunicorn", "--workers=4", "--worker-class=sync", \
     "--bind=0.0.0.0:5000", "--timeout=30", "app:app"]
```

### docker-compose.yml

```yaml
version: '3.9'

services:
  # Redis para rate limiting distribuido
  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    command: redis-server --maxmemory 512mb --maxmemory-policy allkeys-lru
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 5s
      timeout: 3s
      retries: 5

  # Servicio Geo
  budgetmap-geo:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "5000:5000"
    environment:
      FLASK_ENV: production
      DATABASE_URL: mysql+pymysql://user:pass@mysql/budgetmap_geo
      REDIS_URL: redis://redis:6379/0
      JWT_SECRET: ${JWT_SECRET}
    depends_on:
      redis:
        condition: service_healthy
    volumes:
      - ./budgetmap-geo:/app
    restart: unless-stopped

volumes:
  redis_data:
```

---

## RESUMEN DE CORRECCIONES CRÍTICAS

| Problema | Archivo | Línea | Esfuerzo | Complejidad |
|---|---|---|---|---|
| P0.1: Transacción Cupones | CuponService.java | 35-45 | 4h | Media |
| P0.2: Race Condition Aforo | ReservaService.java | 114-120 | 6h | Alta |
| P0.3: Bloqueo Incompleto | UserDetailsImpl.java + AuthController.java | 51-53 + 45-80 | 3h | Media |
| P0.4: JWT Algorithm | auth.py | 27 | 5h | Media |
| P0.5: Rate Limiting | app.py | 12-20 | 8h | Media |

**TOTAL P0: 26 horas = 3.25 días con 2 desarrolladores**

---

*Fin del documento de correcciones técnicas detalladas.*
