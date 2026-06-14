package com.unicine.service.user;

import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;

import org.springframework.validation.annotation.Validated;

import com.unicine.entity.user.Persona;
import com.unicine.util.validation.group.OnCreate;
import com.unicine.util.validation.group.OnUpdate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface PersonaServicio<T extends Persona> {
    // SECTION: Metodos propios

    T login(@Valid String correo, String password) throws Exception;

    T registrar(@Validated(OnCreate.class) T persona) throws Exception;

    T actualizar(@Validated(OnUpdate.class) T persona) throws Exception;

    T cambiarPassword(@Validated(OnCreate.class) T persona, String passwordActual, String passwordNuevo) throws Exception;

    void eliminar(@Valid T persona, boolean confirmacion) throws Exception;

    Optional<T> obtener(@NotNull(message = ValidationMessages.CEDULA_NOT_NULL) @Positive(message = ValidationMessages.CEDULA_POSITIVE) Integer cedula) throws Exception;

    List<T> listar();
}
