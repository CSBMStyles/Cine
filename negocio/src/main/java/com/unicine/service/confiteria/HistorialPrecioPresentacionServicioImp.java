package com.unicine.service.confiteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;
import com.unicine.entity.confiteria.HistorialPrecioPresentacion;
import com.unicine.enums.confiteria.TipoCambioPrecioPresentacion;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.confiteria.HistorialPrecioPresentacionRepo;
import com.unicine.transfer.dto.request.HistorialPrecioPresentacionRequest;
import com.unicine.transfer.dto.response.HistorialPrecioPresentacionResponse;
import com.unicine.transfer.mapper.HistorialPrecioPresentacionMapper;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

/**
 * Implementacion del historial de precios de presentaciones de confiteria.
 * 
 * PERMISOS (a gestionar en capa API):
 * - Cliente y administrador de teatro: solo consulta del ultimo registro
 *   para visualizar el porcentaje de descuento.
 * - Administrador: consulta completa y eliminacion de historial.
 */
@Service
@Validated
public class HistorialPrecioPresentacionServicioImp implements HistorialPrecioPresentacionServicio {

    private final HistorialPrecioPresentacionRepo historialRepo;
    private final HistorialPrecioPresentacionMapper historialMapper;

    public HistorialPrecioPresentacionServicioImp(HistorialPrecioPresentacionRepo historialRepo, HistorialPrecioPresentacionMapper historialMapper) {
        this.historialRepo = historialRepo;
        this.historialMapper = historialMapper;
    }

    // SECTION: Metodos de soporte

    private ConfiteriaPresentacion crearPresentacionReferencia(Integer codigo) {
        ConfiteriaPresentacion presentacion = new ConfiteriaPresentacion();
        presentacion.setCodigo(codigo);
        return presentacion;
    }

    private void validarExiste(Optional<HistorialPrecioPresentacion> historial) {
        if (historial.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRICE_HISTORY_NOT_FOUND);
        }
    }

    private Integer calcularPorcentaje(Double precioBase, Double precioNuevo, TipoCambioPrecioPresentacion tipo) {
        if (precioBase == null || precioBase == 0) {
            return 0;
        }

        double diferencia = tipo == TipoCambioPrecioPresentacion.DESCUENTO_TEMPORAL
                ? precioBase - precioNuevo
                : precioNuevo - precioBase;

        return (int) Math.round((diferencia / precioBase) * 100);
    }

    // SECTION: Implementacion de servicios

    @Override
    public HistorialPrecioPresentacionResponse registrar(HistorialPrecioPresentacionRequest request) throws Exception {

        if (request.getPrecioAnterior().equals(request.getPrecioNuevo())) {
            return null;
        }

        if (request.getPrecioNuevo() == null) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRICE_HISTORY_BASE_PRICE_REQUIRED);
        }

        TipoCambioPrecioPresentacion tipo = request.getPrecioNuevo() > request.getPrecioAnterior()
                ? TipoCambioPrecioPresentacion.AUMENTO
                : TipoCambioPrecioPresentacion.DESCUENTO_TEMPORAL;

        Integer porcentaje = calcularPorcentaje(request.getPrecioAnterior(), request.getPrecioNuevo(), tipo);

        HistorialPrecioPresentacion historial = HistorialPrecioPresentacion.builder()
                .precioAnterior(request.getPrecioAnterior())
                .precioNuevo(request.getPrecioNuevo())
                .tipoCambio(tipo)
                .porcentaje(porcentaje)
                .fechaCambio(LocalDateTime.now())
                .presentacion(crearPresentacionReferencia(request.getPresentacionCodigo()))
                .build();

        HistorialPrecioPresentacion registro = historialRepo.save(historial);
        return historialMapper.toResponse(registro);
    }

    @Override
    public List<HistorialPrecioPresentacionResponse> listarPorPresentacion(Integer codigoPresentacion) throws Exception {
        List<HistorialPrecioPresentacion> historial = historialRepo.findByPresentacionCodigoOrderByFechaCambioDesc(codigoPresentacion);
        if (historial.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRICE_HISTORY_NOT_FOUND);
        }
        return historialMapper.toResponseList(historial);
    }

    @Override
    public Optional<HistorialPrecioPresentacionResponse> obtenerUltimoPorPresentacion(Integer codigoPresentacion) throws Exception {
        return historialRepo.findTopByPresentacionCodigoOrderByFechaCambioDesc(codigoPresentacion)
                .map(historialMapper::toResponse);
    }

    @Override
    public void eliminarPorPresentacion(Integer codigoPresentacion) throws Exception {
        List<HistorialPrecioPresentacion> historial = historialRepo.findByPresentacionCodigoOrderByFechaCambioDesc(codigoPresentacion);
        historialRepo.deleteAll(historial);
    }

    @Override
    public void eliminarTodo() throws Exception {
        historialRepo.deleteAll();
    }

    @Override
    public Optional<HistorialPrecioPresentacionResponse> obtener(Integer codigo) throws Exception {
        Optional<HistorialPrecioPresentacion> buscado = historialRepo.findById(codigo);
        validarExiste(buscado);
        return buscado.map(historialMapper::toResponse);
    }
}
