package com.unicine.test.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Date;
import java.util.HexFormat;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.unicine.entity.auth.SesionRefresh;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.repository.auth.SesionRefreshRepo;
import com.unicine.security.JwtServicio;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.auth.RefrescoServicio;
import com.unicine.transfer.dto.auth.ParTokensResponse;

/**
 * Tests de integracion de autenticacion (5.3): rutas publicas, 401 por token
 * ausente/invalido/expirado, refresh como access y ciclo de refresh.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityAuthenticationIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtServicio jwtServicio;

    @Autowired
    private RefrescoServicio refrescoServicio;

    @Autowired
    private SesionRefreshRepo sesionRepo;

    @Value("${jwt.secret}")
    private String secreto;

    private UsuarioPrincipal principalCliente() {
        return new UsuarioPrincipal(1009000011, "pepe@hotmail.com", null,
                TipoUsuario.CLIENTE, List.of());
    }

    private String tokenAcceso() {
        return jwtServicio.emitirAcceso(principalCliente());
    }

    private String tokenExpirado() {
        try {
            Date ahora = new Date();
            JWSSigner firmante = new MACSigner(secreto);
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .issuer("unicine")
                    .subject("1009000011")
                    .issueTime(new Date(ahora.getTime() - 120_000))
                    .expirationTime(new Date(ahora.getTime() - 60_000))
                    .claim("tipo", "CLIENTE")
                    .claim("teatroIds", List.of())
                    .claim("proposito", "acceso")
                    .build();
            SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
            jwt.sign(firmante);
            return jwt.serialize();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar el token expirado", e);
        }
    }

    private String refreshJson(String refreshToken) {
        return "{\"refreshToken\":\"%s\"}".formatted(refreshToken);
    }

    private String hash(String token) throws Exception {
        byte[] digesto = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digesto);
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(result.getResponse().getContentAsString());
        System.out.println("<<<\n");
    }

    @Test
    void rutaPublicaSinToken200() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/ciudades"))
                .andExpect(status().isOk())
                .andReturn();
        sout("rutaPublicaSinToken200", result);
    }

    @Test
    void rutaProtegidaSinToken401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/compras/1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_INVALID_CREDENTIALS"))
                .andReturn();
        sout("rutaProtegidaSinToken401", result);
    }

    @Test
    void cuponesSinToken401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/cupones/activos"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_INVALID_CREDENTIALS"))
                .andReturn();
        sout("cuponesSinToken401", result);
    }

    @Test
    void historialPreciosSinToken401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/historial-precios/ultimo")
                        .param("presentacion", "1"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_INVALID_CREDENTIALS"))
                .andReturn();
        sout("historialPreciosSinToken401", result);
    }

    @Test
    void tokenInvalido401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/compras/1")
                        .header("Authorization", "Bearer roto"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_INVALID_CREDENTIALS"))
                .andReturn();
        sout("tokenInvalido401", result);
    }

    @Test
    void tokenExpirado401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/compras/1")
                        .header("Authorization", "Bearer " + tokenExpirado()))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("tokenExpirado401", result);
    }

    @Test
    void refreshComoAccess401() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/compras/1")
                        .header("Authorization", "Bearer " + jwtServicio.emitirRefresco(principalCliente())))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("refreshComoAccess401", result);
    }

    @Test
    void refreshVigenteRota200() throws Exception {
        ParTokensResponse par = refrescoServicio.crearSesion(principalCliente());

        MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshJson(par.getRefreshToken())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn();
        sout("refreshVigenteRota200", result);
    }

    @Test
    void refreshExpirado401() throws Exception {
        ParTokensResponse par = refrescoServicio.crearSesion(principalCliente());
        SesionRefresh sesion = sesionRepo.findByTokenHash(hash(par.getRefreshToken())).orElseThrow();
        sesion.setExpiraEn(Instant.now().minusSeconds(60));
        sesionRepo.save(sesion);

        MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshJson(par.getRefreshToken())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_TOKEN_EXPIRED"))
                .andReturn();
        sout("refreshExpirado401", result);
    }

    @Test
    void refreshRevocado401() throws Exception {
        ParTokensResponse par = refrescoServicio.crearSesion(principalCliente());

        mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshJson(par.getRefreshToken())))
                .andExpect(status().isOk());

        MvcResult result = mockMvc.perform(post("/api/auth/refresh")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshJson(par.getRefreshToken())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_TOKEN_REVOKED"))
                .andReturn();
        sout("refreshRevocado401", result);
    }
}
