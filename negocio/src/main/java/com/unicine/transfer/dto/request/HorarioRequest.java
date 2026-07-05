package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.showing.Horario}.
 *
 * Incluido:
 * - {@code codigo}, {@code fechaInicio}, {@code fechaFin}.
 *
 * Excluido:
 * - Funcion asociada: se gestiona desde el endpoint de funciones.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HorarioRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotNull(message = ValidationMessages.SCHEDULE_START_NOT_NULL)
    @Future(message = ValidationMessages.SCHEDULE_START_FUTURE)
    private LocalDateTime fechaInicio;

    @NotNull(message = ValidationMessages.SCHEDULE_END_NOT_NULL)
    @Future(message = ValidationMessages.SCHEDULE_END_FUTURE)
    private LocalDateTime fechaFin;
}
