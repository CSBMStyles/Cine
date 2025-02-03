package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import com.unicine.entidades.PeliculaDisposicion;
import com.unicine.util.emuns.EstadoPelicula;

import jakarta.validation.Valid;

public interface PeliculaDisposicionServicio {

    // 1️⃣ Funciones del Administrador

    PeliculaDisposicion registrar(@Valid PeliculaDisposicion peliculaDisposicion) throws Exception;

    PeliculaDisposicion actualizar(@Valid PeliculaDisposicion peliculaDisposicion) throws Exception;

    void eliminar(@Valid PeliculaDisposicion peliculaDisposicion, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Optional<PeliculaDisposicion> obtener(Integer codigo) throws Exception;

    List<PeliculaDisposicion> listar();

    List<PeliculaDisposicion> listarRecomendacionPeliculaEstado(PeliculaDisposicion peliculaDisposicion, EstadoPelicula estadoPelicula);

    List<PeliculaDisposicion> listarPaginado();

    List<PeliculaDisposicion> listarAscendente();

    List<PeliculaDisposicion> listarDescendente();
}
