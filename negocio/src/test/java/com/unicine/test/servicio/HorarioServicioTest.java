package com.unicine.test.servicio;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.dto.FuncionInterseccionDTO;
import com.unicine.entidades.Horario;
import com.unicine.entidades.Sala;
import com.unicine.servicio.HorarioServicio;
import com.unicine.servicio.SalaServicio;
import com.unicine.util.message.Respuesta;
import com.unicine.util.validaciones.atributos.SalaAtributoValidator;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class HorarioServicioTest {

    @Autowired
    private HorarioServicio horarioServicio;

    @Autowired
    private SalaServicio salaServicio;

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

            System.out.println("Sala encontrada: " + sala);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            Respuesta<?> actualizado = horarioServicio.registrar(horario, sala);

            if (actualizado.getData() instanceof FuncionInterseccionDTO) {

                FuncionInterseccionDTO funcionRespuesta = (FuncionInterseccionDTO) actualizado.getData();

                System.out.println("Mensaje: " + actualizado.getMensaje() + "\n" + "Función: " + funcionRespuesta + "\n" + "Exito: " + actualizado.isExito());

            }
            
            if (actualizado.getData() instanceof Horario) {

                Horario horarioRespuesta = (Horario) actualizado.getData();

                System.out.println("Mensaje: " + actualizado.getMensaje() + "\n" + "Horario: " + horarioRespuesta + "\n" + "Exito: " + actualizado.isExito());
            }

            Assertions.assertTrue(actualizado.isExito());

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        LocalDateTime fechaInicio = LocalDateTime.of(2025, 12, 24, 15, 00);
        LocalDateTime fechaFin = LocalDateTime.of(2025, 12, 24, 16, 00);

        Horario horario;

        try {
            horario = horarioServicio.obtener(7).orElse(null);

            horario.setFechaInicio(fechaInicio);
            horario.setFechaFin(fechaFin);

        } catch (Exception e) { throw new RuntimeException(e); }
        
        try {
            Respuesta<?> actualizado = horarioServicio.actualizar(horario);

            if (actualizado.getData() instanceof FuncionInterseccionDTO) {

                FuncionInterseccionDTO funcionRespuesta = (FuncionInterseccionDTO) actualizado.getData();

                System.out.println("Mensaje: " + actualizado.getMensaje() + "\n" + "Función: " + funcionRespuesta + "\n" + "Exito: " + actualizado.isExito());

            }
            
            if (actualizado.getData() instanceof Horario) {

                Horario horarioRespuesta = (Horario) actualizado.getData();

                System.out.println("Mensaje: " + actualizado.getMensaje() + "\n" + "Horario: " + horarioRespuesta + "\n" + "Exito: " + actualizado.isExito());
            }

            Assertions.assertTrue(actualizado.isExito());

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Horario horario;

        try {
            horario = horarioServicio.obtener(1).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            horarioServicio.eliminar(horario, true);

        } catch (Exception e) { throw new RuntimeException(e); }

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

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Horario> lista = horarioServicio.listar();

            Assertions.assertEquals(7, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // 🟥

    @ParameterizedTest
    @CsvSource({
        // Caso 1: Intervalo completamente dentro del horario existente {17:00 - 18:00}
        "'2025-12-22T17:30:00','2025-12-22T17:59:00'",
        // Caso 2: La hora de inicio es anterior, pero el final se cruza {final en 17:30]
        "'2025-12-22T16:30:00','2025-12-22T17:30:00'",
        // Caso 3: La hora de inicio se cruza, pero el final es posterior {inicia a 17:30 y final posterior a 18:00}
        "'2025-12-22T17:30:00','2025-12-22T18:30:00'"
    })
    @Sql("classpath:dataset.sql")
    public void registrarSolapado(String inicioStr, String finStr) {
    
        // Se parsean las cadenas recibidas a LocalDateTime
        LocalDateTime fechaInicio = LocalDateTime.parse(inicioStr);
        LocalDateTime fechaFin = LocalDateTime.parse(finStr);
    
        // Se crea el objeto Horario con los tiempos parametrizados
        Horario horario = new Horario(fechaInicio, fechaFin);
    
        Sala sala;
        try {
            // Se obtiene la sala usando el validator en este caso se usa el id 2 para ejemplificar
            sala = salaServicio.obtener(new SalaAtributoValidator(2)).orElse(null);

            System.out.println("Sala encontrada: " + sala);

        } catch (Exception e) { throw new RuntimeException(e); }
    
        try {
            // Se registra el horario y se obtiene la respuesta
            Respuesta<?> actualizado = horarioServicio.registrar(horario, sala);
    
            // Se imprime la respuesta según el tipo de objeto devuelto
            if (actualizado.getData() instanceof FuncionInterseccionDTO) {

                FuncionInterseccionDTO funcionRespuesta = (FuncionInterseccionDTO) actualizado.getData();

                System.out.println("Mensaje: " + actualizado.getMensaje() + "\n" + "Función: " + funcionRespuesta + "\n" + "Exito: " + actualizado.isExito());
            }
            if (actualizado.getData() instanceof Horario) {

                Horario horarioRespuesta = (Horario) actualizado.getData();

                System.out.println("Mensaje: " + actualizado.getMensaje() + "\n" + "Horario: " + horarioRespuesta + "\n" + "Exito: " + actualizado.isExito());
            }
    
            // Se espera que la registración falle por solapamiento, por lo que se verifica que exito es false
            Assertions.assertFalse(actualizado.isExito());

        } catch (Exception e) {

            Assertions.fail(e);

            throw new RuntimeException(e);
        }
    }
}
