package com.unicine.service.purchase;

import java.util.List;
import java.util.Optional;

import com.unicine.transfer.dto.request.EntradaRequest;
import com.unicine.transfer.dto.response.DetalleSillaResponse;
import com.unicine.transfer.dto.response.EntradaResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Servicio de gestion de entradas.
 *
 * Permite registrar, consultar y eliminar entradas asociadas a una compra
 * y una funcion, validando disponibilidad de sillas y actualizando el
 * esquema de ocupacion de la funcion.
 */
public interface EntradaServicio {

    EntradaResponse registrar(@Valid EntradaRequest request) throws Exception;

    EntradaResponse actualizar(@Valid EntradaRequest request) throws Exception;

    void eliminar(
            @NotNull @Positive Integer codigo,
            boolean confirmacion) throws Exception;

    Optional<EntradaResponse> obtener(@NotNull @Positive Integer codigo) throws Exception;

    List<EntradaResponse> listar();

    List<EntradaResponse> listarPaginado();

    List<EntradaResponse> listarPorCompra(@NotNull @Positive Integer codigoCompra) throws Exception;

    List<EntradaResponse> listarPorFuncion(@NotNull @Positive Integer codigoFuncion) throws Exception;

    List<DetalleSillaResponse> obtenerSillasOcupadas(@NotNull @Positive Integer codigoFuncion) throws Exception;
}
