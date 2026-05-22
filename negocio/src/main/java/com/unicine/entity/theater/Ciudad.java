package com.unicine.entity.theater;

import com.unicine.entity.movie.PeliculaDisposicion;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import com.unicine.util.validation.catalog.ValidationMessages;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Ciudad implements Serializable {
    
    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.CITY_NAME_NOT_BLANK)
    @Size(min = 2, max = 100, message = ValidationMessages.CITY_NAME_SIZE_MIN_2)
    @Pattern(
        regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
        message = ValidationMessages.CITY_NAME_PATTERN
    )
    @Column(nullable = false, length = 100)
    private String nombre;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "ciudad", cascade = CascadeType.ALL)
    private List<Teatro> teatros;

    @ToString.Exclude
    @OneToMany(mappedBy = "ciudad", cascade =  CascadeType.ALL)
    private List<PeliculaDisposicion> peliculaDisposicion;

    // SECTION: Constructor

    @Builder
    public Ciudad(String nombre){
        this.nombre = nombre;
    }
}
