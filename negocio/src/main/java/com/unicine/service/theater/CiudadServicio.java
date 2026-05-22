package com.unicine.service.theater;

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

    Optional<Ciudad> obtener(@NotNull(message = "El código no puede estar vacío") @Positive(message = "El código debe ser un número positivo") Integer codigo) throws Exception;

    List<Ciudad> obtenerNombre(@NotBlank(message = "El nombre no puede estar en blanco") String nombre) throws Exception;

    List<Ciudad> listar();

    List<Ciudad> listarPaginado();

    List<Ciudad> listarAscendenteNombre();

    List<Ciudad> listarDescendenteNombre();
}
