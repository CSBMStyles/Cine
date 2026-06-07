package com.unicine.test.service;

import com.unicine.entity.movie.HistorialEstadoPelicula;
import com.unicine.entity.movie.PeliculaDisposicion;
import com.unicine.entity.movie.composed.PeliculaDisposicionCompuesta;
import com.unicine.entity.showing.Funcion;
import com.unicine.entity.showing.Horario;
import com.unicine.entity.theater.Sala;
import com.unicine.enums.movie.EstadoPelicula;
import com.unicine.enums.movie.FormatoPelicula;
import com.unicine.repository.movie.HistorialEstadoPeliculaRepo;
import com.unicine.repository.movie.PeliculaDisposicionRepo;
import com.unicine.repository.showing.FuncionRepo;
import com.unicine.repository.showing.HorarioRepo;
import com.unicine.repository.theater.SalaRepo;
import com.unicine.service.movie.EstadoPeliculaService;
import com.unicine.service.movie.HistorialEstadoPeliculaServicio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@SpringBootTest
@Transactional
public class EstadoPeliculaServiceTest {

    @Autowired
    private EstadoPeliculaService estadoPeliculaService;

    @Autowired
    private HistorialEstadoPeliculaServicio historialServicio;

    @Autowired
    private PeliculaDisposicionRepo disposicionRepo;

    @Autowired
    private FuncionRepo funcionRepo;

    @Autowired
    private HorarioRepo horarioRepo;

    @Autowired
    private SalaRepo salaRepo;

    @Autowired
    private HistorialEstadoPeliculaRepo historialRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void cambioEstadoPreventaAEstrenoEnTiempoReal() throws InterruptedException {

        // Arrange: usar disposicion existente en ciudad 4 (Bogota), pelicula 1
        // La sala 6 pertenece al teatro 2 que esta en ciudad 4
        PeliculaDisposicion disposicion = disposicionRepo.findById(
            new PeliculaDisposicionCompuesta(4, 1)
        ).orElseThrow(() -> new RuntimeException("Disposicion (ciudad=4, pelicula=1) no encontrada"));

        // Forzar estado PREVENTA para el test
        disposicion.setEstadoPelicula(EstadoPelicula.PREVENTA);
        disposicion = disposicionRepo.save(disposicion);

        // Crear horario que empieza en 5 segundos
        LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Bogota"));
        Horario horario = new Horario(ahora.plusSeconds(5), ahora.plusHours(2));
        horario = horarioRepo.save(horario);

        // Obtener sala 6 (teatro 2 -> ciudad 4, coincide con disposicion)
        Sala sala = salaRepo.findById(6).orElseThrow(() -> new RuntimeException("Sala 6 no encontrada"));

        // Crear funcion asociada a la pelicula de la disposicion
        Funcion funcion = new Funcion();
        funcion.setHorario(horario);
        funcion.setSala(sala);
        funcion.setPrecio(10000.0);
        funcion.setFormato(FormatoPelicula.DOBLADO);
        funcion.setPelicula(disposicion.getPelicula());
        funcionRepo.save(funcion);

        // Act 1: verificar estado antes de que pase el tiempo
        PeliculaDisposicion antes = estadoPeliculaService.actualizarEstado(disposicion);
        EstadoPelicula estadoAntes = antes.getEstadoPelicula();

        // Assert 1: deberia estar en PREVENTA (funcion no ha empezado)
        Assertions.assertEquals(EstadoPelicula.PREVENTA, estadoAntes,
            "Antes de la fecha de inicio, la pelicula deberia estar en PREVENTA");

        // Esperar 6 segundos para que la funcion empiece
        Thread.sleep(6000);

        // Act 2: re-evaluar estado despues de que paso el tiempo
        PeliculaDisposicion despues = estadoPeliculaService.actualizarEstado(antes);
        EstadoPelicula estadoDespues = despues.getEstadoPelicula();

        // Assert 2: deberia estar en ESTRENO (funcion ya comenzo)
        Assertions.assertEquals(EstadoPelicula.ESTRENO, estadoDespues,
            "Despues de la fecha de inicio, la pelicula deberia estar en ESTRENO");

        // Assert 3: verificar que se registro en historial
        List<HistorialEstadoPelicula> historial = historialServicio.obtenerPorPelicula(
            disposicion.getPelicula().getCodigo(),
            disposicion.getCiudad().getCodigo()
        );
        Assertions.assertFalse(historial.isEmpty(),
            "Deberia existir al menos un registro en el historial de cambios");

        System.out.println("Test de cambio de estado en tiempo real completado exitosamente");
        System.out.println("Estado antes: " + estadoAntes);
        System.out.println("Estado despues: " + estadoDespues);
    }
}
