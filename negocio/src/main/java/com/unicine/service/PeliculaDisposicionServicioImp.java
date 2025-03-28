package com.unicine.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.PeliculaDisposicion;
import com.unicine.entity.composed.PeliculaDisposicionCompuesta;
import com.unicine.enumeration.EstadoPelicula;
import com.unicine.repository.PeliculaDisposicionRepo;
import com.unicine.service.extend.state.EstadoPeliculaService;

import jakarta.validation.Valid;

@Service
@Validated
public class PeliculaDisposicionServicioImp implements PeliculaDisposicionServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final PeliculaDisposicionRepo peliculaDisposicionRepo;

    @Autowired
    private EstadoPeliculaService estadoPeliculaServicio; // Se inyecta el servicio de estado de pelicula para poder cambiar el estado de la disposicion de pelicula

    public PeliculaDisposicionServicioImp(PeliculaDisposicionRepo peliculaDisposicionRepo) {
        this.peliculaDisposicionRepo = peliculaDisposicionRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para asignar el estado inicial a la disposicion de pelicula
     * @param peliculaDisposicion la clase para ingresar el estado
     */
    private void ingresoEstadoIncial(PeliculaDisposicion peliculaDisposicion) {

        peliculaDisposicion.setEstadoPelicula(EstadoPelicula.PENDIENTE); // Se asigna el estado inicial a al registrar una disposicion
    }

    private PeliculaDisposicion disposicionEstadoModificado(PeliculaDisposicion peliculaDisposicion) { 
        // Se obtiene la disposicion de pelicula y se cambia el estado
        return estadoPeliculaServicio.actualizarEstado(peliculaDisposicion);
    }

    /**
     * Metodo para comprobar la presencia de la disposicion que se esta buscando
     * @param numero
     * @throws
     */
    private void validarExiste(Optional<PeliculaDisposicion> peliculaDisposicion) throws Exception {

        if (peliculaDisposicion.isEmpty()) {
            throw new Exception("La disposicion de pelicula no existe");
        }
    }

    /**
     * Metodo para comprobar la presencia de las relaciones del peliculaDisposicion
     * @param peliculaDisposicion
     * @throws
     */
    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    // SECTION: Implementacion de servicios

    // 1️⃣ Funcion del Administrador

    @Override
    public PeliculaDisposicion registrar(@Valid PeliculaDisposicion peliculaDisposicion) throws Exception { 

        ingresoEstadoIncial(peliculaDisposicion);
        
        return peliculaDisposicionRepo.save(peliculaDisposicion); 
    }

    @Override
    public PeliculaDisposicion actualizar(@Valid PeliculaDisposicion peliculaDisposicion) throws Exception { 

        return disposicionEstadoModificado(peliculaDisposicion); 
    }

    /**
     * Metodo para actualizar el estado de las peliculas automaticamente
     */
    public void actualizarEstadoPeliculas() {
        estadoPeliculaServicio.actualizarEstadosAutomaticamente();
    }

    @Override
    public void eliminar(@Valid PeliculaDisposicion eliminado, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        peliculaDisposicionRepo.delete(eliminado);
    }

    // REVIEW: En este caso se utiliza una clase de validacion para obtener el codigo de la peliculaDisposicion usando las anotaciones para validar

    @Override
    public Optional<PeliculaDisposicion> obtener(@Valid PeliculaDisposicionCompuesta codigo) throws Exception {

        Optional<PeliculaDisposicion> buscado = peliculaDisposicionRepo.findById(codigo);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<PeliculaDisposicion> listar() { return peliculaDisposicionRepo.findAll(); }

    @Override
    public List<PeliculaDisposicion> listarRecomendacionPeliculaEstado(PeliculaDisposicion peliculaDisposicion, EstadoPelicula estadoExcluido) { 

        return peliculaDisposicionRepo.listarDisposicionesPelicula(peliculaDisposicion.getPelicula().getCodigo(), estadoExcluido);
    }

    @Override
    public List<PeliculaDisposicion> listarPaginado() { 

        return peliculaDisposicionRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    @Override
    public List<PeliculaDisposicion> listarAscendente() { 
        
        return peliculaDisposicionRepo.findAll(Sort.by("codigo").ascending());
    }

    @Override
    public List<PeliculaDisposicion> listarDescendente() { 
        
        return peliculaDisposicionRepo.findAll(Sort.by("codigo").descending());
    }
}
