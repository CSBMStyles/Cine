package com.unicine.service.theater;

import com.unicine.transfer.dto.request.TeatroRequest;
import com.unicine.transfer.dto.response.TeatroResponse;
import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface TeatroServicio {

    // 2️⃣ Funciones del Administrador Teatro

    TeatroResponse registrar(@Valid TeatroRequest request) throws Exception;

    TeatroResponse actualizar(@Valid TeatroRequest request) throws Exception;

    void eliminar(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo, boolean confirmacion) throws Exception;

    Optional<TeatroResponse> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<TeatroResponse> listar();

    List<TeatroResponse> listarPaginado();

    List<TeatroResponse> listarAscendente();

    List<TeatroResponse> listarDescendente();
}
