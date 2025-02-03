package com.unicine.test.servicio;


import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entidades.Funcion;
import com.unicine.entidades.FuncionEsquema;
import com.unicine.entidades.Horario;
import com.unicine.entidades.Pelicula;
import com.unicine.entidades.Sala;
import com.unicine.servicio.FuncionEsquemaServicio;
import com.unicine.servicio.SalaServicio;
import com.unicine.servicio.FuncionServicio;
import com.unicine.servicio.HorarioServicio;
import com.unicine.servicio.PeliculaServicio;
import com.unicine.util.emuns.FormatoPelicula;
import com.unicine.util.message.HorarioRespuesta;
import com.unicine.util.validacion.atributos.PeliculaAtributoValidator;
import com.unicine.util.validacion.atributos.SalaAtributoValidator;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class FuncionServicioTest {

    @Autowired
    private FuncionServicio funcionServicio;

    @Autowired
    private SalaServicio salaServicio;

    @Autowired
    private PeliculaServicio peliculaServicio;

    @Autowired
    private HorarioServicio horarioServicio;

    @Autowired
    private FuncionEsquemaServicio funcionEsquemaServicio;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {
        
        // El administrador del teatro debio haber seleccionado sala de las que maneja.

        Sala sala;

        Double precioBase;

        try {
            sala = salaServicio.obtener(new SalaAtributoValidator(5)).orElse(null);

            precioBase = salaServicio.obtenerPrecioBase(sala.getTipoSala());

            System.out.println("\n" + "Sala seleccionada:" + "\n" + sala);
            System.out.println("\n" + "Precio base de la sala:" + "\n" + precioBase);

            Assertions.assertNotNull(sala);

        } catch (Exception e) {

            Assertions.fail(e);

            throw new RuntimeException(e);
        }

        // El horario se crea exclusivamente para la funcion deseada, donde es primero antes del registro de la funcion.

        LocalDateTime fechaInicio = LocalDateTime.of(2025, 12, 30, 20, 00);
        LocalDateTime fechaFin = LocalDateTime.of(2025, 12, 30, 22, 00);

        Horario horario = null;

        Double descuento = 0.0;

        try {
            HorarioRespuesta<?> repuestaHorario = horarioServicio.registrar(new Horario(fechaInicio, fechaFin), sala);

            if (!repuestaHorario.isExito()) {

                Assertions.fail(repuestaHorario.getMensaje() + "\n" + repuestaHorario.getData());
            }

            horario = (Horario) repuestaHorario.getData();

            String dia = horarioServicio.obtenerDia(fechaInicio);

            descuento = horarioServicio.obtenerDescuentoDia(horario);

            System.out.println("\n" + "Horario creado:" + "\n" + horario);

            System.out.println("\n" + "Dia de la semana:" + "\n" + dia);

            System.out.println("\n" + "Descuento del dia:" + "\n" + descuento);

            Assertions.assertTrue(repuestaHorario.isExito());

        } catch (Exception e) {
            
            Assertions.fail(e);

            throw new RuntimeException(e);
        }

        // Selecciona entre una lista la pelicula que se desea registrar en la funcion.

        Pelicula pelicula;

        try {
            pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(1)).orElse(null);

            System.out.println("\n" + "Pelicula seleccionada:" + "\n" + pelicula);

            Assertions.assertNotNull(pelicula);

        } catch (Exception e) {

            Assertions.fail(e);

            throw new RuntimeException(e);
        }

        Funcion funcion;
        
        try {

            Double precio = funcionServicio.calcularPrecio(precioBase, descuento);

            funcion = funcionServicio.registrar(new Funcion(precio, FormatoPelicula.DOBLADO, sala, horario, pelicula));

            System.out.println("\n" + "Funcion registrada:" + "\n" + funcion);

            Assertions.assertNotNull(funcion);

        } catch (Exception e) {

            Assertions.fail(e);

            throw new RuntimeException(e);
        }

        // Una vez registrada la funcion, se procede a crear automaticamente la funcion esquema que contiene la distribucion de silla usada para la funcion.

        try {
            
            FuncionEsquema funcionEsquema = funcionEsquemaServicio.registrar(new FuncionEsquema("", 0, 0, 0, funcion));

            System.out.println("\n" + "Funcion esquema registrado:" + "\n" + funcionEsquema);

            Assertions.assertNotNull(funcionEsquema);


        } catch (Exception e) {

            Assertions.fail(e);

            throw new RuntimeException(e);
        }
    }

/*     @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Integer codigo = 1;

        TeatroAtributoValidator validator = new TeatroAtributoValidator(codigo.toString());

        Teatro teatro;

        try {
            teatro = funcionServicio.obtener(validator).orElse(null);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            funcionServicio.eliminar(teatro, true);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            funcionServicio.obtener(validator);

        } catch (Exception e) {
            // Realizamos una validacion de la prueba para aceptar que el teatro fue eliminado mendiante la excepcion del metodo de obtener
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer codigo = 1;

        try {
            Teatro teatro = funcionServicio.obtener(new TeatroAtributoValidator(codigo.toString())).orElse(null);

            Assertions.assertEquals(codigo, teatro.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + teatro);

        } catch (Exception e) {
            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    } */

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Funcion> lista = funcionServicio.listar();

            Assertions.assertEquals(7, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }
}
