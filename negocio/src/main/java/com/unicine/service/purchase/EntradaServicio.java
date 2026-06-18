package com.unicine.service.purchase;

import java.util.List;
import java.util.Optional;

import com.unicine.entity.purchase.Entrada;
import com.unicine.transfer.data.DetalleSillaDTO;

import jakarta.validation.Valid;

/**
 * Servicio de gestion de entradas.
 *
 * Permite registrar, consultar y eliminar entradas asociadas a una compra
 * y una funcion, validando disponibilidad de sillas y actualizando el
 * esquema de ocupacion de la funcion.
 */
public interface EntradaServicio {

    Entrada registrar(@Valid Entrada entrada) throws Exception;

    Entrada actualizar(@Valid Entrada entrada) throws Exception;

    void eliminar(@Valid Entrada entrada, boolean confirmacion) throws Exception;

    Optional<Entrada> obtener(Integer codigo) throws Exception;

    List<Entrada> listar();

    List<Entrada> listarPaginado();

    List<Entrada> listarPorCompra(Integer codigoCompra) throws Exception;

    List<Entrada> listarPorFuncion(Integer codigoFuncion) throws Exception;

    List<DetalleSillaDTO> obtenerSillasOcupadas(Integer codigoFuncion) throws Exception;
}
