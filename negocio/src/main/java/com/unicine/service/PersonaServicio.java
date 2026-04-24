package com.unicine.service;

import java.util.List;
import java.util.Optional;

import org.springframework.validation.annotation.Validated;

import com.unicine.entity.Persona;
import com.unicine.util.validation.attributes.PersonaAtributoValidator;
import com.unicine.util.validation.group.OnCreate;
import com.unicine.util.validation.group.OnUpdate;

import jakarta.validation.Valid;

public interface PersonaServicio<T extends Persona> {
    // SECTION: Metodos propios

    T login(@Valid String correo, String password) throws Exception;

    T registrar(@Validated(OnCreate.class) T persona) throws Exception;

    T actualizar(@Validated(OnUpdate.class) T persona) throws Exception;

    T cambiarPassword(@Validated(OnCreate.class) T persona, String passwordActual, String passwordNuevo) throws Exception;

    void eliminar(@Valid T persona, boolean confirmacion) throws Exception;

    Optional<T> obtener(@Valid PersonaAtributoValidator cedula) throws Exception;

    List<T> listar();
}
