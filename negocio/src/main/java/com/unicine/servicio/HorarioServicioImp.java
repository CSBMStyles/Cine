package com.unicine.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entidades.Funcion;
import com.unicine.entidades.Horario;
import com.unicine.repo.FuncionRepo;
import com.unicine.repo.HorarioRepo;
import com.unicine.util.message.HorarioRespuesta;

import jakarta.validation.Valid;

@Service
@Validated
public class HorarioServicioImp implements HorarioServicio {

    // NOTE: Teoricamente se uitlizaria el @Autowired para inyectar dependencias, donde se instancia por si solo la clase que se necesita, pero se recomienda utilizar el constructor para eso, ya que el @Service no es va a instanciar
    private final HorarioRepo horarioRepo;

    private final FuncionRepo funcionRepo;

    public HorarioServicioImp(HorarioRepo horarioRepo, FuncionRepo funcionRepo) {
        this.horarioRepo = horarioRepo;
        this.funcionRepo = funcionRepo;
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
    private HorarioRespuesta<Funcion> instanciarRespuesta(Optional<Funcion> funcionSolapada) {

        return new HorarioRespuesta<Funcion>("El horario se solapa con otra función", funcionSolapada.get(), false);
    }

    /**
     * Funcion para instanciar la respuesta de la operación exitosa
     * @param guardado
     * @return respuesta conteniendo el mensaje, horario y estado de la operación
     */
    private HorarioRespuesta<Horario> instanciarRespuesta(Horario guardado) {

        return new HorarioRespuesta<Horario>("Horario registrado exitosamente", guardado, true);
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
    public HorarioRespuesta<?> registrar(@Valid Horario horario) throws Exception {

        Optional<Funcion> funcionSolapada = funcionRepo.solapaHorarioSala(horario.getFuncion(), horario);

        if (funcionSolapada.isPresent()) {
            return instanciarRespuesta(funcionSolapada);

        } else { Horario guardado = horarioRepo.save(horario);
            return instanciarRespuesta(guardado);
        }
    }

    @Override
    public Horario actualizar(@Valid Horario horario) throws Exception {

        return horarioRepo.save(horario);
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
