package com.unicine.service.movie;

import com.unicine.transfer.dto.request.PeliculaRequest;
import com.unicine.transfer.dto.response.PeliculaResponse;
import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface PeliculaServicio {

    // 1️⃣ Funciones del Administrador

    PeliculaResponse registrar(@Valid PeliculaRequest request) throws Exception;

    PeliculaResponse actualizar(@Valid PeliculaRequest request) throws Exception;

    void eliminar(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Optional<PeliculaResponse> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<PeliculaResponse> obtenerNombrePeliculas(@NotBlank(message = ValidationMessages.NAME_NOT_BLANK) String nombre) throws Exception;

    List<PeliculaResponse> listar();

    List<PeliculaResponse> listarPaginado();

    List<PeliculaResponse> listarAscendente();

    List<PeliculaResponse> listarDescendente();
}
