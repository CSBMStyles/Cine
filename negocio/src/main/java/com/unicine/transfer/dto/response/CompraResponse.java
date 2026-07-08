package com.unicine.transfer.dto.response;

import com.unicine.enums.purchase.MedioPago;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de salida para la entidad {@link com.unicine.entity.purchase.Compra}.
 *
 * Incluido:
 * - {@code codigo}, {@code estado}, {@code medioPago}, {@code fechaCompra}, {@code fechaPelicula}, {@code valorTotal}.
 * - Cupon de cliente, cliente, funcion, entradas y confiteria anidados.
 *
 * Excluido:
 * - No aplica.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraResponse {

    private Integer codigo;

    private Boolean estado;

    private MedioPago medioPago;

    private LocalDateTime fechaCompra;

    private LocalDateTime fechaPelicula;

    private Double valorTotal;

    private CuponClienteResponse cuponCliente;

    private ClienteResponse cliente;

    private FuncionResponse funcion;

    private List<EntradaResponse> entradas;

    private List<CompraConfiteriaResponse> compraConfiterias;
}
