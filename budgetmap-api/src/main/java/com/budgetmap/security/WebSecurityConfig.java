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

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
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
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
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
                .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/favicon.ico").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/info/**").permitAll()
                .requestMatchers("/explorador/**", "/aliado/**", "/anfitrion/**", "/admin/**", "/soporte/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/error/**", "/health").permitAll()

                // ======================================================
                // 2. ENDPOINTS DE API PÚBLICOS (Sin Token)
                // ======================================================
                .requestMatchers("/api/auth/**", "/api/destacados/**", "/api/geo/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/lugares/aprobados/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/establecimientos/aprobados/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/eventos/activos/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/promociones/activas/**").permitAll()

                // ======================================================
                // 3. RUTAS COMPARTIDAS / COMUNES (Cualquier Rol Autenticado)
                // ======================================================
                .requestMatchers("/api/usuarios/me").authenticated()

                // ======================================================
                // 4. REGLAS ESPECÍFICAS PARA EL ROL: EXPLORADOR
                // ======================================================
                .requestMatchers("/api/config-alertas/**").hasRole("EXPLORADOR")
                .requestMatchers("/api/notificaciones/**").hasRole("EXPLORADOR")
                .requestMatchers(HttpMethod.POST, "/api/reservas").hasRole("EXPLORADOR")
                .requestMatchers("/api/reservas/mis-reservas").hasRole("EXPLORADOR")
                .requestMatchers("/api/reservas/*/cancelar").hasRole("EXPLORADOR")

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
                .requestMatchers(HttpMethod.GET, "/api/establecimientos/*").hasAnyRole("EXPLORADOR", "LOCAL_ALIADO", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/eventos/*").hasAnyRole("EXPLORADOR", "ANFITRION", "ADMINISTRADOR")
                .requestMatchers(HttpMethod.GET, "/api/promociones/establecimiento/*").hasAnyRole("EXPLORADOR", "LOCAL_ALIADO")
                .requestMatchers(HttpMethod.GET, "/api/promociones/mi-establecimiento/*").hasAnyRole("EXPLORADOR", "LOCAL_ALIADO")

                // ======================================================
                // 8. REGLAS EXCLUSIVAS PARA: MODERADOR / ADMINISTRADOR
                // ======================================================
                .requestMatchers("/api/establecimientos/admin/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                .requestMatchers("/api/lugares/admin/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                .requestMatchers("/api/pqrs/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                .requestMatchers("/api/aprobaciones/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                .requestMatchers("/api/moderadores/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/admin/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/api/usuarios/**").hasRole("ADMINISTRADOR")

                // ======================================================
                // 9. REGLA DE PROTECCIÓN DE SEGURIDAD ABSOLUTA
                // ======================================================
                .anyRequest().authenticated()
            );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}