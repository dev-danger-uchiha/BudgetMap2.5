package com.budgetmap.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed-origins:http://localhost:3000,https://budgetmap-api.onrender.com}")
    private String corsAllowedOrigins;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOrigins = Arrays.asList(corsAllowedOrigins.split(","));
        
        configuration.setAllowedOriginPatterns(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Nota de Seguridad: CSRF está deshabilitado porque la API es Stateless y usa tokens JWT en la cabecera Authorization.
            // Si los tokens JWT se migraran a cookies (HttpOnly), CSRF DEBE ser habilitado obligatoriamente.
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // 0. IMPONER HTTPS Y CABECERAS DE SEGURIDAD BASE
            // Nota: Se delega el forzado de HTTPS a la infraestructura (Render) para no romper el entorno local (localhost).
            .headers(headers -> headers
                .frameOptions(frame -> frame.deny()) // Proteccion contra Clickjacking (X-Frame-Options: DENY)
                // CSP Relajado para permitir estilos en línea, fuentes externas (Google Fonts) y CDNs
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src * 'unsafe-inline' 'unsafe-eval' data: blob:; frame-ancestors 'none';")) 
                .httpStrictTransportSecurity(hsts -> hsts.includeSubDomains(true).maxAgeInSeconds(31536000)) // HSTS (1 año)
            )
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"No autorizado\",\"message\":\"Token inválido o ausente\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Prohibido\",\"message\":\"No tiene permisos para este recurso\"}");
                }))
            .authorizeHttpRequests(auth -> auth
                
                // ======================================================
                // 1. RECURSOS PÚBLICOS Y FRONTEND (Páginas y Estáticos)
                // ======================================================
                .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/favicon.ico", "/recuperar-password.html", "/reset-password.html").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/info/**").permitAll()
                .requestMatchers("/explorador/**", "/aliado/**", "/anfitrion/**", "/admin/**", "/soporte/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/error/**", "/health", "/uploads/**").permitAll()

                // ======================================================
                // 2. ENDPOINTS DE API PÚBLICOS (Sin Token)
                // ======================================================
                .requestMatchers("/api/auth/**", "/api/destacados/**", "/api/geo/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/lugares/aprobados/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/establecimientos/aprobados/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/eventos/activos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/promociones/activas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/usuarios/leaderboard").permitAll()

                // ======================================================
                // 3. RUTAS COMPARTIDAS / COMUNES (Cualquier Rol Autenticado)
                // ======================================================
                .requestMatchers("/api/usuarios/me").authenticated()

                // ======================================================
                // 4. REGLAS ESPECÍFICAS PARA EL ROL: EXPLORADOR Y PRO
                // ======================================================
                .requestMatchers("/api/config-alertas/**").hasAnyRole("EXPLORADOR", "EXPLORADOR_PRO")
                .requestMatchers("/api/notificaciones/**").hasAnyRole("EXPLORADOR", "EXPLORADOR_PRO")
                .requestMatchers(HttpMethod.POST, "/api/reservas").hasAnyRole("EXPLORADOR", "EXPLORADOR_PRO")
                .requestMatchers("/api/reservas/mis-reservas").hasAnyRole("EXPLORADOR", "EXPLORADOR_PRO")
                .requestMatchers("/api/reservas/*/cancelar").hasAnyRole("EXPLORADOR", "EXPLORADOR_PRO")
                .requestMatchers("/api/pasarela/**").hasAnyRole("EXPLORADOR", "EXPLORADOR_PRO")

                // ======================================================
                // 5. REGLAS ESPECÍFICAS PARA EL ROL: LOCAL_ALIADO
                // ======================================================
                .requestMatchers("/api/establecimientos/mi-establecimiento").hasRole("LOCAL_ALIADO")
                .requestMatchers(HttpMethod.POST, "/api/establecimientos").hasRole("LOCAL_ALIADO")
                .requestMatchers(HttpMethod.PUT, "/api/establecimientos/**").hasRole("LOCAL_ALIADO")
                .requestMatchers("/api/promociones/mis-promociones").hasRole("LOCAL_ALIADO")
                .requestMatchers(HttpMethod.POST, "/api/promociones").hasRole("LOCAL_ALIADO")
                .requestMatchers(HttpMethod.DELETE, "/api/promociones/**").hasRole("LOCAL_ALIADO")
                .requestMatchers("/api/reservas/establecimiento/**").hasRole("LOCAL_ALIADO")
                .requestMatchers("/api/reservas/redimir/**").hasRole("LOCAL_ALIADO")

                // ======================================================
                // 6. REGLAS ESPECÍFICAS PARA EL ROL: ANFITRIÓN
                // ======================================================
                .requestMatchers("/api/eventos/mis-eventos").hasRole("ANFITRION")
                .requestMatchers("/api/eventos/mis-estadisticas").hasRole("ANFITRION")
                .requestMatchers(HttpMethod.POST, "/api/eventos").hasRole("ANFITRION")
                .requestMatchers(HttpMethod.DELETE, "/api/eventos/**").hasRole("ANFITRION")
                .requestMatchers("/api/reservas/*/confirmar").hasRole("ANFITRION")

                // ======================================================
                // 7. REGLAS MULTI-ROL (Lecturas Cruzadas de Datos)
                // ======================================================
                .requestMatchers(HttpMethod.GET, "/api/establecimientos/*").hasAnyRole("EXPLORADOR", "EXPLORADOR_PRO", "LOCAL_ALIADO", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/eventos/*").hasAnyRole("EXPLORADOR", "EXPLORADOR_PRO", "ANFITRION", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/promociones/establecimiento/*").hasAnyRole("EXPLORADOR", "EXPLORADOR_PRO", "LOCAL_ALIADO", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/promociones/mi-establecimiento/*").hasAnyRole("EXPLORADOR", "EXPLORADOR_PRO", "LOCAL_ALIADO")

                // ======================================================
                // 8. REGLAS EXCLUSIVAS PARA: MODERADOR / ADMINISTRADOR
                // ======================================================
                .requestMatchers("/api/establecimientos/admin/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                .requestMatchers("/api/lugares/admin/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                .requestMatchers("/api/aprobaciones/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                .requestMatchers("/api/moderadores/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")

                // ======================================================
                // 9. REGLA DE PROTECCIÓN DE SEGURIDAD ABSOLUTA
                // ======================================================
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}