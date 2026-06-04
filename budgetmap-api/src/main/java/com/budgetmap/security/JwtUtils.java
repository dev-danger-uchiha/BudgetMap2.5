package com.budgetmap.security;

import com.budgetmap.exception.JwtException;
import com.budgetmap.model.enums.RolUsuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private int jwtExpirationMs;

    private SecretKey signingKey;

    @jakarta.annotation.PostConstruct
    public void init() {
        if (!StringUtils.hasText(jwtSecret)) {
            throw new JwtException(
                    "ERROR CRITICO: JWT_SECRET no esta configurado. " +
                            "Debe definir la variable de entorno JWT_SECRET antes de iniciar la aplicacion.");
        }

        if (jwtSecret.length() < 32) {
            throw new JwtException(
                    "ERROR CRITICO: JWT_SECRET debe tener al menos 32 caracteres. " +
                            "Longitud actual: " + jwtSecret.length());
        }

        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        log.info("JWT Utils inicializado correctamente");
    }

    private SecretKey getSigningKey() {
        return signingKey;
    }

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .subject(String.valueOf(userPrincipal.getId())) // Using ID instead of email to avoid PII exposure
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateTokenFromUsername(String username, Long id, String nombre, RolUsuario rol) {
        return Jwts.builder()
                .subject(String.valueOf(id)) // Using ID instead of email
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String getSubjectFromJwtToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado al obtener subject: {}", maskToken(token));
            throw e;
        }
    }

    public Long getUserIdFromJwtToken(String token) {
        try {
            String subject = getSubjectFromJwtToken(token);
            return Long.parseLong(subject);
        } catch (Exception e) {
            log.error("Error al obtener userId del token: {}", e.getMessage());
            return null;
        }
    }

    // Method kept for compatibility but should not be used anymore since rol is removed from token.
    public RolUsuario getRolFromJwtToken(String token) {
        log.warn("Intentando obtener rol desde el token, pero el claim fue removido por seguridad.");
        return null;
    }

    public Claims getAllClaimsFromToken(String token) {
        return parseClaims(token);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            parseClaims(authToken);
            return true;

        } catch (SecurityException e) {
            log.error("Firma JWT invalida: {}", e.getMessage());

        } catch (MalformedJwtException e) {
            log.error("Token JWT mal formado: {}", e.getMessage());

        } catch (ExpiredJwtException e) {
            log.debug("Token JWT expirado: {}", maskToken(authToken));

        } catch (UnsupportedJwtException e) {
            log.error("Token JWT no soportado: {}", e.getMessage());

        } catch (IllegalArgumentException e) {
            log.error("Token JWT vacio o null");
        }

        return false;
    }

    public Date getExpirationDateFromToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getExpiration();
        }
    }

    public boolean isTokenExpired(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private String maskToken(String token) {
        if (token == null || token.length() < 15) {
            return "***";
        }
        return token.substring(0, 10) + "..." + token.substring(token.length() - 5);
    }
}