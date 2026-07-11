package com.unicine.transfer.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * DTO de entrada para registrar una compra completa incluyendo
 * entradas y productos de confiteria.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraCompletaRequest {

    @Valid
    @NotNull
    private CompraRequest compra;

    @Valid
    @NotNull
    private List<EntradaRequest> entradas;

    @Valid
    @NotNull
    private List<CompraConfiteriaRequest> confiterias;
}
