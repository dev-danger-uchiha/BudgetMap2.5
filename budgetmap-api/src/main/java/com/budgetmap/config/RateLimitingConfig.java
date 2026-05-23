package com.budgetmap.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
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

    private final Map<String, Bucket> generalBuckets = new ConcurrentHashMap<>();

    private final Map<String, Bucket> authBuckets = new ConcurrentHashMap<>();

    private Bucket createGeneralBucket() {
        Bandwidth limit = Bandwidth.classic(
                100, // capacidad
                Refill.intervally(100, Duration.ofMinutes(1)) // recarga
        );
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Bucket createAuthBucket() {
        Bandwidth limit = Bandwidth.classic(
                5, // capacidad
                Refill.intervally(5, Duration.ofMinutes(1)) // recarga
        );
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

        Bucket bucket;
        boolean isAuthEndpoint = path.contains("/api/auth/login") ||
                path.contains("/api/auth/registro");

        if (isAuthEndpoint) {
            bucket = authBuckets.computeIfAbsent(clientIp, k -> createAuthBucket());
        } else {
            bucket = generalBuckets.computeIfAbsent(clientIp, k -> createGeneralBucket());
        }

        if (bucket.tryConsume(1)) {
            addRateLimitHeaders(response, bucket);
            filterChain.doFilter(request, response);
        } else {
            log.warn("Rate limit excedido para IP: {} en endpoint: {}", clientIp, path);

            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"error\":\"Demasiadas solicitudes\",\"message\":\"Por favor espere antes de intentar nuevamente\"}");
        }
    }

    private void addRateLimitHeaders(HttpServletResponse response, Bucket bucket) {
        response.setHeader("X-RateLimit-Remaining",
                String.valueOf(bucket.getAvailableTokens()));

        response.setHeader("X-RateLimit-Limit",
                String.valueOf(bucket.getAvailableTokens() + 1));
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }

    public void cleanupOldBuckets() {
        log.debug("Limpiando buckets de rate limiting");
    }
}