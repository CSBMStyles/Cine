package com.unicine.service.purchase;

import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import com.unicine.transfer.dto.request.CompraCompletaRequest;
import com.unicine.transfer.dto.request.CompraRequest;
import com.unicine.transfer.dto.response.CompraResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface CompraServicio {

    // SECTION: Metodos CRUD base

    CompraResponse registrar(@Valid CompraRequest request) throws Exception;

    CompraResponse actualizar(@Valid CompraRequest request) throws Exception;

    void eliminar(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    Optional<CompraResponse> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<CompraResponse> listar();

    List<CompraResponse> listarPaginado();

    // SECTION: Metodos de negocio

    CompraResponse registrarCompraCompleta(@Valid CompraCompletaRequest request) throws Exception;

    List<CompraResponse> obtenerComprasCliente(@NotNull(message = ValidationMessages.CEDULA_NOT_NULL) @Positive(message = ValidationMessages.CEDULA_POSITIVE) Integer cedula) throws Exception;

    Double obtenerTotalComprasCliente(@NotNull(message = ValidationMessages.CEDULA_NOT_NULL) @Positive(message = ValidationMessages.CEDULA_POSITIVE) Integer cedula) throws Exception;
}
