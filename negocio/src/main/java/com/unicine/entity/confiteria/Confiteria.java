package com.unicine.entity.confiteria;

import com.unicine.entity.image.Imagen;
import com.unicine.entity.purchase.CompraConfiteria;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
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

import com.unicine.entity.image.interfaced.Imagenable;
import com.unicine.enums.confiteria.CategoriaConfiteria;
import com.unicine.util.validation.catalog.ValidationMessages;

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

    @NotBlank(message = ValidationMessages.CONFECTIONERY_NAME_NOT_BLANK)
    @Size(max = 100, message = ValidationMessages.CONFECTIONERY_NAME_SIZE_MAX_HUNDRED)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotNull(message = ValidationMessages.CONFECTIONERY_PRICE_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.CONFECTIONERY_PRICE_POSITIVE_OR_ZERO)
    @Column(nullable = false)
    private Double precio;

    @NotNull(message = ValidationMessages.CONFECTIONERY_CATEGORY_NOT_NULL)
    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private CategoriaConfiteria categoria;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "confiteria", cascade = CascadeType.ALL)
    private List<CompraConfiteria> compraConfiterias;

    @ToString.Exclude
    @OneToMany(mappedBy = "confiteria", cascade = CascadeType.ALL)
    private List<Imagen> imagenes;

    // SECTION: Constructor

    @Builder
    public Confiteria(String nombre, Double precio, CategoriaConfiteria categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }

    @Override
    public String getCarpetaPrefijo() {
        return "confiterias";
    }
}
