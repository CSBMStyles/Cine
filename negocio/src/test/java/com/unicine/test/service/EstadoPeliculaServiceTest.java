package com.unicine.test.service;

import com.unicine.entity.movie.HistorialEstadoPelicula;
import com.unicine.entity.movie.PeliculaDisposicion;
import com.unicine.entity.movie.composed.PeliculaDisposicionCompuesta;
import com.unicine.entity.showing.Funcion;
import com.unicine.entity.showing.Horario;
import com.unicine.entity.theater.Sala;
import com.unicine.enums.movie.EstadoPelicula;
import com.unicine.enums.movie.FormatoPelicula;
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

    // SECTION: Metodos de soporte

    // Metodo para obtener una disposicion del dataset y forzar un estado
    private PeliculaDisposicion obtenerDisposicionConEstado(Integer ciudadId, Integer peliculaId, EstadoPelicula estado) {
        PeliculaDisposicion disposicion = disposicionRepo.findById(
            new PeliculaDisposicionCompuesta(ciudadId, peliculaId)
        ).orElseThrow(() -> new RuntimeException("Disposicion (ciudad=" + ciudadId + ", pelicula=" + peliculaId + ") no encontrada"));

        disposicion.setEstadoPelicula(estado);
        return disposicionRepo.save(disposicion);
    }

    // Metodo para crear un horario vivo que comienza en N segundos
    private Horario crearHorarioVivo(Integer segundosInicio) {
        LocalDateTime ahora = LocalDateTime.now(ZoneId.of("America/Bogota"));
        Horario horario = new Horario(ahora.plusSeconds(segundosInicio), ahora.plusHours(2));
        return horarioRepo.save(horario);
    }

    // Metodo para obtener sala del dataset
    private Sala obtenerSala(Integer codigoSala) {
        return salaRepo.findById(codigoSala)
            .orElseThrow(() -> new RuntimeException("Sala " + codigoSala + " no encontrada"));
    }

    // Metodo para crear una funcion asociada a una disposicion y sala
    private Funcion crearFuncionConDisposicion(PeliculaDisposicion disposicion, Horario horario, Sala sala) {
        Funcion funcion = new Funcion();
        funcion.setHorario(horario);
        funcion.setSala(sala);
        funcion.setPrecio(10000.0);
        funcion.setFormato(FormatoPelicula.DOBLADO);
        funcion.setPelicula(disposicion.getPelicula());
        return funcionRepo.save(funcion);
    }

    // Metodo para verificar estado y pausar
    private PeliculaDisposicion verificarEstado(PeliculaDisposicion disposicion, EstadoPelicula esperado, String mensaje, Integer pausaMs) throws InterruptedException {
        PeliculaDisposicion actualizado = estadoPeliculaService.actualizarEstado(disposicion);
        Assertions.assertEquals(esperado, actualizado.getEstadoPelicula(), mensaje);
        Thread.sleep(pausaMs);
        return actualizado;
    }

    // Metodo para verificar estado sin pausar
    private PeliculaDisposicion verificarEstado(PeliculaDisposicion disposicion, EstadoPelicula esperado, String mensaje) {
        PeliculaDisposicion actualizado = estadoPeliculaService.actualizarEstado(disposicion);
        Assertions.assertEquals(esperado, actualizado.getEstadoPelicula(), mensaje);
        return actualizado;
    }

    // Metodo para verificar que existe historial de cambios
    private void assertHistorialExiste(PeliculaDisposicion disposicion) {
        List<HistorialEstadoPelicula> historial = historialServicio.obtenerPorPelicula(
            disposicion.getPelicula().getCodigo(),
            disposicion.getCiudad().getCodigo()
        );
        Assertions.assertFalse(historial.isEmpty(),
            "Deberia existir al menos un registro en el historial de cambios");
    }

    // !SECTION

    @Test
    @Sql("classpath:dataset.sql")
    public void cambioEstadoPreventaEstrenoTiempoReal() throws InterruptedException {

        // Arrange: disposicion en ciudad 4 (Bogota), pelicula 1
        PeliculaDisposicion disposicion = obtenerDisposicionConEstado(4, 1, EstadoPelicula.PREVENTA);

        // Horario que empieza en 8 segundos
        Horario horario = crearHorarioVivo(8);

        // Sala 6 (teatro 2 -> ciudad 4)
        Sala sala = obtenerSala(6);

        // Crear funcion asociada
        crearFuncionConDisposicion(disposicion, horario, sala);

        // Act & Assert: PREVENTA inicial, esperar 5 segundos
        PeliculaDisposicion estadoInicial = verificarEstado(disposicion, EstadoPelicula.PREVENTA,
            "Antes de la fecha de inicio, la pelicula deberia estar en PREVENTA", 5000);

        // Act & Assert: PREVENTA al segundo 5, esperar 5 segundos
        System.out.println("Estado al segundo 5: " + estadoInicial.getEstadoPelicula());
        PeliculaDisposicion estadoSegundoCinco = verificarEstado(estadoInicial, EstadoPelicula.PREVENTA,
            "Aun en PREVENTA, la funcion no ha iniciado", 5000);

        // Act & Assert: ESTRENO al segundo 10
        System.out.println("Estado al segundo 10: " + estadoSegundoCinco.getEstadoPelicula());
        PeliculaDisposicion estadoSegundoDiez = verificarEstado(estadoSegundoCinco, EstadoPelicula.ESTRENO,
            "Despues de la fecha de inicio, la pelicula deberia estar en ESTRENO");

        // Esperar 5 segundos finales
        Thread.sleep(5000);

        // Assert: historial de cambios
        assertHistorialExiste(disposicion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void estrenoCarteleraDespuesDeSieteDias() {
        // Arrange: disposicion en ESTRENO con fecha de inicio hace 8 dias
        PeliculaDisposicion disposicion = obtenerDisposicionConEstado(4, 1, EstadoPelicula.ESTRENO);
        LocalDateTime haceOchoDias = LocalDateTime.now(ZoneId.of("America/Bogota")).minusDays(8);
        disposicion.setFechaFuncionInicial(haceOchoDias);
        disposicionRepo.save(disposicion);

        // Act: recalcular estado sin funciones activas nuevas
        PeliculaDisposicion actualizada = estadoPeliculaService.actualizarEstado(disposicion);

        // Assert: 7+ dias desde el estreno y sin funciones activas -> CARTELERA
        Assertions.assertEquals(EstadoPelicula.CARTELERA, actualizada.getEstadoPelicula(),
            "Tras 7 dias desde el estreno sin funciones, debe pasar a CARTELERA");
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void carteleraFueraDeCarteleraSinFunciones() {
        // Arrange: disposicion en CARTELERA. El dataset tiene funciones activas
        // para varias disposiciones, asi que usamos la combinacion (1, 1) que no
        // tiene funciones en el dataset y forzamos el estado CARTELERA.
        PeliculaDisposicion disposicion = obtenerDisposicionConEstado(1, 1, EstadoPelicula.CARTELERA);
        LocalDateTime haceOchoDias = LocalDateTime.now(ZoneId.of("America/Bogota")).minusDays(8);
        disposicion.setFechaFuncionInicial(haceOchoDias);
        disposicionRepo.save(disposicion);

        // Act
        PeliculaDisposicion actualizada = estadoPeliculaService.actualizarEstado(disposicion);

        // Assert: sin funciones activas debe pasar a FUERA_CARTELERA
        Assertions.assertEquals(EstadoPelicula.FUERA_CARTELERA, actualizada.getEstadoPelicula(),
            "En CARTELERA sin funciones activas, debe pasar a FUERA_CARTELERA");
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void tareaProgramadaSeEjecutaSinExcepcion() {
        // Verifica que el metodo anotado con @Scheduled se puede invocar
        // directamente sin lanzar excepciones. La ejecucion real del cron
        // queda fuera del alcance de un test unitario.
        Assertions.assertDoesNotThrow(() -> estadoPeliculaService.actualizarEstadosAutomaticamente());
    }
}
