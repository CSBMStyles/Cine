package com.unicine.service.showing;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.showing.Horario;
import com.unicine.entity.theater.Sala;
import com.unicine.transfer.dto.request.FuncionRequest;
import com.unicine.transfer.dto.response.FuncionResponse;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface FuncionServicio {

    // *️⃣ Funciones de Soporte

    Double calcularPrecio(Sala sala, Horario horario);

    // 2️⃣ Funciones del Administrador de Teatro

    FuncionResponse registrar(@Valid FuncionRequest request) throws Exception;

    FuncionResponse actualizar(@Valid FuncionRequest request) throws Exception;

    void eliminar(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Optional<FuncionResponse> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<FuncionResponse> listar();

    List<FuncionResponse> listarPaginado();

    List<FuncionResponse> listarAscendente();

    List<FuncionResponse> listarDescendente();
}
