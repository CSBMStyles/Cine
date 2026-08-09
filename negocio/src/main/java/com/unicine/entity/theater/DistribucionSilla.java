package com.unicine.entity.theater;

import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
import com.unicine.util.validation.catalog.ValidationMessages;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class DistribucionSilla implements Serializable {

    // SECTION: Atributos

    @Id
    @Column(name = "id")
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer codigo;

    @NotBlank(message = ValidationMessages.SEAT_SCHEMA_NOT_BLANK)
    @Column(nullable = false, columnDefinition = "json")
    private String esquema;

    @NotNull(message = ValidationMessages.SEAT_TOTAL_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SEAT_TOTAL_POSITIVE)
    @Column(nullable = false)
    private Integer totalSillas;

    @NotNull(message = ValidationMessages.SEAT_ROWS_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SEAT_ROWS_POSITIVE)
    @Column(nullable = false)
    private Integer filas;

    @NotNull(message = ValidationMessages.SEAT_COLUMNS_NOT_NULL)
    @PositiveOrZero(message = ValidationMessages.SEAT_COLUMNS_POSITIVE)
    @Column(nullable = false)
    private Integer columnas;

    // !SECTION
    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "distribucionSilla", cascade = CascadeType.ALL)
    private List<Sala> salas;
    
    // !SECTION
    // SECTION: Constructor

    @Builder
    public DistribucionSilla(String esquema, Integer totalSillas, Integer filas, Integer columnas) {
        this.esquema = esquema;
        this.totalSillas = totalSillas;
        this.filas = filas;
        this.columnas = columnas;
    }
    // !SECTION
}
