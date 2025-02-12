package com.unicine.entidades;

/* NOTE: Clase que extiende de la clase Persona obteniendo sus atributos, en la base de datos aparece a causa de que es entidad */

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Entity
@ToString
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Administrador extends Persona implements Serializable {

    @Builder
    public Administrador(Integer cedula, String nombre, String apellido, String correo, String password) {
        super(cedula, nombre, apellido, correo, password);
    }
}