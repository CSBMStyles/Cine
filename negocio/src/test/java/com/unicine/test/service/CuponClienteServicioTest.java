package com.unicine.test.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.purchase.Cupon;
import com.unicine.entity.purchase.CuponCliente;
import com.unicine.entity.user.Cliente;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.service.purchase.CuponClienteServicio;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

/**
 * Tests unitarios para CuponClienteServicioImp.
 * Cubre CRUD y consultas por cliente, estado y cupones redimidos.
 */
@SpringBootTest
@Transactional
public class CuponClienteServicioTest {

    @Autowired
    private CuponClienteServicio cuponClienteServicio;

    // 🟩 CASOS POSITIVOS

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        try {
            Cupon cupon = Cupon.builder().build();
            cupon.setCodigo(4);

            Cliente cliente = Cliente.builder().cedula(1009000011).build();

            CuponCliente cuponCliente = CuponCliente.builder()
                    .estado(true)
                    .cupon(cupon)
                    .cliente(cliente)
                    .build();

            CuponCliente registrado = cuponClienteServicio.registrar(cuponCliente);

            Assertions.assertNotNull(registrado);
            Assertions.assertNotNull(registrado.getCodigo());
            Assertions.assertTrue(registrado.getEstado());

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
            CuponCliente cuponCliente = cuponClienteServicio.obtener(1).orElse(null);
            Assertions.assertNotNull(cuponCliente);

            cuponCliente.setEstado(false);

            CuponCliente actualizado = cuponClienteServicio.actualizar(cuponCliente);

            Assertions.assertFalse(actualizado.getEstado());

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
            CuponCliente cuponCliente = cuponClienteServicio.obtener(1).orElse(null);

            Assertions.assertNotNull(cuponCliente);
            Assertions.assertEquals(1005000055, cuponCliente.getCliente().getCedula());

            System.out.println("\nRegistro encontrado:\n" + cuponCliente);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<CuponCliente> cuponesClientes = cuponClienteServicio.listar();

            Assertions.assertEquals(5, cuponesClientes.size());

            System.out.println("\nListado de cupones asignados:\n" + cuponesClientes);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        try {
            List<CuponCliente> cuponesClientes = cuponClienteServicio.listarPaginado();

            Assertions.assertEquals(5, cuponesClientes.size());

            System.out.println("\nListado paginado de cupones asignados:\n" + cuponesClientes);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorCliente() {

        try {
            List<CuponCliente> cuponesClientes = cuponClienteServicio.listarPorCliente(1006000044);

            Assertions.assertEquals(2, cuponesClientes.size());

            System.out.println("\nCupones del cliente:\n" + cuponesClientes);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarActivosPorCliente() {

        try {
            List<CuponCliente> cuponesClientes = cuponClienteServicio.listarActivosPorCliente(1006000044);

            Assertions.assertEquals(1, cuponesClientes.size());
            Assertions.assertTrue(cuponesClientes.get(0).getEstado());

            System.out.println("\nCupones activos del cliente:\n" + cuponesClientes);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarInactivosPorCliente() {

        try {
            List<CuponCliente> cuponesClientes = cuponClienteServicio.listarInactivosPorCliente(1006000044);

            Assertions.assertEquals(1, cuponesClientes.size());
            Assertions.assertFalse(cuponesClientes.get(0).getEstado());

            System.out.println("\nCupones inactivos del cliente:\n" + cuponesClientes);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerPorCuponYCliente() {

        try {
            CuponCliente cuponCliente = cuponClienteServicio.obtenerPorCuponYCliente(1, 1005000055).orElse(null);

            Assertions.assertNotNull(cuponCliente);
            Assertions.assertEquals(1, cuponCliente.getCodigo());

            System.out.println("\nAsignacion encontrada por cupon y cliente:\n" + cuponCliente);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void contarRedimidosPorCliente() {

        try {
            Long cantidad = cuponClienteServicio.contarRedimidosPorCliente(1006000044);

            Assertions.assertEquals(2, cantidad);

            System.out.println("\nCupones redimidos por el cliente: " + cantidad);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        try {
            CuponCliente cuponCliente = cuponClienteServicio.obtener(2).orElse(null);
            Assertions.assertNotNull(cuponCliente);

            cuponClienteServicio.eliminar(cuponCliente, true);

            try {
                cuponClienteServicio.obtener(2);
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
            cuponClienteServicio.obtener(codigo);

            Assertions.fail("Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.ENT016.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminarSinConfirmacion() {

        try {
            CuponCliente cuponCliente = cuponClienteServicio.obtener(1).orElse(null);
            Assertions.assertNotNull(cuponCliente);

            cuponClienteServicio.eliminar(cuponCliente, false);

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
    public void listarActivosPorClienteSinActivos() {

        try {
            cuponClienteServicio.listarActivosPorCliente(1008000022);

            Assertions.fail("Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.ENT016.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }
}
