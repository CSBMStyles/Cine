package com.unicine.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Beans JWT que solo existen en contexto completo.
 * Los slices @WebMvcTest no importan esta clase: reciben el conversor
 * de negacion via ObjectProvider en SecurityConfig.
 */
@Configuration
public class JwtConfig {

    // SECTION: Conversor

    /**
     * Conversor de JWT validado a autenticacion con UsuarioPrincipal.
     */
    @Bean
    public JwtPrincipalConverter jwtPrincipalConverter(JwtServicio jwtServicio) {
        return new JwtPrincipalConverter(jwtServicio);
    }

    // !SECTION
}
