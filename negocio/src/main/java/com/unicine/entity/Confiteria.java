package com.unicine.entity;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

import com.unicine.entity.interfa.Imagenable;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Confiteria implements Serializable, Imagenable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotBlank(message = "El nombre no puede estar en blanco")
    @Size(max = 100, message = "El nombre no puede tener más de cien caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull(message = "El precio no puede estar vacío")
    @PositiveOrZero(message = "El precio debe ser un número positivo o cero")
    @Column(nullable = false)
    private Double precio;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "confiteria", cascade = CascadeType.ALL)
    private List<CompraConfiteria> compraConfiterias;

    @ToString.Exclude
    @OneToMany(mappedBy = "confiteria", cascade = CascadeType.ALL)
    private List<Imagen> imagenes;

    // SECTION: Constructor

    @Builder
    public Confiteria(String nombre, Double precio) {
        this.nombre = nombre;
        this.precio = precio;
    }

    @Override
    public String getCarpetaPrefijo() {
        return "confiterias";
    }
}
