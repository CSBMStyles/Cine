package com.unicine.service;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.Teatro;
import com.unicine.util.validation.attributes.TeatroAtributoValidator;

import jakarta.validation.Valid;

public interface TeatroServicio {

    // 2️⃣ Funciones del Administrador Teatro

    Teatro registrar(@Valid Teatro teatro) throws Exception;

    Teatro actualizar(@Valid Teatro teatro) throws Exception;

    void eliminar(@Valid Teatro teatro, boolean confirmacion) throws Exception;

    Optional<Teatro> obtener(@Valid TeatroAtributoValidator codigo) throws Exception;

    List<Teatro> listar();

    List<Teatro> listarPaginado();

    List<Teatro> listarAscendente();

    List<Teatro> listarDescendente();
}
