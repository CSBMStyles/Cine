package com.unicine.service;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.Funcion;
import com.unicine.entity.Horario;
import com.unicine.entity.Sala;

import jakarta.validation.Valid;

public interface FuncionServicio {

    // *️⃣ Funciones de Soporte

    Double calcularPrecio(Sala sala, Horario horario);

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
