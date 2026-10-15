package com.unicine.service.theater;

import com.unicine.enums.theater.TipoSala;
import com.unicine.transfer.dto.request.SalaRequest;
import com.unicine.transfer.dto.response.SalaResponse;
import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface SalaServicio {

    // 3️⃣ Funciones del Administrador Teatro

    SalaResponse registrar(@Valid SalaRequest request) throws Exception;

    SalaResponse actualizar(@Valid SalaRequest request) throws Exception;

    // 1️⃣ Funciones del Administrador

    void eliminar(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Double obtenerPrecioBase(TipoSala tipoSala);

    Optional<SalaResponse> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<SalaResponse> obtenerNombre(@NotBlank(message = ValidationMessages.NAME_NOT_BLANK) String nombre) throws Exception;

    Optional<SalaResponse> obtenerIdTeatro(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo, @NotNull(message = ValidationMessages.THEATER_ID_NOT_NULL) @Positive(message = ValidationMessages.THEATER_ID_POSITIVE) Integer teatro) throws Exception;

    List<SalaResponse> obtenerNombresTeatro(@NotBlank(message = ValidationMessages.NAME_NOT_BLANK) String nombre, @NotNull(message = ValidationMessages.THEATER_ID_NOT_NULL) @Positive(message = ValidationMessages.THEATER_ID_POSITIVE) Integer teatro) throws Exception;

    List<SalaResponse> listarPorTeatro(@NotNull(message = ValidationMessages.THEATER_ID_NOT_NULL) @Positive(message = ValidationMessages.THEATER_ID_POSITIVE) Integer teatro) throws Exception;

    List<SalaResponse> listar();

    List<SalaResponse> listarPaginado();

    List<SalaResponse> listarPaginado(org.springframework.data.domain.Pageable pageable);

    List<SalaResponse> listarAscendente();

    List<SalaResponse> listarDescendente();
}
