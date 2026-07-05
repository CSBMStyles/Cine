package com.unicine.transfer.dto.request;

import com.unicine.enums.purchase.MedioPago;
import com.unicine.util.validation.catalog.ValidationMessages;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de entrada para la entidad {@link com.unicine.entity.purchase.Compra}.
 *
 * Incluido:
 * - {@code codigo}, {@code estado}, {@code medioPago}, {@code fechaCompra}, {@code fechaPelicula}, {@code valorTotal}.
 * - Identificadores de relaciones: {@code cuponClienteCodigo} (opcional), {@code clienteCedula}, {@code funcionCodigo}.
 *
 * Excluido:
 * - Entradas y confiteria de la compra: se gestionan por endpoints especificos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraRequest {

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer codigo;

    @NotNull(message = ValidationMessages.PURCHASE_STATUS_NOT_NULL)
    private Boolean estado;

    @NotNull(message = ValidationMessages.PURCHASE_PAYMENT_NOT_NULL)
    private MedioPago medioPago;

    @NotNull(message = ValidationMessages.PURCHASE_DATE_NOT_NULL)
    private LocalDateTime fechaCompra;

    @NotNull(message = ValidationMessages.PURCHASE_MOVIE_DATE_NOT_NULL)
    @FutureOrPresent(message = ValidationMessages.PURCHASE_MOVIE_DATE_FUTURE)
    private LocalDateTime fechaPelicula;

    @NotNull(message = ValidationMessages.PURCHASE_TOTAL_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.PURCHASE_TOTAL_POSITIVE)
    private Double valorTotal;

    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer cuponClienteCodigo;

    @NotNull(message = ValidationMessages.PURCHASE_CLIENT_NOT_NULL)
    @Positive(message = ValidationMessages.CEDULA_POSITIVE)
    private Integer clienteCedula;

    @NotNull(message = ValidationMessages.PURCHASE_SHOWING_NOT_NULL)
    @Positive(message = ValidationMessages.ID_POSITIVE)
    private Integer funcionCodigo;
}
