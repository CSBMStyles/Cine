package com.unicine.test.service;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.purchase.Cupon;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.service.purchase.CuponServicio;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

/**
 * Tests unitarios para CuponServicioImp.
 * Cubre CRUD y consultas por vigencia, criterio, rango de descuento y asignaciones.
 */
@SpringBootTest
@Transactional
public class CuponServicioTest {

    @Autowired
    private CuponServicio cuponServicio;

    // 🟩 CASOS POSITIVOS

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        try {
            Cupon cupon = Cupon.builder()
                    .descripcion("Cupon del 20% de descuento")
                    .descuento(0.2)
                    .criterio("Cumpleanos")
                    .fechaVencimiento(LocalDateTime.now().plusMonths(1))
                    .build();

            Cupon registrado = cuponServicio.registrar(cupon);

            Assertions.assertNotNull(registrado);
            Assertions.assertNotNull(registrado.getCodigo());
            Assertions.assertEquals("Cupon del 20% de descuento", registrado.getDescripcion());
            Assertions.assertEquals(0.2, registrado.getDescuento());
            Assertions.assertEquals("Cumpleanos", registrado.getCriterio());

            System.out.println("\nRegistro creado correctamente:\n" + registrado);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        try {
            Cupon cupon = cuponServicio.obtener(1).orElse(null);
            Assertions.assertNotNull(cupon);

            cupon.setDescuento(0.2);

            Cupon actualizado = cuponServicio.actualizar(cupon);

            Assertions.assertEquals(0.2, actualizado.getDescuento());

            System.out.println("\nRegistro actualizado correctamente:\n" + actualizado);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        try {
            Cupon cupon = cuponServicio.obtener(1).orElse(null);

            Assertions.assertNotNull(cupon);
            Assertions.assertEquals("Primer registro", cupon.getCriterio());
            Assertions.assertEquals(0.15, cupon.getDescuento());

            System.out.println("\nRegistro encontrado:\n" + cupon);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Cupon> cupones = cuponServicio.listar();

            Assertions.assertEquals(3, cupones.size());

            System.out.println("\nListado de cupones:\n" + cupones);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        try {
            List<Cupon> cupones = cuponServicio.listarPaginado();

            Assertions.assertEquals(3, cupones.size());

            System.out.println("\nListado paginado de cupones:\n" + cupones);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarActivos() {

        try {
            List<Cupon> cupones = cuponServicio.listarActivos();

            Assertions.assertEquals(3, cupones.size());

            System.out.println("\nCupones activos encontrados:\n" + cupones);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql({"classpath:dataset.sql", "classpath:expired-cupon.sql"})
    public void listarVencidos() {

        try {
            List<Cupon> cupones = cuponServicio.listarVencidos();

            Assertions.assertEquals(1, cupones.size());
            Assertions.assertEquals("Cupon expirado para test", cupones.get(0).getCriterio());

            System.out.println("\nCupones vencidos encontrados:\n" + cupones);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void buscarPorCriterio() {

        try {
            List<Cupon> resultados = cuponServicio.buscarPorCriterio("Primer");

            Assertions.assertEquals(2, resultados.size());

            System.out.println("\nResultados de busqueda por criterio:\n" + resultados);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorRangoDescuento() {

        try {
            List<Cupon> cupones = cuponServicio.listarPorRangoDescuento(0.1, 0.2);

            Assertions.assertEquals(2, cupones.size());

            System.out.println("\nCupones dentro del rango de descuento:\n" + cupones);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarConAsignaciones() {

        try {
            List<Cupon> cupones = cuponServicio.listarConAsignaciones();

            Assertions.assertEquals(2, cupones.size());

            System.out.println("\nCupones con asignaciones:\n" + cupones);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        try {
            Cupon cupon = cuponServicio.obtener(4).orElse(null);
            Assertions.assertNotNull(cupon);

            cuponServicio.eliminar(cupon, true);

            try {
                cuponServicio.obtener(4);
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

        Integer codigo = 9999;

        try {
            cuponServicio.obtener(codigo);

            Assertions.fail("Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminarSinConfirmacion() {

        try {
            Cupon cupon = cuponServicio.obtener(1).orElse(null);
            Assertions.assertNotNull(cupon);

            cuponServicio.eliminar(cupon, false);

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
    public void buscarPorCriterioInexistente() {

        try {
            cuponServicio.buscarPorCriterio("CriterioInexistenteXYZ");

            Assertions.fail("Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarVencidosVacio() {

        try {
            cuponServicio.listarVencidos();

            Assertions.fail("Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_COUPON_NOT_FOUND.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }
}
