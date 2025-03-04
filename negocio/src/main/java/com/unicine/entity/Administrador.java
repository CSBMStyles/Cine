package com.unicine.entity;

import jakarta.persistence.CascadeType;

/* NOTE: Clase que extiende de la clase Persona obteniendo sus atributos, en la base de datos aparece a causa de que es entidad */

import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.io.Serializable;

import com.unicine.entity.interfaced.Imagenable;

@Entity
@ToString
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Administrador extends Persona implements Serializable, Imagenable {

    @ToString.Exclude
    @OneToOne(mappedBy = "administrador", cascade = CascadeType.ALL)
    private Imagen imagen;

    @Builder
    public Administrador(Integer cedula, String nombre, String apellido, String correo, String password) {
        super(cedula, nombre, apellido, correo, password);
    }

    @Override
    public String getCarpetaPrefijo() {
        return "administradores";
    }
}