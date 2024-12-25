package com.unicine.entidades;

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
import java.time.LocalDateTime;

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

    @NotNull(message = "El medio de pago no puede estar vacío")
    @Column (nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private MedioPago medioPago;

    @NotNull(message = "La fecha de compra no puede estar vacía")
    @Column(nullable = false)
    private LocalDateTime fechaCompra;

    @NotNull(message = "La fecha de la película no puede estar vacía")
    @FutureOrPresent(message = "La fecha de la película debe estar en el presente o en el futuro")
    @Column(nullable = false)
    private LocalDateTime fechaPelicula;

    @NotNull(message = "El valor total no puede estar vacío")
    @PositiveOrZero(message = "El valor total debe ser un número positivo")
    @Column(nullable = false)
    private Double valorTotal;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "compra")
    private List<CompraConfiteria> compraConfiterias;

    @OneToOne
    private CuponCliente cuponCliente;

    @ManyToOne
    private Cliente cliente;

    @ManyToOne
    private Funcion funcion;

    @ToString.Exclude
    @OneToMany(mappedBy = "compra", cascade = CascadeType.ALL)
    private List<Entrada> entradas;
    
    // SECTION: Constructor

    @Builder
    public Compra(MedioPago medioPago, CuponCliente cuponCliente, Cliente cliente, Funcion funcion) {
        this.medioPago = medioPago;
        this.fechaCompra = LocalDateTime.now();
        this.cuponCliente = cuponCliente;
        this.cliente = cliente;
        this.funcion = funcion;
    }
}
