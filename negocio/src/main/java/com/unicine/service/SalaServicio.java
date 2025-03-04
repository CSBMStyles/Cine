package com.unicine.service;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.Sala;
import com.unicine.enumeration.TipoSala;
import com.unicine.util.validation.attributes.SalaAtributoValidator;

import jakarta.validation.Valid;

public interface SalaServicio {

    // *️⃣ Funciones de Soporte

    Double obtenerPrecioBase(TipoSala tipoSala);

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
