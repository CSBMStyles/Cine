package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import com.unicine.entidades.DistribucionSilla;
import com.unicine.util.validacion.atributos.DistribucionAttributeValidator;

import jakarta.validation.Valid;

public interface DistribucionSillaServicio {

    // 2️⃣ Funciones del Administrador Teatro

    DistribucionSilla registrar(@Valid DistribucionSilla distribucion) throws Exception;

    DistribucionSilla actualizar(@Valid DistribucionSilla distribucion) throws Exception;

    void eliminar(@Valid DistribucionSilla distribucion, boolean confirmacion) throws Exception;

    Optional<DistribucionSilla> obtener(@Valid DistribucionAttributeValidator validacion) throws Exception;

    List<DistribucionSilla> listar();

    List<DistribucionSilla> listarPaginado();

    List<DistribucionSilla> listarAscendente();

    List<DistribucionSilla> listarDescendente();
}
