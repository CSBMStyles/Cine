package com.unicine.service.movie;

import java.util.List;
import java.util.Optional;

import com.unicine.transfer.dto.request.PeliculaDisposicionRequest;
import com.unicine.transfer.dto.response.PeliculaDisposicionResponse;
import com.unicine.enums.movie.EstadoPelicula;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface PeliculaDisposicionServicio {

    // 1️⃣ Funciones del Administrador

    PeliculaDisposicionResponse registrar(@Valid PeliculaDisposicionRequest request) throws Exception;

    PeliculaDisposicionResponse actualizar(@Valid PeliculaDisposicionRequest request) throws Exception;

    void actualizarEstadoPeliculas();

    void eliminar(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer peliculaCodigo,
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer ciudadCodigo,
            boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Optional<PeliculaDisposicionResponse> obtener(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer peliculaCodigo,
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer ciudadCodigo) throws Exception;

    List<PeliculaDisposicionResponse> listar();

    List<PeliculaDisposicionResponse> listarRecomendacionPeliculaEstado(@Valid PeliculaDisposicionRequest request, EstadoPelicula estadoPelicula);

    List<PeliculaDisposicionResponse> listarPaginado();

    List<PeliculaDisposicionResponse> listarPaginado(org.springframework.data.domain.Pageable pageable);

    List<PeliculaDisposicionResponse> listarAscendente();

    List<PeliculaDisposicionResponse> listarDescendente();
}
