package com.unicine.service.purchase;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.purchase.CuponCliente;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Interfaz de servicio para la gestion de cupones asignados a clientes.
 * 
 * Administra la relacion entre un cupon global y un cliente especifico,
 * incluyendo su estado de disponibilidad y consultas por cliente.
 */
public interface CuponClienteServicio {

    // ============================================================
    // CRUD BASE
    // ============================================================

    CuponCliente registrar(@Valid CuponCliente cuponCliente) throws Exception;

    CuponCliente actualizar(@Valid CuponCliente cuponCliente) throws Exception;

    void eliminar(@Valid CuponCliente cuponCliente, boolean confirmacion) throws Exception;

    Optional<CuponCliente> obtener(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigo) throws Exception;

    List<CuponCliente> listar();

    List<CuponCliente> listarPaginado();

    // ============================================================
    // METODOS DE NEGOCIO
    // ============================================================

    /**
     * Lista las asignaciones de cupones de un cliente especifico.
     * 
     * @param cedula Cedula del cliente
     * @return Lista de asignaciones del cliente
     * @throws Exception si el cliente no existe o no tiene asignaciones
     */
    List<CuponCliente> listarPorCliente(
            @NotNull(message = ValidationMessages.CEDULA_NOT_NULL)
            @Positive(message = ValidationMessages.CEDULA_POSITIVE)
            Integer cedula) throws Exception;

    /**
     * Lista las asignaciones activas de un cliente especifico.
     * 
     * @param cedula Cedula del cliente
     * @return Lista de asignaciones activas del cliente
     * @throws Exception si el cliente no tiene asignaciones activas
     */
    List<CuponCliente> listarActivosPorCliente(
            @NotNull(message = ValidationMessages.CEDULA_NOT_NULL)
            @Positive(message = ValidationMessages.CEDULA_POSITIVE)
            Integer cedula) throws Exception;

    /**
     * Lista las asignaciones inactivas de un cliente especifico.
     * 
     * @param cedula Cedula del cliente
     * @return Lista de asignaciones inactivas del cliente
     * @throws Exception si el cliente no tiene asignaciones inactivas
     */
    List<CuponCliente> listarInactivosPorCliente(
            @NotNull(message = ValidationMessages.CEDULA_NOT_NULL)
            @Positive(message = ValidationMessages.CEDULA_POSITIVE)
            Integer cedula) throws Exception;

    /**
     * Obtiene una asignacion especifica por cupon y cliente.
     * 
     * @param codigoCupon Codigo del cupon
     * @param cedula Cedula del cliente
     * @return Asignacion encontrada
     * @throws Exception si no se encuentra la asignacion
     */
    Optional<CuponCliente> obtenerPorCuponYCliente(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigoCupon,
            @NotNull(message = ValidationMessages.CEDULA_NOT_NULL)
            @Positive(message = ValidationMessages.CEDULA_POSITIVE)
            Integer cedula) throws Exception;

    /**
     * Cuenta cuantos cupones ha redimido un cliente en compras.
     * 
     * @param cedula Cedula del cliente
     * @return Cantidad de cupones redimidos
     * @throws Exception si el cliente no ha redimido cupones
     */
    Long contarRedimidosPorCliente(
            @NotNull(message = ValidationMessages.CEDULA_NOT_NULL)
            @Positive(message = ValidationMessages.CEDULA_POSITIVE)
            Integer cedula) throws Exception;
}
