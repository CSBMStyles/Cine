package com.unicine.service;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.DistribucionSilla;
import com.unicine.util.validation.attributes.DistribucionAtributoValidator;

import jakarta.validation.Valid;

public interface DistribucionSillaServicio {

    // 2️⃣ Funciones del Administrador Teatro

    DistribucionSilla registrar(@Valid DistribucionSilla distribucion) throws Exception;

    DistribucionSilla actualizar(@Valid DistribucionSilla distribucion) throws Exception;

    void eliminar(@Valid DistribucionSilla distribucion, boolean confirmacion) throws Exception;

    Optional<DistribucionSilla> obtener(@Valid DistribucionAtributoValidator validacion) throws Exception;

    List<DistribucionSilla> listar();

    List<DistribucionSilla> listarPaginado();

    List<DistribucionSilla> listarAscendente();

    List<DistribucionSilla> listarDescendente();
}
