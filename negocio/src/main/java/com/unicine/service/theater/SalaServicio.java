package com.unicine.service.theater;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.theater.Sala;
import com.unicine.enums.theater.TipoSala;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public interface SalaServicio {

    // *️⃣ Funciones de Soporte

    Double obtenerPrecioBase(TipoSala tipoSala);

    // 2️⃣ Funciones del Administrador de Teatro

    Sala registrar(@Valid Sala sala) throws Exception;

    Sala actualizar(@Valid Sala sala) throws Exception;

    void eliminar(@Valid Sala sala, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Globales

    Optional<Sala> obtener(@NotNull(message = "El código no puede estar vacío") @Positive(message = "El código debe ser un número positivo") Integer codigo) throws Exception;

    List<Sala> obtenerNombre(@NotBlank(message = "El nombre no puede estar en blanco") String nombre) throws Exception;

    Optional<Sala> obtenerIdTeatro(@NotNull(message = "El código no puede estar vacío") @Positive(message = "El código debe ser un número positivo") Integer codigo, @NotNull(message = "El código de teatro no puede estar vacío") @Positive(message = "El código de teatro debe ser un número positivo") Integer teatro) throws Exception;

    List<Sala> obtenerNombresTeatro(@NotBlank(message = "El nombre no puede estar en blanco") String nombre, @NotNull(message = "El código de teatro no puede estar vacío") @Positive(message = "El código de teatro debe ser un número positivo") Integer teatro) throws Exception;

    List<Sala> listar();

    List<Sala> listarPaginado();

    List<Sala> listarAscendente();

    List<Sala> listarDescendente();
}
