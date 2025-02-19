package com.unicine.service;

import java.io.File;
import java.util.List;
import java.util.Optional;

import com.unicine.entity.Imagen;
import com.unicine.entity.interfa.Imagenable;

import jakarta.validation.Valid;

public interface ImagenServicio {

    // *️⃣ Funciones Generales

    Imagen registrar(@Valid Imagen imagen, File file, Imagenable propietario) throws Exception;

    Imagen actualizar(@Valid Imagen imagen, File file, String fileIdAntiguo, Imagenable propietario) throws Exception;

    void eliminar(@Valid Imagen imagen, boolean confirmacion) throws Exception;

    Optional<Imagen> obtener(@Valid String codigo) throws Exception;

    List<Imagen> listar();

    List<Imagen> listarPaginado();

    List<Imagen> listarAscendente();

    List<Imagen> listarDescendente();
}
