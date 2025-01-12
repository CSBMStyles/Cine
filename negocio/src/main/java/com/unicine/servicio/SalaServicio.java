package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import com.unicine.entidades.Sala;
import com.unicine.util.validacion.atributos.SalaAtributoValidator;

import jakarta.validation.Valid;

public interface SalaServicio {

    // 2️⃣ Funciones del Administrador de Teatro

    Sala registrar(@Valid Sala sala) throws Exception;

    Sala actualizar(@Valid Sala sala) throws Exception;

    void eliminar(@Valid Sala sala, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Globales

    Optional<Sala> obtener(@Valid SalaAtributoValidator codigo) throws Exception;

    List<Sala> obtenerNombre(@Valid SalaAtributoValidator nombre) throws Exception;

    Optional<Sala> obtenerIdTeatro(@Valid SalaAtributoValidator codigo, Integer teatro) throws Exception;

    List<Sala> obtenerNombresTeatro(@Valid SalaAtributoValidator nombre, Integer teatro) throws Exception;

    List<Sala> listar();

    List<Sala> listarPaginado();

    List<Sala> listarAscendente();

    List<Sala> listarDescendente();
}
