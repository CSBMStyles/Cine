package com.unicine.service.theater;

import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import com.unicine.entity.theater.Ciudad;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface CiudadServicio {

    // 1️⃣ Funciones del Administrador

    Ciudad registrar(@Valid Ciudad ciudad) throws Exception;

    Ciudad actualizar(@Valid Ciudad ciudad) throws Exception;

    void eliminar(@Valid Ciudad ciudad) throws Exception;

    // *️⃣ Funciones Generales

    Optional<Ciudad> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<Ciudad> obtenerNombre(@NotBlank(message = ValidationMessages.NAME_NOT_BLANK) String nombre) throws Exception;

    List<Ciudad> listar();

    List<Ciudad> listarPaginado();

    List<Ciudad> listarAscendenteNombre();

    List<Ciudad> listarDescendenteNombre();
}
