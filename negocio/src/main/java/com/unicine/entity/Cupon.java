package com.unicine.entidades;

import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class Cupon implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @Lob
    @NotBlank(message = "La descripción no puede estar en blanco")
    @Column(nullable = false, columnDefinition = "text")
    private String descripcion;

    @NotNull(message = "El descuento no puede estar vacío")
    @PositiveOrZero(message = "El descuento debe ser un número positivo o cero")
    @Max(value = 100, message = "El descuento no puede ser mayor al total")
    @Column(nullable = false)
    private Double descuento;

    @NotBlank(message = "El criterio no puede estar en blanco")
    @Size(max = 100, message = "El criterio no puede tener más de cien caracteres")
    @Column(nullable = false, length = 100)
    private String criterio;

    @NotNull(message = "La fecha de vencimiento no puede estar vacía")
    @FutureOrPresent(message = "La fecha de vencimiento debe estar en el presente o en el futuro")
    @Column(nullable = false)
    private LocalDateTime fechaVencimiento;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "cupon", cascade =  CascadeType.ALL)
    private List<CuponCliente> cuponClientes;
    
    // SECTION: Constructor

    @Builder
    public Cupon(String descripcion, Double descuento, String criterio, LocalDateTime fechaVencimiento) {
        this.descripcion = descripcion;
        this.descuento = descuento;
        this.criterio = criterio;
        this.fechaVencimiento = fechaVencimiento;
    }
}
