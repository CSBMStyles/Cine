package com.unicine.service.user;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.transfer.dto.request.AdministradorTeatroRequest;
import com.unicine.transfer.dto.response.AdministradorTeatroResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface AdministradorTeatroServicio {

    AdministradorTeatro login(String correo, String password) throws Exception;

    AdministradorTeatroResponse registrar(@Valid AdministradorTeatroRequest request) throws Exception;

    AdministradorTeatroResponse actualizar(@Valid AdministradorTeatroRequest request) throws Exception;

    AdministradorTeatro cambiarPassword(AdministradorTeatro administrador, String passwordActual, String passwordNuevo) throws Exception;

    void eliminar(@NotNull @Positive Integer cedula, boolean confirmacion) throws Exception;

    Optional<AdministradorTeatroResponse> obtener(@NotNull @Positive Integer cedula) throws Exception;

    List<AdministradorTeatroResponse> listar();
}
