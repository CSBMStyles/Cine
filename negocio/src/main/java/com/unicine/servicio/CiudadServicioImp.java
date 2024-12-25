package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entidades.Ciudad;
import com.unicine.repo.CiudadRepo;

import jakarta.validation.Valid;

@Service
@Validated
public class CiudadServicioImp implements CiudadServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final CiudadRepo ciudadRepo;

    public CiudadServicioImp(CiudadRepo ciudadRepo) {
        this.ciudadRepo = ciudadRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del ciudad que se esta buscando
     * @param ciudad
     * @throws
     */
    private void validarExiste(Optional<Ciudad> ciudad) throws Exception {

        if (ciudad.isEmpty()) {
            throw new Exception("No existe ciudad con el codigo proporcionado");
        }
    }

    /**
     * Metodo para comprobar la presencia la lista de ciudades que se esta buscando
     * @param ciudad
     * @throws
     */
    private void validarExiste(List<Ciudad> ciudad) throws Exception {

        if (ciudad.isEmpty()) {
            throw new Exception("No existe ciudades con ese nombre");
        }
    }

    /**
     * Metodo para comprobar que el nombre de la ciudad solo contenga letras y espacios
     * @param nombre
     * @throws
     */
    private void validarNombre(String nombre) throws Exception {   

        if (nombre.length() < 2) {
            throw new Exception("El nombre de la ciudad no debe ser menor a dos caracteres");
        } 
        if (nombre.length() > 100) {
            throw new Exception("El nombre de la ciudad no debe pasar los cien caracteres");
        }

        if (!nombre.matches("^[a-zA-Z ]+$")) {
            throw new Exception("El nombre de la ciudad solo puede contener letras y espacios");
        }
    }

    // SECTION: Implementacion de servicios

    @Override
    public Ciudad registrar(@Valid Ciudad ciudad) throws Exception { return ciudadRepo.save(ciudad); }

    @Override
    public Ciudad actualizar(@Valid Ciudad ciudad) throws Exception { return ciudadRepo.save(ciudad); }

    @Override
    public void eliminar(@Valid Ciudad eliminado) throws Exception { ciudadRepo.delete(eliminado); }

    @Override
    public Optional<Ciudad> obtener(Integer codigo) throws Exception {

        Optional<Ciudad> buscado = ciudadRepo.findById(codigo);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<Ciudad> listar() { return ciudadRepo.findAll(); }

    @Override
    public List<Ciudad> listarPaginado() { 

        return ciudadRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    @Override
    public List<Ciudad> listarNombres(String nombre) throws Exception { 

        validarNombre(nombre);

        List<Ciudad> ciudades = ciudadRepo.findByNombre(nombre);

        validarExiste(ciudades);

        return ciudades; 
    }

    @Override
    public List<Ciudad> listarAscendenteNombre() { 
        
        return ciudadRepo.findAll(Sort.by("nombre").ascending());
    }

    @Override
    public List<Ciudad> listarDescendenteNombre() { 
        
        return ciudadRepo.findAll(Sort.by("nombre").descending());
    }

}
