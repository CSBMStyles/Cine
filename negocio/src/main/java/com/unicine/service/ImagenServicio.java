package com.unicine.service;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.Imagen;
import com.unicine.entity.interfaced.Imagenable;
import com.unicine.transfer.record.VersionArchivo;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;

public interface ImagenServicio {

    // *️⃣ Funciones Generales

    Imagen registrar(@Valid Imagen imagen, MultipartFile file, Imagenable propietario) throws Exception;

    Imagen actualizar(@Valid Imagen imagen, MultipartFile file, Imagenable propietario) throws Exception;

    Imagen restaurar(@Valid Imagen imagen, String versionId) throws Exception;

    Imagen renombrar(@Valid Imagen imagen, String nuevoNombre, Imagenable propietario) throws Exception;

    void eliminar(@Valid Imagen imagen, boolean confirmacion) throws Exception;

    void eliminarMultiple(@Valid List<Imagen> imagenes, boolean confirmacion) throws Exception;

    Optional<Imagen> obtener(@Valid String codigo) throws Exception;

    List<String> listar(@Valid Imagenable propietario) throws Exception;

    List<VersionArchivo> listarVersiones(String fileId) throws Exception;

    List<Imagen> listarPaginado();

    List<Imagen> listarAscendente();

    List<Imagen> listarDescendente();
}
