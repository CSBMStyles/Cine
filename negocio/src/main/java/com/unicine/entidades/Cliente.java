package com.unicine.entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.Builder;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.unicine.util.validacion.anotaciones.MultiPattern;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Cliente extends Persona implements Serializable {

    // SECTION: Atributos

    @NotNull(message = "El estado no puede estar vacío")
    @Column(nullable = false)
    private Boolean estado;

    @NotNull(message = "El apellido no puede estar vacío")
    @Past(message = "La fecha de nacimiento debe estar en el pasado")
    @Column(nullable = false)
    private LocalDate fechaNacimiento;

    @ElementCollection
    @Column(nullable = true)
    private Map<String, String> imagenes;

    @Size(max = 5, message = "El teléfono no puede tener más de cinco telefonos")
    @MultiPattern({
        @Pattern(regexp = "^[0-9]+$", message = "El teléfono solo puede contener números"),
        @Pattern(regexp = "^.{10}$", message = "El teléfono debe tener exactamente diez caracteres")
    })
    @ElementCollection
    @Column(nullable = true, length = 20)
    private List<String> telefonos;

    // SECTION: Relaciones

    @ToString.Exclude
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Compra> compras;

    @ToString.Exclude
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<CuponCliente> cuponClientes;

    @ToString.Exclude
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
    private List<Coleccion> colecciones;

    // SECTION: Constructor

    @Builder
    public Cliente(Integer cedula, String nombre, String apellido, String correo, String password, Boolean estado, LocalDate fechaNacimiento, Map<String, String> imagenes, List<String> telefonos) {
        super(cedula, nombre, apellido, correo, password);
        this.estado = false;
        this.fechaNacimiento = fechaNacimiento;
        this.imagenes = imagenes;
        this.telefonos = telefonos;
    }

    /*
     * Método que retorna la imagen principal de la película
     * @return String url de la imagen principal
     */
    public String getImagenPrincipal(){
        if (!imagenes.isEmpty()){
            String primera = imagenes.keySet().toArray()[0].toString(); // Recorre el mapa de imágenes y obtiene la primera
            return imagenes.get(primera);
        }
        return ""; // En caso de que no haya imágenes
    }
}