package com.unicine.service.confiteria;

import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import com.unicine.enums.confiteria.CategoriaConfiteria;
import com.unicine.transfer.dto.request.ConfiteriaRequest;
import com.unicine.transfer.dto.response.ConfiteriaResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Interfaz de servicio para la gestion de productos de confiteria.
 * 
 * Administra el catalogo de productos disponibles en el cine,
 * clasificados por categoria: combos, bebidas, snacks, dulces y otros.
 */
public interface ConfiteriaServicio {

    // SECTION: Crud base

    ConfiteriaResponse registrar(@Valid ConfiteriaRequest request) throws Exception;

    ConfiteriaResponse actualizar(@Valid ConfiteriaRequest request) throws Exception;

    void eliminar(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigo,
            boolean confirmacion) throws Exception;

    Optional<ConfiteriaResponse> obtener(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigo) throws Exception;

    List<ConfiteriaResponse> listar();

    List<ConfiteriaResponse> listarPaginado();

    // !SECTION
    // SECTION: Metodos de negocio

    /**
     * Lista los productos filtrados por categoria.
     * 
     * @param categoria Categoria de producto
     * @return Lista de productos de esa categoria
     * @throws Exception si no hay productos en la categoria
     */
    List<ConfiteriaResponse> listarPorCategoria(CategoriaConfiteria categoria) throws Exception;

    /**
     * Busca productos de confiteria por nombre (busqueda parcial).
     * 
     * @param nombre Texto a buscar en el nombre
     * @return Lista de productos que coinciden
     * @throws Exception si no se encuentran coincidencias
     */
    List<ConfiteriaResponse> buscarPorNombre(String nombre) throws Exception;
    // !SECTION
}
