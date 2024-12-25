package com.unicine.test.repo;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import com.unicine.entidades.Horario;
import com.unicine.repo.HorarioRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class HorarioTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private HorarioRepo horarioRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        LocalDateTime fechaInicio = LocalDateTime.of(2024, 12, 30, 20, 00);
        LocalDateTime fechaFin = LocalDateTime.of(2024, 12, 30, 22, 00);

        Horario horario = new Horario(fechaInicio, fechaFin);
        horario.setCodigo(7);

        Horario guardado = horarioRepo.save(horario);

        Assertions.assertNotNull(guardado);

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Horario guardado = horarioRepo.findById(1).orElse(null);

        System.out.println(guardado);

        LocalDateTime fechaInicio = LocalDateTime.of(2024, 12, 10, 16, 00);
        guardado.setFechaInicio(fechaInicio);

        Horario actualizado = horarioRepo.save(guardado);

        Assertions.assertEquals("2024-12-10T16:00", actualizado.getFechaInicio().toString());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Horario buscado = horarioRepo.findById(1).orElse(null);

        System.out.println(buscado);

        horarioRepo.delete(buscado);

        Horario verificacion = horarioRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Horario> buscado = horarioRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerDiaHora() {

        LocalDateTime fechaInicio = LocalDateTime.of(2024, 12, 30, 20, 00);

        // Crear un formateador con tres letras para el día
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE", Locale.of("es"));

        // Formatear la fecha, obtener el día y convertir a mayúsculas
        String dia = fechaInicio.format(formatter).toUpperCase();
        // Obtener la hora y los minutos
        LocalTime horaMinutos = fechaInicio.toLocalTime();

        System.out.println("Día de la semana: " + dia);
        
        System.out.println("Hora y minutos: " + horaMinutos);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Horario> horarios = horarioRepo.findAll();

        Assertions.assertEquals(6, horarios.size());

        System.out.println("\n" + "Listado de registros:");

        for (Horario h : horarios) {
            System.out.println(h);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Horario> horarios = horarioRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, horarios.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Horario h : horarios) {
            System.out.println(h);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Horario> horarios = horarioRepo.findAll(Sort.by("criterio"));

        Assertions.assertEquals(6, horarios.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Horario h : horarios) {
            System.out.println(h);
        }
    }
}
