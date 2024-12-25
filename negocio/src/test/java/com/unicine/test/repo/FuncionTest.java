package com.unicine.test.repo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.unicine.dto.DetalleFuncionesDTO;
import com.unicine.entidades.Funcion;
import com.unicine.entidades.Horario;
import com.unicine.entidades.Pelicula;
import com.unicine.entidades.Sala;
import com.unicine.repo.FuncionRepo;
import com.unicine.repo.HorarioRepo;
import com.unicine.repo.PeliculaRepo;
import com.unicine.repo.SalaRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FuncionTest {

    /**
     * NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado
     */

    @Autowired
    private FuncionRepo funcionRepo;

    @Autowired
    private SalaRepo salaRepo;

    @Autowired
    private HorarioRepo horarioRepo;

    @Autowired
    private PeliculaRepo peliculaRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Sala sala = salaRepo.findById(1).orElse(null);

        Horario horario = horarioRepo.findById(1).orElse(null);

        Pelicula pelicula = peliculaRepo.findById(1).orElse(null);

        Funcion funcion = new Funcion(6000.00, sala, horario, pelicula);
        funcion.setCodigo(8);

        Funcion guardado = funcionRepo.save(funcion);

        Assertions.assertNotNull(guardado);

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Funcion guardado = funcionRepo.findById(1).orElse(null);

        System.out.println(guardado);

        guardado.setPrecio(8000.00);

        Funcion actualizado = funcionRepo.save(guardado);

        Assertions.assertEquals(8000, actualizado.getPrecio());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Funcion buscado = funcionRepo.findById(1).orElse(null);

        System.out.println(buscado);

        funcionRepo.delete(buscado);

        Funcion verificacion = funcionRepo.findById(1).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Funcion> buscado = funcionRepo.findById(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Funcion> funciones = funcionRepo.findAll();

        Assertions.assertEquals(7, funciones.size());

        System.out.println("\n" + "Listado de registros:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Funcion> funciones = funcionRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, funciones.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Funcion> funciones = funcionRepo.findAll(Sort.by("codigo").ascending());

        Assertions.assertEquals(7, funciones.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

    // SECTION: Consultas personalizadas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerFuncionesHorarioSala() {

        Horario horario = horarioRepo.findById(1).orElse(null);

        List<Funcion> funciones = funcionRepo.obtenerFuncionesHorarioSala(6, horario);

        Assertions.assertEquals(1, funciones.size());

        System.out.println("\n" + "Listado de funciones por sala y horario:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarFuncionesSala() {

        List<Funcion> funciones = funcionRepo.listarFuncionesSala(1);

        Assertions.assertEquals(1, funciones.size());

        System.out.println("\n" + "Listado de funciones por sala:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarFuncionesTeatro() {

        List<Funcion> funciones = funcionRepo.listarFuncionesTeatro(1);

        Assertions.assertEquals(1, funciones.size());

        System.out.println("\n" + "Listado de funciones por teatro:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarFuncionesCiudad() {

        List<Funcion> funciones = funcionRepo.listarFuncionesCiudad(2);

        Assertions.assertEquals(2, funciones.size());

        System.out.println("\n" + "Listado de funciones por ciudad:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void verificarDisponibilidad() {

        List<Funcion> funciones = funcionRepo.verificarDisponibilidad(1);

        Assertions.assertFalse(funciones.isEmpty());

        System.out.println("\n" + "Funciones disponibles:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarDetallesFunciones() {
        List<DetalleFuncionesDTO> detalle = funcionRepo.listarDetallesFunciones(1);

        Assertions.assertEquals(1, detalle.size());

        System.out.println("\n" + "Listado de detalles de funciones:");

        detalle.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void funcionesComprasVaciasTeatro() {

        List<Funcion> funciones = funcionRepo.funcionesComprasVaciasTeatro(1);

        Assertions.assertEquals(1, funciones.size());

        System.out.println("\n" + "Listado de funciones con compras vacias de un teatro:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarFuncionesTeatroFecha() {

        LocalDate fechaRecibida = LocalDate.of(2024, 12, 15);

        LocalDateTime fechaFin = fechaRecibida.atStartOfDay().plusDays(1);

        List<Funcion> funciones = funcionRepo.listarFuncionesTeatroFecha(1, LocalDateTime.now(), fechaFin);

        Assertions.assertEquals(1, funciones.size());

        System.out.println("\n" + "Listado de funciones por teatro y fecha:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

}
