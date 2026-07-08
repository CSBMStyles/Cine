package com.unicine.service.movie;

import java.util.List;

import com.unicine.transfer.dto.request.HistorialEstadoPeliculaRequest;
import com.unicine.transfer.dto.response.HistorialEstadoPeliculaResponse;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface HistorialEstadoPeliculaServicio {

    HistorialEstadoPeliculaResponse registrar(@Valid HistorialEstadoPeliculaRequest request);

    List<HistorialEstadoPeliculaResponse> obtenerPorPelicula(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer peliculaId,
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer ciudadId);

    void eliminarPorPelicula(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer peliculaId,
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer ciudadId);

    long contarPorPelicula(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer peliculaId,
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer ciudadId);

    Integer obtenerAntiguedadUltimoCambio(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer peliculaId,
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer ciudadId);

    void alertarSiHistorialViejo(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer peliculaId,
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer ciudadId);
}
