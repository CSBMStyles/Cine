package com.unicine.service;

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
import com.unicine.entity.Funcion;
import com.unicine.entity.Horario;
import com.unicine.entity.Sala;
import com.unicine.repository.FuncionRepo;
import com.unicine.repository.HorarioRepo;
import com.unicine.util.initializer.HorarioDescuentoInit;

import jakarta.validation.Valid;

@Service
@Validated
public class HorarioServicioImp implements HorarioServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final HorarioRepo horarioRepo;

    private final FuncionRepo funcionRepo;

    private final HorarioDescuentoInit descuentoInitializer;

    public HorarioServicioImp(HorarioRepo horarioRepo, FuncionRepo funcionRepo, HorarioDescuentoInit descuentoInitializer) {
        this.horarioRepo = horarioRepo;
        this.funcionRepo = funcionRepo;
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
            throw new Exception("El horario no existe");
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

            return new Respuesta<Funcion>("El horario se solapa con otra función", funcion, false);
        } else {

            Horario guardado = horarioRepo.save(horario);

            return new Respuesta<Horario>("Horario registrado exitosamente", guardado, true);
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

    // SECTION: Implementacion de servicios

    // 2️⃣ Funciones del Administrador de Horario

    @Override
    public Respuesta<?> registrar(@Valid Horario horario, Sala sala) throws Exception {

        Optional<Funcion> funcionSolapada = funcionRepo.solapaHorarioSala(sala.getCodigo(), horario.getFechaInicio(), horario.getFechaFin());

        return comprobacionRespuesta(funcionSolapada, horario);
    }

    @Override
    public Respuesta<?> actualizar(@Valid Horario horario) throws Exception {

        // Se extrae el código de la sala de la función para mayor claridad
        Integer salaCodigo = horario.getFuncion().getSala().getCodigo();

        Optional<Funcion> funcionSolapada = funcionRepo.solapaHorarioTeatro(salaCodigo, horario.getCodigo(), horario.getFechaInicio(), horario.getFechaFin());

        return comprobacionRespuesta(funcionSolapada, horario);
    }

    @Override
    public void eliminar(@Valid Horario eliminado, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        horarioRepo.delete(eliminado);
    }

    @Override
    public Optional<Horario> obtener(Integer codigo) throws Exception {

        Optional<Horario> buscado = horarioRepo.findById(codigo);

        validarExiste(buscado);

        return buscado;
    }

    @Override
    public List<Horario> listar() { return horarioRepo.findAll(); }

    @Override
    public List<Horario> listarPaginado() { 

        return horarioRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    @Override
    public List<Horario> listarAscendente() { 
        
        return horarioRepo.findAll(Sort.by("codigo").ascending());
    }

    @Override
    public List<Horario> listarDescendente() { 
        
        return horarioRepo.findAll(Sort.by("codigo").descending());
    }
}
