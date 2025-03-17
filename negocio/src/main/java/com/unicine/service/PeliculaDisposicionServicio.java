package com.unicine.service;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.PeliculaDisposicion;
import com.unicine.entity.composed.PeliculaDisposicionCompuesta;
import com.unicine.enumeration.EstadoPelicula;

import jakarta.validation.Valid;

public interface PeliculaDisposicionServicio {

    // 1️⃣ Funciones del Administrador

    PeliculaDisposicion registrar(@Valid PeliculaDisposicion peliculaDisposicion) throws Exception;

    PeliculaDisposicion actualizar(@Valid PeliculaDisposicion peliculaDisposicion) throws Exception;

    void eliminar(@Valid PeliculaDisposicion peliculaDisposicion, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Optional<PeliculaDisposicion> obtener(@Valid PeliculaDisposicionCompuesta codigo) throws Exception;

    List<PeliculaDisposicion> listar();

    List<PeliculaDisposicion> listarRecomendacionPeliculaEstado(PeliculaDisposicion peliculaDisposicion, EstadoPelicula estadoPelicula);

    List<PeliculaDisposicion> listarPaginado();

    List<PeliculaDisposicion> listarAscendente();

    List<PeliculaDisposicion> listarDescendente();
}
