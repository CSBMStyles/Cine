package com.unicine.test.service;


import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.api.response.Respuesta;
import com.unicine.entity.Funcion;
import com.unicine.entity.FuncionEsquema;
import com.unicine.entity.Horario;
import com.unicine.entity.Pelicula;
import com.unicine.entity.PeliculaDisposicion;
import com.unicine.entity.Sala;
import com.unicine.entity.composed.PeliculaDisposicionCompuesta;
import com.unicine.enumeration.FormatoPelicula;
import com.unicine.service.FuncionEsquemaServicio;
import com.unicine.service.FuncionServicio;
import com.unicine.service.HorarioServicio;
import com.unicine.service.PeliculaDisposicionServicio;
import com.unicine.service.PeliculaServicio;
import com.unicine.service.SalaServicio;
import com.unicine.util.validation.attributes.PeliculaAtributoValidator;
import com.unicine.util.validation.attributes.SalaAtributoValidator;

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

    @Autowired
    private PeliculaDisposicionServicio disposicionServicio;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {
        
        // El administrador del teatro debio haber seleccionado sala de las que maneja.

        Sala sala;

        try {
            sala = salaServicio.obtener(new SalaAtributoValidator(5)).orElse(null);

            System.out.println("\n" + "Sala seleccionada:" + "\n" + sala);

            Assertions.assertNotNull(sala);

        } catch (Exception e) { throw new RuntimeException(e); }

        // El horario se crea exclusivamente para la funcion deseada, donde es primero antes del registro de la funcion.

        LocalDateTime fechaInicio = LocalDateTime.of(2025, 12, 30, 20, 00);
        LocalDateTime fechaFin = LocalDateTime.of(2025, 12, 30, 22, 00);

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
            peliculaDisposicion = disposicionServicio.obtener(codigo).orElse(null);

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
            
            disposicionServicio.actualizar(peliculaDisposicion);

            System.out.println("\n" + "Disposicion actualizada:" + "\n" + peliculaDisposicion);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            
            FuncionEsquema funcionEsquema = funcionEsquemaServicio.registrar(new FuncionEsquema(funcion));

            System.out.println("\n" + "Funcion esquema registrado:" + "\n" + funcionEsquema);

            Assertions.assertNotNull(funcionEsquema);


        } catch (Exception e) { throw new RuntimeException(e); }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        // NOTE: El administrador cuando actualiza tiene la posibilidad de modificar la referencia del horario, sala y pelicula, entonces en una interfaz tenemos una lista y selecciona una que lo reemplaza.
        
        // Primero obtenemos la funcion a actualizar.

        Funcion funcion;

        try {
            funcion = funcionServicio.obtener(1).orElse(null);

            System.out.println("\n" + "Registro encontrado:" + "\n" + funcion);

            Assertions.assertEquals(1, funcion.getCodigo());

        } catch (Exception e) { throw new RuntimeException(e); }

        boolean modificarSala = true; // Tomamos el codigo de la sala que se desea cambiar.

        boolean modificaHorario = true; // En caso que modifique el horario

        // En caso que modifique el la sala necesitamos recalcular el precio de la funcion.

        funcion.setFormato(FormatoPelicula.SUBTITULADO);

        if (modificarSala) {

            try {
                
                Sala sala = salaServicio.obtener(new SalaAtributoValidator(3)).orElse(null);

                funcion.setSala(sala);

                System.out.println("\n" + "Sala seleccionada:" + "\n" + sala);

                Assertions.assertNotNull(sala);

            } catch (Exception e) { throw new RuntimeException(e); }
        }

        if (modificaHorario) {

            Horario horario = funcion.getHorario();

            System.out.println("\n" + "Horario antes de modificar:" + "\n" + horario);

            try {

                LocalDateTime fechaInicio = LocalDateTime.of(2025, 12, 14, 05, 30);

                horario.setFechaInicio(fechaInicio);

                Respuesta<?> repuestaHorario = horarioServicio.actualizar(horario);
    
                if (!repuestaHorario.isExito()) {
    
                    Assertions.fail(repuestaHorario.getMensaje() + "\n" + repuestaHorario.getData());
                }
    
                horario = (Horario) repuestaHorario.getData();
    
                String dia = horarioServicio.obtenerDia(horario.getFechaInicio());
    
                System.out.println("\n" + "Horario actualizado:" + "\n" + horario);
    
                System.out.println("\n" + "Dia de la semana:" + "\n" + dia);
    
                funcion.setHorario(horario);
    
                Assertions.assertTrue(repuestaHorario.isExito());
    
            } catch (Exception e) { throw new RuntimeException(e); }
        }

        // Comprobamos si la disposicion existe para la pelicula y ciudad seleccionada

        PeliculaDisposicion peliculaDisposicion;

        PeliculaDisposicionCompuesta codigo = new PeliculaDisposicionCompuesta(funcion.getSala().getTeatro().getCiudad().getCodigo(), funcion.getPelicula().getCodigo());

        try {
            peliculaDisposicion = disposicionServicio.obtener(codigo).orElse(null);

            System.out.println("\n" + "Disposicion seleccionada:" + "\n" + peliculaDisposicion);

            Assertions.assertNotNull(peliculaDisposicion);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {

            Funcion actualizado = funcionServicio.actualizar(funcion);

            Assertions.assertNotNull(actualizado);

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            
            disposicionServicio.actualizar(peliculaDisposicion);

            System.out.println("\n" + "Disposicion actualizada:" + "\n" + peliculaDisposicion);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Integer codigoFuncion = 1;

        Integer codigoHorario;

        Integer codigoEsquema;

        Funcion funcion;

        try {
            funcion = funcionServicio.obtener(codigoFuncion).orElse(null);

            System.out.println("\n" + "Registro encontrado:" + "\n" + funcion);

            System.out.println("\n" + "Horario:" + "\n" + funcion.getHorario().getCodigo());

            System.out.println("\n" + "Funcion de Esquema:" + "\n" + funcion.getFuncionEsquema().getCodigo());

            codigoHorario = funcion.getHorario().getCodigo();

            codigoEsquema = funcion.getFuncionEsquema().getCodigo();

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            funcionServicio.eliminar(funcion, true);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            funcionServicio.obtener(codigoFuncion);

        } catch (Exception e) {

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }

        try {
            horarioServicio.obtener(codigoHorario);

        } catch (Exception e) {

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }

        try {
            funcionEsquemaServicio.obtener(codigoEsquema);

        } catch (Exception e) {

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer codigo = 1;

        try {
            Funcion funcion = funcionServicio.obtener(1).orElse(null);

            Assertions.assertEquals(codigo, funcion.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + funcion);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Funcion> lista = funcionServicio.listar();

            Assertions.assertEquals(7, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        try {
            List<Funcion> lista = funcionServicio.listarPaginado();

            Assertions.assertEquals(5, lista.size());

            System.out.println("\n" + "Listado de registros paginado:");

            lista.forEach(System.out::println);

        } catch (Exception e) { throw new RuntimeException(e); }
    }
    
    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerDiasSemana() {
        // Crear fechas para cada día de la semana en 2025
        LocalDateTime lunes = LocalDateTime.now(ZoneId.of("America/Bogota"));     // Lunes
        LocalDateTime martes = lunes.plusDays(1);                                   // Martes
        LocalDateTime miercoles = martes.plusDays(1);                                // Miércoles
        LocalDateTime jueves = miercoles.plusDays(1);                                   // Jueves
        LocalDateTime viernes = jueves.plusDays(1);                                  // Viernes
        LocalDateTime sabado = viernes.plusDays(1);                                   // Sábado
        LocalDateTime domingo = sabado.plusDays(1);                                  // Domingo
        
        // Mostrar los días y sus descuentos usando un ciclo for
        String[] diasNombres = {"1", "2", "3", "4", "5", "6", "7"};

        LocalDateTime[] fechas = {lunes, martes, miercoles, jueves, viernes, sabado, domingo};
        
        for (int i = 0; i < diasNombres.length; i++) {
            imprimirDiaDescuento(diasNombres[i], fechas[i]);
        }

    }
    
    private void imprimirDiaDescuento(String nombreDia, LocalDateTime fecha) {

        String codigoDia = horarioServicio.obtenerDia(fecha);

        Double descuento = horarioServicio.obtenerDescuentoDia(fecha);

        System.out.printf("%s: codigo = '%s', descuento = %s%n", 
                         nombreDia, codigoDia, descuento != null ? descuento * 100 + "%" : "null");
    }
}
