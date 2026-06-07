package com.unicine.event.movie;

import com.unicine.entity.movie.Coleccion;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.movie.PeliculaDisposicion;
import com.unicine.entity.movie.composed.PeliculaDisposicionCompuesta;
import com.unicine.entity.user.Cliente;
import com.unicine.repository.movie.ColeccionRepo;
import com.unicine.repository.movie.PeliculaDisposicionRepo;
import com.unicine.service.notification.EmailService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Listener que reacciona a cambios de estado de película.
 * Notifica por email a los clientes que tienen la película en favoritos
 * y han activado las notificaciones.
 */
@Component
public class PeliculaStateChangeEventListener {

    private final ColeccionRepo coleccionRepo;
    private final PeliculaDisposicionRepo disposicionRepo;
    private final EmailService emailService;

    public PeliculaStateChangeEventListener(ColeccionRepo coleccionRepo,
                                            PeliculaDisposicionRepo disposicionRepo,
                                            EmailService emailService) {
        this.coleccionRepo = coleccionRepo;
        this.disposicionRepo = disposicionRepo;
        this.emailService = emailService;
    }

    @EventListener
    @Transactional(readOnly = true)
    public void manejarCambioEstado(PeliculaStateChangeEvent evento) {
        PeliculaDisposicionCompuesta id = new PeliculaDisposicionCompuesta(evento.ciudadId(), evento.peliculaId());
        PeliculaDisposicion disposicion = disposicionRepo.findById(id).orElse(null);
        if (disposicion == null) {
            return;
        }

        Pelicula pelicula = disposicion.getPelicula();
        List<Coleccion> suscriptores = coleccionRepo.findByPeliculaAndNotificacionActiva(pelicula, true);

        if (suscriptores.isEmpty()) {
            return;
        }

        String asunto = String.format("Unicine: \"%s\" ahora en %s", pelicula.getNombre(), evento.estadoNuevo());
        String contenidoBase = String.format(
            "<p>Hola,</p>" +
            "<p>La película <strong>\"%s\"</strong> ha cambiado de estado:</p>" +
            "<ul><li>Estado anterior: <strong>%s</strong></li>" +
            "<li>Estado nuevo: <strong>%s</strong></li></ul>" +
            "<p>¡No te la pierdas en Unicine!</p>",
            pelicula.getNombre(),
            evento.estadoAnterior(),
            evento.estadoNuevo()
        );

        for (Coleccion coleccion : suscriptores) {
            Cliente cliente = coleccion.getCliente();
            if (cliente != null && cliente.getCorreo() != null) {
                emailService.enviarEmail(asunto, contenidoBase, cliente.getCorreo());
            }
        }
    }
}
