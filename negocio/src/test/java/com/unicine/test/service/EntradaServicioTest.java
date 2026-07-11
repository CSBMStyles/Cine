package com.unicine.test.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.purchase.Entrada;
import com.unicine.entity.showing.FuncionEsquema;
import com.unicine.exception.BusinessRuleException;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.purchase.EntradaRepo;
import com.unicine.repository.showing.FuncionEsquemaRepo;
import com.unicine.repository.showing.FuncionRepo;
import com.unicine.service.purchase.EntradaServicio;
import com.unicine.transfer.dto.request.EntradaRequest;
import com.unicine.transfer.dto.response.DetalleSillaResponse;
import com.unicine.transfer.dto.response.EntradaResponse;
import com.unicine.util.parser.DistribucionSillaParser;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class EntradaServicioTest {

    @Autowired
    private EntradaServicio entradaServicio;

    @Autowired
    private CompraRepo compraRepo;

    @Autowired
    private FuncionRepo funcionRepo;

    @Autowired
    private EntradaRepo entradaRepo;

    @Autowired
    private FuncionEsquemaRepo funcionEsquemaRepo;

    @Autowired
    private DistribucionSillaParser distribucionSillaParser;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() throws Exception {
        EntradaRequest request = EntradaRequest.builder()
                .precio(7000.0)
                .fila(1)
                .columna(1)
                .compraCodigo(1)
                .funcionCodigo(6)
                .build();

        EntradaResponse guardada = entradaServicio.registrar(request);

        Assertions.assertNotNull(guardada);
        Assertions.assertEquals(1, guardada.getFila());
        Assertions.assertEquals(1, guardada.getColumna());

        System.out.println("\nRegistro guardado:");
        System.out.println(guardada);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarSillaOcupada() {
        EntradaRequest request = EntradaRequest.builder()
                .precio(7000.0)
                .fila(2)
                .columna(5)
                .compraCodigo(1)
                .funcionCodigo(6)
                .build();

        Assertions.assertThrows(BusinessRuleException.class, () -> entradaServicio.registrar(request));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarSillaFueraDistribucion() {
        EntradaRequest request = EntradaRequest.builder()
                .precio(7000.0)
                .fila(100)
                .columna(100)
                .compraCodigo(1)
                .funcionCodigo(6)
                .build();

        Assertions.assertThrows(BusinessRuleException.class, () -> entradaServicio.registrar(request));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarFuncionNoCoincide() {
        EntradaRequest request = EntradaRequest.builder()
                .precio(7000.0)
                .fila(1)
                .columna(1)
                .compraCodigo(1)
                .funcionCodigo(1)
                .build();

        Assertions.assertThrows(BusinessRuleException.class, () -> entradaServicio.registrar(request));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() throws Exception {
        EntradaResponse buscado = entradaServicio.obtener(1).orElse(null);
        Assertions.assertNotNull(buscado);

        Entrada entidad = entradaRepo.findById(1).orElse(null);
        Assertions.assertNotNull(entidad);

        EntradaRequest request = EntradaRequest.builder()
                .codigo(buscado.getCodigo())
                .precio(20000.0)
                .fila(buscado.getFila())
                .columna(buscado.getColumna())
                .compraCodigo(entidad.getCompra().getCodigo())
                .funcionCodigo(entidad.getFuncion().getCodigo())
                .build();

        EntradaResponse actualizado = entradaServicio.actualizar(request);

        Assertions.assertEquals(20000.0, actualizado.getPrecio());

        System.out.println("\nRegistro actualizado:");
        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() throws Exception {
        entradaServicio.eliminar(1, true);

        Optional<Entrada> verificacion = entradaRepo.findById(1);

        Assertions.assertTrue(verificacion.isEmpty());

        System.out.println("\nRegistro eliminado");
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() throws Exception {
        Optional<EntradaResponse> buscado = entradaServicio.obtener(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\nRegistro obtenido:");
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {
        List<EntradaResponse> entradas = entradaServicio.listar();

        Assertions.assertEquals(6, entradas.size());

        System.out.println("\nListado de registros:");
        entradas.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorCompra() throws Exception {
        List<EntradaResponse> entradas = entradaServicio.listarPorCompra(4);

        Assertions.assertEquals(2, entradas.size());

        System.out.println("\nListado de entradas por compra:");
        entradas.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorFuncion() throws Exception {
        List<EntradaResponse> entradas = entradaServicio.listarPorFuncion(3);

        Assertions.assertEquals(2, entradas.size());

        System.out.println("\nListado de entradas por funcion:");
        entradas.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerSillasOcupadas() throws Exception {
        List<DetalleSillaResponse> sillas = entradaServicio.obtenerSillasOcupadas(3);

        Assertions.assertEquals(2, sillas.size());

        System.out.println("\nSillas ocupadas:");
        sillas.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void esquemaTemporalSincronizadoAlRegistrar() throws Exception {
        EntradaRequest request = EntradaRequest.builder()
                .precio(7000.0)
                .fila(1)
                .columna(1)
                .compraCodigo(1)
                .funcionCodigo(6)
                .build();

        entradaServicio.registrar(request);

        FuncionEsquema esquema = funcionEsquemaRepo.findByFuncionCodigo(6).orElse(null);
        String[][] matriz = distribucionSillaParser.parse(esquema.getEsquemaTemporal());

        Assertions.assertEquals("O", matriz[0][0]);
    }
}
