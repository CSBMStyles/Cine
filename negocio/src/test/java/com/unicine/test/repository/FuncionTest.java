package com.unicine.test.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.unicine.entity.Funcion;
import com.unicine.entity.Horario;
import com.unicine.entity.Pelicula;
import com.unicine.entity.Sala;
import com.unicine.enumeration.FormatoPelicula;
import com.unicine.repository.FuncionRepo;
import com.unicine.repository.HorarioRepo;
import com.unicine.repository.PeliculaRepo;
import com.unicine.repository.SalaRepo;
import com.unicine.transfer.data.DetalleFuncionesDTO;
import com.unicine.transfer.mapper.DetalleFuncionMapper;
import com.unicine.transfer.projetion.DetalleFuncionesProjection;

@DataJpaTest
@Import(DetalleFuncionMapper.class)
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

    @Autowired
    private DetalleFuncionMapper funcionMapper;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Sala sala = salaRepo.findById(1).orElse(null);

        Horario horario = horarioRepo.findById(1).orElse(null);

        Pelicula pelicula = peliculaRepo.findById(1).orElse(null);

        Funcion funcion = new Funcion(FormatoPelicula.DOBLADO, sala, horario, pelicula);
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

        List<Funcion> funciones = funcionRepo.listarFuncionesCiudad(4);

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
        List<DetalleFuncionesProjection> detalle = funcionRepo.listarDetallesFunciones(1);

        Assertions.assertEquals(1, detalle.size());

        // Mapeamos manualmente cada proyección a nuestro DTO
        List<DetalleFuncionesDTO> dtos = detalle.stream().map(funcionMapper::convertirDTO).collect(Collectors.toList());

        System.out.println("\nListado de detalles de funciones (DTO):");
        
        dtos.forEach(dto -> System.out.println(dto));                                           
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

        List<Funcion> funciones = funcionRepo.listarFuncionesTeatroFecha(1, LocalDateTime.now(ZoneId.of("America/Bogota")), fechaFin);

        Assertions.assertEquals(1, funciones.size());

        System.out.println("\n" + "Listado de funciones por teatro y fecha:");

        for (Funcion f : funciones) {
            System.out.println(f);
        }
    }

}
