package com.unicine.entity;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Imagen implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id", length = 50)
    @EqualsAndHashCode.Include
    private String codigo;

    @Column(nullable = false, length = 200)
    private String url;

    // SECTION: Relaciones

    @OneToOne
    private Cliente cliente;

    @OneToOne
    private Administrador administrador;

    @OneToOne
    private AdministradorTeatro administradorTeatro;

    @ManyToOne
    private Pelicula pelicula;

    @ManyToOne
    private Confiteria confiteria;
}
