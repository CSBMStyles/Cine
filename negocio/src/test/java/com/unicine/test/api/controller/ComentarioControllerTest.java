package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.ComentarioController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.movie.ComentarioServicio;
import com.unicine.transfer.dto.response.ClienteResponse;
import com.unicine.transfer.dto.response.ComentarioResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para ComentarioController — lectura publica, escritura propia o ADMIN.
 */
@WebMvcTest(controllers = ComentarioController.class)
@Import(SecurityConfig.class)
class ComentarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ComentarioServicio comentarioServicio;

    private UsuarioPrincipal principalAdmin() {
        return new UsuarioPrincipal(2001, "admin@test.com", "hash", TipoUsuario.ADMINISTRADOR);
    }

    private UsuarioPrincipal principalCliente(Integer cedula) {
        return new UsuarioPrincipal(cedula, "pepe@test.com", "hash", TipoUsuario.CLIENTE);
    }

    private ComentarioResponse comentario(Integer codigo, Integer cedula) {
        return ComentarioResponse.builder()
                .codigo(codigo)
                .texto("Buena pelicula")
                .cliente(ClienteResponse.builder().cedula(cedula).build())
                .build();
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    void listarPublico200() throws Exception {
        when(comentarioServicio.listar()).thenReturn(List.of(comentario(1, 1009000011)));

        MvcResult result = mockMvc.perform(get("/api/comentarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("listarPublico200", result);
    }

    @Test
    void filtrarPorPelicula200() throws Exception {
        when(comentarioServicio.listarPorPelicula(4)).thenReturn(List.of(comentario(1, 1009000011)));

        MvcResult result = mockMvc.perform(get("/api/comentarios").param("pelicula", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();
        sout("filtrarPorPelicula200", result);
    }

    @Test
    void obtenerInexistente404() throws Exception {
        when(comentarioServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/comentarios/999"))
                .andExpect(status().isNotFound())
                .andReturn();
        sout("obtener404", result);
    }

    @Test
    void registrarFuerzaCedulaPropia201() throws Exception {
        when(comentarioServicio.registrar(any())).thenReturn(comentario(1, 1009000011));

        String body = """
                {"texto":"Buena pelicula","likes":0,"dislikes":0,"fecha":"2030-01-01T10:00:00","clienteCedula":999,"peliculaCodigo":4}
                """;

        MvcResult result = mockMvc.perform(post("/api/comentarios")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();
        sout("registrarPropio201", result);
    }

    @Test
    void eliminarAjeno403() throws Exception {
        when(comentarioServicio.obtener(1)).thenReturn(Optional.of(comentario(1, 999)));

        MvcResult result = mockMvc.perform(delete("/api/comentarios/1")
                        .param("confirmacion", "true")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("eliminarAjeno403", result);
    }

    @Test
    void likeConAuth200() throws Exception {
        when(comentarioServicio.darLike(1)).thenReturn(comentario(1, 1009000011));

        MvcResult result = mockMvc.perform(post("/api/comentarios/1/like")
                        .with(user(principalCliente(1009000011)))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();
        sout("like200", result);
    }

    @Test
    void likeSinAuth401() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/comentarios/1/like").with(csrf()))
                .andExpect(status().isUnauthorized())
                .andReturn();
        sout("like401", result);
    }
}
