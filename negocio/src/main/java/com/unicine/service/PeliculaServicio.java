package com.unicine.service;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.Pelicula;
import com.unicine.util.validation.attributes.PeliculaAtributoValidator;

import jakarta.validation.Valid;

public interface PeliculaServicio {

    // 1️⃣ Funciones del Administrador

    Pelicula registrar(@Valid Pelicula pelicula) throws Exception;

    Pelicula actualizar(@Valid Pelicula pelicula) throws Exception;

    void eliminar(@Valid Pelicula pelicula, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Optional<Pelicula> obtener(@Valid PeliculaAtributoValidator codigo) throws Exception;

    List<Pelicula> obtenerNombrePeliculas(@Valid PeliculaAtributoValidator nombre) throws Exception;

    List<Pelicula> listar();

    List<Pelicula> listarPaginado();

    List<Pelicula> listarAscendente();

    List<Pelicula> listarDescendente();
}
