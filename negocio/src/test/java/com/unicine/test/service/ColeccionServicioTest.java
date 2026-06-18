package com.unicine.test.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.movie.Coleccion;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.user.Cliente;
import com.unicine.enums.movie.EstadoPropio;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.service.movie.ColeccionServicio;
import com.unicine.util.validation.catalog.domain.MovieErrorCatalog;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

/**
 * Tests unitarios para ColeccionServicioImp.
 * Cubre CRUD con clave compuesta y metodos de negocio
 * como puntuaciones, estados y consultas por cliente o pelicula.
 */
@SpringBootTest
@Transactional
public class ColeccionServicioTest {

    @Autowired
    private ColeccionServicio coleccionServicio;

    // 🟩 CASOS POSITIVOS

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Integer cedula = 1009000011;
        Integer codigoPelicula = 2;

        Cliente cliente = Cliente.builder().cedula(cedula).build();
        Pelicula pelicula = new Pelicula();
        pelicula.setCodigo(codigoPelicula);

        Coleccion coleccion = Coleccion.builder()
                .puntuacion(5.0)
                .estadoPeliculaPropio(EstadoPropio.FAVORITO)
                .cliente(cliente)
                .pelicula(pelicula)
                .build();

        try {
            Coleccion registrada = coleccionServicio.registrar(coleccion);

            Assertions.assertNotNull(registrada);
            Assertions.assertEquals(cedula, registrada.getCliente().getCedula());
            Assertions.assertEquals(codigoPelicula, registrada.getPelicula().getCodigo());
            Assertions.assertEquals(5.0, registrada.getPuntuacion());
            Assertions.assertEquals(EstadoPropio.FAVORITO, registrada.getEstadoPeliculaPropio());

            System.out.println("\nRegistro creado correctamente:\n" + registrada);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Integer cedula = 1009000011;
        Integer codigoPelicula = 1;

        try {
            Coleccion coleccion = coleccionServicio.obtener(cedula, codigoPelicula).orElse(null);
            Assertions.assertNotNull(coleccion);
            Assertions.assertEquals(4.0, coleccion.getPuntuacion());

            coleccion.setPuntuacion(2.0);
            coleccion.setEstadoPeliculaPropio(EstadoPropio.EN_ESPERA);

            Coleccion actualizada = coleccionServicio.actualizar(coleccion);

            Assertions.assertEquals(2.0, actualizada.getPuntuacion());
            Assertions.assertEquals(EstadoPropio.EN_ESPERA, actualizada.getEstadoPeliculaPropio());

            System.out.println("\nRegistro actualizado correctamente:\n" + actualizada);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer cedula = 1009000011;
        Integer codigoPelicula = 1;

        try {
            Coleccion coleccion = coleccionServicio.obtener(cedula, codigoPelicula).orElse(null);

            Assertions.assertNotNull(coleccion);
            Assertions.assertEquals(4.0, coleccion.getPuntuacion());
            Assertions.assertEquals(EstadoPropio.VISTO, coleccion.getEstadoPeliculaPropio());

            System.out.println("\nRegistro encontrado:\n" + coleccion);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Coleccion> colecciones = coleccionServicio.listar();

            Assertions.assertEquals(5, colecciones.size());

            System.out.println("\nListado de colecciones:\n" + colecciones);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        try {
            List<Coleccion> colecciones = coleccionServicio.listarPaginado();

            Assertions.assertEquals(5, colecciones.size());

            System.out.println("\nListado paginado de colecciones:\n" + colecciones);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorCliente() {

        Integer cedula = 1008000022;

        try {
            List<Coleccion> colecciones = coleccionServicio.listarPorCliente(cedula);

            Assertions.assertEquals(2, colecciones.size());

            System.out.println("\nColecciones del cliente " + cedula + ":\n" + colecciones);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorPelicula() {

        Integer codigoPelicula = 3;

        try {
            List<Coleccion> colecciones = coleccionServicio.listarPorPelicula(codigoPelicula);

            Assertions.assertEquals(2, colecciones.size());

            System.out.println("\nColecciones de la pelicula " + codigoPelicula + ":\n" + colecciones);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerPuntuacionPromedioPelicula() {

        Integer codigoPelicula = 1;

        try {
            Double promedio = coleccionServicio.obtenerPuntuacionPromedioPelicula(codigoPelicula);

            Double esperado = 3.5;
            Assertions.assertEquals(esperado, promedio);

            System.out.println("\nPuntuacion promedio de la pelicula " + codigoPelicula + ": " + promedio);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void contarColeccionesCliente() {

        Integer cedula = 1009000011;

        try {
            Long cantidad = coleccionServicio.contarColeccionesCliente(cedula);

            Assertions.assertEquals(1L, cantidad);

            System.out.println("\nColecciones del cliente " + cedula + ": " + cantidad);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void calificarPelicula() {

        Integer cedula = 1009000011;
        Integer codigoPelicula = 1;
        Double nuevaPuntuacion = 1.0;

        try {
            Coleccion actualizada = coleccionServicio.calificarPelicula(cedula, codigoPelicula, nuevaPuntuacion);

            Assertions.assertEquals(nuevaPuntuacion, actualizada.getPuntuacion());

            System.out.println("\nPelicula calificada correctamente:\n" + actualizada);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void cambiarEstadoPelicula() {

        Integer cedula = 1009000011;
        Integer codigoPelicula = 1;
        EstadoPropio nuevoEstado = EstadoPropio.EN_ESPERA;

        try {
            Coleccion actualizada = coleccionServicio.cambiarEstadoPelicula(cedula, codigoPelicula, nuevoEstado);

            Assertions.assertEquals(nuevoEstado, actualizada.getEstadoPeliculaPropio());

            System.out.println("\nEstado cambiado correctamente:\n" + actualizada);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Integer cedula = 1009000011;
        Integer codigoPelicula = 1;

        try {
            Coleccion coleccion = coleccionServicio.obtener(cedula, codigoPelicula).orElse(null);
            Assertions.assertNotNull(coleccion);

            coleccionServicio.eliminar(coleccion, true);

            // Verificar que ya no existe
            try {
                coleccionServicio.obtener(cedula, codigoPelicula);
                Assertions.fail("Deberia lanzar ResourceNotFoundException");
            } catch (ResourceNotFoundException e) {
                System.out.println("\nRegistro eliminado correctamente");
            }

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    // 🟥 CASOS NEGATIVOS

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerInexistente() {

        Integer cedula = 9999;
        Integer codigoPelicula = 9999;

        try {
            coleccionServicio.obtener(cedula, codigoPelicula);

            Assertions.fail("Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COLLECTION_NOT_FOUND.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminarSinConfirmacion() {

        Integer cedula = 1009000011;
        Integer codigoPelicula = 1;

        try {
            Coleccion coleccion = coleccionServicio.obtener(cedula, codigoPelicula).orElse(null);
            Assertions.assertNotNull(coleccion);

            coleccionServicio.eliminar(coleccion, false);

            Assertions.fail("Deberia lanzar RuntimeException por falta de confirmacion");

        } catch (RuntimeException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertTrue(e.getMessage().contains("confirmada"));

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorClienteInexistente() {

        Integer cedula = 9999;

        try {
            coleccionServicio.listarPorCliente(cedula);

            Assertions.fail("Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(UserErrorCatalog.DOMAIN_USER_ENTITY_CLIENT_NOT_FOUND.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorPeliculaSinColecciones() {

        Integer codigoPelicula = 5;

        try {
            coleccionServicio.listarPorPelicula(codigoPelicula);

            Assertions.fail("Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_COLLECTION_NOT_FOUND.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }
}
