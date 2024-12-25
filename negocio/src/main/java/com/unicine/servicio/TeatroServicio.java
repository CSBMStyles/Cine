package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import com.unicine.entidades.Teatro;

import jakarta.validation.Valid;

public interface TeatroServicio {

    // 2️⃣ Funciones del Administrador Teatro

    Teatro registrar(@Valid Teatro teatro) throws Exception;

    Teatro actualizar(@Valid Teatro teatro) throws Exception;

    void eliminar(@Valid Teatro teatro, boolean confirmacion) throws Exception;

    Optional<Teatro> obtener(Integer codigo) throws Exception;

    List<Teatro> listar();

    List<Teatro> listarPaginado();

    List<Teatro> listarAscendente();

    List<Teatro> listarDescendente();
}
