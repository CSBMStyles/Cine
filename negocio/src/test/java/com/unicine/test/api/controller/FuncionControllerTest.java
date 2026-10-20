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

import com.unicine.api.controller.FuncionController;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.purchase.EntradaServicio;
import com.unicine.service.showing.FuncionServicio;
import com.unicine.service.theater.SalaServicio;
import com.unicine.transfer.dto.response.CiudadResponse;
import com.unicine.transfer.dto.response.DetalleSillaResponse;
import com.unicine.transfer.dto.response.FuncionResponse;
import com.unicine.transfer.dto.response.HorarioResponse;
import com.unicine.transfer.dto.response.PeliculaResponse;
import com.unicine.transfer.dto.response.SalaResponse;
import com.unicine.transfer.dto.response.TeatroResponse;
import com.unicine.util.config.SecurityConfig;

/**
 * Tests slice para FuncionController — cartelera publica + sillas.
 */
@WebMvcTest(controllers = FuncionController.class)
@Import(SecurityConfig.class)
class FuncionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FuncionServicio funcionServicio;

    @MockitoBean
    private EntradaServicio entradaServicio;

    @MockitoBean
    private SalaServicio salaServicio;

    private UsuarioPrincipal principalCliente() {
        return new UsuarioPrincipal(1009000011, "pepe@test.com", "hash", TipoUsuario.CLIENTE);
    }

    private UsuarioPrincipal principalAdmin() {
        return new UsuarioPrincipal(2001, "admin@test.com", "hash", TipoUsuario.ADMINISTRADOR);
    }

    private UsuarioPrincipal gestorTeatro(Integer... teatros) {
        return new UsuarioPrincipal(
                3001, "teatro@test.com", "hash", TipoUsuario.ADMINISTRADOR_TEATRO, List.of(teatros));
    }

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    void registrarComoGestor201() throws Exception {
        when(salaServicio.obtener(5)).thenReturn(Optional.of(
                SalaResponse.builder().codigo(5).teatro(TeatroResponse.builder().codigo(1).build()).build()));
        when(funcionServicio.registrar(any())).thenReturn(FuncionResponse.builder().codigo(3).build());

        String body = """
                {"precio":17000.0,"formato":"DOBLADO","salaCodigo":5,"horarioCodigo":1,"peliculaCodigo":1}
                """;

        MvcResult result = mockMvc.perform(post("/api/funciones").with(csrf())
                        .with(user(gestorTeatro(1)))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value(3))
                .andReturn();

        sout("registrarFuncionGestor201", result);
    }

    @Test
    void registrarTeatroAjeno403() throws Exception {
        when(salaServicio.obtener(5)).thenReturn(Optional.of(
                SalaResponse.builder().codigo(5).teatro(TeatroResponse.builder().codigo(1).build()).build()));
        String body = """
                {"precio":17000.0,"formato":"DOBLADO","salaCodigo":5,"horarioCodigo":1,"peliculaCodigo":1}
                """;

        MvcResult result = mockMvc.perform(post("/api/funciones").with(csrf())
                        .with(user(gestorTeatro(99)))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();

        sout("registrarFuncionTeatroAjeno403", result);
    }

    @Test
    void registrarComoCliente403() throws Exception {
        String body = """
                {"precio":17000.0,"formato":"DOBLADO","salaCodigo":5,"horarioCodigo":1,"peliculaCodigo":1}
                """;

        MvcResult result = mockMvc.perform(post("/api/funciones").with(csrf())
                        .with(user(principalCliente()))
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isForbidden())
                .andReturn();

        sout("registrarFuncionCliente403", result);
    }

    @Test
    void listarFuncionesPublico200() throws Exception {
        when(funcionServicio.listar()).thenReturn(List.of(
                FuncionResponse.builder().codigo(1).precio(15000.0).build()));

        MvcResult result = mockMvc.perform(get("/api/funciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();

        sout("listarFuncionesPublico200", result);
    }

    @Test
    void obtenerFuncionPorId200() throws Exception {
        when(funcionServicio.obtener(1)).thenReturn(Optional.of(
                FuncionResponse.builder().codigo(1).precio(15000.0).build()));

        MvcResult result = mockMvc.perform(get("/api/funciones/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.codigo").value(1))
                .andReturn();

        sout("obtenerFuncion200", result);
    }

    @Test
    void obtenerFuncionNoExiste404() throws Exception {
        when(funcionServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/funciones/999"))
                .andExpect(status().isNotFound())
                .andReturn();

        sout("obtenerFuncion404", result);
    }

    @Test
    void sillasOcupadas200() throws Exception {
        when(funcionServicio.obtener(1)).thenReturn(Optional.of(
                FuncionResponse.builder().codigo(1).build()));
        when(entradaServicio.obtenerSillasOcupadas(1)).thenReturn(List.of(
                new DetalleSillaResponse(1, 1, 1)));

        MvcResult result = mockMvc.perform(get("/api/funciones/1/sillas-ocupadas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].filaEntrada").value(1))
                .andReturn();

        sout("sillasOcupadas200", result);
    }

    @Test
    void sillasOcupadasFuncionNoExiste404() throws Exception {
        when(funcionServicio.obtener(999)).thenReturn(Optional.empty());

        MvcResult result = mockMvc.perform(get("/api/funciones/999/sillas-ocupadas"))
                .andExpect(status().isNotFound())
                .andReturn();

        sout("sillasOcupadas404", result);
    }

    // SECTION: Filtros de cartelera

    private FuncionResponse funcion(Integer codigo, Integer pelicula, Integer ciudad, String fechaInicio) {
        return FuncionResponse.builder()
                .codigo(codigo)
                .precio(15000.0)
                .pelicula(pelicula == null ? null : PeliculaResponse.builder().codigo(pelicula).build())
                .sala(SalaResponse.builder()
                        .codigo(10)
                        .teatro(TeatroResponse.builder()
                                .codigo(20)
                                .ciudad(ciudad == null ? null : CiudadResponse.builder().codigo(ciudad).build())
                                .build())
                        .build())
                .horario(fechaInicio == null ? null : HorarioResponse.builder()
                        .codigo(30)
                        .fechaInicio(java.time.LocalDateTime.parse(fechaInicio))
                        .fechaFin(java.time.LocalDateTime.parse(fechaInicio).plusHours(2))
                        .build())
                .build();
    }

    @Test
    void filtrarPorCiudad200() throws Exception {
        when(funcionServicio.listar()).thenReturn(List.of(
                funcion(1, 100, 1, "2026-09-10T18:00:00"),
                funcion(2, 100, 2, "2026-09-10T20:00:00")));

        MvcResult result = mockMvc.perform(get("/api/funciones").param("ciudad", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value(1))
                .andReturn();

        sout("filtrarPorCiudad200", result);
    }

    @Test
    void filtrarPorFecha200() throws Exception {
        when(funcionServicio.listar()).thenReturn(List.of(
                funcion(1, 100, 1, "2026-09-10T18:00:00"),
                funcion(2, 100, 1, "2026-09-11T18:00:00")));

        MvcResult result = mockMvc.perform(get("/api/funciones").param("fecha", "2026-09-11"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].codigo").value(2))
                .andReturn();

        sout("filtrarPorFecha200", result);
    }

    @Test
    void filtrosCombinadosVacios200() throws Exception {
        when(funcionServicio.listar()).thenReturn(List.of(
                funcion(1, 100, 1, "2026-09-10T18:00:00")));

        MvcResult result = mockMvc.perform(get("/api/funciones")
                        .param("pelicula", "999")
                        .param("ciudad", "1")
                        .param("fecha", "2026-09-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0))
                .andReturn();

        sout("filtrosCombinadosVacios200", result);
    }

    @Test
    void fechaInvalida400() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/funciones").param("fecha", "no-es-fecha"))
                .andExpect(status().isBadRequest())
                .andReturn();

        sout("fechaInvalida400", result);
    }

    @Test
    void ordenarDescendente200() throws Exception {
        when(funcionServicio.listar()).thenReturn(List.of(
                funcion(1, 100, 1, "2026-09-10T18:00:00"),
                funcion(2, 100, 1, "2026-09-10T20:00:00")));

        MvcResult result = mockMvc.perform(get("/api/funciones")
                        .param("sort", "codigo")
                        .param("direction", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value(2))
                .andExpect(jsonPath("$[1].codigo").value(1))
                .andReturn();

        sout("ordenarDescendente200", result);
    }

    // !SECTION
    // SECTION: Eliminacion por rol

    private FuncionResponse funcionEnTeatro(Integer codigo, Integer teatro) {
        return FuncionResponse.builder()
                .codigo(codigo)
                .sala(SalaResponse.builder()
                        .codigo(5)
                        .teatro(TeatroResponse.builder().codigo(teatro).build())
                        .build())
                .build();
    }

    @Test
    void eliminarComoGestorPropio204() throws Exception {
        when(funcionServicio.obtener(3)).thenReturn(Optional.of(funcionEnTeatro(3, 1)));

        MvcResult result = mockMvc.perform(delete("/api/funciones/3")
                        .param("confirmacion", "true")
                        .with(user(gestorTeatro(1)))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn();
        sout("eliminarFuncionPropia204", result);
    }

    @Test
    void eliminarTeatroAjeno403() throws Exception {
        when(funcionServicio.obtener(3)).thenReturn(Optional.of(funcionEnTeatro(3, 1)));

        MvcResult result = mockMvc.perform(delete("/api/funciones/3")
                        .param("confirmacion", "true")
                        .with(user(gestorTeatro(99)))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("eliminarFuncionTeatroAjeno403", result);
    }

    @Test
    void eliminarComoAdmin204() throws Exception {
        when(funcionServicio.obtener(3)).thenReturn(Optional.of(funcionEnTeatro(3, 1)));

        MvcResult result = mockMvc.perform(delete("/api/funciones/3")
                        .param("confirmacion", "true")
                        .with(user(principalAdmin()))
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andReturn();
        sout("eliminarFuncionAdmin204", result);
    }

    @Test
    void eliminarComoCliente403() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/funciones/3")
                        .param("confirmacion", "true")
                        .with(user(principalCliente()))
                        .with(csrf()))
                .andExpect(status().isForbidden())
                .andReturn();
        sout("eliminarFuncionCliente403", result);
    }

    // !SECTION
}
