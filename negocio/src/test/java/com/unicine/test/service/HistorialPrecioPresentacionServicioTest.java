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
public class HistorialPrecioPresentacionServicioTest {

    @Autowired
    private HistorialPrecioPresentacionServicio historialServicio;

    @Autowired
    private ConfiteriaPresentacionServicio presentacionServicio;

    @Autowired
    private ConfiteriaServicio confiteriaServicio;

    private ConfiteriaPresentacion crearPresentacionActualizacion(Integer codigo, Double precio) throws Exception {
        Confiteria confiteria = confiteriaServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(confiteria);

        ConfiteriaPresentacion presentacion = ConfiteriaPresentacion.builder()
                .porcion(1.0)
                .unidadMedida(UnidadMedida.UNIDAD)
                .precio(precio)
                .confiteria(confiteria)
                .build();
        presentacion.setCodigo(codigo);
        return presentacion;
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerUltimoPorPresentacion() throws Exception {
        presentacionServicio.actualizar(crearPresentacionActualizacion(1, 25000.0), LocalDateTime.now().plusDays(5).truncatedTo(java.time.temporal.ChronoUnit.MICROS));

        HistorialPrecioPresentacion ultimo = historialServicio.obtenerUltimoPorPresentacion(1).orElse(null);

        Assertions.assertNotNull(ultimo);
        Assertions.assertEquals(TipoCambioPrecioPresentacion.DESCUENTO_TEMPORAL, ultimo.getTipoCambio());

        System.out.println("\nUltimo historial:\n" + ultimo);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorPresentacion() throws Exception {
        presentacionServicio.actualizar(crearPresentacionActualizacion(1, 25000.0), LocalDateTime.now().plusDays(5).truncatedTo(java.time.temporal.ChronoUnit.MICROS));
        presentacionServicio.actualizar(crearPresentacionActualizacion(1, 30000.0), null);

        List<HistorialPrecioPresentacion> historial = historialServicio.listarPorPresentacion(1);

        Assertions.assertEquals(2, historial.size());

        System.out.println("\nHistorial de precios:");
        historial.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminarPorPresentacion() throws Exception {
        presentacionServicio.actualizar(crearPresentacionActualizacion(1, 25000.0), LocalDateTime.now().plusDays(5).truncatedTo(java.time.temporal.ChronoUnit.MICROS));

        Assertions.assertTrue(historialServicio.obtenerUltimoPorPresentacion(1).isPresent());

        historialServicio.eliminarPorPresentacion(1);

        Assertions.assertTrue(historialServicio.obtenerUltimoPorPresentacion(1).isEmpty());

        System.out.println("\nHistorial eliminado por presentacion.");
    }
}
