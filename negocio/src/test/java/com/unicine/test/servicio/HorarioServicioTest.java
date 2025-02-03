package com.unicine.test.servicio;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entidades.Funcion;
import com.unicine.entidades.Horario;
import com.unicine.entidades.Sala;
import com.unicine.repo.FuncionRepo;
import com.unicine.servicio.HorarioServicio;
import com.unicine.servicio.SalaServicio;
import com.unicine.util.message.HorarioRespuesta;
import com.unicine.util.validacion.atributos.SalaAtributoValidator;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class HorarioServicioTest {

    @Autowired
    private HorarioServicio horarioServicio;

    @Autowired
    private SalaServicio salaServicio;

    @Autowired
    private FuncionRepo funcionRepo;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        LocalDateTime fechaInicio = LocalDateTime.of(2025, 12, 30, 20, 00);
        LocalDateTime fechaFin = LocalDateTime.of(2025, 12, 30, 22, 00);

        Horario horario = new Horario(fechaInicio, fechaFin);

        Sala sala;

        try {
            sala = salaServicio.obtener(new SalaAtributoValidator(2)).orElse(null);

            System.out.println("Sala: " + sala);
        } catch (Exception e) {

            Assertions.fail(e);

            throw new RuntimeException(e);
        }

        try {
            HorarioRespuesta<?> nuevo = horarioServicio.registrar(horario, sala);

            if (nuevo.getData() instanceof Funcion) {

                Funcion funcionRespuesta = (Funcion) nuevo.getData();

                System.out.println("Mensaje: " + nuevo.getMensaje() + "\n" + "Función: " + funcionRespuesta + "\n" + "Exito: " + nuevo.isExito());

            }
            
            if (nuevo.getData() instanceof Horario) {

                Horario horarioRespuesta = (Horario) nuevo.getData();

                System.out.println("Mensaje: " + nuevo.getMensaje() + "\n" + "Horario: " + horarioRespuesta + "\n" + "Exito: " + nuevo.isExito());
            }

            Assertions.assertTrue(nuevo.isExito());

        } catch (Exception e) {

            Assertions.fail(e);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarSolapado() {

        LocalDateTime fechaInicio = LocalDateTime.of(2025, 12, 22, 17, 30, 00);
        LocalDateTime fechaFin = LocalDateTime.of(2025, 12, 22, 17, 59, 00);

        Horario horario = new Horario(fechaInicio, fechaFin);

        Sala sala;

        try {
            sala = salaServicio.obtener(new SalaAtributoValidator(2)).orElse(null);

            System.out.println("Sala: " + sala);
        } catch (Exception e) {

            Assertions.fail(e);

            throw new RuntimeException(e);
        }

        // TODO: Comprobar implementacion de DTO

        try {
            HorarioRespuesta<?> nuevo = horarioServicio.registrar(horario, sala);

            if (nuevo.getData() instanceof Funcion) {

                Funcion funcionRespuesta = (Funcion) nuevo.getData();

                System.out.println("Mensaje: " + nuevo.getMensaje() + "\n" + "Función: " + funcionRespuesta + "\n" + "Exito: " + nuevo.isExito());

            }
            
            if (nuevo.getData() instanceof Horario) {

                Horario horarioRespuesta = (Horario) nuevo.getData();

                System.out.println("Mensaje: " + nuevo.getMensaje() + "\n" + "Horario: " + horarioRespuesta + "\n" + "Exito: " + nuevo.isExito());
            }

            Assertions.assertFalse(nuevo.isExito());

        } catch (Exception e) {

            Assertions.fail(e);

            throw new RuntimeException(e);
        }
    }

    /* @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        String nombre = "P-01: Premier Dorada";

        try{
            Horario horario = horarioServicio.obtener(7).orElse(null);

            horario.setFechaInicio(nombre);

            Horario actualizado = horarioServicio.actualizar(horario);

            Assertions.assertEquals(nombre, actualizado.getNombre());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    } */

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Horario horario;

        try {
            horario = horarioServicio.obtener(1).orElse(null);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            horarioServicio.eliminar(horario, true);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            horarioServicio.obtener(1);

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
            Horario horario = horarioServicio.obtener(codigo).orElse(null);

            Assertions.assertEquals(codigo, horario.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + horario);

        } catch (Exception e) {
            Assertions.assertTrue(true);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Horario> lista = horarioServicio.listar();

            Assertions.assertEquals(7, lista.size());

            System.out.println("\n" + "Listado de registros:");

            LocalDateTime horaActual = LocalDateTime.now(ZoneId.of("America/Bogota"));

            System.out.println("Hora actual: " + horaActual);

            lista.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }
}
