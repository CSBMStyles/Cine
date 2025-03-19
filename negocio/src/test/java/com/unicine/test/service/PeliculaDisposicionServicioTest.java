package com.unicine.test.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.api.response.Respuesta;
import com.unicine.entity.Ciudad;
import com.unicine.entity.Funcion;
import com.unicine.entity.Horario;
import com.unicine.entity.Pelicula;
import com.unicine.entity.PeliculaDisposicion;
import com.unicine.entity.Sala;
import com.unicine.entity.composed.PeliculaDisposicionCompuesta;
import com.unicine.enumeration.EstadoPelicula;
import com.unicine.enumeration.FormatoPelicula;
import com.unicine.service.CiudadServicio;
import com.unicine.service.FuncionServicio;
import com.unicine.service.HorarioServicio;
import com.unicine.service.PeliculaDisposicionServicio;
import com.unicine.service.PeliculaServicio;
import com.unicine.service.SalaServicio;
import com.unicine.util.validation.attributes.CiudadAtributoValidator;
import com.unicine.util.validation.attributes.PeliculaAtributoValidator;
import com.unicine.util.validation.attributes.SalaAtributoValidator;

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
    private PeliculaServicio peliculaServicio;

    @Autowired 
    private CiudadServicio ciudadServicio;

    @Autowired
    private HorarioServicio horarioServicio;

    @Test
    @Sql("classpath:dataset.sql") 
    public void registrar() { // ✅ Estado: PENDIENTE inicial entonces no deberia haber problemas

        // Obtenemos la pelicula apartir del que se haya seleccionado, esta configuracion se recomienda que este en la misma interfaz de la creacion de pelicula
        Pelicula pelicula;
        try {
            pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(5)).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        // Obtenemos la ciudad apartir de una lista, puede automatizarse para que se seleccione la ciudad por apartir del administrador que lo crea y su ubicacion
        Ciudad ciudad;

        try {
            ciudad = ciudadServicio.obtener(new CiudadAtributoValidator(3)).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        PeliculaDisposicion peliculaDisposicion = new PeliculaDisposicion(pelicula, ciudad);

        try {
            PeliculaDisposicion nuevo = peliculaDisposicionServicio.registrar(peliculaDisposicion);
            
            Assertions.assertEquals("PENDIENTE", nuevo.getEstadoPelicula().toString());
            
            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        PeliculaDisposicionCompuesta codigo = new PeliculaDisposicionCompuesta(1, 1);

        try {
            PeliculaDisposicion peliculaDisposicion = peliculaDisposicionServicio.obtener(codigo).orElse(null);

            peliculaDisposicion.setEstadoPelicula(EstadoPelicula.PREVENTA);

            PeliculaDisposicion actualizado = peliculaDisposicionServicio.actualizar(peliculaDisposicion);

            Assertions.assertEquals(EstadoPelicula.PREVENTA, actualizado.getEstadoPelicula());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {
        
        PeliculaDisposicionCompuesta codigo = new PeliculaDisposicionCompuesta(1, 1);

        try {
            PeliculaDisposicion peliculaDisposicion = peliculaDisposicionServicio.obtener(codigo).orElse(null);

            peliculaDisposicionServicio.eliminar(peliculaDisposicion, true);
            
            Assertions.assertThrows(Exception.class, () -> {
                peliculaDisposicionServicio.obtener(codigo);
            });

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        PeliculaDisposicionCompuesta codigo = new PeliculaDisposicionCompuesta(1, 1);

        try {
            PeliculaDisposicion peliculaDisposicion = peliculaDisposicionServicio.obtener(codigo).orElse(null);

            Assertions.assertEquals(codigo, peliculaDisposicion.getCiudad().getCodigo() + peliculaDisposicion.getPelicula().getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + peliculaDisposicion);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {
        try {
            List<PeliculaDisposicion> lista = peliculaDisposicionServicio.listar();

            Assertions.assertTrue(lista.size() > 0);

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarRecomendacionPeliculaEstado() {

        PeliculaDisposicionCompuesta codigo = new PeliculaDisposicionCompuesta(1, 1);

        try {

            EstadoPelicula estado = EstadoPelicula.FUERA_CARTELERA;

            PeliculaDisposicion peliculaDisposicion = peliculaDisposicionServicio.obtener(codigo).orElse(null);

            List<PeliculaDisposicion> lista = peliculaDisposicionServicio.listarRecomendacionPeliculaEstado(peliculaDisposicion, estado);

            Assertions.assertTrue(lista.size() > 0);

            System.out.println("\n" + "Listado de recomendaciones:");

            lista.forEach(System.out::println);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    // ℹ️ Pruebas para los cambios de estados

    @Test
    @Sql("classpath:dataset.sql") 
    public void cambiarPendientePreventa() { // ✅ Estado: PREVENTA

        // El administrador del teatro debio haber seleccionado sala de las que maneja.

        Sala sala;

        try {
            sala = salaServicio.obtener(new SalaAtributoValidator(4)).orElse(null); // Esta sala pertenece a la ciudad {1}

            System.out.println("\n" + "Sala seleccionada:" + "\n" + sala);

            Assertions.assertNotNull(sala);

        } catch (Exception e) { throw new RuntimeException(e); }

        // El horario se crea exclusivamente para la funcion deseada, donde es primero antes del registro de la funcion.

        LocalDateTime fechaInicio = LocalDateTime.of(2025, 03, 20, 15, 00);
        LocalDateTime fechaFin = LocalDateTime.of(2025, 03, 20, 17, 00);

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

        } catch (Exception e) { throw new RuntimeException(e); }

        // Selecciona entre una lista la pelicula que se desea registrar en la funcion.

        Pelicula pelicula;

        try {
            pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(4)).orElse(null);

            System.out.println("\n" + "Pelicula seleccionada:" + "\n" + pelicula);

            Assertions.assertNotNull(pelicula);

        } catch (Exception e) { throw new RuntimeException(e); }

        // Comprobamos si la disposicion existe para la pelicula y ciudad seleccionada

        PeliculaDisposicion peliculaDisposicion;

        PeliculaDisposicionCompuesta codigo = new PeliculaDisposicionCompuesta(sala.getTeatro().getCiudad().getCodigo(), pelicula.getCodigo());

        try {
            peliculaDisposicion = peliculaDisposicionServicio.obtener(codigo).orElse(null);

            System.out.println("\n" + "Disposicion seleccionada:" + "\n" + peliculaDisposicion);

            Assertions.assertNotNull(peliculaDisposicion);

        } catch (Exception e) { throw new RuntimeException(e); }


        // Se registra la funcion, donde se le asigna el formato de la pelicula, sala, horario y pelicula.

        Funcion funcion;
        
        try {

            funcion = funcionServicio.registrar(new Funcion(FormatoPelicula.DOBLADO, sala, horario, pelicula));

            System.out.println("\n" + "Funcion registrada:" + "\n" + funcion);

            Assertions.assertNotNull(funcion);

        } catch (Exception e) { throw new RuntimeException(e); }

        // Una vez registrada la funcion, se procede a crear automaticamente la funcion esquema que contiene la distribucion de silla usada para la funcion.

        try {
            
            PeliculaDisposicion disposicionActual = peliculaDisposicionServicio.actualizar(peliculaDisposicion);

            Assertions.assertEquals("PREVENTA", disposicionActual.getEstadoPelicula().toString());

            System.out.println("\n" + "Disposicion actualizada:" + "\n" + peliculaDisposicion);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    /**
     * Para esta simulación no deberia haber intervencion humana o accion, entonces teniendo en cuenta que la aplicacion se encuentra en ejecucion esta prueba trata de que cuando se inicia creamos una función simulando que existe y que esa funcion no ha comenzado, pasado el tiempo de la fechaInicio se cambia el estado a ESTRENO.
     */
    @Test
    @Sql("classpath:dataset.sql") 
    public void cambiarPreventaEstreno() { // 🔍 Estado: ESTRENO

        // El administrador del teatro debio haber seleccionado sala de las que maneja.

        Sala sala;

        try {
            sala = salaServicio.obtener(new SalaAtributoValidator(4)).orElse(null); // Esta sala pertenece a la ciudad {1}

            System.out.println("\n" + "Sala seleccionada:" + "\n" + sala);

            Assertions.assertNotNull(sala);

        } catch (Exception e) { throw new RuntimeException(e); }

        // El horario se crea exclusivamente para la funcion deseada, donde es primero antes del registro de la funcion.
        // El horario lo ponemos en un futuro proximo para simular la existencia de una funcion existente y tambien mientras de se hace la prueba ver como cambia el estado de la disposicion al llegar el tiempo de la fechaInicio

        LocalDateTime fechaInicio = LocalDateTime.now().plusSeconds(30);
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

        } catch (Exception e) { throw new RuntimeException(e); }

        // Selecciona entre una lista la pelicula que se desea registrar en la funcion.

        Pelicula pelicula;

        try {
            pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(1)).orElse(null);

            System.out.println("\n" + "Pelicula seleccionada:" + "\n" + pelicula);

            Assertions.assertNotNull(pelicula);

        } catch (Exception e) { throw new RuntimeException(e); }

        // Comprobamos si la disposicion existe para la pelicula y ciudad seleccionada

        PeliculaDisposicion peliculaDisposicion;

        PeliculaDisposicionCompuesta codigo = new PeliculaDisposicionCompuesta(sala.getTeatro().getCiudad().getCodigo(), pelicula.getCodigo());

        try {
            peliculaDisposicion = peliculaDisposicionServicio.obtener(codigo).orElse(null);

            System.out.println("\n" + "Estado inicial: " + peliculaDisposicion.getEstadoPelicula());

            Assertions.assertEquals("PREVENTA", peliculaDisposicion.getEstadoPelicula().toString());

        } catch (Exception e) { throw new RuntimeException(e); }


        // Se registra la funcion, donde se le asigna el formato de la pelicula, sala, horario y pelicula.

        Funcion funcion;
        
        try {

            funcion = funcionServicio.registrar(new Funcion(FormatoPelicula.DOBLADO, sala, horario, pelicula));

            System.out.println("\n" + "Funcion registrada:" + "\n" + funcion);

            Assertions.assertNotNull(funcion);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            System.out.println("Esperando que comience la función a las " + fechaInicio);
            // Esperamos hasta después de la fecha de inicio
            Thread.sleep(12000); // 12 segundos
            
            // Invocamos el método automático (similar a lo que haría el scheduler)
            // Esto ejecuta la misma lógica que ejecutaría automáticamente
            peliculaDisposicionServicio.actualizarEstadoPeliculas();
            
            // Obtenemos el estado actualizado
            PeliculaDisposicion disposicionActualizada = peliculaDisposicionServicio.obtener(codigo).orElse(null);
            System.out.println("\nEstado después de actualización automática: " + disposicionActualizada.getEstadoPelicula());
            
            Assertions.assertEquals(EstadoPelicula.ESTRENO, disposicionActualizada.getEstadoPelicula());
            } catch (Exception e) {
                e.printStackTrace();
        }
    }
}
