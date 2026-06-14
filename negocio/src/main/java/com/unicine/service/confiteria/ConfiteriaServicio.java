package com.unicine.service.confiteria;

import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.enums.confiteria.CategoriaConfiteria;

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

    // ============================================================
    // CRUD BASE
    // ============================================================

    Confiteria registrar(@Valid Confiteria confiteria) throws Exception;

    Confiteria actualizar(@Valid Confiteria confiteria) throws Exception;

    void eliminar(@Valid Confiteria confiteria, boolean confirmacion) throws Exception;

    Optional<Confiteria> obtener(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigo) throws Exception;

    List<Confiteria> listar();

    List<Confiteria> listarPaginado();

    // ============================================================
    // METODOS DE NEGOCIO
    // ============================================================

    /**
     * Lista los productos filtrados por categoria.
     * 
     * @param categoria Categoria de producto
     * @return Lista de productos de esa categoria
     * @throws Exception si no hay productos en la categoria
     */
    List<Confiteria> listarPorCategoria(CategoriaConfiteria categoria) throws Exception;

    /**
     * Busca productos de confiteria por nombre (busqueda parcial).
     * 
     * @param nombre Texto a buscar en el nombre
     * @return Lista de productos que coinciden
     * @throws Exception si no se encuentran coincidencias
     */
    List<Confiteria> buscarPorNombre(String nombre) throws Exception;
}
