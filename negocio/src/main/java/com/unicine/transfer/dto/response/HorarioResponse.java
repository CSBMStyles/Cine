package com.unicine.transfer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.showing.Horario}.
 *
 * Incluido:
 * - {@code codigo}, {@code fechaInicio}, {@code fechaFin}.
 *
 * Excluido:
 * - Funcion asociada para evitar ciclos.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioResponse {

    private Integer codigo;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;
}
