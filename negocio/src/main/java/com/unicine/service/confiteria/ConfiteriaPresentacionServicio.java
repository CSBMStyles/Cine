package com.unicine.service.confiteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.unicine.transfer.dto.request.ConfiteriaPresentacionRequest;
import com.unicine.transfer.dto.response.ConfiteriaPresentacionResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Servicio para la gestion de presentaciones de confiteria.
 * 
 * Una presentacion define una porcion/unidad de medida y su precio.
 * El precio y el precioBase son editables por administrador y administrador de teatro.
 */
public interface ConfiteriaPresentacionServicio {

    ConfiteriaPresentacionResponse registrar(@Valid ConfiteriaPresentacionRequest request) throws Exception;

    ConfiteriaPresentacionResponse actualizar(@Valid ConfiteriaPresentacionRequest request, LocalDateTime fechaExpiracionTemporal) throws Exception;

    void eliminar(
            @NotNull(message = "El codigo no puede estar vacio")
            @Positive(message = "El codigo debe ser un numero positivo")
            Integer codigo,
            boolean confirmacion) throws Exception;

    Optional<ConfiteriaPresentacionResponse> obtener(
            @NotNull(message = "El codigo no puede estar vacio")
            @Positive(message = "El codigo debe ser un numero positivo")
            Integer codigo) throws Exception;

    List<ConfiteriaPresentacionResponse> listar();

    List<ConfiteriaPresentacionResponse> listarPorConfiteria(Integer codigoConfiteria) throws Exception;

    List<ConfiteriaPresentacionResponse> listarConDescuentoTemporal();
}
