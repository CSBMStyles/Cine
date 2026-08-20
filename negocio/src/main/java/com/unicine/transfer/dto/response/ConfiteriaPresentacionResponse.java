package com.unicine.transfer.dto.response;

import com.unicine.enums.confiteria.UnidadMedida;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.confiteria.ConfiteriaPresentacion}.
 *
 * Incluido:
 * - {@code codigo}, {@code porcion}, {@code unidadMedida}, {@code precio}, {@code precioBase}, {@code fechaExpiracionTemporal}.
 * - Confiteria anidada.
 *
 * Excluido:
 * - Historial de precios para evitar ciclos.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfiteriaPresentacionResponse {

    private Integer codigo;

    private Double porcion;

    private UnidadMedida unidadMedida;

    private Double precio;

    private Double precioBase;

    private LocalDateTime fechaExpiracionTemporal;

    private ConfiteriaResponse confiteria;
}
