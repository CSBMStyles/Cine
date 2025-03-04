package com.unicine.util.validation.attributes;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TeatroAtributoValidator {

    @Pattern(regexp = "^[1-9]\\d*$", message = "El código del teatro debe ser un número positivo, los caracteres no son permitidos")
    private String codigo;
}
