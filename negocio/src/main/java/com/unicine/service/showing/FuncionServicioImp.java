package com.unicine.service.showing;

import com.unicine.service.theater.SalaServicio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.showing.Funcion;
import com.unicine.entity.showing.Horario;
import com.unicine.entity.theater.Sala;
import com.unicine.repository.showing.FuncionRepo;

import jakarta.validation.Valid;
import com.unicine.util.validation.catalog.domain.ShowingErrorCatalog;
import com.unicine.exception.ResourceNotFoundException;

@Service
@Validated
public class FuncionServicioImp implements FuncionServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final FuncionRepo funcionRepo;
    private final SalaServicio salaServicio;
    private final HorarioServicio horarioServicio;

    public FuncionServicioImp(FuncionRepo funcionRepo, SalaServicio salaServicio, HorarioServicio horarioServicio) {
        this.funcionRepo = funcionRepo;
        this.salaServicio = salaServicio;
        this.horarioServicio = horarioServicio;
    }

    // SECTION: Metodos de soporte

    /**
     * Método para calcular el precio de la función
     * @param precioBase
     * @param descuentoDia
     * @return precio de la función
     */
    public Double calcularPrecio(Sala sala, Horario horario) { 
        
        Double precioBase = salaServicio.obtenerPrecioBase(sala.getTipoSala());

        Double descuentoDia = horarioServicio.obtenerDescuentoDia(horario.getFechaInicio());

        return precioBase - (precioBase * descuentoDia); }

    /**
     * Método para reemplazar el precio de la función
     * @param funcion
     */
    public void reemplazarPrecio(Funcion funcion) {

        Double precio = calcularPrecio(funcion.getSala(), funcion.getHorario());

        funcion.setPrecio(precio);
    }

    /**
     * Metodo para comprobar la presencia la función que se esta buscando
     * @param funcion
     */
    private void validarExiste(Optional<Funcion> funcion) throws Exception {

        if (funcion.isEmpty()) {
            throw new ResourceNotFoundException(ShowingErrorCatalog.DOMAIN_SHOWING_ENTITY_FUNCTION_NOT_FOUND);
        }
    }

    /**
     * Metodo para validar la confirmacion de la eliminacion
     * @param confirmacion
     */
    private void comprobarConfirmacion(boolean confirmacion) throws Exception {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    // SECTION: Implementacion de servicios

    // 2️⃣ Funciones del Administrador de Teatro

    @Override
    public Funcion registrar(@Valid Funcion funcion) throws Exception { 

        reemplazarPrecio(funcion);

        Funcion guardada = funcionRepo.save(funcion);

        // TODO: emitir evento de dominio FUNCION_CREADA para reactividad futura (SSE/WebSockets)

        return guardada;
    }

    @Override
    public Funcion actualizar(@Valid Funcion funcion) throws Exception { 
        
        reemplazarPrecio(funcion);

        return funcionRepo.save(funcion); 
    }

    @Override
    public void eliminar(@Valid Funcion eliminado, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        funcionRepo.delete(eliminado);
    }

    // *️⃣ Funciones Generales

    @Override
    public Optional<Funcion> obtener(Integer codigo) throws Exception {

        Optional<Funcion> buscado = funcionRepo.findById(codigo);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<Funcion> listar() { return funcionRepo.findAll(); }

    @Override
    public List<Funcion> listarPaginado() { 

        return funcionRepo.findAll(PageRequest.of(0, 5, Sort.by("codigo").ascending())).toList();
    }

    @Override
    public List<Funcion> listarAscendente() { 
        
        return funcionRepo.findAll(Sort.by("codigo").ascending());
    }

    @Override
    public List<Funcion> listarDescendente() { 
        
        return funcionRepo.findAll(Sort.by("codigo").descending());
    }
}
