package com.unicine.entity.image;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.entity.user.Administrador;
import com.unicine.entity.user.Cliente;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank(message = "El código no puede estar en blanco")
    @Column(name = "id", length = 50)
    @EqualsAndHashCode.Include
    private String codigo;

    @NotBlank(message = "La URL no puede estar en blanco")
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
