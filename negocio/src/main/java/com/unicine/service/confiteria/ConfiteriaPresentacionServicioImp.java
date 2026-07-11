package com.unicine.service.confiteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;
import com.unicine.enums.confiteria.TipoCambioPrecioPresentacion;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.confiteria.ConfiteriaPresentacionRepo;
import com.unicine.transfer.dto.request.ConfiteriaPresentacionRequest;
import com.unicine.transfer.dto.request.HistorialPrecioPresentacionRequest;
import com.unicine.transfer.dto.response.ConfiteriaPresentacionResponse;
import com.unicine.transfer.mapper.ConfiteriaPresentacionMapper;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

/**
 * Implementacion del servicio de presentaciones de confiteria.
 * 
 * PERMISOS (a gestionar en capa API):
 * - Administrador y administrador de teatro: pueden crear, actualizar y eliminar
 *   presentaciones, incluyendo precio y precioBase.
 * - Cliente: solo consulta.
 */
@Service
@Validated
public class ConfiteriaPresentacionServicioImp implements ConfiteriaPresentacionServicio {

    private final ConfiteriaPresentacionRepo presentacionRepo;
    private final HistorialPrecioPresentacionServicio historialServicio;
    private final ConfiteriaPresentacionMapper presentacionMapper;

    public ConfiteriaPresentacionServicioImp(ConfiteriaPresentacionRepo presentacionRepo,
                                              HistorialPrecioPresentacionServicio historialServicio,
                                              ConfiteriaPresentacionMapper presentacionMapper) {
        this.presentacionRepo = presentacionRepo;
        this.historialServicio = historialServicio;
        this.presentacionMapper = presentacionMapper;
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<ConfiteriaPresentacion> presentacion) {
        if (presentacion.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRESENTATION_NOT_FOUND);
        }
    }

    private void validarExiste(List<ConfiteriaPresentacion> presentaciones) {
        if (presentaciones.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_PRESENTATION_NOT_FOUND);
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) throws Exception {
        if (!confirmacion) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_DELETE_DELETE_NOT_CONFIRMED);
        }
    }

    // SECTION: Implementacion de servicios CRUD

    @Override
    public ConfiteriaPresentacionResponse registrar(ConfiteriaPresentacionRequest request) throws Exception {

        ConfiteriaPresentacion presentacion = presentacionMapper.toEntity(request);

        if (presentacion.getPrecioBase() == null) {
            presentacion.setPrecioBase(presentacion.getPrecio());
        }

        ConfiteriaPresentacion registro = presentacionRepo.save(presentacion);
        return presentacionMapper.toResponse(registro);
    }

    @Override
    public ConfiteriaPresentacionResponse actualizar(ConfiteriaPresentacionRequest request, LocalDateTime fechaExpiracionTemporal) throws Exception {

        Optional<ConfiteriaPresentacion> buscado = presentacionRepo.findById(request.getCodigo());
        validarExiste(buscado);

        ConfiteriaPresentacion existente = buscado.get();
        Double precioAnterior = existente.getPrecio();
        Double precioNuevo = request.getPrecio();
        Double precioBaseAnterior = existente.getPrecioBase();

        ConfiteriaPresentacion presentacionMapeada = presentacionMapper.toEntity(request);

        existente.setPorcion(presentacionMapeada.getPorcion());
        existente.setUnidadMedida(presentacionMapeada.getUnidadMedida());
        existente.setConfiteria(presentacionMapeada.getConfiteria());

        if (!precioAnterior.equals(precioNuevo)) {
            HistorialPrecioPresentacionRequest historialRequest = HistorialPrecioPresentacionRequest.builder()
                .precioAnterior(precioAnterior)
                .precioNuevo(precioNuevo)
                .tipoCambio(TipoCambioPrecioPresentacion.AUMENTO)
                .porcentaje(0)
                .fechaCambio(LocalDateTime.now())
                .presentacionCodigo(existente.getCodigo())
                .build();

            historialServicio.registrar(historialRequest);

            if (precioNuevo > precioBaseAnterior) {
                existente.setPrecioBase(precioNuevo);
                existente.setFechaExpiracionTemporal(null);
            } else {
                existente.setFechaExpiracionTemporal(fechaExpiracionTemporal);
            }

            existente.setPrecio(precioNuevo);
        }

        ConfiteriaPresentacion actualizado = presentacionRepo.save(existente);
        return presentacionMapper.toResponse(actualizado);
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);

        Optional<ConfiteriaPresentacion> buscado = presentacionRepo.findById(codigo);
        validarExiste(buscado);

        presentacionRepo.delete(buscado.get());
    }

    @Override
    public Optional<ConfiteriaPresentacionResponse> obtener(Integer codigo) throws Exception {
        Optional<ConfiteriaPresentacion> buscado = presentacionRepo.findById(codigo);
        validarExiste(buscado);
        return buscado.map(presentacionMapper::toResponse);
    }

    @Override
    public List<ConfiteriaPresentacionResponse> listar() {
        return presentacionMapper.toResponseList(presentacionRepo.findAll());
    }

    @Override
    public List<ConfiteriaPresentacionResponse> listarPorConfiteria(Integer codigoConfiteria) throws Exception {
        List<ConfiteriaPresentacion> presentaciones = presentacionRepo.findByConfiteriaCodigo(codigoConfiteria);
        validarExiste(presentaciones);
        return presentacionMapper.toResponseList(presentaciones);
    }

    @Override
    public List<ConfiteriaPresentacionResponse> listarConDescuentoTemporal() {
        return presentacionMapper.toResponseList(presentacionRepo.findConDescuentoTemporalActivo());
    }
}
