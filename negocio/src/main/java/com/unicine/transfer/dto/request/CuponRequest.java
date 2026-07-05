package com.unicine.transfer.dto.request;

import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.purchase.Cupon}.
 *
 * Incluido:
 * - {@code codigo}, {@code descripcion}, {@code descuento}, {@code criterio}, {@code fechaVencimiento}.
 *
 * Excluido:
 * - Cupones de clientes: se gestionan desde el endpoint correspondiente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuponRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.COUPON_DESCRIPTION_NOT_BLANK)
    private String descripcion;

    @NotNull(message = ValidationMessages.COUPON_DISCOUNT_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.DISCOUNT_POSITIVE_OR_ZERO)
    @Max(value = 100, message = ValidationMessages.DISCOUNT_MAX_TOTAL)
    private Double descuento;

    @NotBlank(message = ValidationMessages.COUPON_CRITERION_NOT_BLANK)
    @Size(max = 100, message = ValidationMessages.CRITERION_SIZE_MAX_HUNDRED)
    private String criterio;

    @NotNull(message = ValidationMessages.COUPON_EXPIRY_NOT_NULL)
    @FutureOrPresent(message = ValidationMessages.COUPON_EXPIRY_FUTURE)
    private LocalDateTime fechaVencimiento;
}
