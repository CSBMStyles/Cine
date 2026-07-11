package com.unicine.test.service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.enums.confiteria.TipoCambioPrecioPresentacion;
import com.unicine.enums.confiteria.UnidadMedida;
import com.unicine.service.confiteria.ConfiteriaPresentacionServicio;
import com.unicine.service.confiteria.ConfiteriaServicio;
import com.unicine.service.confiteria.HistorialPrecioPresentacionServicio;
import com.unicine.transfer.dto.request.ConfiteriaPresentacionRequest;
import com.unicine.transfer.dto.response.ConfiteriaPresentacionResponse;
import com.unicine.transfer.dto.response.ConfiteriaResponse;
import com.unicine.transfer.dto.response.HistorialPrecioPresentacionResponse;

@SpringBootTest
@Transactional
public class ConfiteriaPresentacionServicioTest {

    @Autowired
    private ConfiteriaPresentacionServicio presentacionServicio;

    @Autowired
    private ConfiteriaServicio confiteriaServicio;

    @Autowired
    private HistorialPrecioPresentacionServicio historialServicio;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() throws Exception {
        ConfiteriaResponse confiteria = confiteriaServicio.obtener(7).orElse(null);
        Assertions.assertNotNull(confiteria);

        ConfiteriaPresentacionRequest request = ConfiteriaPresentacionRequest.builder()
                .porcion(1.5)
                .unidadMedida(UnidadMedida.L)
                .precio(9000.0)
                .confiteriaCodigo(confiteria.getCodigo())
                .build();

        ConfiteriaPresentacionResponse registrada = presentacionServicio.registrar(request);

        Assertions.assertNotNull(registrada);
        Assertions.assertNotNull(registrada.getCodigo());
        Assertions.assertEquals(9000.0, registrada.getPrecio());
        Assertions.assertEquals(9000.0, registrada.getPrecioBase());

        System.out.println("\nPresentacion registrada:\n" + registrada);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void aplicarDescuentoTemporal() throws Exception {
        ConfiteriaResponse confiteria = confiteriaServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(confiteria);

        ConfiteriaPresentacionRequest request = ConfiteriaPresentacionRequest.builder()
                .codigo(1)
                .porcion(1.0)
                .unidadMedida(UnidadMedida.UNIDAD)
                .precio(25600.0)
                .confiteriaCodigo(confiteria.getCodigo())
                .build();

        LocalDateTime expiracion = LocalDateTime.now().plusDays(7).truncatedTo(ChronoUnit.MICROS);

        ConfiteriaPresentacionResponse actualizada = presentacionServicio.actualizar(request, expiracion);

        Assertions.assertEquals(25600.0, actualizada.getPrecio());
        Assertions.assertEquals(32000.0, actualizada.getPrecioBase());
        Assertions.assertEquals(expiracion, actualizada.getFechaExpiracionTemporal());

        boolean esPrecioTemporal = actualizada.getFechaExpiracionTemporal() != null
                && actualizada.getFechaExpiracionTemporal().isAfter(LocalDateTime.now());
        Assertions.assertTrue(esPrecioTemporal);

        int porcentajeDescuento = calcularPorcentajeDescuento(actualizada.getPrecioBase(), actualizada.getPrecio());
        Assertions.assertEquals(20, porcentajeDescuento);

        HistorialPrecioPresentacionResponse historial = historialServicio.obtenerUltimoPorPresentacion(1).orElse(null);
        Assertions.assertNotNull(historial);
        Assertions.assertEquals(TipoCambioPrecioPresentacion.DESCUENTO_TEMPORAL, historial.getTipoCambio());
        Assertions.assertEquals(20, historial.getPorcentaje());

        System.out.println("\nDescuento aplicado:\n" + actualizada);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void aplicarAumentoCambiaBase() throws Exception {
        ConfiteriaResponse confiteria = confiteriaServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(confiteria);

        ConfiteriaPresentacionRequest request = ConfiteriaPresentacionRequest.builder()
                .codigo(1)
                .porcion(1.0)
                .unidadMedida(UnidadMedida.UNIDAD)
                .precio(35000.0)
                .confiteriaCodigo(confiteria.getCodigo())
                .build();

        ConfiteriaPresentacionResponse actualizada = presentacionServicio.actualizar(request, null);

        Assertions.assertEquals(35000.0, actualizada.getPrecio());
        Assertions.assertEquals(35000.0, actualizada.getPrecioBase());
        Assertions.assertNull(actualizada.getFechaExpiracionTemporal());

        boolean esPrecioTemporal = actualizada.getFechaExpiracionTemporal() != null
                && actualizada.getFechaExpiracionTemporal().isAfter(LocalDateTime.now());
        Assertions.assertFalse(esPrecioTemporal);

        HistorialPrecioPresentacionResponse historial = historialServicio.obtenerUltimoPorPresentacion(1).orElse(null);
        Assertions.assertNotNull(historial);
        Assertions.assertEquals(TipoCambioPrecioPresentacion.AUMENTO, historial.getTipoCambio());

        System.out.println("\nAumento aplicado:\n" + actualizada);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void sinCambioDePrecioNoGeneraHistorial() throws Exception {
        ConfiteriaPresentacionResponse presentacion = presentacionServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(presentacion);

        ConfiteriaPresentacionRequest request = ConfiteriaPresentacionRequest.builder()
                .codigo(presentacion.getCodigo())
                .porcion(2.0)
                .unidadMedida(presentacion.getUnidadMedida())
                .precio(presentacion.getPrecio())
                .precioBase(presentacion.getPrecioBase())
                .confiteriaCodigo(presentacion.getConfiteria().getCodigo())
                .build();

        ConfiteriaPresentacionResponse actualizada = presentacionServicio.actualizar(request, null);

        Assertions.assertNotNull(actualizada);

        HistorialPrecioPresentacionResponse historial = historialServicio.obtenerUltimoPorPresentacion(1).orElse(null);
        Assertions.assertNull(historial);

        System.out.println("\nActualizacion sin cambio de precio, sin historial.");
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorConfiteria() throws Exception {
        List<ConfiteriaPresentacionResponse> presentaciones = presentacionServicio.listarPorConfiteria(7);

        Assertions.assertEquals(2, presentaciones.size());

        System.out.println("\nPresentaciones de Agua:");
        presentaciones.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarConDescuentoTemporal() throws Exception {
        ConfiteriaResponse confiteria = confiteriaServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(confiteria);

        ConfiteriaPresentacionRequest request = ConfiteriaPresentacionRequest.builder()
                .codigo(1)
                .porcion(1.0)
                .unidadMedida(UnidadMedida.UNIDAD)
                .precio(25000.0)
                .confiteriaCodigo(confiteria.getCodigo())
                .build();

        presentacionServicio.actualizar(request, LocalDateTime.now().plusDays(5).truncatedTo(ChronoUnit.MICROS));

        List<ConfiteriaPresentacionResponse> descuentos = presentacionServicio.listarConDescuentoTemporal();

        Assertions.assertEquals(1, descuentos.size());

        System.out.println("\nPresentaciones con descuento temporal:");
        descuentos.forEach(System.out::println);
    }

    private int calcularPorcentajeDescuento(Double precioBase, Double precioNuevo) {
        if (precioBase == null || precioBase == 0) {
            return 0;
        }
        return (int) Math.round(((precioBase - precioNuevo) / precioBase) * 100);
    }
}
