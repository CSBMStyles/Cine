package com.unicine.service.showing;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.api.response.Respuesta;
import com.unicine.entity.showing.Funcion;
import com.unicine.entity.showing.Horario;
import com.unicine.repository.showing.FuncionRepo;
import com.unicine.repository.showing.HorarioRepo;
import com.unicine.transfer.dto.request.HorarioRequest;
import com.unicine.transfer.dto.response.FuncionInterseccionResponse;
import com.unicine.transfer.dto.response.HorarioResponse;
import com.unicine.transfer.mapper.FuncionInterseccionMapper;
import com.unicine.transfer.mapper.HorarioMapper;
import com.unicine.util.initializer.HorarioDescuentoInit;

import com.unicine.util.validation.catalog.domain.ShowingErrorCatalog;
import com.unicine.exception.ResourceNotFoundException;

@Service
@Validated
public class HorarioServicioImp implements HorarioServicio {

    // Note: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final HorarioRepo horarioRepo;

    private final HorarioMapper horarioMapper;

    private final FuncionRepo funcionRepo;

    private final FuncionInterseccionMapper funcionInterseccionMapper;

    private final HorarioDescuentoInit descuentoInitializer;

    public HorarioServicioImp(HorarioRepo horarioRepo, HorarioMapper horarioMapper, FuncionRepo funcionRepo, FuncionInterseccionMapper funcionInterseccionMapper, HorarioDescuentoInit descuentoInitializer) {
        this.horarioRepo = horarioRepo;
        this.horarioMapper = horarioMapper;
        this.funcionRepo = funcionRepo;
        this.funcionInterseccionMapper = funcionInterseccionMapper;
        this.descuentoInitializer = descuentoInitializer;
    }

    /**
     * Método para obtener el descuento segun el día de la semana
     * @param horario
     * @return descuento del día
     */
    @Override
    public Double obtenerDescuentoDia(LocalDateTime fechaInicio) {

        String dia = obtenerDia(fechaInicio);

        return descuentoInitializer.obtenerDescuento(dia);
    }

    /**
     * Método para obtener el día de la semana
     * @param fechaInicio
     * @return día de la semana usando un formato de tres letras
     */
    @Override
    public String obtenerDia(LocalDateTime fechaInicio) {

        // Crear un formateador con tres letras para el día
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE", Locale.of("es"));

        // Formatear la fecha, obtener el día y convertir a mayúsculas
        String dia = fechaInicio.format(formatter).toUpperCase();

        return dia;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del horario que se esta buscando
     * @param horario
     * @throws
     */
    private void validarExiste(Optional<Horario> horario) throws Exception {

        if (horario.isEmpty()) {
            throw new ResourceNotFoundException(ShowingErrorCatalog.DOMAIN_SHOWING_ENTITY_SCHEDULE_NOT_FOUND);
        }
    }

    /**
     * Funcion para instanciar la respuesta de la operación fallida
     * @param funcion solapada
     * @return respuesta conteniendo el mensaje, funcion y estado de la operación
     */
    private Respuesta<?> comprobacionRespuesta(Optional<Funcion> funcionSolapada, Horario horario) {

        if (funcionSolapada.isPresent()) {
            
            Funcion funcion = funcionSolapada.get();

            FuncionInterseccionResponse interseccion = funcionInterseccionMapper.convertirDTO(funcion);

            return new Respuesta<>("El horario se solapa con otra función", interseccion, false);
        } else {

            Horario guardado = horarioRepo.save(horario);

            HorarioResponse response = horarioMapper.toResponse(guardado);

            return new Respuesta<>("Horario registrado exitosamente", response, true);
        }
    }

    /**
     * Metodo para validar la confirmación de la eliminación
     * @param confirmacion
     */
    private void comprobarConfirmacion(boolean confirmacion) throws Exception {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    // !SECTION
    // SECTION: Implementacion de servicios

    // 2️⃣ Funciones del Administrador de Horario

    @Override
    public Respuesta<?> registrar(HorarioRequest request, Integer salaCodigo) throws Exception {

        Horario horario = horarioMapper.toEntity(request);

        Optional<Funcion> funcionSolapada = funcionRepo.solapaHorarioSala(salaCodigo, horario.getFechaInicio(), horario.getFechaFin());

        return comprobacionRespuesta(funcionSolapada, horario);
    }

    @Override
    public Respuesta<?> actualizar(HorarioRequest request) throws Exception {

        Optional<Horario> existente = horarioRepo.findById(request.getCodigo());
        validarExiste(existente);

        Horario horario = existente.get();
        horario.setFechaInicio(request.getFechaInicio());
        horario.setFechaFin(request.getFechaFin());

        // Se extrae el código de la sala de la función para mayor claridad
        Integer salaCodigo = horario.getFuncion().getSala().getCodigo();

        Optional<Funcion> funcionSolapada = funcionRepo.solapaHorarioTeatro(salaCodigo, horario.getCodigo(), horario.getFechaInicio(), horario.getFechaFin());

        return comprobacionRespuesta(funcionSolapada, horario);
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        Optional<Horario> buscado = horarioRepo.findById(codigo);
        validarExiste(buscado);
        horarioRepo.delete(buscado.get());
    }

    @Override
    public Optional<HorarioResponse> obtener(Integer codigo) throws Exception {

        Optional<Horario> buscado = horarioRepo.findById(codigo);

        validarExiste(buscado);

        return buscado.map(horarioMapper::toResponse);
    }

    @Override
    public List<HorarioResponse> listar() { return horarioMapper.toResponseList(horarioRepo.findAll()); }

    @Override
    public List<HorarioResponse> listarPaginado() { 

        return horarioMapper.toResponseList(horarioRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    @Override
    public List<HorarioResponse> listarAscendente() { 
        
        return horarioMapper.toResponseList(horarioRepo.findAll(Sort.by("codigo").ascending()));
    }

    @Override
    public List<HorarioResponse> listarDescendente() { 
        
        return horarioMapper.toResponseList(horarioRepo.findAll(Sort.by("codigo").descending()));
    }
    // !SECTION
}
