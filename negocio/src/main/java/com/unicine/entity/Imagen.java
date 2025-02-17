package com.unicine.entity;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;
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

import com.unicine.enumeration.FormatoPelicula;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Imagen implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Integer codigo;

    @NotBlank(message = "La url no puede estar vacía")
    @Column(nullable = false, length = 200)
    private String url;

    // SECTION: Relaciones

    @OneToOne
    private Cliente cliente;

    @ManyToOne
    private Pelicula pelicula;

    @ManyToOne
    private Confiteria confiteria;
    
    // SECTION: Constructor

    @Builder
    public Imagen(Integer codigo, String url, Cliente cliente, Pelicula pelicula, Confiteria confiteria) {
        this.codigo = codigo;
        this.url = url;
        this.cliente = cliente;
        this.pelicula = pelicula;
        this.confiteria = confiteria;
    }
}
