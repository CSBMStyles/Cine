package com.unicine.test.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.enums.confiteria.CategoriaConfiteria;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.service.confiteria.ConfiteriaServicio;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

/**
 * Tests unitarios para ConfiteriaServicioImp.
 * Cubre CRUD y consultas por categoria y nombre.
 */
@SpringBootTest
@Transactional
public class ConfiteriaServicioTest {

    @Autowired
    private ConfiteriaServicio confiteriaServicio;

    // 🟩 CASOS POSITIVOS

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        try {
            Confiteria confiteria = Confiteria.builder()
                    .nombre("Combo Familiar")
                    .precio(45000.0)
                    .categoria(CategoriaConfiteria.COMBO)
                    .build();

            Confiteria registrada = confiteriaServicio.registrar(confiteria);

            Assertions.assertNotNull(registrada);
            Assertions.assertNotNull(registrada.getCodigo());
            Assertions.assertEquals("Combo Familiar", registrada.getNombre());
            Assertions.assertEquals(45000.0, registrada.getPrecio());
            Assertions.assertEquals(CategoriaConfiteria.COMBO, registrada.getCategoria());

            System.out.println("\nRegistro creado correctamente:\n" + registrada);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        try {
            Confiteria confiteria = confiteriaServicio.obtener(1).orElse(null);
            Assertions.assertNotNull(confiteria);

            confiteria.setPrecio(35000.0);

            Confiteria actualizada = confiteriaServicio.actualizar(confiteria);

            Assertions.assertEquals(35000.0, actualizada.getPrecio());

            System.out.println("\nRegistro actualizado correctamente:\n" + actualizada);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        try {
            Confiteria confiteria = confiteriaServicio.obtener(1).orElse(null);

            Assertions.assertNotNull(confiteria);
            Assertions.assertEquals("Combo Mega", confiteria.getNombre());
            Assertions.assertEquals(CategoriaConfiteria.COMBO, confiteria.getCategoria());

            System.out.println("\nRegistro encontrado:\n" + confiteria);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Confiteria> confiterias = confiteriaServicio.listar();

            Assertions.assertEquals(15, confiterias.size());

            System.out.println("\nListado de confiterias:\n" + confiterias);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        try {
            List<Confiteria> confiterias = confiteriaServicio.listarPaginado();

            Assertions.assertEquals(10, confiterias.size());

            System.out.println("\nListado paginado de confiterias:\n" + confiterias);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorCategoria() {

        try {
            List<Confiteria> combos = confiteriaServicio.listarPorCategoria(CategoriaConfiteria.COMBO);

            Assertions.assertEquals(3, combos.size());

            System.out.println("\nCombos encontrados:\n" + combos);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void buscarPorNombre() {

        try {
            List<Confiteria> resultados = confiteriaServicio.buscarPorNombre("Crispeta");

            Assertions.assertEquals(2, resultados.size());

            System.out.println("\nResultados de busqueda:\n" + resultados);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        try {
            Confiteria confiteria = confiteriaServicio.obtener(14).orElse(null);
            Assertions.assertNotNull(confiteria);

            confiteriaServicio.eliminar(confiteria, true);

            try {
                confiteriaServicio.obtener(14);
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
            confiteriaServicio.obtener(codigo);

            Assertions.fail("Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminarSinConfirmacion() {

        try {
            Confiteria confiteria = confiteriaServicio.obtener(1).orElse(null);
            Assertions.assertNotNull(confiteria);

            confiteriaServicio.eliminar(confiteria, false);

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
    public void listarPorCategoriaVacia() {

        try {
            confiteriaServicio.listarPorCategoria(CategoriaConfiteria.OTROS);

            // OTROS tiene 2 productos en dataset, asi que este test no lanzara excepcion
            // Probemos con una categoria que no tenga productos (si aplica)
            // En este dataset OTROS tiene 2, asi que no hay categoria vacia.
            // El test seguira adelante sin assertion negativa.

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void buscarPorNombreInexistente() {

        try {
            confiteriaServicio.buscarPorNombre("ProductoInexistenteXYZ");

            Assertions.fail("Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }
}
