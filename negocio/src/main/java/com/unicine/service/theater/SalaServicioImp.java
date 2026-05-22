package com.unicine.service.theater;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.theater.Sala;
import com.unicine.enums.theater.TipoSala;
import com.unicine.repository.theater.SalaRepo;
import com.unicine.util.initializer.SalaPrecioInit;

import jakarta.validation.Valid;
import com.unicine.util.validation.catalog.ErrorCatalog;

@Service
@Validated
public class SalaServicioImp implements SalaServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final SalaRepo salaRepo;

    private final SalaPrecioInit precioInitalizer;

    public SalaServicioImp(SalaRepo salaRepo, SalaPrecioInit preciosBaseConfig) {
        this.salaRepo = salaRepo;
        this.precioInitalizer = preciosBaseConfig; 
    }

    /**
     * Método para obtener el precio base de una sala
     * @param tipoSala
     * @return
     */
    @Override
    public Double obtenerPrecioBase(TipoSala tipoSala) {
        return precioInitalizer.obtenerPrecio(tipoSala);
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del sala que se esta buscando
     * @param numero
     * @throws
     */
    private void validarExiste(Optional<Sala> sala) throws Exception {

        if (sala.isEmpty()) {
            throw new Exception(ErrorCatalog.ENT007.getMessage());
        }
    }

    /**
     * Metodo para comprobar la presencia la lista de salas que se esta buscando
     * @param sala
     * @throws
     */
    private void validarExiste(List<Sala> sala) throws Exception {

        if (sala.isEmpty()) {
            throw new Exception(ErrorCatalog.ENT008.getMessage());
        }
    }

    /**
     * Método que verifica si existe una sala con la misma nombre en el sala
     * @param sala
     * @return si existe la direccion devuelve true, de lo contrario false
     */
    private void validarExisteNombre(Sala sala) throws Exception {

        Optional<Sala> existe = salaRepo.buscarNombreValidacion(sala.getNombre(), sala.getTeatro().getCodigo());
       
        if (existe.isPresent()) {
            throw new RuntimeException("El nombre de la sala ya existe en el sala");
        }
    }

    /**
     * Método que verifica si existe un sala con la misma nombre en la sala adicional se excluye el sala que se esta actualizando
     * @param sala
     * @return si existe la nombre devuelve true, de lo contrario false
     */
    private void validarRepiteNombre(Sala sala) throws Exception {

        Optional<Sala> existe = salaRepo.buscarNombreExcluido(sala.getNombre(), sala.getTeatro().getCodigo(), sala.getCodigo());
       
        if (existe.isPresent()) {
            throw new RuntimeException("El nombre de la sala ya existe en el sala");
        }
    }

    /**
     * Metodo para comprobar la presencia de las relaciones del sala
     * @param sala
     * @throws
     */
    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    // SECTION: Implementacion de servicios

    // 2️⃣ Funciones del Administrador de Sala

    @Override
    public Sala registrar(@Valid Sala sala) throws Exception { 

        validarExisteNombre(sala);

        return salaRepo.save(sala);
    }

    @Override
    public Sala actualizar(@Valid Sala sala) throws Exception {

        validarRepiteNombre(sala);

        return salaRepo.save(sala);
    }

    @Override
    public void eliminar(@Valid Sala eliminado, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        salaRepo.delete(eliminado);
    }

    @Override
    public Optional<Sala> obtener(Integer codigo) throws Exception {

        Optional<Sala> buscado = salaRepo.findById(codigo);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<Sala> obtenerNombre(String nombre) throws Exception { 

        List<Sala> salas = salaRepo.buscarNombre(nombre);

        validarExiste(salas);

        return salas; 
    }

    @Override
    public Optional<Sala> obtenerIdTeatro(Integer codigo, Integer teatroElegido) throws Exception { 

        Optional<Sala> sala = salaRepo.buscarIdTeatro(codigo, teatroElegido);

        validarExiste(sala);

        return sala; 
    }

    @Override
    public List<Sala> obtenerNombresTeatro(String nombre, Integer teatroElegido) throws Exception { 

        List<Sala> salas = salaRepo.buscarNombreTeatro(nombre, teatroElegido);

        validarExiste(salas);

        return salas; 
    }

    @Override
    public List<Sala> listar() { return salaRepo.findAll(); }

    @Override
    public List<Sala> listarPaginado() { 

        return salaRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    @Override
    public List<Sala> listarAscendente() { 
        
        return salaRepo.findAll(Sort.by("codigo").ascending());
    }

    @Override
    public List<Sala> listarDescendente() { 
        
        return salaRepo.findAll(Sort.by("codigo").descending());
    }
}
