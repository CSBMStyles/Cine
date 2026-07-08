package com.unicine.test.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.api.response.Respuesta;
import com.unicine.entity.showing.Funcion;
import com.unicine.entity.showing.Horario;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.theater.Sala;
import com.unicine.enums.movie.EstadoPelicula;
import com.unicine.enums.movie.FormatoPelicula;
import com.unicine.repository.movie.PeliculaRepo;
import com.unicine.repository.showing.FuncionRepo;
import com.unicine.repository.theater.CiudadRepo;
import com.unicine.repository.theater.SalaRepo;
import com.unicine.service.showing.FuncionServicio;
import com.unicine.service.showing.HorarioServicio;
import com.unicine.service.movie.PeliculaDisposicionServicio;
import com.unicine.service.theater.SalaServicio;
import com.unicine.transfer.dto.request.PeliculaDisposicionRequest;
import com.unicine.transfer.dto.response.PeliculaDisposicionResponse;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@Transactional
public class PeliculaDisposicionServicioTest {

    @Autowired
    private PeliculaDisposicionServicio peliculaDisposicionServicio;

    @Autowired
    private FuncionServicio funcionServicio;

    @Autowired
    private SalaServicio salaServicio;

    @Autowired
    private PeliculaRepo peliculaRepo;

    @Autowired
    private CiudadRepo ciudadRepo;

    @Autowired
    private SalaRepo salaRepo;

    @Autowired
    private HorarioServicio horarioServicio;

    @Autowired
    private FuncionRepo funcionRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        PeliculaDisposicionRequest request = PeliculaDisposicionRequest.builder()
                .estadoPelicula(EstadoPelicula.PENDIENTE)
                .peliculaCodigo(5)
                .ciudadCodigo(3)
                .build();

