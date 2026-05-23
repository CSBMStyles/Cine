package com.unicine.entity.purchase;

import com.unicine.entity.showing.Funcion;
import com.unicine.entity.user.Cliente;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.util.List;

import com.unicine.enums.purchase.MedioPago;
import com.unicine.util.validation.catalog.ValidationMessages;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Compra implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotNull(message = ValidationMessages.PURCHASE_STATUS_NOT_NULL)
    @Column(nullable = false)
    private Boolean estado;

    @NotNull(message = ValidationMessages.PURCHASE_PAYMENT_NOT_NULL)
    @Column (nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private MedioPago medioPago;

    @NotNull(message = ValidationMessages.PURCHASE_DATE_NOT_NULL)
    @Column(nullable = false)
    private LocalDateTime fechaCompra;

    @NotNull(message = ValidationMessages.PURCHASE_MOVIE_DATE_NOT_NULL)
    @FutureOrPresent(message = ValidationMessages.PURCHASE_MOVIE_DATE_FUTURE)
    @Column(nullable = false)
    private LocalDateTime fechaPelicula;

    @NotNull(message = ValidationMessages.PURCHASE_TOTAL_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.PURCHASE_TOTAL_POSITIVE)
    @Column(nullable = false)
    private Double valorTotal;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "compra")
    private List<CompraConfiteria> compraConfiterias;

    @OneToOne
    private CuponCliente cuponCliente;

    @ManyToOne
    @NotNull(message = ValidationMessages.PURCHASE_CLIENT_NOT_NULL)
    private Cliente cliente;

    @ManyToOne
    @NotNull(message = ValidationMessages.PURCHASE_SHOWING_NOT_NULL)
    private Funcion funcion;

    @ToString.Exclude
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<Entrada> entradas;
    
    // SECTION: Constructor

    @Builder
    public Compra(Boolean estado, MedioPago medioPago, CuponCliente cuponCliente, Cliente cliente, Funcion funcion) {
        this.estado = estado;
        this.medioPago = medioPago;
        this.fechaCompra = LocalDateTime.now(ZoneId.of("America/Bogota"));
        this.cuponCliente = cuponCliente;
        this.cliente = cliente;
        this.funcion = funcion;
    }
}
