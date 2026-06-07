package com.unicine.service.movie;

import com.unicine.entity.movie.HistorialEstadoPelicula;
import com.unicine.enums.movie.EstadoPelicula;

import java.util.List;

public interface HistorialEstadoPeliculaServicio {

    HistorialEstadoPelicula registrar(Integer peliculaId, Integer ciudadId, EstadoPelicula anterior, EstadoPelicula nuevo);

    List<HistorialEstadoPelicula> obtenerPorPelicula(Integer peliculaId, Integer ciudadId);

    void eliminarPorPelicula(Integer peliculaId, Integer ciudadId);

    long contarPorPelicula(Integer peliculaId, Integer ciudadId);

    Integer obtenerAntiguedadUltimoCambio(Integer peliculaId, Integer ciudadId);

    void alertarSiHistorialViejo(Integer peliculaId, Integer ciudadId);
}
