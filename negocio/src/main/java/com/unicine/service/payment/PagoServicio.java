package com.unicine.service.payment;

import com.unicine.entity.payment.Pago;
import com.unicine.transfer.dto.request.OrdenPagoRequest;
import com.unicine.transfer.dto.response.OrdenPagoResponse;
import com.unicine.util.validation.catalog.ValidationMessages;
import java.util.List;
import java.util.Optional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

// Puerto de cobros con Strategy+Adapter: los controllers hablaran contra esta
// interfaz y el adaptador de Mercado Pago (4.7.2+) vivira en la futura
// PagoServicioImp, asi Wompi entrara despues sin tocar controllers.
public interface PagoServicio {

    // 2️⃣ Funciones del Cliente

    // SECTION: Metodos de negocio

    Pago registrarPagoPendiente(
            @NotNull(message = ValidationMessages.PAYMENT_PURCHASE_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE) Integer compraCodigo,
            @NotNull(message = ValidationMessages.PAYMENT_AMOUNT_NOT_NULL)
            @PositiveOrZero(message = ValidationMessages.PAYMENT_AMOUNT_POSITIVE) Double montoEsperado) throws Exception;

    Optional<Pago> obtenerPorCompra(
            @NotNull(message = ValidationMessages.PAYMENT_PURCHASE_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE) Integer compraCodigo) throws Exception;

    OrdenPagoResponse crearOrden(@Valid OrdenPagoRequest request) throws Exception;

    OrdenPagoResponse conciliarEstado(
            @NotNull(message = ValidationMessages.PAYMENT_PURCHASE_NOT_NULL)
            @Positive(message = ValidationMessages.ID_POSITIVE) Integer compraCodigo) throws Exception;

    // !SECTION

    // 1️⃣ Funciones del Administrador

    List<Pago> listar();

    // *️⃣ Funciones Generales

    Optional<OrdenPagoResponse> procesarNotificacion(String dataId, String tipo,
                                                     String firma, String requestId) throws Exception;
}
