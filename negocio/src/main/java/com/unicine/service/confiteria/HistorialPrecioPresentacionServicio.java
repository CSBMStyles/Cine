package com.unicine.service.confiteria;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;
import com.unicine.entity.confiteria.HistorialPrecioPresentacion;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Servicio de historial de precios de presentaciones de confiteria.
 *
 * PERMISOS:
 * - Cliente y administrador de teatro: solo consulta del ultimo registro
 *   para visualizar el porcentaje de descuento.
 * - Administrador: consulta completa y eliminacion de historial.
 */
public interface HistorialPrecioPresentacionServicio {

    HistorialPrecioPresentacion registrarCambio(ConfiteriaPresentacion presentacion,
                                                 Double precioAnterior,
                                                 Double precioNuevo,
                                                 Double precioBaseAnterior) throws Exception;

    List<HistorialPrecioPresentacion> listarPorPresentacion(Integer codigoPresentacion) throws Exception;

    Optional<HistorialPrecioPresentacion> obtenerUltimoPorPresentacion(Integer codigoPresentacion) throws Exception;

    void eliminarPorPresentacion(@NotNull @Positive Integer codigoPresentacion) throws Exception;

    void eliminarTodo() throws Exception;

    Optional<HistorialPrecioPresentacion> obtener(
            @NotNull(message = "El codigo no puede estar vacio")
            @Positive(message = "El codigo debe ser un numero positivo")
            Integer codigo) throws Exception;
}
