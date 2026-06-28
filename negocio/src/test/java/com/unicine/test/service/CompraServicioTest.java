package com.unicine.test.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;
import com.unicine.entity.purchase.Compra;
import com.unicine.entity.purchase.CompraConfiteria;
import com.unicine.entity.purchase.CuponCliente;
import com.unicine.entity.purchase.Entrada;
import com.unicine.entity.showing.Funcion;
import com.unicine.entity.user.Cliente;
import com.unicine.enums.purchase.MedioPago;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.service.purchase.CompraServicio;
import com.unicine.util.validation.catalog.SuccessCatalog;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

/**
 * Tests unitarios para CompraServicioImp.
 * Cubre flujo completo de registro con entradas y confiteria,
 * validacion de cupones, calculo de totales y control de estados.
 */
@SpringBootTest
@Transactional
public class CompraServicioTest {

    @Autowired
    private CompraServicio compraServicio;

    // 🟩 CASOS POSITIVOS

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarCompraCompleta() {

        Integer cedula = 1009000011;
        Integer codigoFuncion = 1;

        Cliente cliente = Cliente.builder().cedula(cedula).build();
        Funcion funcion = new Funcion();
        funcion.setCodigo(codigoFuncion);

        Compra compra = Compra.builder()
                .estado(true)
                .medioPago(MedioPago.NEQUI)
                .cliente(cliente)
                .funcion(funcion)
                .build();
        compra.setFechaPelicula(LocalDateTime.now().plusDays(1));

        List<Entrada> entradas = List.of(
                Entrada.builder().precio(10000.0).fila(1).columna(1).build()
        );

        ConfiteriaPresentacion presentacion = new ConfiteriaPresentacion();
        presentacion.setCodigo(1);

        List<CompraConfiteria> confiterias = List.of(
                CompraConfiteria.builder().precio(5000.0).unidades(2).presentacion(presentacion).build()
        );

        try {
            Compra registrada = compraServicio.registrarCompraCompleta(compra, entradas, confiterias);

            Double esperado = 20000.0;
            Assertions.assertEquals(esperado, registrada.getValorTotal());
            Assertions.assertNotNull(registrada.getCodigo());
            Assertions.assertTrue(registrada.getEstado());

            System.out.println("\n" + SuccessCatalog.SUC401.getMessage() + ":\n" + registrada);

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer codigo = 1;

        try {
            Compra compra = compraServicio.obtener(codigo).orElse(null);

            Assertions.assertEquals(codigo, compra.getCodigo());
            Assertions.assertTrue(compra.getEstado());

            System.out.println("\n" + "Registro encontrado:" + "\n" + compra);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertTrue(false);
            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Compra> lista = compraServicio.listar();

            Assertions.assertEquals(6, lista.size());

            System.out.println("\n" + "Listado de registros:");
            lista.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertTrue(false);
            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        try {
            List<Compra> lista = compraServicio.listarPaginado();

            Assertions.assertEquals(6, lista.size());

            System.out.println("\n" + "Listado paginado:");
            lista.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertTrue(false);
            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerComprasCliente() {

        Integer cedula = 1008000022;

        try {
            List<Compra> compras = compraServicio.obtenerComprasCliente(cedula);

            Assertions.assertEquals(2, compras.size());

            System.out.println("\n" + "Compras del cliente:");
            compras.forEach(System.out::println);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertTrue(false);
            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerTotalComprasCliente() {

        Integer cedula = 1008000022;

        try {
            Double total = compraServicio.obtenerTotalComprasCliente(cedula);

            Double esperado = 89000.0;
            Assertions.assertEquals(esperado, total);

            System.out.println("\n" + "Total gastado por cliente: " + total);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertTrue(false);
            throw new RuntimeException(e);
        }
    }

    // 🟥 CASOS NEGATIVOS

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerInexistente() {

        Integer codigo = 9999;

        try {
            compraServicio.obtener(codigo);

            Assertions.assertTrue(false, "Deberia lanzar ResourceNotFoundException");

        } catch (ResourceNotFoundException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND.getCode(), e.getErrorCode());

        } catch (Exception e) {
            System.out.println("Mensaje de error inesperado: " + e.getMessage());
            Assertions.assertTrue(false);
        }
    }

    @Test
    @Sql({"classpath:dataset.sql", "classpath:expired-cupon.sql"})
    public void registrarCuponExpirado() {

        Integer cedula = 1005000055;
        Integer codigoFuncion = 1;

        Cliente cliente = Cliente.builder().cedula(cedula).build();
        Funcion funcion = new Funcion();
        funcion.setCodigo(codigoFuncion);
        CuponCliente cuponCliente = new CuponCliente();
        cuponCliente.setCodigo(6);

        Compra compra = Compra.builder()
                .estado(true)
                .medioPago(MedioPago.NEQUI)
                .cliente(cliente)
                .funcion(funcion)
                .cuponCliente(cuponCliente)
                .build();
        compra.setFechaPelicula(LocalDateTime.now().plusDays(1));

        List<Entrada> entradas = List.of(
                Entrada.builder().precio(10000.0).fila(1).columna(1).build()
        );

        List<CompraConfiteria> confiterias = new ArrayList<>();

        try {
            compraServicio.registrarCompraCompleta(compra, entradas, confiterias);

            Assertions.fail("Deberia lanzar BusinessRuleException por cupon expirado");

        } catch (BusinessRuleException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_COUPON_EXPIRED.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarCuponUsado() {

        Integer cedula = 1006000044;
        Integer codigoFuncion = 1;

        Cliente cliente = Cliente.builder().cedula(cedula).build();
        Funcion funcion = new Funcion();
        funcion.setCodigo(codigoFuncion);
        CuponCliente cuponCliente = new CuponCliente();
        cuponCliente.setCodigo(2);

        Compra compra = Compra.builder()
                .estado(true)
                .medioPago(MedioPago.VISA)
                .cliente(cliente)
                .funcion(funcion)
                .cuponCliente(cuponCliente)
                .build();
        compra.setFechaPelicula(LocalDateTime.now().plusDays(1));

        List<Entrada> entradas = List.of(
                Entrada.builder().precio(10000.0).fila(2).columna(2).build()
        );

        List<CompraConfiteria> confiterias = new ArrayList<>();

        try {
            compraServicio.registrarCompraCompleta(compra, entradas, confiterias);

            Assertions.fail("Deberia lanzar BusinessRuleException por cupon usado");

        } catch (BusinessRuleException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_COUPON_ALREADY_USED.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizarCompraProcesada() {

        Integer codigo = 5;

        try {
            Compra compra = compraServicio.obtener(codigo).orElse(null);
            Assertions.assertNotNull(compra);
            Assertions.assertFalse(compra.getEstado(), "La compra deberia estar procesada (estado=false)");

            compra.setValorTotal(99999.0);
            compra.setFechaPelicula(LocalDateTime.now().plusDays(1));
            compraServicio.actualizar(compra);

            Assertions.fail("Deberia lanzar BusinessRuleException por compra procesada");

        } catch (BusinessRuleException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_PURCHASE_ALREADY_PROCESSED.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }
}
