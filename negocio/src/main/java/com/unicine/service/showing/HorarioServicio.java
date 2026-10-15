package com.unicine.service.showing;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.unicine.transfer.dto.request.HorarioRequest;
import com.unicine.transfer.dto.response.HorarioResponse;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface HorarioServicio {

    // 3️⃣ Funciones del Administrador Teatro

    HorarioResponse registrar(@Valid HorarioRequest request, @NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer salaCodigo) throws Exception;

    HorarioResponse actualizar(@Valid HorarioRequest request) throws Exception;

    // 1️⃣ Funciones del Administrador

    void eliminar(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Double obtenerDescuentoDia(LocalDateTime fechaInicio);

    String obtenerDia(LocalDateTime fechaInicio);

    Optional<HorarioResponse> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<HorarioResponse> listar();

    List<HorarioResponse> listarPaginado();

    List<HorarioResponse> listarAscendente();

    List<HorarioResponse> listarDescendente();
}
