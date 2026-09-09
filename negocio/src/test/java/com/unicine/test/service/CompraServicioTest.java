package com.unicine.test.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.enums.purchase.MedioPago;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.service.purchase.CompraServicio;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.purchase.CuponClienteRepo;
import com.unicine.repository.purchase.EntradaRepo;
import com.unicine.transfer.dto.request.CompraCompletaRequest;
import com.unicine.transfer.dto.request.CompraConfiteriaRequest;
import com.unicine.transfer.dto.request.CompraRequest;
import com.unicine.transfer.dto.request.EntradaRequest;
import com.unicine.transfer.dto.response.CompraResponse;
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

    @Autowired
    private CompraRepo compraRepo;

    @Autowired
    private CuponClienteRepo cuponClienteRepo;

    @Autowired
    private EntradaRepo entradaRepo;

    // 🟩 Casos positivos

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarCompraCompleta() {

        Integer cedula = 1009000011;
        Integer codigoFuncion = 1;

        CompraRequest compraRequest = CompraRequest.builder()
                .estado(true)
                .medioPago(MedioPago.NEQUI)
                .fechaCompra(LocalDateTime.now())
                .fechaPelicula(LocalDateTime.now().plusDays(1))
                .valorTotal(0.0)
                .clienteCedula(cedula)
                .funcionCodigo(codigoFuncion)
                .build();

        List<EntradaRequest> entradas = List.of(
                EntradaRequest.builder()
                        .precio(10000.0)
                        .fila(1)
                        .columna(1)
                        .compraCodigo(1)
                        .funcionCodigo(codigoFuncion)
                        .build()
        );

        List<CompraConfiteriaRequest> confiterias = List.of(
                CompraConfiteriaRequest.builder()
                        .precio(5000.0)
                        .unidades(2)
                        .compraCodigo(1)
                        .presentacionCodigo(1)
                        .build()
        );

        CompraCompletaRequest request = CompraCompletaRequest.builder()
                .compra(compraRequest)
                .entradas(entradas)
                .confiterias(confiterias)
                .build();

        try {
            CompraResponse registrada = compraServicio.registrarCompraCompleta(request);

            // Precio server-side: entrada toma precio de funcion(1)=7000 (ignora 10000 del body)
            // + confiteria 2x5000 = 17000.0
            Double esperado = 17000.0;
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
            CompraResponse compra = compraServicio.obtener(codigo).orElse(null);

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
            List<CompraResponse> lista = compraServicio.listar();

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
            List<CompraResponse> lista = compraServicio.listarPaginado();

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
            List<CompraResponse> compras = compraServicio.obtenerComprasCliente(cedula);

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

    // 🟥 Casos negativos

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

        CompraRequest compraRequest = CompraRequest.builder()
                .estado(true)
                .medioPago(MedioPago.NEQUI)
                .fechaCompra(LocalDateTime.now())
                .fechaPelicula(LocalDateTime.now().plusDays(1))
                .valorTotal(0.0)
                .cuponClienteCodigo(6)
                .clienteCedula(cedula)
                .funcionCodigo(codigoFuncion)
                .build();

        List<EntradaRequest> entradas = List.of(
                EntradaRequest.builder()
                        .precio(10000.0)
                        .fila(1)
                        .columna(1)
                        .compraCodigo(1)
                        .funcionCodigo(codigoFuncion)
                        .build()
        );

        List<CompraConfiteriaRequest> confiterias = new ArrayList<>();

        CompraCompletaRequest request = CompraCompletaRequest.builder()
                .compra(compraRequest)
                .entradas(entradas)
                .confiterias(confiterias)
                .build();

        try {
            compraServicio.registrarCompraCompleta(request);

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

        CompraRequest compraRequest = CompraRequest.builder()
                .estado(true)
                .medioPago(MedioPago.VISA)
                .fechaCompra(LocalDateTime.now())
                .fechaPelicula(LocalDateTime.now().plusDays(1))
                .valorTotal(0.0)
                .cuponClienteCodigo(2)
                .clienteCedula(cedula)
                .funcionCodigo(codigoFuncion)
                .build();

        List<EntradaRequest> entradas = List.of(
                EntradaRequest.builder()
                        .precio(10000.0)
                        .fila(2)
                        .columna(2)
                        .compraCodigo(1)
                        .funcionCodigo(codigoFuncion)
                        .build()
        );

        List<CompraConfiteriaRequest> confiterias = new ArrayList<>();

        CompraCompletaRequest request = CompraCompletaRequest.builder()
                .compra(compraRequest)
                .entradas(entradas)
                .confiterias(confiterias)
                .build();

        try {
            compraServicio.registrarCompraCompleta(request);

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
            CompraResponse compra = compraServicio.obtener(codigo).orElse(null);
            Assertions.assertNotNull(compra);
            Assertions.assertFalse(compra.getEstado(), "La compra deberia estar procesada (estado=false)");

            CompraRequest request = CompraRequest.builder()
                    .codigo(compra.getCodigo())
                    .estado(compra.getEstado())
                    .medioPago(compra.getMedioPago())
                    .fechaCompra(compra.getFechaCompra())
                    .fechaPelicula(LocalDateTime.now().plusDays(1))
                    .valorTotal(99999.0)
                    .clienteCedula(compra.getCliente().getCedula())
                    .funcionCodigo(compra.getFuncion().getCodigo())
                    .build();

            compraServicio.actualizar(request);

            Assertions.fail("Deberia lanzar BusinessRuleException por compra procesada");

        } catch (BusinessRuleException e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            Assertions.assertEquals(PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_PURCHASE_ALREADY_PROCESSED.getCode(), e.getErrorCode());

        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }
    }

    // SECTION: 4.4.1 invariantes transaccionales

    private CompraCompletaRequest compraCompleta(Integer clienteCedula, Integer funcionCodigo,
                                                 int fila, int columna, Integer cuponClienteCodigo) {
        CompraRequest compraRequest = CompraRequest.builder()
                .estado(true)
                .medioPago(MedioPago.NEQUI)
                .fechaCompra(LocalDateTime.now())
                .fechaPelicula(LocalDateTime.now().plusDays(1))
                .valorTotal(0.0)
                .clienteCedula(clienteCedula)
                .funcionCodigo(funcionCodigo)
                .cuponClienteCodigo(cuponClienteCodigo)
                .build();

        List<EntradaRequest> entradas = List.of(
                EntradaRequest.builder()
                        .precio(999.0)
                        .fila(fila)
                        .columna(columna)
                        .compraCodigo(1)
                        .funcionCodigo(funcionCodigo)
                        .build()
        );

        return CompraCompletaRequest.builder()
                .compra(compraRequest)
                .entradas(entradas)
                .confiterias(new ArrayList<>())
                .build();
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void sillaOcupadaHaceRollbackTotal() {
        long comprasAntes = compraRepo.count();

        try {
            // Funcion 6, silla (2,5) ocupada en dataset + cupon 1 valido de Luisa
            compraServicio.registrarCompraCompleta(
                    compraCompleta(1005000055, 6, 2, 5, 1));

            Assertions.fail("Deberia lanzar BusinessRuleException por silla ocupada");

        } catch (BusinessRuleException e) {
            System.out.println("Rollback esperado: " + e.getMessage());
            Assertions.assertEquals(
                    PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_SELECTED_SEAT_ALREADY_OCCUPIED.getCode(),
                    e.getErrorCode());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Error inesperado: " + e.getMessage());
        }

        // Nada persistido y cupon intacto (no consumido)
        Assertions.assertEquals(comprasAntes, compraRepo.count());
        Assertions.assertTrue(cuponClienteRepo.findById(1).orElseThrow().getEstado());
    }

    @Test
    @Sql("classpath:dataset.sql")
    @Sql(scripts = "classpath:cleanup-compra-concurrente.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void compraConcurrenteMismaSillaPersisteUnaSola() throws Exception {
        // NOT_SUPPORTED: @Sql commitea el dataset para que los hilos
        // (transacciones propias) vean los datos; limpieza explicita al final.
        ExecutorService pool = Executors.newFixedThreadPool(2);
        CountDownLatch listo = new CountDownLatch(1);
        Integer funcion = 1;
        Integer codigoGanadora = null;

        try {
            var tarea = (java.util.concurrent.Callable<Object>) () -> {
                listo.await(10, TimeUnit.SECONDS);
                try {
                    // Cliente 1005000055 existe en dataset; sin cupon para aislar la carrera
                    return compraServicio.registrarCompraCompleta(
                            compraCompleta(1005000055, funcion, 3, 3, null));
                } catch (Exception e) {
                    return e;
                }
            };

            Future<Object> f1 = pool.submit(tarea);
            Future<Object> f2 = pool.submit(tarea);
            listo.countDown();

            Object r1 = f1.get(60, TimeUnit.SECONDS);
            Object r2 = f2.get(60, TimeUnit.SECONDS);

            long exitos = java.util.stream.Stream.of(r1, r2)
                    .filter(r -> r instanceof CompraResponse).count();
            long conflictos = java.util.stream.Stream.of(r1, r2)
                    .filter(r -> r instanceof BusinessRuleException
                            && PurchaseErrorCatalog.DOMAIN_PURCHASE_BUSINESS_RULE_SELECTED_SEAT_ALREADY_OCCUPIED
                                    .getCode().equals(((BusinessRuleException) r).getErrorCode()))
                    .count();

            System.out.println("\nExitos=" + exitos + " conflictos=" + conflictos);

            Assertions.assertEquals(1, exitos);
            Assertions.assertEquals(1, conflictos);
            Assertions.assertTrue(entradaRepo.existsByFilaAndColumnaAndFuncionCodigo(3, 3, funcion));

            // Limpieza explicita (sin rollback: NOT_SUPPORTED + hilos commitean)
            codigoGanadora = java.util.stream.Stream.of(r1, r2)
                    .filter(r -> r instanceof CompraResponse)
                    .map(r -> ((CompraResponse) r).getCodigo())
                    .findFirst()
                    .orElse(null);

        } finally {
            pool.shutdownNow();
        }

        if (codigoGanadora != null) {
            entradaRepo.deleteAll(entradaRepo.findByCompraCodigo(codigoGanadora));
            compraRepo.deleteById(codigoGanadora);
        }
    }

    // !SECTION
}
