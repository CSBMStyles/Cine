package com.unicine.util.validation.attributes;

import com.unicine.util.validation.annotation.MultiPattern;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CiudadAtributoValidator {

    @Positive(message = "El codigo de la ciudad debe ser un numero positivo")
    @Max(value = 100, message = "El codigo de la ciudad no debe ser mayor a cien")
    private Integer codigo;

    @MultiPattern({
        @Pattern(regexp = ".{2,}", message = "El nombre de la ciudad no debe ser menor a dos caracteres"),
        @Pattern(regexp = ".{1,100}", message = "El nombre de la ciudad no debe pasar los cien caracteres"),
        @Pattern(regexp = "^[a-zA-Z ]+$", message = "El nombre de la ciudad solo puede contener letras y espacios")
    })
    private String nombre;

    public CiudadAtributoValidator(Integer codigo) {
        this.codigo = codigo;
    }

    public CiudadAtributoValidator(String nombre) {
        this.nombre = nombre;
    }
    
}
