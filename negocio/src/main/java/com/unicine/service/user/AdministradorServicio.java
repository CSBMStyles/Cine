package com.unicine.service.user;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.user.Administrador;
import com.unicine.transfer.dto.request.AdministradorRequest;
import com.unicine.transfer.dto.response.AdministradorResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface AdministradorServicio {

    Administrador login(String correo, String password) throws Exception;

    AdministradorResponse registrar(@Valid AdministradorRequest request) throws Exception;

    AdministradorResponse actualizar(@Valid AdministradorRequest request) throws Exception;

    Administrador cambiarPassword(Administrador administrador, String passwordActual, String passwordNuevo) throws Exception;

    void eliminar(@NotNull @Positive Integer cedula, boolean confirmacion) throws Exception;

    Optional<AdministradorResponse> obtener(@NotNull @Positive Integer cedula) throws Exception;

    List<AdministradorResponse> listar();
}
