package com.unicine.service.movie;

import java.util.List;
import java.util.Optional;

import com.unicine.transfer.dto.request.ComentarioRequest;
import com.unicine.transfer.dto.response.ComentarioResponse;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Interfaz de servicio para la gestion de comentarios y reseñas de peliculas.
 *
 * Permite registrar, consultar y reaccionar a comentarios de clientes
 * que hayan asistido a una funcion de la pelicula.
 */
public interface ComentarioServicio {

    // ============================================================
    // SECTION CRUD BASE
    // ============================================================

    ComentarioResponse registrar(@Valid ComentarioRequest request) throws Exception;

    ComentarioResponse actualizar(@Valid ComentarioRequest request) throws Exception;

    void eliminar(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigo,
            boolean confirmacion) throws Exception;

    Optional<ComentarioResponse> obtener(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigo) throws Exception;

    List<ComentarioResponse> listar();

    List<ComentarioResponse> listarPaginado();

    // !SECTION

    // ============================================================
    // METODOS DE NEGOCIO
    // ============================================================

    /**
     * Lista los comentarios de una pelicula especifica.
     *
     * @param codigoPelicula Codigo de la pelicula
     * @return Lista de comentarios
     * @throws Exception si la pelicula no existe o no tiene comentarios
     */
    List<ComentarioResponse> listarPorPelicula(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigoPelicula) throws Exception;

    /**
     * Lista los comentarios de un cliente especifico.
     *
     * @param cedula Cedula del cliente
     * @return Lista de comentarios
     * @throws Exception si el cliente no existe o no tiene comentarios
     */
    List<ComentarioResponse> listarPorCliente(
            @NotNull(message = ValidationMessages.CEDULA_NOT_NULL)
            @Positive(message = ValidationMessages.CEDULA_POSITIVE)
            Integer cedula) throws Exception;

    /**
     * Incrementa en uno los likes de un comentario.
     *
     * @param codigo Codigo del comentario
     * @return Comentario actualizado
     * @throws Exception si el comentario no existe
     */
    ComentarioResponse darLike(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigo) throws Exception;

    /**
     * Incrementa en uno los dislikes de un comentario.
     *
     * @param codigo Codigo del comentario
     * @return Comentario actualizado
     * @throws Exception si el comentario no existe
     */
    ComentarioResponse darDislike(
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigo) throws Exception;
}
