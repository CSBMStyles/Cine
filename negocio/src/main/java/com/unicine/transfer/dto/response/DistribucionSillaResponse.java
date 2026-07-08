package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.theater.DistribucionSilla}.
 *
 * Incluido:
 * - {@code codigo}, {@code esquema}, {@code totalSillas}, {@code filas}, {@code columnas}.
 *
 * Excluido:
 * - {@code salas}: evita ciclos y carga innecesaria.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistribucionSillaResponse {

    private Integer codigo;

    private String esquema;

    private Integer totalSillas;

    private Integer filas;

    private Integer columnas;
}
