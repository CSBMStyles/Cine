package com.unicine.test.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.JwtServicio;
import com.unicine.security.UsuarioPrincipal;

@SpringBootTest
@AutoConfigureMockMvc
public class JwtFilterIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtServicio jwtServicio;

    private String tokenPara(Integer cedula) {
        UsuarioPrincipal principal = new UsuarioPrincipal(
                cedula, "filtro@test.com", null, TipoUsuario.CLIENTE, List.of());
        return jwtServicio.emitirAcceso(principal);
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    void rutaPublicaSinToken200() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/ciudades"))
                .andExpect(status().isOk())
                .andReturn();

        sout("publica sin token", result);
    }

    @Test
    void protegidaSinToken401ApiError() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/compras/999999"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_INVALID_CREDENTIALS"))
                .andReturn();

        sout("protegida sin token", result);
    }

    @Test
    void tokenInvalido401SinDetallesFirma() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/compras/999999")
                        .header("Authorization", "Bearer roto-no-es-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("DOMAIN_USER_AUTH_INVALID_CREDENTIALS"))
                .andReturn();

        String body = result.getResponse().getContentAsString().toLowerCase();
        assert !body.contains("signature") : "el 401 no debe exponer detalles de firma";
        sout("token invalido", result);
    }

    @Test
    void tokenValidoAutentica() throws Exception {
        // 404 y no 401 demuestra que el filtro autentico: llego al controller y el servicio no hallo la compra
        MvcResult result = mockMvc.perform(get("/api/compras/999999")
                        .header("Authorization", "Bearer " + tokenPara(1009000011)))
                .andExpect(status().isNotFound())
                .andReturn();

        sout("token valido", result);
    }
}
