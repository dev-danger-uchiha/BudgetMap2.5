package com.example.budgetmap.security;

import com.example.budgetmap.model.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

        private final UserDetailsService userDetailsService;

        @Autowired
        public SecurityConfig(UserDetailsService userDetailsService) {
                this.userDetailsService = userDetailsService;
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public DaoAuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider dao = new DaoAuthenticationProvider();
                dao.setUserDetailsService(userDetailsService);
                dao.setPasswordEncoder(passwordEncoder());
                return dao;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .authenticationProvider(authenticationProvider())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/h2-console/**") // permitir H2 console si
                                                                                           // aplica
                                )
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/", "/home", "/login", "/registro", "/css/**",
                                                                "/js/**", "/img/**", "/h2-console/**")
                                                .permitAll()
                                                .requestMatchers("/admin/**", "/usuarios/**")
                                                .hasAuthority(Role.ROL_ADMIN.name())
                                                .requestMatchers("/moderador/**")
                                                .hasAuthority(Role.ROL_MODERADOR.name())
                                                .requestMatchers("/establecimiento/**")
                                                .hasAuthority(Role.ROL_ESTABLECIMIENTO.name())
                                                .requestMatchers("/cliente/**", "/perfil/**")
                                                .hasAuthority(Role.ROL_CLIENTE.name())
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .defaultSuccessUrl("/redirigir", true)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                                .logoutSuccessUrl("/login?logout")
                                                .invalidateHttpSession(true)
                                                .clearAuthentication(true)
                                                .deleteCookies("JSESSIONID"))
                                .exceptionHandling(ex -> ex
                                                .accessDeniedPage("/login?denied"))
                                .sessionManagement(session -> session
                                                .maximumSessions(1)
                                                .maxSessionsPreventsLogin(false));

                // permitir H2 console en desarrollo
                http.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()));

                return http.build();
        }
}
