package com.unicine.test.service;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.service.purchase.CuponClienteServicio;
import com.unicine.transfer.dto.request.CuponClienteRequest;
import com.unicine.transfer.dto.response.CuponClienteResponse;
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

    // 🟩 Casos positivos

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        try {
            CuponClienteRequest request = CuponClienteRequest.builder()
                    .estado(true)
                    .cuponCodigo(4)
                    .clienteCedula(1009000011)
                    .build();

            CuponClienteResponse registrado = cuponClienteServicio.registrar(request);

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
            CuponClienteResponse existente = cuponClienteServicio.obtener(1).orElse(null);
            Assertions.assertNotNull(existente);

            CuponClienteRequest request = CuponClienteRequest.builder()
                    .codigo(existente.getCodigo())
                    .estado(false)
                    .cuponCodigo(existente.getCupon().getCodigo())
                    .clienteCedula(existente.getCliente().getCedula())
                    .build();

            CuponClienteResponse actualizado = cuponClienteServicio.actualizar(request);

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
            CuponClienteResponse cuponCliente = cuponClienteServicio.obtener(1).orElse(null);

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
            List<CuponClienteResponse> cuponesClientes = cuponClienteServicio.listar();

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
            List<CuponClienteResponse> cuponesClientes = cuponClienteServicio.listarPaginado();

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
            List<CuponClienteResponse> cuponesClientes = cuponClienteServicio.listarPorCliente(1006000044);

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
            List<CuponClienteResponse> cuponesClientes = cuponClienteServicio.listarActivosPorCliente(1006000044);

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
            List<CuponClienteResponse> cuponesClientes = cuponClienteServicio.listarInactivosPorCliente(1006000044);

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
            CuponClienteResponse cuponCliente = cuponClienteServicio.obtenerPorCuponYCliente(1, 1005000055).orElse(null);

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
            CuponClienteResponse cuponCliente = cuponClienteServicio.obtener(2).orElse(null);
            Assertions.assertNotNull(cuponCliente);

            cuponClienteServicio.eliminar(2, true);

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

    // 🟥 Casos negativos

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerInexistente() {

        Integer codigo = 9999;

        try {
            cuponClienteServicio.obtener(codigo);

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
    public void eliminarSinConfirmacion() throws Exception {
        CuponClienteResponse cuponCliente = cuponClienteServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(cuponCliente);

        Assertions.assertThrows(BusinessRuleException.class,
                () -> cuponClienteServicio.eliminar(1, false));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarActivosPorClienteSinActivos() {

        try {
            cuponClienteServicio.listarActivosPorCliente(1008000022);

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
