package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import com.unicine.entidades.Ciudad;
import com.unicine.util.validaciones.atributos.CiudadAtributoValidator;

import jakarta.validation.Valid;

public interface CiudadServicio {

    // 1️⃣ Funciones del Administrador

    Ciudad registrar(@Valid Ciudad ciudad) throws Exception;

    Ciudad actualizar(@Valid Ciudad ciudad) throws Exception;

    void eliminar(@Valid Ciudad ciudad) throws Exception;

    // *️⃣ Funciones Generales

    Optional<Ciudad> obtener(@Valid CiudadAtributoValidator codigo) throws Exception;

    List<Ciudad> obtenerNombre(@Valid CiudadAtributoValidator nombre) throws Exception;

    List<Ciudad> listar();

    List<Ciudad> listarPaginado();

    List<Ciudad> listarAscendenteNombre();

    List<Ciudad> listarDescendenteNombre();
}
