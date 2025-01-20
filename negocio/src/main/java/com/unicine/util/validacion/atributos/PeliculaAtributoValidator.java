package com.unicine.util.validacion.atributos;

import com.unicine.util.validacion.anotaciones.MultiPattern;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeliculaAtributoValidator {

    @Positive(message = "El codigo de la ciudad debe ser un numero positivo")
    @Max(value = 100, message = "El codigo de la ciudad no debe ser mayor a cien")
    private Integer codigo;

    @MultiPattern({
        @Pattern(regexp = ".{2,}", message = "El nombre de la ciudad no debe ser menor a dos caracteres"),
        @Pattern(regexp = ".{1,100}", message = "El nombre de la ciudad no debe pasar los cien caracteres"),
    })
    private String nombre;

    public PeliculaAtributoValidator(Integer codigo) {
        this.codigo = codigo;
    }

    public PeliculaAtributoValidator(String nombre) {
        this.nombre = nombre;
    }
    
}
