package com.unicine.service.confiteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;

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

    ConfiteriaPresentacion registrar(@Valid ConfiteriaPresentacion presentacion) throws Exception;

    ConfiteriaPresentacion actualizar(@Valid ConfiteriaPresentacion presentacion, LocalDateTime fechaExpiracionTemporal) throws Exception;

    void eliminar(@Valid ConfiteriaPresentacion presentacion, boolean confirmacion) throws Exception;

    Optional<ConfiteriaPresentacion> obtener(
            @NotNull(message = "El codigo no puede estar vacio")
            @Positive(message = "El codigo debe ser un numero positivo")
            Integer codigo) throws Exception;

    List<ConfiteriaPresentacion> listar();

    List<ConfiteriaPresentacion> listarPorConfiteria(Integer codigoConfiteria) throws Exception;

    List<ConfiteriaPresentacion> listarConDescuentoTemporal();
}
