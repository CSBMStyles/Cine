package com.unicine.test.payment;

import com.unicine.enums.payment.EstadoPago;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EstadoPagoTest {

    @Test
    public void transitarFlujoFeliz() {
        Assertions.assertTrue(EstadoPago.PENDIENTE.puedePasarA(EstadoPago.EN_VERIFICACION));
        Assertions.assertTrue(EstadoPago.EN_VERIFICACION.puedePasarA(EstadoPago.PAGADA));
    }

    @Test
    public void rechazarSaltosYRetrocesos() {
        Assertions.assertFalse(EstadoPago.PENDIENTE.puedePasarA(EstadoPago.PAGADA));
        Assertions.assertFalse(EstadoPago.EN_VERIFICACION.puedePasarA(EstadoPago.PENDIENTE));
        Assertions.assertFalse(EstadoPago.PENDIENTE.puedePasarA(null));
        Assertions.assertFalse(EstadoPago.PAGADA.puedePasarA(EstadoPago.PAGADA));
    }

    @Test
    public void estadosFinalesNoAvanzan() {
        for (EstadoPago destino : EstadoPago.values()) {
            Assertions.assertFalse(EstadoPago.PAGADA.puedePasarA(destino));
            Assertions.assertFalse(EstadoPago.FALLIDA.puedePasarA(destino));
            Assertions.assertFalse(EstadoPago.EXPIRADA.puedePasarA(destino));
        }
    }
}
