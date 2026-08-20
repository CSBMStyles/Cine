package com.unicine.entity.image;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.entity.user.Administrador;
import com.unicine.entity.user.Cliente;
import com.unicine.enums.image.TipoImagen;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import com.unicine.util.validation.catalog.ValidationMessages;
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
    @NotBlank(message = ValidationMessages.IMAGE_CODE_NOT_BLANK)
    @Column(name = "id", length = 50)
    @EqualsAndHashCode.Include
    private String codigo;

    @NotBlank(message = ValidationMessages.IMAGE_URL_NOT_BLANK)
    @Column(nullable = false, length = 200)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_imagen", length = 20)
    private TipoImagen tipoImagen;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "es_principal")
    private Boolean principal;

    // !SECTION
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
    // !SECTION
}
