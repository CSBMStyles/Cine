package com.unicine.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.unicine.api.response.Respuesta;
import com.unicine.entity.Horario;
import com.unicine.entity.Sala;

import jakarta.validation.Valid;

public interface HorarioServicio {

    // *️⃣ Funciones de Soporte

    Double obtenerDescuentoDia(LocalDateTime fechaInicio);

    String obtenerDia(LocalDateTime fechaInicio);

    // 2️⃣ Funciones del Administrador de Teatro

    Respuesta<?> registrar(@Valid Horario horario, Sala sala) throws Exception;

    Respuesta<?> actualizar(@Valid Horario horario) throws Exception;

    void eliminar(@Valid Horario horario, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Globales

    Optional<Horario> obtener(Integer codigo) throws Exception;

    List<Horario> listar();

    List<Horario> listarPaginado();

    List<Horario> listarAscendente();

    List<Horario> listarDescendente();
}
