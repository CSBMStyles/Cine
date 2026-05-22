package com.unicine.service.theater;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.theater.DistribucionSilla;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface DistribucionSillaServicio {

    // 2️⃣ Funciones del Administrador Teatro

    DistribucionSilla registrar(@Valid DistribucionSilla distribucion) throws Exception;

    DistribucionSilla actualizar(@Valid DistribucionSilla distribucion) throws Exception;

    void eliminar(@Valid DistribucionSilla distribucion, boolean confirmacion) throws Exception;

    Optional<DistribucionSilla> obtener(@NotNull(message = "El código no puede estar vacío") @Positive(message = "El código debe ser un número positivo") Integer codigo) throws Exception;

    List<DistribucionSilla> listar();

    List<DistribucionSilla> listarPaginado();

    List<DistribucionSilla> listarAscendente();

    List<DistribucionSilla> listarDescendente();
}
