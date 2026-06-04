package com.unicine.service.movie;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.movie.Coleccion;
import com.unicine.enums.movie.EstadoPropio;

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

    Coleccion registrar(@Valid Coleccion coleccion) throws Exception;

    Coleccion actualizar(@Valid Coleccion coleccion) throws Exception;

    void eliminar(@Valid Coleccion coleccion, boolean confirmacion) throws Exception;

    Optional<Coleccion> obtener(
            @NotNull(message = "La cedula no puede estar vacia")
            @Positive(message = "La cedula debe ser un numero positivo")
            Integer cedula,
            @NotNull(message = "El codigo de pelicula no puede estar vacio")
            @Positive(message = "El codigo debe ser un numero positivo")
            Integer codigoPelicula) throws Exception;

    List<Coleccion> listar();

    List<Coleccion> listarPaginado();

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
    List<Coleccion> listarPorCliente(Integer cedula) throws Exception;

    /**
     * Lista todas las colecciones asociadas a una pelicula.
     * 
     * @param codigoPelicula Codigo de la pelicula
     * @return Lista de colecciones de la pelicula
     * @throws Exception si la pelicula no existe o no tiene colecciones
     */
    List<Coleccion> listarPorPelicula(Integer codigoPelicula) throws Exception;

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
    Coleccion calificarPelicula(Integer cedula, Integer codigoPelicula, Double puntuacion) throws Exception;

    /**
     * Cambia el estado de una pelicula en la coleccion de un cliente.
     * 
     * @param cedula Cedula del cliente
     * @param codigoPelicula Codigo de la pelicula
     * @param estado Nuevo estado (VISTO, EN_ESPERA, FAVORITO)
     * @return Coleccion actualizada
     * @throws Exception si la coleccion no existe
     */
    Coleccion cambiarEstadoPelicula(Integer cedula, Integer codigoPelicula, EstadoPropio estado) throws Exception;
}
