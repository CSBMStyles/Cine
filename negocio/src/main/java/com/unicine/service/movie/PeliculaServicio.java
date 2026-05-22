package com.unicine.service.movie;

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

    Optional<Pelicula> obtener(@NotNull(message = "El código no puede estar vacío") @Positive(message = "El código debe ser un número positivo") Integer codigo) throws Exception;

    List<Pelicula> obtenerNombrePeliculas(@NotBlank(message = "El nombre no puede estar en blanco") String nombre) throws Exception;

    List<Pelicula> listar();

    List<Pelicula> listarPaginado();

    List<Pelicula> listarAscendente();

    List<Pelicula> listarDescendente();
}
