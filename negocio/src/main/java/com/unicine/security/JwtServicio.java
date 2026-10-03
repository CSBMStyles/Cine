package com.unicine.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.unicine.enums.user.TipoUsuario;

/**
 * Emision y validacion de access tokens JWT (HS256).
 * Subject = cedula; claims propios: tipo, teatroIds.
 * El secreto se exige de minimo 32 bytes y se valida al arrancar (fail-fast).
 */
@Service
public class JwtServicio {

    // SECTION: Configuracion

    private final NimbusJwtEncoder codificador;

    private final NimbusJwtDecoder decodificador;

    private final long minutosAcceso;

    private final long diasRefresco;

    private final String emisor;

    public JwtServicio(
            @Value("${jwt.secret:}") String secreto,
            @Value("${jwt.access-minutes:15}") long minutosAcceso,
            @Value("${jwt.refresh-days:7}") long diasRefresco,
            @Value("${jwt.issuer:unicine}") String emisor) {
        byte[] bytes = secreto == null ? new byte[0] : secreto.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalStateException(
                    "JWT_SECRET debe tener minimo 32 bytes para HS256; configure la variable JWT_SECRET");
        }
        SecretKey llave = new SecretKeySpec(bytes, "HmacSHA256");
        this.codificador = new NimbusJwtEncoder(new ImmutableSecret<>(llave));
        this.decodificador = NimbusJwtDecoder.withSecretKey(llave).macAlgorithm(MacAlgorithm.HS256).build();
        this.minutosAcceso = minutosAcceso;
        this.diasRefresco = diasRefresco;
        this.emisor = emisor;
    }

    // !SECTION
    // SECTION: Emision

    /**
     * Emite un access token de vida corta para el principal dado.
     * Proposito "acceso": nunca aceptado como refresh.
     */
    public String emitirAcceso(UsuarioPrincipal principal) {
        return emitir(principal, minutosAcceso * 60, "acceso");
    }

    /**
     * Emite un refresh token de vida larga para el principal dado.
     * Proposito "refresco": nunca aceptado como access.
     */
    public String emitirRefresco(UsuarioPrincipal principal) {
        return emitir(principal, diasRefresco * 24 * 60 * 60, "refresco");
    }

    private String emitir(UsuarioPrincipal principal, long segundosVida, String proposito) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(emisor)
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(segundosVida))
                .subject(String.valueOf(principal.getCedula()))
                .claim("tipo", principal.getTipo().name())
                .claim("teatroIds", principal.getTeatroIds())
                .claim("proposito", proposito)
                .build();
        JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
        return codificador.encode(JwtEncoderParameters.from(cabecera, claims)).getTokenValue();
    }

    // !SECTION
    // SECTION: Validacion

    /**
     * Valida firma y expiracion; lanza JwtException si el token no es aceptable.
     */
    public Jwt validar(String token) {
        return decodificador.decode(token);
    }

    /**
     * Valida y exige proposito "refresco"; lanza JwtException si es de acceso.
     */
    public Jwt validarRefresco(String token) {
        Jwt jwt = decodificador.decode(token);
        if (!"refresco".equals(jwt.getClaimAsString("proposito"))) {
            throw new org.springframework.security.oauth2.jwt.JwtException(
                    "Proposito invalido: no es un refresh token");
        }
        return jwt;
    }

    /**
     * Reconstruye el principal desde claims ya validados (stateless, sin BD).
     */
    public UsuarioPrincipal principalDesde(Jwt jwt) {
        Integer cedula = Integer.valueOf(jwt.getSubject());
        TipoUsuario tipo = TipoUsuario.valueOf(jwt.getClaimAsString("tipo"));
        List<Integer> teatros = jwt.getClaim("teatroIds");
        return new UsuarioPrincipal(cedula, null, null, tipo,
                teatros == null ? List.of() : teatros);
    }

    // !SECTION
}
