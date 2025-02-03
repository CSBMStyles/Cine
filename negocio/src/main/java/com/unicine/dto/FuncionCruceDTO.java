package com.unicine.dto;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.collection.spi.PersistentMap;

import com.unicine.util.emuns.FormatoPelicula;
import com.unicine.util.emuns.GeneroPelicula;

import jakarta.persistence.ElementCollection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FuncionCruceDTO {

    private String nombreSala;

    private String nombrePelicula;

    private FormatoPelicula formato;

    private Object imagenesPelicula;

    /* private List<GeneroPelicula> generos;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin; */

    public FuncionCruceDTO(String nombreSala, String nombrePelicula, FormatoPelicula formato, Object imagenesPelicula) {
        this.nombreSala = nombreSala;
        this.nombrePelicula = nombrePelicula;
        this.formato = formato;
        this.imagenesPelicula = imagenesPelicula;
    }
}
