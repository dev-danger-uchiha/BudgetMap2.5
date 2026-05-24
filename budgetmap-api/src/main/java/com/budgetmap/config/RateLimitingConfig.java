package com.budgetmap.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Order(1)
public class RateLimitingConfig extends OncePerRequestFilter {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(RateLimitingConfig.class);

    // Balde general: 1 por cada IP que hace peticiones normales
    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    // Balde de auth: 1 por cada IP que intenta login/registro
    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();

    /**
     * Balde general: 100 peticiones por minuto por IP.
     * ✅ Corregido para Bucket4j 8.x con Bandwidth.builder()
     */
    private Bucket createGeneralBucket() {
        Bandwidth limit = Bandwidth.simple(100, Duration.ofMinutes(1));
        
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    /**
     * Balde de auth: máximo 5 intentos de login/registro por minuto por IP.
     * Protege contra ataques de fuerza bruta.
     * ✅ Corregido para Bucket4j 8.x con Bandwidth.builder()
     */
    private Bucket createAuthBucket() {
        Bandwidth limit = Bandwidth.simple(5, Duration.ofMinutes(1));
        
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String clientIp = getClientIP(request);
        String path = request.getRequestURI();

        // Detectar si es un endpoint de autenticación para usar el balde más estricto
        boolean isAuthEndpoint = path.contains("/api/auth/login") ||
                path.contains("/api/auth/registro");

        Bucket bucket;
        if (isAuthEndpoint) {
            bucket = authBuckets.computeIfAbsent(clientIp, k -> createAuthBucket());
        } else {
            bucket = generalBuckets.computeIfAbsent(clientIp, k -> createGeneralBucket());
        }

        // Intentar consumir 1 token. Si hay tokens disponibles, deja pasar la petición.
        if (bucket.tryConsume(1)) {
            addRateLimitHeaders(response, bucket);
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit excedido para IP: {} en endpoint: {}", clientIp, path);

            response.setStatus(429);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    "{\"error\":\"Demasiadas solicitudes\",\"message\":\"Por favor espere antes de intentar nuevamente\"}");
        }
    }

    /**
     * Agrega headers informativos a la respuesta para que el frontend
     * sepa cuántas peticiones le quedan disponibles.
     */
    private void addRateLimitHeaders(HttpServletResponse response, Bucket bucket) {
        long tokensRestantes = bucket.getAvailableTokens();
        response.setHeader("X-RateLimit-Remaining", String.valueOf(tokensRestantes));
        response.setHeader("X-RateLimit-Limit", String.valueOf(tokensRestantes + 1));
    }

    /**
     * Extrae la IP real del cliente.
     * Si hay un proxy o Nginx por delante, usa el header X-Forwarded-For.
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        // X-Forwarded-For puede tener múltiples IPs separadas por coma.
        // La primera es siempre la del cliente original.
        return xfHeader.split(",")[0].trim();
    }

    /**
     * Método para limpiar baldes viejos si se implementa un scheduler.
     * Por ahora solo loguea. Se puede activar con @Scheduled si se necesita.
     */
    public void cleanupOldBuckets() {
        log.debug("Limpiando buckets de rate limiting. General: {}, Auth: {}",
                generalBuckets.size(), authBuckets.size());
        // Opcional: agregar lógica para limpiar IPs que llevan mucho tiempo inactivas
    }
}