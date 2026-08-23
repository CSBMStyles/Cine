package com.unicine.service.theater;

import com.unicine.transfer.dto.request.CiudadRequest;
import com.unicine.transfer.dto.response.CiudadResponse;
import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface CiudadServicio {

    // 1️⃣ Funciones del Administrador

    CiudadResponse registrar(@Valid CiudadRequest request) throws Exception;

    CiudadResponse actualizar(@Valid CiudadRequest request) throws Exception;

    void eliminar(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    // *️⃣ Funciones Generales

    Optional<CiudadResponse> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<CiudadResponse> obtenerNombre(@NotBlank(message = ValidationMessages.NAME_NOT_BLANK) String nombre) throws Exception;

    List<CiudadResponse> listar();

    List<CiudadResponse> listarPaginado();

    // Paginado con Pageable — usado por CiudadController (task 4.2)
    List<CiudadResponse> listarPaginado(org.springframework.data.domain.Pageable pageable);

    List<CiudadResponse> listarAscendenteNombre();

    List<CiudadResponse> listarDescendenteNombre();
}
