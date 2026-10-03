package com.unicine.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;

/**
 * Convierte un JWT validado en autenticacion con {@link UsuarioPrincipal}.
 * Stateless: reconstruye el principal desde claims sin ir a BD.
 * Fallos de firma/expiracion los maneja el resource server antes de llegar aqui.
 * Se declara como @Bean en {@link JwtConfig} para que los slices @WebMvcTest
 * (que no cargan @Service) no intenten construirlo.
 */
public class JwtPrincipalConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    // SECTION: Conversion

    private final JwtServicio jwtServicio;

    public JwtPrincipalConverter(JwtServicio jwtServicio) {
        this.jwtServicio = jwtServicio;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        if ("refresco".equals(jwt.getClaimAsString("proposito"))) {
            throw new org.springframework.security.authentication.InsufficientAuthenticationException(
                    "Un refresh token no es un access token");
        }
        UsuarioPrincipal principal = jwtServicio.principalDesde(jwt);
        return new UsernamePasswordAuthenticationToken(
                principal, jwt.getTokenValue(), principal.getAuthorities());
    }

    /**
     * Conversor que niega todo (fail-closed) para contextos sin JwtServicio,
     * como los slices @WebMvcTest. Nunca se invoca en produccion.
     */
    public static JwtPrincipalConverter denegado() {
        return new JwtPrincipalConverter(null) {
            @Override
            public AbstractAuthenticationToken convert(Jwt jwt) {
                throw new org.springframework.security.authentication.InsufficientAuthenticationException(
                        "JWT no configurado");
            }
        };
    }

    // !SECTION
}
