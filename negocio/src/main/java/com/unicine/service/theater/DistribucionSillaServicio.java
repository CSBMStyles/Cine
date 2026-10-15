package com.unicine.service.theater;

import com.unicine.transfer.dto.request.DistribucionSillaRequest;
import com.unicine.transfer.dto.response.DistribucionSillaResponse;
import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface DistribucionSillaServicio {

    // 3️⃣ Funciones del Administrador Teatro

    DistribucionSillaResponse registrar(@Valid DistribucionSillaRequest request) throws Exception;

    DistribucionSillaResponse actualizar(@Valid DistribucionSillaRequest request) throws Exception;

    // 1️⃣ Funciones del Administrador

    void eliminar(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo, boolean confirmacion) throws Exception;

    // 2️⃣ Funciones del Cliente

    Optional<DistribucionSillaResponse> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<DistribucionSillaResponse> listar();

    List<DistribucionSillaResponse> listarPaginado();

    List<DistribucionSillaResponse> listarAscendente();

    List<DistribucionSillaResponse> listarDescendente();
}
