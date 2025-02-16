package com.unicine.service;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.Ciudad;

import jakarta.validation.Valid;

public interface CompraServicio {
    // SECTION: Metodos propios

    Ciudad registrar(@Valid Ciudad ciudad) throws Exception;

    Ciudad actualizar(@Valid Ciudad ciudad) throws Exception;

    void eliminar(@Valid Ciudad ciudad) throws Exception;

    Optional<Ciudad> obtener(@Valid Integer codigo) throws Exception;

    List<Ciudad> listar();

    List<Ciudad> listarPaginado();
}
