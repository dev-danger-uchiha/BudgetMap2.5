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
                .subject(userPrincipal.getUsername())
                .claim("id", userPrincipal.getId())
                .claim("nombre", userPrincipal.getNombre())
                .claim("rol", userPrincipal.getRol().name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String generateTokenFromUsername(String username, Long id, String nombre, RolUsuario rol) {
        return Jwts.builder()
                .subject(username)
                .claim("id", id)
                .claim("nombre", nombre)
                .claim("rol", rol.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        try {
            Claims claims = parseClaims(token);
            return claims.getSubject();
        } catch (ExpiredJwtException e) {
            log.warn("Token expirado al obtener username: {}", maskToken(token));
            throw e;
        }
    }

    public Long getUserIdFromJwtToken(String token) {
        try {
            Claims claims = parseClaims(token);
            Object idClaim = claims.get("id");

            if (idClaim == null) {
                log.error("Token no contiene claim 'id'");
                return null;
            }

            if (idClaim instanceof Integer) {
                return ((Integer) idClaim).longValue();
            }
            if (idClaim instanceof Long) {
                return (Long) idClaim;
            }

            log.error("Tipo de claim 'id' no valido: {}", idClaim.getClass());
            return null;

        } catch (JwtException e) {
            log.error("Error al obtener userId del token: {}", e.getMessage());
            return null;
        }
    }

    public RolUsuario getRolFromJwtToken(String token) {
        try {
            Claims claims = parseClaims(token);
            String rolStr = claims.get("rol", String.class);

            if (rolStr == null) {
                log.error("Token no contiene claim 'rol'");
                return null;
            }

            try {
                return RolUsuario.valueOf(rolStr);
            } catch (IllegalArgumentException e) {
                log.error("Rol no valido en token: {}", rolStr);
                return null;
            }

        } catch (JwtException e) {
            log.error("Error al obtener rol del token: {}", e.getMessage());
            return null;
        }
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