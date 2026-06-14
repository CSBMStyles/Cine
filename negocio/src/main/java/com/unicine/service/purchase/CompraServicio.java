package com.unicine.service.purchase;

import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import com.unicine.entity.purchase.Compra;
import com.unicine.entity.purchase.Entrada;
import com.unicine.entity.purchase.CompraConfiteria;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface CompraServicio {

    // SECTION: Metodos CRUD base

    Compra registrar(@Valid Compra compra) throws Exception;

    Compra actualizar(@Valid Compra compra) throws Exception;

    void eliminar(@Valid Compra compra) throws Exception;

    Optional<Compra> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<Compra> listar();

    List<Compra> listarPaginado();

    // SECTION: Metodos de negocio

    Compra registrarCompraCompleta(Compra compra, List<Entrada> entradas, List<CompraConfiteria> confiterias) throws Exception;

    List<Compra> obtenerComprasCliente(@NotNull(message = ValidationMessages.CEDULA_NOT_NULL) @Positive(message = ValidationMessages.CEDULA_POSITIVE) Integer cedula) throws Exception;

    Double obtenerTotalComprasCliente(@NotNull(message = ValidationMessages.CEDULA_NOT_NULL) @Positive(message = ValidationMessages.CEDULA_POSITIVE) Integer cedula) throws Exception;
}
