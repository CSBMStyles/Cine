package com.unicine.service.confiteria;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.confiteria.ConfiteriaPresentacion;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.confiteria.ConfiteriaPresentacionRepo;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

import jakarta.validation.Valid;

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

    public ConfiteriaPresentacionServicioImp(ConfiteriaPresentacionRepo presentacionRepo,
                                              HistorialPrecioPresentacionServicio historialServicio) {
        this.presentacionRepo = presentacionRepo;
        this.historialServicio = historialServicio;
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
    public ConfiteriaPresentacion registrar(@Valid ConfiteriaPresentacion presentacion) throws Exception {

        if (presentacion.getPrecioBase() == null) {
            presentacion.setPrecioBase(presentacion.getPrecio());
        }

        return presentacionRepo.save(presentacion);
    }

    @Override
    public ConfiteriaPresentacion actualizar(@Valid ConfiteriaPresentacion presentacion, LocalDateTime fechaExpiracionTemporal) throws Exception {

        Optional<ConfiteriaPresentacion> buscado = presentacionRepo.findById(presentacion.getCodigo());
        validarExiste(buscado);

        ConfiteriaPresentacion existente = buscado.get();
        Double precioAnterior = existente.getPrecio();
        Double precioNuevo = presentacion.getPrecio();
        Double precioBaseAnterior = existente.getPrecioBase();

        existente.setPorcion(presentacion.getPorcion());
        existente.setUnidadMedida(presentacion.getUnidadMedida());
        existente.setConfiteria(presentacion.getConfiteria());

        if (!precioAnterior.equals(precioNuevo)) {
            historialServicio.registrarCambio(existente, precioAnterior, precioNuevo, precioBaseAnterior);

            if (precioNuevo > precioBaseAnterior) {
                existente.setPrecioBase(precioNuevo);
                existente.setFechaExpiracionTemporal(null);
            } else {
                existente.setFechaExpiracionTemporal(fechaExpiracionTemporal);
            }

            existente.setPrecio(precioNuevo);
        }

        return presentacionRepo.save(existente);
    }

    @Override
    public void eliminar(@Valid ConfiteriaPresentacion presentacion, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);
        presentacionRepo.delete(presentacion);
    }

    @Override
    public Optional<ConfiteriaPresentacion> obtener(Integer codigo) throws Exception {
        Optional<ConfiteriaPresentacion> buscado = presentacionRepo.findById(codigo);
        validarExiste(buscado);
        return buscado;
    }

    @Override
    public List<ConfiteriaPresentacion> listar() {
        return presentacionRepo.findAll();
    }

    @Override
    public List<ConfiteriaPresentacion> listarPorConfiteria(Integer codigoConfiteria) throws Exception {
        List<ConfiteriaPresentacion> presentaciones = presentacionRepo.findByConfiteriaCodigo(codigoConfiteria);
        validarExiste(presentaciones);
        return presentaciones;
    }

    @Override
    public List<ConfiteriaPresentacion> listarConDescuentoTemporal() {
        return presentacionRepo.findConDescuentoTemporalActivo();
    }
}
