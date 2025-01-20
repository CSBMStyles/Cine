package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import com.unicine.entidades.Funcion;

import jakarta.validation.Valid;

public interface FuncionServicio {

    // 2️⃣ Funciones del Administrador de Teatro

    Funcion registrar(@Valid Funcion funcion) throws Exception;

    Funcion actualizar(@Valid Funcion funcion) throws Exception;

    void eliminar(@Valid Funcion funcion, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Generales

    Optional<Funcion> obtener(Integer codigo) throws Exception;

    List<Funcion> listar();

    List<Funcion> listarPaginado();

    List<Funcion> listarAscendente();

    List<Funcion> listarDescendente();
}
