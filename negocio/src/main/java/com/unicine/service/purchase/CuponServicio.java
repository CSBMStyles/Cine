package com.unicine.service.purchase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.unicine.entity.purchase.Cupon;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Interfaz de servicio para la gestion de cupones de descuento.
 * 
 * Administra los cupones globales disponibles para ser asignados a los clientes
 * y permite consultas por estado, criterio, rango de descuento y asignaciones.
 */
public interface CuponServicio {

    // ============================================================
    // CRUD BASE
    // ============================================================

    Cupon registrar(@Valid Cupon cupon) throws Exception;

    Cupon actualizar(@Valid Cupon cupon) throws Exception;

    void eliminar(@Valid Cupon cupon, boolean confirmacion) throws Exception;

    Optional<Cupon> obtener(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigo) throws Exception;

    List<Cupon> listar();

    List<Cupon> listarPaginado();

    // ============================================================
    // METODOS DE NEGOCIO
    // ============================================================

    /**
     * Lista los cupones cuya fecha de vencimiento aun no ha pasado.
     * 
     * @return Lista de cupones activos
     * @throws Exception si no hay cupones activos
     */
    List<Cupon> listarActivos() throws Exception;

    /**
     * Lista los cupones cuya fecha de vencimiento ya paso.
     * 
     * @return Lista de cupones vencidos
     * @throws Exception si no hay cupones vencidos
     */
    List<Cupon> listarVencidos() throws Exception;

    /**
     * Busca cupones cuyo criterio contenga el texto dado.
     * 
     * @param criterio Texto a buscar en el criterio del cupon
     * @return Lista de cupones que coinciden
     * @throws Exception si no se encuentran coincidencias
     */
    List<Cupon> buscarPorCriterio(String criterio) throws Exception;

    /**
     * Lista los cupones cuyo descuento este dentro del rango indicado.
     * 
     * @param min Valor minimo del descuento (inclusive)
     * @param max Valor maximo del descuento (inclusive)
     * @return Lista de cupones dentro del rango
     * @throws Exception si no hay cupones en el rango
     */
    List<Cupon> listarPorRangoDescuento(
            @NotNull(message = ValidationMessages.DISCOUNT_NOT_NULL)
            @PositiveOrZero(message = ValidationMessages.DISCOUNT_POSITIVE_OR_ZERO)
            Double min,
            @NotNull(message = ValidationMessages.DISCOUNT_NOT_NULL)
            @PositiveOrZero(message = ValidationMessages.DISCOUNT_POSITIVE_OR_ZERO)
            Double max) throws Exception;

    /**
     * Lista los cupones que tienen al menos una asignacion a un cliente.
     * 
     * @return Lista de cupones con asignaciones
     * @throws Exception si no hay cupones con asignaciones
     */
    List<Cupon> listarConAsignaciones() throws Exception;
}
