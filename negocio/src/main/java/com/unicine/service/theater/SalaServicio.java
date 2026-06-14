package com.unicine.service.theater;

import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import com.unicine.entity.theater.Sala;
import com.unicine.enums.theater.TipoSala;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface SalaServicio {

    // *️⃣ Funciones de Soporte

    Double obtenerPrecioBase(TipoSala tipoSala);

    // 2️⃣ Funciones del Administrador de Teatro

    Sala registrar(@Valid Sala sala) throws Exception;

    Sala actualizar(@Valid Sala sala) throws Exception;

    void eliminar(@Valid Sala sala, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Globales

    Optional<Sala> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<Sala> obtenerNombre(@NotBlank(message = ValidationMessages.NAME_NOT_BLANK) String nombre) throws Exception;

    Optional<Sala> obtenerIdTeatro(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo, @NotNull(message = ValidationMessages.THEATER_ID_NOT_NULL) @Positive(message = ValidationMessages.THEATER_ID_POSITIVE) Integer teatro) throws Exception;

    List<Sala> obtenerNombresTeatro(@NotBlank(message = ValidationMessages.NAME_NOT_BLANK) String nombre, @NotNull(message = ValidationMessages.THEATER_ID_NOT_NULL) @Positive(message = ValidationMessages.THEATER_ID_POSITIVE) Integer teatro) throws Exception;

    List<Sala> listar();

    List<Sala> listarPaginado();

    List<Sala> listarAscendente();

    List<Sala> listarDescendente();
}
