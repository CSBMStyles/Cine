package com.unicine.transfer.dto.response;

import com.unicine.enums.confiteria.TipoCambioPrecioPresentacion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.confiteria.HistorialPrecioPresentacion}.
 *
 * Incluido:
 * - {@code codigo}, {@code precioAnterior}, {@code precioNuevo}, {@code tipoCambio}, {@code porcentaje}, {@code fechaCambio}.
 * - Presentacion de confiteria anidada.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialPrecioPresentacionResponse {

    private Integer codigo;

    private Double precioAnterior;

    private Double precioNuevo;

    private TipoCambioPrecioPresentacion tipoCambio;

    private Integer porcentaje;

    private LocalDateTime fechaCambio;

    private ConfiteriaPresentacionResponse presentacion;
}
