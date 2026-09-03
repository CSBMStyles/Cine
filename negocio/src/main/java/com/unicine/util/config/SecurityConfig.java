package com.unicine.util.config;

import java.io.IOException;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unicine.exception.handler.ApiError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Configuracion base de Spring Security para la API REST de UniCine.
 *
 * <p>Objetivo 4.1: baseline stateless, CSRF deshabilitado, CORS explicito,
 * sesiones sin estado y errores 401/403 serializados como {@link ApiError}.
 * No crea usuarios en memoria, no simula JWT ni hardcodea credenciales.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // SECTION: Configuracion base

    /**
     * Cadena de filtros principal para la API.
     *
     * <p>Rutas publicas temporales (catalogo lectura):</p>
     * <ul>
     *   <li>GET /api/peliculas/**, /api/funciones/**, /api/ciudades/**, /api/teatros/**, /api/salas/**</li>
     *   <li>GET /actuator/health</li>
     *   <li>POST /api/auth/** (placeholder para registro/login 4.3.2)</li>
     * </ul>
     * <p>El resto requiere autenticacion. Metodos protegidos con @PreAuthorize se validan
     * via {@code @EnableMethodSecurity}.</p>
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/api/peliculas/**",
                                "/api/funciones/**",
                                "/api/ciudades/**",
                                "/api/teatros/**",
                                "/api/salas/**")
                        .permitAll()
                        .requestMatchers("/actuator/health")
                        .permitAll()
                        .requestMatchers("/api/auth/**")
                        .permitAll()
                        .requestMatchers("/security/public", "/security/public/**")
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler()))
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .logout(logout -> logout.disable());

        return http.build();
    }

    // !SECTION
    // SECTION: Handlers 401/403

    /**
     * 401 Unauthorized con formato ApiError cuando no hay autenticacion.
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                org.springframework.security.core.AuthenticationException authException) -> {
            ApiError error = ApiError.of(
                    HttpStatus.UNAUTHORIZED.value(),
                    HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                    "DOMAIN_USER_AUTH_INVALID_CREDENTIALS",
                    "No autenticado. Inicie sesion para acceder a este recurso.",
                    request.getRequestURI(),
                    List.of());

            try {
                escribirApiError(response, HttpStatus.UNAUTHORIZED.value(), error);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * 403 Forbidden con formato ApiError cuando hay autenticacion pero sin permisos.
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                org.springframework.security.access.AccessDeniedException accessDeniedException) -> {
            ApiError error = ApiError.of(
                    HttpStatus.FORBIDDEN.value(),
                    HttpStatus.FORBIDDEN.getReasonPhrase(),
                    "DOMAIN_USER_AUTH_ACTION_NOT_PERMITTED",
                    "No tiene permisos para realizar esta accion.",
                    request.getRequestURI(),
                    List.of());

            try {
                escribirApiError(response, HttpStatus.FORBIDDEN.value(), error);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private void escribirApiError(HttpServletResponse response, int status, ApiError error) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        // Evitar creacion de sesion por el contenedor
        response.setHeader("Cache-Control", "no-store");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        response.getWriter().write(mapper.writeValueAsString(error));
    }

    // !SECTION
    // SECTION: Cifrado

    /**
     * Encoder para hashing de contrasenas. No se crean usuarios hardcodeados.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // !SECTION
    // SECTION: CORS

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        // Para baseline permisivo; endurecer en Fase 5 cuando el frontend tenga origen fijo
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(false);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // !SECTION
}
