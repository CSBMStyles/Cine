package com.unicine.util.validation.attributes;

import com.unicine.util.validation.annotation.MultiPattern;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeliculaAtributoValidator {

    @Positive(message = "El codigo de la pelicula debe ser un numero positivo")
    @Max(value = 1000, message = "El codigo de la pelicula no debe ser mayor a mil")
    private Integer codigo;

    @MultiPattern({
        @Pattern(regexp = ".{1,100}", message = "El nombre de la pelicula no debe pasar los cien caracteres"),
    })
    private String nombre;

    public PeliculaAtributoValidator(Integer codigo) {
        this.codigo = codigo;
    }

    public PeliculaAtributoValidator(String nombre) {
        this.nombre = nombre;
    }
    
}
