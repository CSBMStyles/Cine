package com.unicine.service;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.FuncionEsquema;

import jakarta.validation.Valid;

public interface FuncionEsquemaServicio {

    // *️⃣ Funciones Automaticas

    FuncionEsquema registrar(@Valid FuncionEsquema funcionEsquema) throws Exception;

    FuncionEsquema actualizar(@Valid FuncionEsquema funcionEsquema) throws Exception;

    void eliminar(@Valid FuncionEsquema funcionEsquema, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Globales

    Optional<FuncionEsquema> obtener(Integer codigo) throws Exception;

    List<FuncionEsquema> listar();
}
