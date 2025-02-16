package com.unicine.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.unicine.entity.Pelicula;
import com.unicine.util.validaciones.atributos.PeliculaAtributoValidator;

import jakarta.validation.Valid;

public interface PeliculaServicio {

    // 1️⃣ Funciones del Administrador

    Pelicula registrar(@Valid Pelicula pelicula, File imagen) throws Exception;

    Pelicula actualizar(@Valid Pelicula pelicula, File imagen, String fileIdAntiguo) throws Exception;

    void eliminar(@Valid Pelicula pelicula, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Optional<Pelicula> obtener(@Valid PeliculaAtributoValidator codigo) throws Exception;

    List<Pelicula> obtenerNombrePeliculas(@Valid PeliculaAtributoValidator nombre) throws Exception;

    List<Pelicula> listar();

    List<Pelicula> listarPaginado();

    List<Pelicula> listarAscendente();

    List<Pelicula> listarDescendente();
}
