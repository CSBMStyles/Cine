package com.unicine.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.util.List;

import com.unicine.entity.interfaced.Imagenable;

import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true) // NOTE: El `call super` es para que se muestren los atributos de la clase padre
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class AdministradorTeatro extends Persona implements Serializable, Imagenable {

    // NOTE: Se agrega la anotación `cascade` para que se realicen las operaciones en cascada osea que si se elimina un administrador se elimina el teatro que construye

    @ToString.Exclude
    @OneToMany(mappedBy = "administradorTeatro", cascade =  CascadeType.ALL) 
    private List<Teatro> teatros;

    @ToString.Exclude
    @OneToOne(mappedBy = "administradorTeatro", cascade = CascadeType.ALL)
    private Imagen imagen;

    @Builder
    public AdministradorTeatro(Integer cedula, String nombre, String apellido, String correo, String password) {
        super(cedula, nombre, apellido, correo, password);
    }

    @Override
    public String getCarpetaPrefijo() {
        return "administradores-teatro";
    }
}
