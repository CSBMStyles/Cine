package com.unicine.repository.movie;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.unicine.entity.movie.Comentario;

@Repository
public interface ComentarioRepo extends JpaRepository<Comentario, Integer> {

    // Note: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    // SECTION: Relacion con pelicula

    /**
     * Consulta para obtener los comentarios de una pelicula.
     * @param codigoPelicula codigo de la pelicula
     * @return lista de comentarios
     */
    List<Comentario> findByPeliculaCodigo(Integer codigoPelicula);

    // !SECTION
    // SECTION: Relacion con cliente

    /**
     * Consulta para obtener los comentarios de un cliente.
     * @param cedula cedula del cliente
     * @return lista de comentarios
     */
    List<Comentario> findByClienteCedula(Integer cedula);
    // !SECTION
}
