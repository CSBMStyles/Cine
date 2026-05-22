package com.unicine.service.theater;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.theater.Teatro;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface TeatroServicio {

    // 2️⃣ Funciones del Administrador Teatro

    Teatro registrar(@Valid Teatro teatro) throws Exception;

    Teatro actualizar(@Valid Teatro teatro) throws Exception;

    void eliminar(@Valid Teatro teatro, boolean confirmacion) throws Exception;

    Optional<Teatro> obtener(@NotNull(message = "El código no puede estar vacío") @Positive(message = "El código debe ser un número positivo") Integer codigo) throws Exception;

    List<Teatro> listar();

    List<Teatro> listarPaginado();

    List<Teatro> listarAscendente();

    List<Teatro> listarDescendente();
}
