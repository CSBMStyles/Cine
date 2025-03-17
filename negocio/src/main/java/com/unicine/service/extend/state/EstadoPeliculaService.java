package com.unicine.service.extend.state;

import com.unicine.entity.PeliculaDisposicion;
import com.unicine.entity.Funcion;
import com.unicine.enumeration.EstadoPelicula;
import com.unicine.repository.FuncionRepo;
import com.unicine.repository.PeliculaDisposicionRepo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class EstadoPeliculaService {

    private final PeliculaDisposicionRepo disposicionRepo;

    private final FuncionRepo funcionRepo;

    public EstadoPeliculaService(PeliculaDisposicionRepo disposicionRepo, FuncionRepo funcionRepo) {
        this.disposicionRepo = disposicionRepo;
        this.funcionRepo = funcionRepo;
    }

    /**
     * Actualiza el estado de una película según su contexto actual
     * @param disposicion La disposición de película a actualizar
     * @return La disposición actualizada
     */
    @Transactional // La razón de usar transacciones es para asegurar que la operación se realice de manera atómica
    public PeliculaDisposicion actualizarEstado(PeliculaDisposicion disposicion) {

        EstadoPelicula estadoActual = disposicion.getEstadoPelicula();

        EstadoPelicula nuevoEstado = calcularEstado(disposicion);
        
        if (estadoActual != nuevoEstado) {

            disposicion.setEstadoPelicula(nuevoEstado); // Actualiza el estado de la película

            if (nuevoEstado == EstadoPelicula.ESTRENO) {

                disposicion.setFechaFuncionInicial(LocalDateTime.now(ZoneId.of("America/Bogota")));
            }

            return disposicionRepo.save(disposicion);
        }
        
        return disposicion;
    }

    /**
     * Calcula el nuevo estado de una película según su disposición actual
     * @param disposicion La disposición de película a actualizar
     * @return Nuevo estado calculado
     */
    private EstadoPelicula calcularEstado(PeliculaDisposicion disposicion) {

        Integer peliculaId = disposicion.getPelicula().getCodigo();

        Integer ciudadId = disposicion.getCiudad().getCodigo();
        
        // Verificar si tiene funciones activas
        boolean tieneFuncionesActivas = verificarFuncionesActivas(peliculaId, ciudadId);
        
        EstadoPelicula estadoActual = disposicion.getEstadoPelicula();

        switch (estadoActual) {
            case PENDIENTE:

                // Si tiene funciones programadas pero no ha llegado la fecha, pasa a PREVENTA
                if (tieneFuncionesActivas && !primeraFuncionInicio(peliculaId, ciudadId)) {
                    return EstadoPelicula.PREVENTA;
                }
                break;
                
            case PREVENTA:
                // Si ya comenzó la función, pasa a ESTRENO
                if (primeraFuncionInicio(peliculaId, ciudadId)) {
                    estadoActual = EstadoPelicula.ESTRENO;
                } 
                
                if (!tieneFuncionesActivas) {
                    estadoActual = EstadoPelicula.PENDIENTE;  // Vuelve a PENDIENTE si cancelaron las funciones
                }

                return estadoActual; // Conserva su estado si tiene funciones activas
                
            case ESTRENO:

                // Si pasaron 7 días desde el estreno, pasa a CARTELERA
                if (pasaronDiasDesdeFecha(disposicion.getFechaFuncionInicial(), 7)) {
                    estadoActual = EstadoPelicula.CARTELERA;
                }

                if (!tieneFuncionesActivas) {
                    estadoActual = EstadoPelicula.FUERA_CARTELERA;
                }

                return estadoActual; // Conserva su estado si tiene funciones activas
                
            case CARTELERA:
                // Si ya no hay funciones activas, pasa a FUERA_CARTELERA
                if (!tieneFuncionesActivas) {
                    return EstadoPelicula.FUERA_CARTELERA;
                }
                break;
                
            case FUERA_CARTELERA:
                // Si vuelve a tener funciones, pasa a CARTELERA
                if (tieneFuncionesActivas) {
                    // Si pasaron menos de 7 días desde su registro, puede volver a ESTRENO
                    if (!pasaronDiasDesdeFecha(disposicion.getFechaFuncionInicial(), 7)) {
                        return EstadoPelicula.ESTRENO;
                    } else {
                        return EstadoPelicula.CARTELERA;
                    }
                }
                break;
        }
        
        // Si no hay cambio de estado, mantiene el actual
        return disposicion.getEstadoPelicula();
    }

    /**
     * Verifica si existen funciones activas para una película en una ciudad (disposicion)
     * @param peliculaId ID de la película
     * @param ciudadId ID de la ciudad
     * @return true si hay funciones activas, false en caso contrario
     */
    private boolean verificarFuncionesActivas(Integer peliculaId, Integer ciudadId) {

        LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Bogota"));
        // Verifica si hay funciones activas en la base de datos
        return !funcionRepo.buscarFuncionesActivasDisposicion(peliculaId, ciudadId, ahora).isEmpty();
    }
    
    /**
     * Verifica si ya comenzó la primera función de una película
     * @param peliculaId ID de la película
     * @param ciudadId ID de la ciudad
     * @return true si la primera función ya comenzó, false en caso contrario
     */
    private boolean primeraFuncionInicio(Integer peliculaId, Integer ciudadId) {

        LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Bogota"));

        List<Funcion> funciones = funcionRepo.buscarPrimeraFuncionDisposicion(peliculaId, ciudadId);
        
        if (funciones.isEmpty()) {
            return false;
        }
        
        Funcion primeraFuncion = funciones.get(0);
        // Verifica si la fecha de inicio de la primera función es anterior a la fecha actual

        boolean estado = primeraFuncion.getHorario().getFechaInicio().isBefore(ahora);

        return estado;
    }
    
    /**
     * Comprueba si han pasado cierto número de días desde una fecha
     * @param fecha Fecha a comprobar
     * @param dias Número de días a comprobar
     * @return true si han pasado los días, false en caso contrario
     */
    private boolean pasaronDiasDesdeFecha(LocalDateTime fecha, int dias) {
        if (fecha == null) {
            return false;
        }
        
        LocalDate fechaLimite = fecha.toLocalDate().plusDays(dias);

        return LocalDate.now().isAfter(fechaLimite);
    }

    /**
     * Tarea programada para actualizar estados automáticamente se ejecuta cada día a medianoche
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void actualizarEstadosAutomaticamente() {

        List<PeliculaDisposicion> todasLasDisposiciones = disposicionRepo.findAll();
        
        for (PeliculaDisposicion disposicion : todasLasDisposiciones) {
            actualizarEstado(disposicion);
        }
    }
}