package com.unicine.service.movie;

import com.unicine.entity.movie.HistorialEstadoPelicula;
import com.unicine.entity.movie.PeliculaDisposicion;
import com.unicine.entity.movie.composed.PeliculaDisposicionCompuesta;
import com.unicine.enums.movie.EstadoPelicula;
import com.unicine.repository.movie.HistorialEstadoPeliculaRepo;
import com.unicine.repository.movie.PeliculaDisposicionRepo;
import com.unicine.service.notification.EmailService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class HistorialEstadoPeliculaServicioImp implements HistorialEstadoPeliculaServicio {

    private final HistorialEstadoPeliculaRepo historialRepo;
    private final PeliculaDisposicionRepo disposicionRepo;
    private final EmailService emailService;

    public HistorialEstadoPeliculaServicioImp(HistorialEstadoPeliculaRepo historialRepo,
                                              PeliculaDisposicionRepo disposicionRepo,
                                              EmailService emailService) {
        this.historialRepo = historialRepo;
        this.disposicionRepo = disposicionRepo;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public HistorialEstadoPelicula registrar(Integer peliculaId, Integer ciudadId, EstadoPelicula anterior, EstadoPelicula nuevo) {
        PeliculaDisposicionCompuesta id = new PeliculaDisposicionCompuesta(ciudadId, peliculaId);
        PeliculaDisposicion disposicion = disposicionRepo.findById(id).orElse(null);
        if (disposicion == null) {
            return null;
        }

        HistorialEstadoPelicula historial = HistorialEstadoPelicula.builder()
                .estadoAnterior(anterior)
                .estadoNuevo(nuevo)
                .fechaCambio(LocalDateTime.now(ZoneId.of("America/Bogota")))
                .peliculaDisposicion(disposicion)
                .build();

        return historialRepo.save(historial);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HistorialEstadoPelicula> obtenerPorPelicula(Integer peliculaId, Integer ciudadId) {
        return historialRepo.findByPeliculaDisposicion_Pelicula_CodigoAndPeliculaDisposicion_Ciudad_Codigo(peliculaId, ciudadId);
    }

    @Override
    @Transactional
    public void eliminarPorPelicula(Integer peliculaId, Integer ciudadId) {
        historialRepo.deleteByPeliculaDisposicion_Pelicula_CodigoAndPeliculaDisposicion_Ciudad_Codigo(peliculaId, ciudadId);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarPorPelicula(Integer peliculaId, Integer ciudadId) {
        return historialRepo.countByPeliculaDisposicion_Pelicula_CodigoAndPeliculaDisposicion_Ciudad_Codigo(peliculaId, ciudadId);
    }

    @Override
    @Transactional(readOnly = true)
    public Integer obtenerAntiguedadUltimoCambio(Integer peliculaId, Integer ciudadId) {
        HistorialEstadoPelicula ultimo = historialRepo.findTopByPeliculaDisposicion_Pelicula_CodigoAndPeliculaDisposicion_Ciudad_CodigoOrderByFechaCambioDesc(peliculaId, ciudadId);
        if (ultimo == null) {
            return null;
        }
        return (int) ChronoUnit.DAYS.between(ultimo.getFechaCambio(), LocalDateTime.now(ZoneId.of("America/Bogota")));
    }

    @Override
    @Transactional(readOnly = true)
    public void alertarSiHistorialViejo(Integer peliculaId, Integer ciudadId) {
        Integer dias = obtenerAntiguedadUltimoCambio(peliculaId, ciudadId);
        if (dias != null && dias >= 180) {
            PeliculaDisposicionCompuesta id = new PeliculaDisposicionCompuesta(ciudadId, peliculaId);
            PeliculaDisposicion disposicion = disposicionRepo.findById(id).orElse(null);
            if (disposicion == null) return;

            String asunto = "Unicine: Alerta de película estancada";
            String contenido = String.format(
                "<p>La película <strong>%s</strong> lleva <strong>%d días</strong> sin cambiar de estado.</p>" +
                "<p>Estado actual: <strong>%s</strong></p>" +
                "<p>Considere actualizar su programación.</p>",
                disposicion.getPelicula().getNombre(),
                dias,
                disposicion.getEstadoPelicula()
            );
            // TODO: cambiar destinatario a admin teatro creador de la película
            emailService.enviarEmail(asunto, contenido, "cristianbarrera100@gmail.com");
        }
    }
}
