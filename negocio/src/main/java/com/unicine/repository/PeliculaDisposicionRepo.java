package com.unicine.repository;

import com.unicine.entity.PeliculaDisposicion;
import com.unicine.enumeration.EstadoPelicula;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PeliculaDisposicionRepo extends JpaRepository<PeliculaDisposicion, Integer> {
    
// NOTE: En la creacion del repositorio se extiende de jpa repository, se le pasa la entidad y el tipo de dato de la llave primaria

    /**
     * Consulta que permite obtener el estado de la pelicula en una ciudad
     * @param atributos: codigo de la pelicula, codigo de la ciudad
     * @return disposicion de la pelicula
     */
    @Query("select pd from PeliculaDisposicion pd where pd.pelicula.codigo = :codigoPelicula and pd.ciudad.codigo = :codigoCiudad")
    Optional<PeliculaDisposicion> obtenerDisposicionCiudad(Integer codigoPelicula, Integer codigoCiudad);

    /**
     * Consulta para obtener las disposiciones de una película excluyendo un estado específico
     * @param atributo: codigo de la película, estado de la película
     * @return lista de disposiciones de la película
     */
    @Query("select pd from PeliculaDisposicion pd where pd.pelicula.codigo = :codigoPelicula and pd.estadoPelicula != :estadoExcluir")
    List<PeliculaDisposicion> listarDisposicionesPelicula(Integer codigoPelicula, EstadoPelicula estadoExcluir);
}
