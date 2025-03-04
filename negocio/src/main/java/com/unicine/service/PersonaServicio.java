package com.unicine.service;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.Persona;
import com.unicine.util.validation.attributes.PersonaAtributoValidator;

import jakarta.validation.Valid;

public interface PersonaServicio<T extends Persona> {
    // SECTION: Metodos propios

    T login(@Valid String correo, String password) throws Exception;

    T registrar(@Valid T persona) throws Exception;

    T actualizar(@Valid T persona) throws Exception;

    void eliminar(@Valid T persona, boolean confirmacion) throws Exception;

    Optional<T> obtener(@Valid PersonaAtributoValidator cedula) throws Exception;

    List<T> listar();
}
