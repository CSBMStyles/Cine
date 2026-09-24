package com.unicine.entity.payment;

import com.unicine.enums.payment.EstadoPago;
import com.unicine.util.validation.catalog.ValidationMessages;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

// Puente con la pasarela (Mercado Pago hoy, Wompi manana). Agregado propio
// referenciado por compraCodigo en lugar de relacion JPA dura para no acoplar
// la transaccion de sillas de Compra con el ciclo de vida del cobro.
@Entity
@Table(name = "pago", uniqueConstraints = {
        @UniqueConstraint(columnNames = "compraCodigo"),
        @UniqueConstraint(columnNames = "mercadoPagoId"),
        @UniqueConstraint(columnNames = "idempotencyKey")
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Pago implements Serializable {

    // SECTION: Atributos

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long codigo;

    @NotNull(message = ValidationMessages.PAYMENT_PURCHASE_NOT_NULL)
    @Column(nullable = false, unique = true)
    private Integer compraCodigo;

    // Nulo hasta que 4.7.2 crea la orden en Mercado Pago.
    @Column(unique = true, length = 100)
    private String mercadoPagoId;

    // URL de Checkout Pro para redirigir al comprador; se guarda para que el
    // reintento devuelva la misma sin crear otra orden (idempotencia 200).
    @Column(length = 500)
    private String checkoutUrl;

    @NotBlank(message = ValidationMessages.PAYMENT_IDEMPOTENCY_NOT_BLANK)
    @Column(nullable = false, unique = true, length = 100)
    private String idempotencyKey;

    // Copiado de Compra.valorTotal al crear; nunca viene del cliente.
    @NotNull(message = ValidationMessages.PAYMENT_AMOUNT_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.PAYMENT_AMOUNT_POSITIVE)
    @Column(nullable = false)
    private Double montoEsperado;

    @NotNull(message = ValidationMessages.PAYMENT_STATUS_NOT_NULL)
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private EstadoPago estado;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    // !SECTION
    // SECTION: Constructor

    @Builder
    public Pago(Integer compraCodigo, String mercadoPagoId, String checkoutUrl, String idempotencyKey, Double montoEsperado, EstadoPago estado) {
        this.compraCodigo = compraCodigo;
        this.mercadoPagoId = mercadoPagoId;
        this.checkoutUrl = checkoutUrl;
        this.idempotencyKey = idempotencyKey;
        this.montoEsperado = montoEsperado;
        this.estado = estado;
        this.fechaCreacion = LocalDateTime.now(ZoneId.of("America/Bogota"));
        this.fechaActualizacion = this.fechaCreacion;
    }
    // !SECTION
}
