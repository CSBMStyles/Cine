package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import com.unicine.entidades.Horario;
import com.unicine.util.message.HorarioRespuesta;

import jakarta.validation.Valid;

public interface HorarioServicio {

    // 2️⃣ Funciones del Administrador de Teatro

    HorarioRespuesta<?> registrar(@Valid Horario horario) throws Exception;

    Horario actualizar(@Valid Horario horario) throws Exception;

    void eliminar(@Valid Horario horario, boolean confirmacion) throws Exception;

    // *️⃣ Funciones Globales

    Optional<Horario> obtener(Integer codigo) throws Exception;

    List<Horario> listar();

    List<Horario> listarPaginado();

    List<Horario> listarAscendente();

    List<Horario> listarDescendente();
}