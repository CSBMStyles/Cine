package com.unicine.test.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.JwtServicio;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.image.ImageKitService;

/**
 * Tests de integracion de autorizacion (5.3): roles, alcance por teatro y
 * ownership sobre recursos persistidos. ImageKit se mockea para no salir a red.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@Sql("classpath:dataset.sql")
class SecurityAuthorizationIT {

    private static final String IMAGEN_PEPE = "67c243ec432c4764163416e0";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtServicio jwtServicio;

    @MockitoBean
    private ImageKitService imageKitService;

    private UsuarioPrincipal clienteJuan() {
        return new UsuarioPrincipal(1008000022, "juan@outlook.com", null,
                TipoUsuario.CLIENTE, List.of());
    }

    private UsuarioPrincipal clientePepe() {
        return new UsuarioPrincipal(1009000011, "pepe@hotmail.com", null,
                TipoUsuario.CLIENTE, List.of());
    }

    private UsuarioPrincipal admin() {
        return new UsuarioPrincipal(1001000000, "cristiansimelot@gmail.com", null,
                TipoUsuario.ADMINISTRADOR, List.of());
    }

    private UsuarioPrincipal teatro() {
        return new UsuarioPrincipal(1557000000, "rodolfo.restrepo@uqvirtual.edu.com", null,
                TipoUsuario.ADMINISTRADOR_TEATRO, List.of(5));
    }

    private String token(UsuarioPrincipal principal) {
        return jwtServicio.emitirAcceso(principal);
    }

    private String bodyHorario() {
        return """
                {"fechaInicio":"2030-01-01T10:00:00","fechaFin":"2030-01-01T12:00:00"}
                """;
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(result.getResponse().getContentAsString());
        System.out.println("<<<\n");
    }

    @Test
    void clienteNoPuedeCrearCiudad403() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/ciudades")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token(clienteJuan()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ciudad IT\"}"))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("clienteNoPuedeCrearCiudad403", result);
    }

    @Test
    void adminCreaCiudad201() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/ciudades")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token(admin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Ciudad IT\"}"))
                .andExpect(status().isCreated())
                .andReturn();
        sout("adminCreaCiudad201", result);
    }

    @Test
    void teatroActualizaHorarioPropio200() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/horarios/6")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token(teatro()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyHorario()))
                .andExpect(status().isOk())
                .andReturn();
        sout("teatroActualizaHorarioPropio200", result);
    }

    @Test
    void teatroActualizaHorarioAjeno403() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/horarios/1")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token(teatro()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyHorario()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("teatroActualizaHorarioAjeno403", result);
    }

    @Test
    void adminActualizaHorarioAjeno200() throws Exception {
        MvcResult result = mockMvc.perform(put("/api/horarios/1")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token(admin()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyHorario()))
                .andExpect(status().isOk())
                .andReturn();
        sout("adminActualizaHorarioAjeno200", result);
    }

    @Test
    void clienteCompraPropia200() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/compras/1")
                        .header("Authorization", "Bearer " + token(clienteJuan())))
                .andExpect(status().isOk())
                .andReturn();
        sout("clienteCompraPropia200", result);
    }

    @Test
    void clienteCompraAjena403() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/compras/2")
                        .header("Authorization", "Bearer " + token(clienteJuan())))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("clienteCompraAjena403", result);
    }

    @Test
    void adminCompraAjena200() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/compras/2")
                        .header("Authorization", "Bearer " + token(admin())))
                .andExpect(status().isOk())
                .andReturn();
        sout("adminCompraAjena200", result);
    }

    @Test
    void imagenPropia204() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/imagenes/" + IMAGEN_PEPE)
                        .param("confirmacion", "true")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token(clientePepe())))
                .andExpect(status().isNoContent())
                .andReturn();
        sout("imagenPropia204", result);
    }

    @Test
    void imagenAjena403() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/imagenes/" + IMAGEN_PEPE)
                        .param("confirmacion", "true")
                        .with(csrf())
                        .header("Authorization", "Bearer " + token(clienteJuan())))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("imagenAjena403", result);
    }
}
