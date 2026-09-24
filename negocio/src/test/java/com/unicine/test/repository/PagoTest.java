package com.unicine.test.repository;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.jdbc.Sql;

import com.unicine.entity.payment.Pago;
import com.unicine.enums.payment.EstadoPago;
import com.unicine.repository.payment.PagoRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class PagoTest {

    @Autowired
    private PagoRepo pagoRepo;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarPendiente() {
        Pago pago = Pago.builder()
                .compraCodigo(1)
                .mercadoPagoId(null)
                .idempotencyKey(UUID.randomUUID().toString())
                .montoEsperado(17000.0)
                .estado(EstadoPago.PENDIENTE)
                .build();

        Pago guardado = pagoRepo.save(pago);

        Assertions.assertNotNull(guardado.getCodigo());
        Assertions.assertEquals(EstadoPago.PENDIENTE, guardado.getEstado());
        Assertions.assertNotNull(guardado.getFechaCreacion());

        Optional<Pago> buscado = pagoRepo.findByCompraCodigo(1);
        Assertions.assertTrue(buscado.isPresent());
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void rechazarCompraDuplicada() {
        Pago primero = Pago.builder()
                .compraCodigo(2)
                .idempotencyKey(UUID.randomUUID().toString())
                .montoEsperado(20000.0)
                .estado(EstadoPago.PENDIENTE)
                .build();
        pagoRepo.saveAndFlush(primero);

        Pago duplicado = Pago.builder()
                .compraCodigo(2)
                .idempotencyKey(UUID.randomUUID().toString())
                .montoEsperado(20000.0)
                .estado(EstadoPago.PENDIENTE)
                .build();

        Assertions.assertThrows(DataIntegrityViolationException.class, () -> {
            pagoRepo.saveAndFlush(duplicado);
        });
    }
}
