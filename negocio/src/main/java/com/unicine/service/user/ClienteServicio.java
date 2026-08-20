package com.unicine.service.user;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.user.Cliente;
import com.unicine.transfer.dto.request.ClienteRequest;
import com.unicine.transfer.dto.response.ClienteResponse;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface ClienteServicio {

    Cliente login(String correo, String password) throws Exception;

    ClienteResponse registrar(@Valid ClienteRequest request) throws Exception;

    ClienteResponse actualizar(@Valid ClienteRequest request) throws Exception;

    Cliente cambiarPassword(Cliente cliente, String passwordActual, String passwordNuevo) throws Exception;

    void eliminar(@NotNull @Positive Integer cedula, boolean confirmacion) throws Exception;

    Optional<ClienteResponse> obtener(@NotNull @Positive Integer cedula) throws Exception;

    List<ClienteResponse> listar();
}