        try {
            PeliculaDisposicionResponse nuevo = peliculaDisposicionServicio.registrar(request);

            Assertions.assertEquals("PENDIENTE", nuevo.getEstadoPelicula().toString());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        try {
            PeliculaDisposicionResponse existente = peliculaDisposicionServicio.obtener(3, 1).orElse(null);
            Assertions.assertNotNull(existente);

            PeliculaDisposicionRequest request = PeliculaDisposicionRequest.builder()
                    .estadoPelicula(EstadoPelicula.ESTRENO)
                    .peliculaCodigo(existente.getPelicula().getCodigo())
                    .ciudadCodigo(existente.getCiudad().getCodigo())
                    .fechaFuncionInicial(existente.getFechaFuncionInicial())
                    .build();

            PeliculaDisposicionResponse actualizado = peliculaDisposicionServicio.actualizar(request);

            Assertions.assertEquals(EstadoPelicula.ESTRENO, actualizado.getEstadoPelicula());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        try {
            peliculaDisposicionServicio.eliminar(1, 1, true);

            Assertions.assertThrows(Exception.class, () -> {
                peliculaDisposicionServicio.obtener(1, 1);
            });

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        try {
            PeliculaDisposicionResponse peliculaDisposicion = peliculaDisposicionServicio.obtener(1, 1).orElse(null);

            Assertions.assertEquals(1, peliculaDisposicion.getCiudad().getCodigo());
            Assertions.assertEquals(1, peliculaDisposicion.getPelicula().getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + peliculaDisposicion);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {
        try {
            List<PeliculaDisposicionResponse> lista = peliculaDisposicionServicio.listar();

            Assertions.assertTrue(lista.size() > 0);

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarRecomendacionPeliculaEstado() {

        try {
            EstadoPelicula estado = EstadoPelicula.FUERA_CARTELERA;

            PeliculaDisposicionRequest request = PeliculaDisposicionRequest.builder()
                    .estadoPelicula(EstadoPelicula.FUERA_CARTELERA)
                    .peliculaCodigo(1)
                    .ciudadCodigo(1)
                    .build();

            List<PeliculaDisposicionResponse> lista = peliculaDisposicionServicio.listarRecomendacionPeliculaEstado(request, estado);

            Assertions.assertTrue(lista.size() > 0);

            System.out.println("\n" + "Listado de recomendaciones:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    // ℹ️ Pruebas para los cambios de estados

    @Test
    @Sql("classpath:dataset.sql")
    public void cambiarPendientePreventa() {

        Sala sala;

        try {
            sala = salaRepo.findById(4).orElse(null);

            System.out.println("\n" + "Sala seleccionada:" + "\n" + sala);

            Assertions.assertNotNull(sala);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        LocalDateTime fechaInicio = LocalDateTime.of(2026, 12, 30, 15, 00);
        LocalDateTime fechaFin = LocalDateTime.of(2026, 12, 30, 17, 00);

        Horario horario = null;

        try {
            Respuesta<?> repuestaHorario = horarioServicio.registrar(new Horario(fechaInicio, fechaFin), sala);

            if (!repuestaHorario.isExito()) {

                Assertions.fail(repuestaHorario.getMensaje() + "\n" + repuestaHorario.getData());
            }

            horario = (Horario) repuestaHorario.getData();

            String dia = horarioServicio.obtenerDia(fechaInicio);

            System.out.println("\n" + "Horario creado:" + "\n" + horario);

            System.out.println("\n" + "Dia de la semana:" + "\n" + dia);

            Assertions.assertTrue(repuestaHorario.isExito());

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        Pelicula pelicula;

        try {
            pelicula = peliculaRepo.findById(4).orElse(null);

            System.out.println("\n" + "Pelicula seleccionada:" + "\n" + pelicula);

            Assertions.assertNotNull(pelicula);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        PeliculaDisposicionResponse peliculaDisposicion;

        try {
            peliculaDisposicion = peliculaDisposicionServicio.obtener(
                    sala.getTeatro().getCiudad().getCodigo(), pelicula.getCodigo()).orElse(null);

            System.out.println("\n" + "Disposicion seleccionada:" + "\n" + peliculaDisposicion);

            Assertions.assertNotNull(peliculaDisposicion);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        Funcion funcion;

        try {

            Funcion funcionNueva = new Funcion(FormatoPelicula.DOBLADO, sala, horario, pelicula);
            funcionNueva.setPrecio(0.0);

            funcion = funcionServicio.registrar(funcionNueva);

            System.out.println("\n" + "Funcion registrada:" + "\n" + funcion);

            Assertions.assertNotNull(funcion);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        try {

            PeliculaDisposicionRequest request = PeliculaDisposicionRequest.builder()
                    .estadoPelicula(EstadoPelicula.PENDIENTE)
                    .peliculaCodigo(peliculaDisposicion.getPelicula().getCodigo())
                    .ciudadCodigo(peliculaDisposicion.getCiudad().getCodigo())
                    .fechaFuncionInicial(peliculaDisposicion.getFechaFuncionInicial())
                    .build();

            PeliculaDisposicionResponse disposicionActual = peliculaDisposicionServicio.actualizar(request);

            Assertions.assertEquals("PREVENTA", disposicionActual.getEstadoPelicula().toString());

            System.out.println("\n" + "Disposicion actualizada:" + "\n" + disposicionActual);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    /**
     * Para esta simulación no deberia haber intervencion humana o accion, entonces teniendo en cuenta que la aplicacion se encuentra en ejecucion esta prueba trata de que cuando se inicia creamos una función simulando que existe y que esa funcion no ha comenzado, pasado el tiempo de la fechaInicio se cambia el estado a ESTRENO.
     */
    @Test
    @Sql("classpath:dataset.sql")
    public void cambiarPreventaEstreno() {

        Sala sala;

        try {
            sala = salaRepo.findById(4).orElse(null);

            System.out.println("\n" + "Sala seleccionada:" + "\n" + sala);

            Assertions.assertNotNull(sala);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        LocalDateTime fechaInicio = LocalDateTime.now().plusSeconds(2);
        LocalDateTime fechaFin = LocalDateTime.now().plusHours(1);

        Horario horario = null;

        try {
            Respuesta<?> repuestaHorario = horarioServicio.registrar(new Horario(fechaInicio, fechaFin), sala);

            if (!repuestaHorario.isExito()) {

                Assertions.fail(repuestaHorario.getMensaje() + "\n" + repuestaHorario.getData());
            }

            horario = (Horario) repuestaHorario.getData();

            String dia = horarioServicio.obtenerDia(fechaInicio);

            System.out.println("\n" + "Horario creado:" + "\n" + horario);

            System.out.println("\n" + "Dia de la semana:" + "\n" + dia);

            Assertions.assertTrue(repuestaHorario.isExito());

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        Pelicula pelicula;

        try {
            pelicula = peliculaRepo.findById(1).orElse(null);

            System.out.println("\n" + "Pelicula seleccionada:" + "\n" + pelicula);

            Assertions.assertNotNull(pelicula);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        PeliculaDisposicionResponse peliculaDisposicion;

        Integer ciudadCodigo = sala.getTeatro().getCiudad().getCodigo();
        Integer peliculaCodigo = pelicula.getCodigo();

        try {
            peliculaDisposicion = peliculaDisposicionServicio.obtener(ciudadCodigo, peliculaCodigo).orElse(null);

            System.out.println("\n" + "Estado inicial: " + peliculaDisposicion.getEstadoPelicula());

            Assertions.assertEquals("PREVENTA", peliculaDisposicion.getEstadoPelicula().toString());

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        Funcion funcion;

        try {

            Funcion funcionNueva = new Funcion(FormatoPelicula.DOBLADO, sala, horario, pelicula);
            funcionNueva.setPrecio(0.0);

            funcion = funcionServicio.registrar(funcionNueva);

            System.out.println("\n" + "Funcion registrada:" + "\n" + funcion);

            Assertions.assertNotNull(funcion);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        try {
            System.out.println("Esperando que comience la función a las " + fechaInicio);
            // Esperamos hasta después de la fecha de inicio
            Thread.sleep(5000); // 5 segundos

            // Invocamos el método automático (similar a lo que haría el scheduler)
            // Esto ejecuta la misma lógica que ejecutaría automáticamente
            peliculaDisposicionServicio.actualizarEstadoPeliculas();

            // Obtenemos el estado actualizado
            PeliculaDisposicionResponse disposicionActualizada = peliculaDisposicionServicio.obtener(ciudadCodigo, peliculaCodigo).orElse(null);
            System.out.println("\nEstado después de actualización automática: " + disposicionActualizada.getEstadoPelicula());

            Assertions.assertEquals(EstadoPelicula.ESTRENO, disposicionActualizada.getEstadoPelicula());
        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            e.printStackTrace();
            Assertions.fail("No debería lanzar excepción: " + e.getMessage());
        }
    }
}
