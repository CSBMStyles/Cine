package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import com.unicine.entidades.FuncionEsquema;

import jakarta.validation.Valid;

public interface FuncionEsquemaServicio {

    // 2️⃣ Funciones del Administrador de Teatro

    FuncionEsquema registrar(@Valid FuncionEsquema funcionEsquema) throws Exception;

    FuncionEsquema actualizar(@Valid FuncionEsquema funcionEsquema) throws Exception;

    void eliminar(@Valid FuncionEsquema funcionEsquema, boolean confirmacion) throws Exception;

    Optional<FuncionEsquema> obtener(Integer codigo) throws Exception;

    List<FuncionEsquema> listar();

    List<FuncionEsquema> listarPaginado();

    List<FuncionEsquema> listarAscendente();

    List<FuncionEsquema> listarDescendente();
}
