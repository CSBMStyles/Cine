package com.unicine.test.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.unicine.api.controller.PeliculaDisposicionController;
import com.unicine.service.movie.PeliculaDisposicionServicio;
import com.unicine.transfer.dto.response.PeliculaDisposicionResponse;
import com.unicine.util.config.SecurityConfig;

@WebMvcTest(controllers = PeliculaDisposicionController.class)
@Import(SecurityConfig.class)
class PeliculaDisposicionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PeliculaDisposicionServicio disposicionServicio;

    private void sout(String titulo, MvcResult result) throws Exception {
        String body = result.getResponse().getContentAsString();
        System.out.println("\n>>> " + titulo + " | status=" + result.getResponse().getStatus());
        System.out.println(body.isBlank() ? "(sin body)" : body);
        System.out.println("<<<\n");
    }

    @Test
    @WithMockUser
    void registrarDisposicion201() throws Exception {
        PeliculaDisposicionResponse mock = PeliculaDisposicionResponse.builder().build();
        when(disposicionServicio.registrar(any())).thenReturn(mock);

        String body = """
                {"peliculaCodigo":1,"ciudadCodigo":1,"estadoPelicula":"PENDIENTE"}
                """;

        MvcResult result = mockMvc.perform(post("/api/pelicula-disposiciones").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn();

        sout("registrarDisposicion201", result);
    }

    @Test
    @WithMockUser
    void listarPaginado200() throws Exception {
        when(disposicionServicio.listarPaginado(any())).thenReturn(List.of(
                PeliculaDisposicionResponse.builder().build()));

        MvcResult result = mockMvc.perform(get("/api/pelicula-disposiciones")
                        .param("page", "0").param("size", "5"))
                .andExpect(status().isOk())
                .andReturn();

        sout("listarPaginado200 ?page=0&size=5", result);
    }

    @Test
    @WithMockUser
    void obtenerPorClaveCompuesta200() throws Exception {
        when(disposicionServicio.obtener(1, 1)).thenReturn(Optional.of(
                PeliculaDisposicionResponse.builder().build()));

        MvcResult result = mockMvc.perform(get("/api/pelicula-disposiciones/1/1"))
                .andExpect(status().isOk())
                .andReturn();

        sout("obtenerDisposicion 1/1", result);
    }

    @Test
    @WithMockUser
    void eliminarConConfirmacion204() throws Exception {
        MvcResult result = mockMvc.perform(delete("/api/pelicula-disposiciones/1/1")
                        .with(csrf()).param("confirmacion", "true"))
                .andExpect(status().isNoContent())
                .andReturn();

        sout("eliminarDisposicion204", result);
    }
}
