package com.unicine.service.movie;

import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import com.unicine.entity.movie.Pelicula;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface PeliculaServicio {

    // 1️⃣ Funciones del Administrador

    Pelicula registrar(@Valid Pelicula pelicula) throws Exception;

    Pelicula actualizar(@Valid Pelicula pelicula) throws Exception;

    void eliminar(@Valid Pelicula pelicula, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Optional<Pelicula> obtener(@NotNull(message = ValidationMessages.ID_NOT_NULL) @Positive(message = ValidationMessages.ID_POSITIVE) Integer codigo) throws Exception;

    List<Pelicula> obtenerNombrePeliculas(@NotBlank(message = ValidationMessages.NAME_NOT_BLANK) String nombre) throws Exception;

    List<Pelicula> listar();

    List<Pelicula> listarPaginado();

    List<Pelicula> listarAscendente();

    List<Pelicula> listarDescendente();
}
