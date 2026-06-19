package com.unicine.service.purchase;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.purchase.CompraConfiteria;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Interfaz de servicio para la gestion de items de confiteria dentro de una compra.
 * 
 * Administra la relacion entre una compra y los productos de confiteria
 * adquiridos, incluyendo consultas por compra y calculo de totales.
 */
public interface CompraConfiteriaServicio {

    // ============================================================
    // CRUD BASE
    // ============================================================

    CompraConfiteria registrar(@Valid CompraConfiteria compraConfiteria) throws Exception;

    CompraConfiteria actualizar(@Valid CompraConfiteria compraConfiteria) throws Exception;

    void eliminar(@Valid CompraConfiteria compraConfiteria, boolean confirmacion) throws Exception;

    Optional<CompraConfiteria> obtener(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigo) throws Exception;

    List<CompraConfiteria> listar();

    List<CompraConfiteria> listarPaginado();

    // ============================================================
    // METODOS DE NEGOCIO
    // ============================================================

    /**
     * Lista los items de confiteria de una compra especifica.
     * 
     * @param codigoCompra Codigo de la compra
     * @return Lista de items de confiteria
     * @throws Exception si la compra no existe o no tiene items
     */
    List<CompraConfiteria> listarPorCompra(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigoCompra) throws Exception;

    /**
     * Lista los items asociados a una confiteria en todas las compras.
     * 
     * @param codigoConfiteria Codigo de la confiteria
     * @return Lista de items de confiteria
     * @throws Exception si la confiteria no existe o no tiene items
     */
    List<CompraConfiteria> listarPorConfiteria(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigoConfiteria) throws Exception;

    /**
     * Calcula el total de confiteria de una compra sumando precio * unidades.
     * 
     * @param codigoCompra Codigo de la compra
     * @return Total de confiteria
     * @throws Exception si la compra no existe
     */
    Double calcularTotalPorCompra(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigoCompra) throws Exception;
}
