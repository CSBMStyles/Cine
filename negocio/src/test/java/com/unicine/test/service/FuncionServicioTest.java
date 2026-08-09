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
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.theater.Sala;
import com.unicine.enums.movie.FormatoPelicula;
import com.unicine.repository.movie.PeliculaRepo;
import com.unicine.repository.theater.SalaRepo;
import com.unicine.service.showing.FuncionEsquemaServicio;
import com.unicine.service.showing.FuncionServicio;
import com.unicine.service.showing.HorarioServicio;
import com.unicine.service.movie.PeliculaDisposicionServicio;
import com.unicine.transfer.dto.request.FuncionEsquemaRequest;
import com.unicine.transfer.dto.request.FuncionRequest;
import com.unicine.transfer.dto.request.HorarioRequest;
import com.unicine.transfer.dto.request.PeliculaDisposicionRequest;
import com.unicine.transfer.dto.response.FuncionEsquemaResponse;
import com.unicine.transfer.dto.response.FuncionResponse;
import com.unicine.transfer.dto.response.HorarioResponse;
import com.unicine.transfer.dto.response.PeliculaDisposicionResponse;

// Important: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class FuncionServicioTest {

    @Autowired
    private FuncionServicio funcionServicio;

    @Autowired
    private SalaRepo salaRepo;

    @Autowired
    private PeliculaRepo peliculaRepo;

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
            sala = salaRepo.findById(5).orElse(null);

            System.out.println("\n" + "Sala seleccionada:" + "\n" + sala);

            Assertions.assertNotNull(sala);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        // El horario se crea exclusivamente para la funcion deseada, donde es primero antes del registro de la funcion.

        LocalDateTime fechaInicio = LocalDateTime.of(2026, 12, 30, 20, 00);
        LocalDateTime fechaFin = LocalDateTime.of(2026, 12, 30, 22, 00);

        HorarioResponse horario;

        try {
            HorarioRequest horarioRequest = HorarioRequest.builder()
                    .fechaInicio(fechaInicio)
                    .fechaFin(fechaFin)
                    .build();

            Respuesta<?> repuestaHorario = horarioServicio.registrar(horarioRequest, sala.getCodigo());

            if (!repuestaHorario.isExito()) {

                Assertions.fail(repuestaHorario.getMensaje() + "\n" + repuestaHorario.getData());
            }

            horario = (HorarioResponse) repuestaHorario.getData();

            String dia = horarioServicio.obtenerDia(fechaInicio);

            System.out.println("\n" + "Horario creado:" + "\n" + horario);

            System.out.println("\n" + "Dia de la semana:" + "\n" + dia);

            Assertions.assertTrue(repuestaHorario.isExito());

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        // Selecciona entre una lista la pelicula que se desea registrar en la funcion.

        Pelicula pelicula;

        try {
            pelicula = peliculaRepo.findById(1).orElse(null);

            System.out.println("\n" + "Pelicula seleccionada:" + "\n" + pelicula);

            Assertions.assertNotNull(pelicula);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        // Comprobamos si la disposicion existe para la pelicula y ciudad seleccionada

        PeliculaDisposicionResponse peliculaDisposicion;

        Integer ciudadCodigo = sala.getTeatro().getCiudad().getCodigo();
        Integer peliculaCodigo = pelicula.getCodigo();

        try {
            peliculaDisposicion = disposicionServicio.obtener(ciudadCodigo, peliculaCodigo).orElse(null);

            System.out.println("\n" + "Disposicion seleccionada:" + "\n" + peliculaDisposicion);

            Assertions.assertNotNull(peliculaDisposicion);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }


        // Se registra la funcion, donde se le asigna el formato de la pelicula, sala, horario y pelicula.

        FuncionResponse funcion;
        
        try {

            FuncionRequest funcionRequest = FuncionRequest.builder()
                    .precio(0.0)
                    .formato(FormatoPelicula.DOBLADO)
                    .salaCodigo(sala.getCodigo())
                    .horarioCodigo(horario.getCodigo())
                    .peliculaCodigo(pelicula.getCodigo())
                    .build();

            funcion = funcionServicio.registrar(funcionRequest);

            System.out.println("\n" + "Funcion registrada:" + "\n" + funcion);

            Assertions.assertNotNull(funcion);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        // Una vez registrada la funcion, se procede a crear automaticamente la funcion esquema que contiene la distribucion de silla usada para la funcion.

        try {
            
            PeliculaDisposicionRequest request = PeliculaDisposicionRequest.builder()
                    .estadoPelicula(peliculaDisposicion.getEstadoPelicula())
                    .peliculaCodigo(peliculaDisposicion.getPelicula().getCodigo())
                    .ciudadCodigo(peliculaDisposicion.getCiudad().getCodigo())
                    .fechaFuncionInicial(peliculaDisposicion.getFechaFuncionInicial())
                    .build();

            disposicionServicio.actualizar(request);

            System.out.println("\n" + "Disposicion actualizada:" + "\n" + peliculaDisposicion);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        try {
            
            FuncionEsquemaRequest esquemaRequest = FuncionEsquemaRequest.builder()
                    .funcionCodigo(funcion.getCodigo())
                    .disponibles(0)
                    .ocupadas(0)
                    .mantenimiento(0)
                    .build();

            FuncionEsquemaResponse funcionEsquema = funcionEsquemaServicio.registrar(esquemaRequest);

            System.out.println("\n" + "Funcion esquema registrado:" + "\n" + funcionEsquema);

            Assertions.assertNotNull(funcionEsquema);


        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        // Note: El administrador cuando actualiza tiene la posibilidad de modificar la referencia del horario, sala y pelicula, entonces en una interfaz tenemos una lista y selecciona una que lo reemplaza.
        
        // Primero obtenemos la funcion a actualizar.

        FuncionResponse funcion;

        try {
            funcion = funcionServicio.obtener(1).orElse(null);

            System.out.println("\n" + "Registro encontrado:" + "\n" + funcion);

            Assertions.assertEquals(1, funcion.getCodigo());

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        boolean modificarSala = true; // Tomamos el codigo de la sala que se desea cambiar.

        boolean modificaHorario = true; // En caso que modifique el horario

        Integer salaCodigo = funcion.getSala().getCodigo();
        Integer horarioCodigo = funcion.getHorario().getCodigo();
        Integer peliculaCodigo = funcion.getPelicula().getCodigo();

        // En caso que modifique el la sala necesitamos recalcular el precio de la funcion.

        FormatoPelicula formato = FormatoPelicula.SUBTITULADO;

        if (modificarSala) {

            try {
                
                Sala sala = salaRepo.findById(3).orElse(null);

                salaCodigo = sala.getCodigo();

                System.out.println("\n" + "Sala seleccionada:" + "\n" + sala);

                Assertions.assertNotNull(sala);

            } catch (Exception e) {
                System.out.println("Mensaje de error: " + e.getMessage());

                throw new RuntimeException(e);
            }
        }

        if (modificaHorario) {

            HorarioResponse horario = funcion.getHorario();

            System.out.println("\n" + "Horario antes de modificar:" + "\n" + horario);

            try {

                LocalDateTime fechaInicio = LocalDateTime.of(2026, 12, 14, 05, 30);

                HorarioRequest horarioRequest = HorarioRequest.builder()
                        .codigo(horario.getCodigo())
                        .fechaInicio(fechaInicio)
                        .fechaFin(horario.getFechaFin())
                        .build();

                Respuesta<?> repuestaHorario = horarioServicio.actualizar(horarioRequest);
    
                if (!repuestaHorario.isExito()) {
    
                    Assertions.fail(repuestaHorario.getMensaje() + "\n" + repuestaHorario.getData());
                }
    
                HorarioResponse horarioActualizado = (HorarioResponse) repuestaHorario.getData();
    
                String dia = horarioServicio.obtenerDia(horarioActualizado.getFechaInicio());
    
                System.out.println("\n" + "Horario actualizado:" + "\n" + horarioActualizado);
    
                System.out.println("\n" + "Dia de la semana:" + "\n" + dia);
    
                horarioCodigo = horarioActualizado.getCodigo();
    
                Assertions.assertTrue(repuestaHorario.isExito());
    
            } catch (Exception e) {
                System.out.println("Mensaje de error: " + e.getMessage());

                throw new RuntimeException(e);
            }
        }

        // Comprobamos si la disposicion existe para la pelicula y ciudad seleccionada

        PeliculaDisposicionResponse peliculaDisposicion;

        Integer ciudadCodigo = funcion.getSala().getTeatro().getCiudad().getCodigo();

        try {
            peliculaDisposicion = disposicionServicio.obtener(ciudadCodigo, peliculaCodigo).orElse(null);

            System.out.println("\n" + "Disposicion seleccionada:" + "\n" + peliculaDisposicion);

            Assertions.assertNotNull(peliculaDisposicion);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        try {

            FuncionRequest funcionRequest = FuncionRequest.builder()
                    .codigo(funcion.getCodigo())
                    .precio(0.0)
                    .formato(formato)
                    .salaCodigo(salaCodigo)
                    .horarioCodigo(horarioCodigo)
                    .peliculaCodigo(peliculaCodigo)
                    .build();

            FuncionResponse actualizado = funcionServicio.actualizar(funcionRequest);

            Assertions.assertNotNull(actualizado);

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        try {
            
            PeliculaDisposicionRequest request = PeliculaDisposicionRequest.builder()
                    .estadoPelicula(peliculaDisposicion.getEstadoPelicula())
                    .peliculaCodigo(peliculaDisposicion.getPelicula().getCodigo())
                    .ciudadCodigo(peliculaDisposicion.getCiudad().getCodigo())
                    .fechaFuncionInicial(peliculaDisposicion.getFechaFuncionInicial())
                    .build();

            disposicionServicio.actualizar(request);

            System.out.println("\n" + "Disposicion actualizada:" + "\n" + peliculaDisposicion);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Integer codigoFuncion = 1;

        Integer codigoHorario;

        Integer codigoEsquema;

        FuncionResponse funcion;

        try {
            funcion = funcionServicio.obtener(codigoFuncion).orElse(null);

            System.out.println("\n" + "Registro encontrado:" + "\n" + funcion);

            System.out.println("\n" + "Horario:" + "\n" + funcion.getHorario().getCodigo());

            System.out.println("\n" + "Funcion de Esquema:" + "\n" + funcion.getFuncionEsquema().getCodigo());

            codigoHorario = funcion.getHorario().getCodigo();

            codigoEsquema = funcion.getFuncionEsquema().getCodigo();

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        try {
            funcionServicio.eliminar(codigoFuncion, true);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }

        try {
            funcionServicio.obtener(codigoFuncion);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }

        try {
            horarioServicio.obtener(codigoHorario);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }

        try {
            funcionEsquemaServicio.obtener(codigoEsquema);

        } catch (Exception e) {

            System.out.println("Mensaje de error: " + e.getMessage());

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer codigo = 1;

        try {
            FuncionResponse funcion = funcionServicio.obtener(1).orElse(null);

            Assertions.assertEquals(codigo, funcion.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + funcion);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<FuncionResponse> lista = funcionServicio.listar();

            Assertions.assertEquals(8, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        try {
            List<FuncionResponse> lista = funcionServicio.listarPaginado();

            Assertions.assertEquals(5, lista.size());

            System.out.println("\n" + "Listado de registros paginado:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());

            throw new RuntimeException(e);
        }
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
