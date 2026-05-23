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
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(
                Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
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
                            response.getWriter()
                                    .write("{\"error\":\"No autorizado\",\"message\":\"Token invalido o ausente\"}");
                        })
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            response.setContentType("application/json");
                            response.getWriter().write(
                                    "{\"error\":\"Prohibido\",\"message\":\"No tiene permisos para este recurso\"}");
                        }))

                .authorizeHttpRequests(auth -> auth
                        // --- 1. ARCHIVOS ESTÁTICOS Y PÁGINAS PÚBLICAS ---
                        // Liberamos index, login, register y la nueva carpeta info con todo su
                        // contenido
                        .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/favicon.ico")
                        .permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/info/**").permitAll()

                        // Carpetas de roles (permitimos cargar el HTML, el JS validará el token
                        // después)
                        .requestMatchers("/explorador/**", "/aliado/**", "/anfitrion/**", "/admin/**", "/soporte/**")
                        .permitAll()

                        // --- 2. API PÚBLICA ---
                        .requestMatchers("/api/auth/**", "/api/geo/**", "/error/**", "/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/lugares/aprobados/**",
                                "/api/establecimientos/aprobados/**")
                        .permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/eventos/**", "/api/promociones/activas/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()

                        // --- 3. REGLAS POR ROL (API) ---
                        .requestMatchers("/api/admin/**", "/api/usuarios/**", "/api/moderadores/**")
                        .hasRole("ADMINISTRADOR")
                        .requestMatchers("/api/aprobaciones/**", "/api/pqrs/asignados/**")
                        .hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers("/api/mi-establecimiento/**").hasRole("LOCAL_ALIADO")
                        .requestMatchers("/api/mis-reservas/**").hasAnyRole("LOCAL_ALIADO", "EXPLORADOR")
                        .requestMatchers("/api/mis-eventos/**").hasRole("ANFITRION")

                        // --- 4. PROTECCIÓN RESTANTE ---
                        .anyRequest().authenticated());

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}