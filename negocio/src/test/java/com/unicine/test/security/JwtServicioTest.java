package com.unicine.test.security;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;

import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.JwtServicio;
import com.unicine.security.UsuarioPrincipal;

public class JwtServicioTest {

    private static final String SECRETO = "0123456789ABCDEF0123456789ABCDEF";

    private UsuarioPrincipal principalCliente() {
        return new UsuarioPrincipal(1009000011, "pepe@hotmail.com", null,
                TipoUsuario.CLIENTE, List.of());
    }

    @Test
    public void emiteConClaimsEsperados() {
        JwtServicio servicio = new JwtServicio(SECRETO, 15, 7, "unicine");

        String token = servicio.emitirAcceso(principalCliente());
        Jwt jwt = servicio.validar(token);

        Assertions.assertEquals("1009000011", jwt.getSubject());
        Assertions.assertEquals("CLIENTE", jwt.getClaimAsString("tipo"));
        Assertions.assertEquals("unicine", jwt.getClaimAsString("iss"));
        Assertions.assertTrue(jwt.getExpiresAt().isAfter(Instant.now().plusSeconds(14 * 60)));
    }

    @Test
    public void firmaDistintaSeRechaza() {
        JwtServicio emisor = new JwtServicio(SECRETO, 15, 7, "unicine");
        JwtServicio otro = new JwtServicio("FEDCBA9876543210FEDCBA9876543210", 15, 7, "unicine");

        String token = emisor.emitirAcceso(principalCliente());

        Assertions.assertThrows(JwtException.class, () -> otro.validar(token));
    }

    @Test
    public void expiradoSeRechaza() throws Exception {
        JwtServicio servicio = new JwtServicio(SECRETO, 15, 7, "unicine");

        com.nimbusds.jose.JWSSigner firmante = new com.nimbusds.jose.crypto.MACSigner(SECRETO);
        com.nimbusds.jwt.JWTClaimsSet claims = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                .issuer("unicine")
                .subject("1009000011")
                .claim("tipo", "CLIENTE")
                .expirationTime(new java.util.Date(System.currentTimeMillis() - 60_000))
                .issueTime(new java.util.Date(System.currentTimeMillis() - 120_000))
                .build();
        com.nimbusds.jwt.SignedJWT jwtFirmado =
                new com.nimbusds.jwt.SignedJWT(new com.nimbusds.jose.JWSHeader(com.nimbusds.jose.JWSAlgorithm.HS256), claims);
        jwtFirmado.sign(firmante);

        Assertions.assertThrows(JwtException.class, () -> servicio.validar(jwtFirmado.serialize()));
    }

    @Test
    public void secretoCortoFallaAlArrancar() {
        Assertions.assertThrows(IllegalStateException.class,
                () -> new JwtServicio("corto", 15, 7, "unicine"));
    }

    @Test
    public void refrescoLlevaPropositoYVidaLarga() {
        JwtServicio servicio = new JwtServicio(SECRETO, 15, 7, "unicine");

        String refresco = servicio.emitirRefresco(principalCliente());
        Jwt jwt = servicio.validarRefresco(refresco);

        Assertions.assertEquals("refresco", jwt.getClaimAsString("proposito"));
        Assertions.assertTrue(jwt.getExpiresAt().isAfter(Instant.now().plusSeconds(6 * 24 * 60 * 60)));
    }

    @Test
    public void accesoNoPasaComoRefresco() {
        JwtServicio servicio = new JwtServicio(SECRETO, 15, 7, "unicine");

        String acceso = servicio.emitirAcceso(principalCliente());

        Assertions.assertThrows(JwtException.class, () -> servicio.validarRefresco(acceso));
    }

    @Test
    public void reconstruyePrincipalSinBd() {
        JwtServicio servicio = new JwtServicio(SECRETO, 15, 7, "unicine");
        Jwt jwt = servicio.validar(servicio.emitirAcceso(principalCliente()));

        UsuarioPrincipal reconstruido = servicio.principalDesde(jwt);

        Assertions.assertEquals(1009000011, reconstruido.getCedula());
        Assertions.assertEquals(TipoUsuario.CLIENTE, reconstruido.getTipo());
    }
}
