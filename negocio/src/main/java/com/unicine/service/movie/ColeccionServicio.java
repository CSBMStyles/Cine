package com.unicine.service.movie;

import java.util.List;
import java.util.Optional;

import com.unicine.transfer.dto.request.ColeccionRequest;
import com.unicine.transfer.dto.response.ColeccionResponse;
import com.unicine.enums.movie.EstadoPropio;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * Interfaz de servicio para la gestion de colecciones de peliculas.
 *
 * Una coleccion representa la relacion entre un cliente y una pelicula,
 * permitiendo registrar el estado de visionado (VISTO, EN_ESPERA, FAVORITO)
 * y una puntuacion personal.
 *
 * La entidad usa clave compuesta (cliente + pelicula), por lo que
 * los metodos de consulta requieren ambos identificadores.
 */
public interface ColeccionServicio {

    // ============================================================
    // CRUD BASE
    // ============================================================

    ColeccionResponse registrar(@Valid ColeccionRequest request) throws Exception;

    ColeccionResponse actualizar(@Valid ColeccionRequest request) throws Exception;

    void eliminar(
            @NotNull(message = ValidationMessages.CEDULA_NOT_NULL)
            @Positive(message = ValidationMessages.CEDULA_POSITIVE)
            Integer cedula,
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigoPelicula,
            boolean confirmacion) throws Exception;

    Optional<ColeccionResponse> obtener(
            @NotNull(message = ValidationMessages.CEDULA_NOT_NULL)
            @Positive(message = ValidationMessages.CEDULA_POSITIVE)
            Integer cedula,
            @NotNull(message = ValidationMessages.ID_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE)
            Integer codigoPelicula) throws Exception;

    List<ColeccionResponse> listar();

    List<ColeccionResponse> listarPaginado();

    // ============================================================
    // METODOS DE NEGOCIO
    // ============================================================

    /**
     * Lista todas las colecciones de un cliente especifico.
     *
     * @param cedula Cedula del cliente
     * @return Lista de colecciones del cliente
     * @throws Exception si el cliente no existe o no tiene colecciones
     */
    List<ColeccionResponse> listarPorCliente(Integer cedula) throws Exception;

    /**
     * Lista todas las colecciones asociadas a una pelicula.
     *
     * @param codigoPelicula Codigo de la pelicula
     * @return Lista de colecciones de la pelicula
     * @throws Exception si la pelicula no existe o no tiene colecciones
     */
    List<ColeccionResponse> listarPorPelicula(Integer codigoPelicula) throws Exception;

    /**
     * Obtiene el promedio de puntuaciones de una pelicula.
     *
     * @param codigoPelicula Codigo de la pelicula
     * @return Promedio de puntuaciones
     * @throws Exception si la pelicula no tiene puntuaciones
     */
    Double obtenerPuntuacionPromedioPelicula(Integer codigoPelicula) throws Exception;

    /**
     * Cuenta cuantas peliculas tiene un cliente en su coleccion.
     *
     * @param cedula Cedula del cliente
     * @return Cantidad de colecciones del cliente
     */
    Long contarColeccionesCliente(Integer cedula) throws Exception;

    /**
     * Asigna o actualiza la puntuacion de una pelicula en la coleccion de un cliente.
     *
     * @param cedula Cedula del cliente
     * @param codigoPelicula Codigo de la pelicula
     * @param puntuacion Nueva puntuacion (1.0 - 5.0)
     * @return Coleccion actualizada
     * @throws Exception si la coleccion no existe
     */
    ColeccionResponse calificarPelicula(Integer cedula, Integer codigoPelicula, Double puntuacion) throws Exception;

    /**
     * Cambia el estado de una pelicula en la coleccion de un cliente.
     *
     * @param cedula Cedula del cliente
     * @param codigoPelicula Codigo de la pelicula
     * @param estado Nuevo estado (VISTO, EN_ESPERA, FAVORITO)
     * @return Coleccion actualizada
     * @throws Exception si la coleccion no existe
     */
    ColeccionResponse cambiarEstadoPelicula(Integer cedula, Integer codigoPelicula, EstadoPropio estado) throws Exception;
}
