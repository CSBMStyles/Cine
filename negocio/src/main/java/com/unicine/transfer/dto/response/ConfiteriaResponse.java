package com.unicine.transfer.dto.response;

import com.unicine.enums.confiteria.CategoriaConfiteria;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.confiteria.Confiteria}.
 *
 * Incluido:
 * - {@code codigo}, {@code nombre}, {@code descripcion}, {@code categoria}, {@code imagenes}.
 *
 * Excluido:
 * - Presentaciones para evitar ciclos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiteriaResponse {

    private Integer codigo;

    private String nombre;

    private String descripcion;

    private CategoriaConfiteria categoria;

    private List<ImagenResponse> imagenes;
}
