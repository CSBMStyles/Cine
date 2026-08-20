package com.unicine.service.showing;

import java.util.List;
import java.util.Optional;

import com.unicine.transfer.dto.request.FuncionEsquemaRequest;
import com.unicine.transfer.dto.response.FuncionEsquemaResponse;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface FuncionEsquemaServicio {

    // *️⃣ Funciones Automaticas

    FuncionEsquemaResponse registrar(@Valid FuncionEsquemaRequest request) throws Exception;

    FuncionEsquemaResponse actualizar(@Valid FuncionEsquemaRequest request) throws Exception;

    void eliminar(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Globales

    Optional<FuncionEsquemaResponse> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<FuncionEsquemaResponse> listar();
}
