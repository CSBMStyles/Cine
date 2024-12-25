package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import com.unicine.entidades.Ciudad;

import jakarta.validation.Valid;

public interface CiudadServicio {

    // 1️⃣ Funciones del Administrador

    Ciudad registrar(@Valid Ciudad ciudad) throws Exception;

    Ciudad actualizar(@Valid Ciudad ciudad) throws Exception;

    void eliminar(@Valid Ciudad ciudad) throws Exception;

    Optional<Ciudad> obtener(Integer codigo) throws Exception;

    List<Ciudad> listar();

    List<Ciudad> listarPaginado();

    List<Ciudad> listarNombres(String nombre) throws Exception;

    List<Ciudad> listarAscendenteNombre();

    List<Ciudad> listarDescendenteNombre();
}
