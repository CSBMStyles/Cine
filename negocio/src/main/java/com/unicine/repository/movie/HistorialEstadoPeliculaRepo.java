package com.unicine.repository.movie;

import com.unicine.entity.movie.HistorialEstadoPelicula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialEstadoPeliculaRepo extends JpaRepository<HistorialEstadoPelicula, Integer> {

    List<HistorialEstadoPelicula> findByPeliculaDisposicion_Pelicula_CodigoAndPeliculaDisposicion_Ciudad_Codigo(Integer peliculaId, Integer ciudadId);

    long countByPeliculaDisposicion_Pelicula_CodigoAndPeliculaDisposicion_Ciudad_Codigo(Integer peliculaId, Integer ciudadId);

    void deleteByPeliculaDisposicion_Pelicula_CodigoAndPeliculaDisposicion_Ciudad_Codigo(Integer peliculaId, Integer ciudadId);

    HistorialEstadoPelicula findTopByPeliculaDisposicion_Pelicula_CodigoAndPeliculaDisposicion_Ciudad_CodigoOrderByFechaCambioDesc(Integer peliculaId, Integer ciudadId);
}
