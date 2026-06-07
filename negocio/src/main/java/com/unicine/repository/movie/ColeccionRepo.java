package com.unicine.repository.movie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.unicine.entity.movie.Coleccion;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.movie.composed.ColeccionCompuesta;

@Repository
public interface ColeccionRepo extends JpaRepository<Coleccion, ColeccionCompuesta> {

    // NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Obtiene todas las colecciones de un cliente especifico.
     * 
     * @param cedula Cedula del cliente
     * @return Lista de colecciones del cliente
     */
    @Query("SELECT c FROM Coleccion c WHERE c.cliente.cedula = :cedula")
    List<Coleccion> listarPorCliente(Integer cedula);

    /**
     * Obtiene todas las colecciones asociadas a una pelicula.
     * 
     * @param codigoPelicula Codigo de la pelicula
     * @return Lista de colecciones de la pelicula
     */
    @Query("SELECT c FROM Coleccion c WHERE c.pelicula.codigo = :codigoPelicula")
    List<Coleccion> listarPorPelicula(Integer codigoPelicula);

    /**
     * Calcula el promedio de puntuaciones de una pelicula.
     * 
     * @param codigoPelicula Codigo de la pelicula
     * @return Promedio de puntuaciones o null si no hay puntuaciones
     */
    @Query("SELECT AVG(c.puntuacion) FROM Coleccion c WHERE c.pelicula.codigo = :codigoPelicula AND c.puntuacion IS NOT NULL")
    Double obtenerPuntuacionPromedio(Integer codigoPelicula);

    /**
     * Cuenta cuantas colecciones tiene un cliente.
     * 
     * @param cedula Cedula del cliente
     * @return Cantidad de colecciones
     */
    @Query("SELECT COUNT(c) FROM Coleccion c WHERE c.cliente.cedula = :cedula")
    Long contarPorCliente(Integer cedula);

    /**
     * Obtiene las colecciones de una pelicula donde el cliente tiene activadas las notificaciones.
     *
     * @param pelicula Pelicula a buscar
     * @param notificacionActiva Estado de la notificacion
     * @return Lista de colecciones con notificaciones activas
     */
    List<Coleccion> findByPeliculaAndNotificacionActiva(Pelicula pelicula, Boolean notificacionActiva);
}
