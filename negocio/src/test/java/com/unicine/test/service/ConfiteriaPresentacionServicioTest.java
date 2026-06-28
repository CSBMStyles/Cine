package com.unicine.test.service;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.entity.confiteria.ConfiteriaPresentacion;
import com.unicine.entity.confiteria.HistorialPrecioPresentacion;
import com.unicine.enums.confiteria.TipoCambioPrecioPresentacion;
import com.unicine.enums.confiteria.UnidadMedida;
import com.unicine.service.confiteria.ConfiteriaPresentacionServicio;
import com.unicine.service.confiteria.ConfiteriaServicio;
import com.unicine.service.confiteria.HistorialPrecioPresentacionServicio;

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
        Confiteria confiteria = confiteriaServicio.obtener(7).orElse(null);
        Assertions.assertNotNull(confiteria);

        ConfiteriaPresentacion presentacion = ConfiteriaPresentacion.builder()
                .porcion(1.5)
                .unidadMedida(UnidadMedida.L)
                .precio(9000.0)
                .confiteria(confiteria)
                .build();

        ConfiteriaPresentacion registrada = presentacionServicio.registrar(presentacion);

        Assertions.assertNotNull(registrada);
        Assertions.assertNotNull(registrada.getCodigo());
        Assertions.assertEquals(9000.0, registrada.getPrecio());
        Assertions.assertEquals(9000.0, registrada.getPrecioBase());

        System.out.println("\nPresentacion registrada:\n" + registrada);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void aplicarDescuentoTemporal() throws Exception {
        Confiteria confiteria = confiteriaServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(confiteria);

        ConfiteriaPresentacion presentacion = ConfiteriaPresentacion.builder()
                .porcion(1.0)
                .unidadMedida(UnidadMedida.UNIDAD)
                .precio(25600.0)
                .confiteria(confiteria)
                .build();
        presentacion.setCodigo(1);

        LocalDateTime expiracion = LocalDateTime.now().plusDays(7).truncatedTo(java.time.temporal.ChronoUnit.MICROS);

        ConfiteriaPresentacion actualizada = presentacionServicio.actualizar(presentacion, expiracion);

        Assertions.assertEquals(25600.0, actualizada.getPrecio());
        Assertions.assertEquals(32000.0, actualizada.getPrecioBase());
        Assertions.assertEquals(expiracion, actualizada.getFechaExpiracionTemporal());
        Assertions.assertTrue(actualizada.esPrecioTemporal());
        Assertions.assertEquals(20, actualizada.calcularPorcentajeDescuento());

        HistorialPrecioPresentacion historial = historialServicio.obtenerUltimoPorPresentacion(1).orElse(null);
        Assertions.assertNotNull(historial);
        Assertions.assertEquals(TipoCambioPrecioPresentacion.DESCUENTO_TEMPORAL, historial.getTipoCambio());
        Assertions.assertEquals(20, historial.getPorcentaje());

        System.out.println("\nDescuento aplicado:\n" + actualizada);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void aplicarAumentoCambiaBase() throws Exception {
        Confiteria confiteria = confiteriaServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(confiteria);

        ConfiteriaPresentacion presentacion = ConfiteriaPresentacion.builder()
                .porcion(1.0)
                .unidadMedida(UnidadMedida.UNIDAD)
                .precio(35000.0)
                .confiteria(confiteria)
                .build();
        presentacion.setCodigo(1);

        ConfiteriaPresentacion actualizada = presentacionServicio.actualizar(presentacion, null);

        Assertions.assertEquals(35000.0, actualizada.getPrecio());
        Assertions.assertEquals(35000.0, actualizada.getPrecioBase());
        Assertions.assertNull(actualizada.getFechaExpiracionTemporal());
        Assertions.assertFalse(actualizada.esPrecioTemporal());

        HistorialPrecioPresentacion historial = historialServicio.obtenerUltimoPorPresentacion(1).orElse(null);
        Assertions.assertNotNull(historial);
        Assertions.assertEquals(TipoCambioPrecioPresentacion.AUMENTO, historial.getTipoCambio());

        System.out.println("\nAumento aplicado:\n" + actualizada);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void sinCambioDePrecioNoGeneraHistorial() throws Exception {
        ConfiteriaPresentacion presentacion = presentacionServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(presentacion);

        presentacion.setPorcion(2.0);

        ConfiteriaPresentacion actualizada = presentacionServicio.actualizar(presentacion, null);

        Assertions.assertNotNull(actualizada);

        HistorialPrecioPresentacion historial = historialServicio.obtenerUltimoPorPresentacion(1).orElse(null);
        Assertions.assertNull(historial);

        System.out.println("\nActualizacion sin cambio de precio, sin historial.");
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorConfiteria() throws Exception {
        List<ConfiteriaPresentacion> presentaciones = presentacionServicio.listarPorConfiteria(7);

        Assertions.assertEquals(2, presentaciones.size());

        System.out.println("\nPresentaciones de Agua:");
        presentaciones.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarConDescuentoTemporal() throws Exception {
        Confiteria confiteria = confiteriaServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(confiteria);

        ConfiteriaPresentacion presentacion = ConfiteriaPresentacion.builder()
                .porcion(1.0)
                .unidadMedida(UnidadMedida.UNIDAD)
                .precio(25000.0)
                .confiteria(confiteria)
                .build();
        presentacion.setCodigo(1);

        presentacionServicio.actualizar(presentacion, LocalDateTime.now().plusDays(5).truncatedTo(java.time.temporal.ChronoUnit.MICROS));

        List<ConfiteriaPresentacion> descuentos = presentacionServicio.listarConDescuentoTemporal();

        Assertions.assertEquals(1, descuentos.size());

        System.out.println("\nPresentaciones con descuento temporal:");
        descuentos.forEach(System.out::println);
    }
}
