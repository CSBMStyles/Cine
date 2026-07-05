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

import com.unicine.entity.purchase.Compra;
import com.unicine.entity.purchase.Entrada;
import com.unicine.entity.showing.Funcion;
import com.unicine.entity.showing.FuncionEsquema;
import com.unicine.exception.BusinessRuleException;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.repository.purchase.EntradaRepo;
import com.unicine.repository.showing.FuncionEsquemaRepo;
import com.unicine.repository.showing.FuncionRepo;
import com.unicine.service.purchase.EntradaServicio;
import com.unicine.transfer.dto.response.DetalleSillaDTO;
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
        Compra compra = compraRepo.findById(1).orElse(null);
        Funcion funcion = funcionRepo.findById(6).orElse(null);

        Entrada entrada = Entrada.builder()
                .precio(7000.0)
                .fila(1)
                .columna(1)
                .compra(compra)
                .funcion(funcion)
                .build();

        Entrada guardada = entradaServicio.registrar(entrada);

        Assertions.assertNotNull(guardada);
        Assertions.assertEquals(1, guardada.getFila());
        Assertions.assertEquals(1, guardada.getColumna());

        System.out.println("\nRegistro guardado:");
        System.out.println(guardada);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarSillaOcupada() {
        Compra compra = compraRepo.findById(1).orElse(null);
        Funcion funcion = funcionRepo.findById(6).orElse(null);

        Entrada entrada = Entrada.builder()
                .precio(7000.0)
                .fila(2)
                .columna(5)
                .compra(compra)
                .funcion(funcion)
                .build();

        Assertions.assertThrows(BusinessRuleException.class, () -> entradaServicio.registrar(entrada));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarSillaFueraDistribucion() {
        Compra compra = compraRepo.findById(1).orElse(null);
        Funcion funcion = funcionRepo.findById(6).orElse(null);

        Entrada entrada = Entrada.builder()
                .precio(7000.0)
                .fila(100)
                .columna(100)
                .compra(compra)
                .funcion(funcion)
                .build();

        Assertions.assertThrows(BusinessRuleException.class, () -> entradaServicio.registrar(entrada));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarFuncionNoCoincide() {
        Compra compra = compraRepo.findById(1).orElse(null);
        Funcion funcion = funcionRepo.findById(1).orElse(null);

        Entrada entrada = Entrada.builder()
                .precio(7000.0)
                .fila(1)
                .columna(1)
                .compra(compra)
                .funcion(funcion)
                .build();

        Assertions.assertThrows(BusinessRuleException.class, () -> entradaServicio.registrar(entrada));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() throws Exception {
        Entrada buscado = entradaServicio.obtener(1).orElse(null);

        buscado.setPrecio(20000.0);

        Entrada actualizado = entradaServicio.actualizar(buscado);

        Assertions.assertEquals(20000.0, actualizado.getPrecio());

        System.out.println("\nRegistro actualizado:");
        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() throws Exception {
        Entrada buscado = entradaServicio.obtener(1).orElse(null);

        entradaServicio.eliminar(buscado, true);

        Optional<Entrada> verificacion = entradaRepo.findById(1);

        Assertions.assertTrue(verificacion.isEmpty());

        System.out.println("\nRegistro eliminado");
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() throws Exception {
        Optional<Entrada> buscado = entradaServicio.obtener(1);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\nRegistro obtenido:");
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {
        List<Entrada> entradas = entradaServicio.listar();

        Assertions.assertEquals(6, entradas.size());

        System.out.println("\nListado de registros:");
        entradas.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorCompra() throws Exception {
        List<Entrada> entradas = entradaServicio.listarPorCompra(4);

        Assertions.assertEquals(2, entradas.size());

        System.out.println("\nListado de entradas por compra:");
        entradas.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorFuncion() throws Exception {
        List<Entrada> entradas = entradaServicio.listarPorFuncion(3);

        Assertions.assertEquals(2, entradas.size());

        System.out.println("\nListado de entradas por funcion:");
        entradas.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerSillasOcupadas() throws Exception {
        List<DetalleSillaDTO> sillas = entradaServicio.obtenerSillasOcupadas(3);

        Assertions.assertEquals(2, sillas.size());

        System.out.println("\nSillas ocupadas:");
        sillas.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void esquemaTemporalSincronizadoAlRegistrar() throws Exception {
        Compra compra = compraRepo.findById(1).orElse(null);
        Funcion funcion = funcionRepo.findById(6).orElse(null);

        Entrada entrada = Entrada.builder()
                .precio(7000.0)
                .fila(1)
                .columna(1)
                .compra(compra)
                .funcion(funcion)
                .build();

        entradaServicio.registrar(entrada);

        FuncionEsquema esquema = funcionEsquemaRepo.findByFuncionCodigo(6).orElse(null);
        String[][] matriz = distribucionSillaParser.parse(esquema.getEsquemaTemporal());

        Assertions.assertEquals("O", matriz[0][0]);
    }
}
