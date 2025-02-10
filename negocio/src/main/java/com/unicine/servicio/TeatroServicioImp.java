package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entidades.Teatro;
import com.unicine.repo.TeatroRepo;
import com.unicine.util.validaciones.atributos.TeatroAtributoValidator;

import jakarta.validation.Valid;

@Service
@Validated
public class TeatroServicioImp implements TeatroServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final TeatroRepo teatroRepo;

    public TeatroServicioImp(TeatroRepo teatroRepo) {
        this.teatroRepo = teatroRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del teatro que se esta buscando
     * @param numero
     * @throws
     */
    private void validarExiste(Optional<Teatro> teatro) throws Exception {

        if (teatro.isEmpty()) {
            throw new Exception("El teatro no existe");
        }
    }

    /**
     * Método que verifica si existe un teatro con la misma direccion en la ciudad
     * @param teatro
     * @return si existe la direccion devuelve true, de lo contrario false
     */
    private void validarExisteDireccion(Teatro teatro) throws Exception {

        Optional<Teatro> existe = teatroRepo.findByDireccion(teatro.getDireccion(), teatro.getCiudad().getCodigo());
       
        if (existe.isPresent()) {
            throw new RuntimeException("La dirección del teatro ya existe en la ciudad");
        }
    }

    /**
     * Método que verifica si existe un teatro con la misma direccion en la ciudad adicional se excluye el teatro que se esta actualizando
     * @param teatro
     * @return si existe la direccion devuelve true, de lo contrario false
     */
    private void validarRepiteDireccion(Teatro teatro) throws Exception {

        Optional<Teatro> existe = teatroRepo.buscarDireccionExcluido(teatro.getDireccion(), teatro.getCiudad().getCodigo(), teatro.getCodigo());
       
        if (existe.isPresent()) {
            throw new RuntimeException("La dirección del teatro ya existe en la ciudad");
        }
    }

    /**
     * Metodo para comprobar la presencia de las relaciones del teatro
     * @param teatro
     * @throws
     */
    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

   /**
    * Metodo para transformar un String a un Integer
    * @param codigo
    * @return
    */
   private Integer transformar(String codigo) { return Integer.parseInt(codigo); }

    // SECTION: Implementacion de servicios

    // 2️⃣ Funciones del Administrador de Teatro

    @Override
    public Teatro registrar(@Valid Teatro teatro) throws Exception { 

        validarExisteDireccion(teatro);

        return teatroRepo.save(teatro);
    }

    @Override
    public Teatro actualizar(@Valid Teatro teatro) throws Exception {

        validarRepiteDireccion(teatro);

        return teatroRepo.save(teatro);
    }

    @Override
    public void eliminar(@Valid Teatro eliminado, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        teatroRepo.delete(eliminado);
    }

    @Override
    public Optional<Teatro> obtener(@Valid TeatroAtributoValidator validator) throws Exception {

        Optional<Teatro> buscado = teatroRepo.findById(transformar(validator.getCodigo()));

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<Teatro> listar() { return teatroRepo.findAll(); }

    @Override
    public List<Teatro> listarPaginado() { 

        return teatroRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    @Override
    public List<Teatro> listarAscendente() { 
        
        return teatroRepo.findAll(Sort.by("codigo").ascending());
    }

    @Override
    public List<Teatro> listarDescendente() { 
        
        return teatroRepo.findAll(Sort.by("codigo").descending());
    }
}
