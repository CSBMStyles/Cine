package com.unicine.service.showing;

import com.unicine.service.theater.SalaServicio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.showing.Funcion;
import com.unicine.entity.showing.Horario;
import com.unicine.entity.theater.Sala;
import com.unicine.repository.movie.PeliculaRepo;
import com.unicine.repository.showing.FuncionRepo;
import com.unicine.repository.showing.HorarioRepo;
import com.unicine.repository.theater.SalaRepo;
import com.unicine.transfer.dto.request.FuncionRequest;
import com.unicine.transfer.dto.response.FuncionResponse;
import com.unicine.transfer.mapper.FuncionMapper;

import com.unicine.util.validation.catalog.domain.MovieErrorCatalog;
import com.unicine.util.validation.catalog.domain.ShowingErrorCatalog;
import com.unicine.util.validation.catalog.domain.TheaterErrorCatalog;
import com.unicine.exception.ResourceNotFoundException;

@Service
@Validated
public class FuncionServicioImp implements FuncionServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final FuncionRepo funcionRepo;
    private final FuncionMapper funcionMapper;
    private final SalaServicio salaServicio;
    private final HorarioServicio horarioServicio;
    private final SalaRepo salaRepo;
    private final HorarioRepo horarioRepo;
    private final PeliculaRepo peliculaRepo;

    public FuncionServicioImp(FuncionRepo funcionRepo, FuncionMapper funcionMapper, SalaServicio salaServicio, HorarioServicio horarioServicio, SalaRepo salaRepo, HorarioRepo horarioRepo, PeliculaRepo peliculaRepo) {
        this.funcionRepo = funcionRepo;
        this.funcionMapper = funcionMapper;
        this.salaServicio = salaServicio;
        this.horarioServicio = horarioServicio;
        this.salaRepo = salaRepo;
        this.horarioRepo = horarioRepo;
        this.peliculaRepo = peliculaRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Método para calcular el precio de la función
     * @param precioBase
     * @param descuentoDia
     * @return precio de la función
     */
    @Override
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

    /**
     * Método para cargar las relaciones reales de una función a partir de un request.
     * @param request
     * @return función con entidades gestionadas por JPA
     */
    private Funcion construirFuncion(FuncionRequest request) throws Exception {

        Funcion funcion = funcionMapper.toEntity(request);

        Sala sala = salaRepo.findById(request.getSalaCodigo())
                .orElseThrow(() -> new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_ENTITY_ROOM_NOT_FOUND));

        Horario horario = horarioRepo.findById(request.getHorarioCodigo())
                .orElseThrow(() -> new ResourceNotFoundException(ShowingErrorCatalog.DOMAIN_SHOWING_ENTITY_SCHEDULE_NOT_FOUND));

        Pelicula pelicula = peliculaRepo.findById(request.getPeliculaCodigo())
                .orElseThrow(() -> new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_MOVIE_NOT_FOUND));

        funcion.setSala(sala);
        funcion.setHorario(horario);
        funcion.setPelicula(pelicula);

        return funcion;
    }

    // SECTION: Implementacion de servicios

    // 2️⃣ Funciones del Administrador de Teatro

    @Override
    public FuncionResponse registrar(FuncionRequest request) throws Exception { 

        Funcion funcion = construirFuncion(request);

        reemplazarPrecio(funcion);

        Funcion guardada = funcionRepo.save(funcion);

        // TODO: emitir evento de dominio FUNCION_CREADA para reactividad futura (SSE/WebSockets)

        return funcionMapper.toResponse(guardada);
    }

    @Override
    public FuncionResponse actualizar(FuncionRequest request) throws Exception { 
        
        Optional<Funcion> existente = funcionRepo.findById(request.getCodigo());
        validarExiste(existente);

        Funcion funcion = construirFuncion(request);
        Funcion actual = existente.get();

        actual.setFormato(funcion.getFormato());
        actual.setSala(funcion.getSala());
        actual.setHorario(funcion.getHorario());
        actual.setPelicula(funcion.getPelicula());

        reemplazarPrecio(actual);

        return funcionMapper.toResponse(funcionRepo.save(actual));
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        Optional<Funcion> buscado = funcionRepo.findById(codigo);
        validarExiste(buscado);
        funcionRepo.delete(buscado.get());
    }

    // *️⃣ Funciones Generales

    @Override
    public Optional<FuncionResponse> obtener(Integer codigo) throws Exception {

        Optional<Funcion> buscado = funcionRepo.findById(codigo);

        validarExiste(buscado);

        return buscado.map(funcionMapper::toResponse);
    }

    @Override
    public List<FuncionResponse> listar() { return funcionMapper.toResponseList(funcionRepo.findAll()); }

    @Override
    public List<FuncionResponse> listarPaginado() { 

        return funcionMapper.toResponseList(funcionRepo.findAll(PageRequest.of(0, 5, Sort.by("codigo").ascending())).toList());
    }

    @Override
    public List<FuncionResponse> listarAscendente() { 
        
        return funcionMapper.toResponseList(funcionRepo.findAll(Sort.by("codigo").ascending()));
    }

    @Override
    public List<FuncionResponse> listarDescendente() { 
        
        return funcionMapper.toResponseList(funcionRepo.findAll(Sort.by("codigo").descending()));
    }
}
