package com.unicine.service.purchase;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.entity.purchase.Compra;
import com.unicine.entity.purchase.CompraConfiteria;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.confiteria.ConfiteriaRepo;
import com.unicine.repository.purchase.CompraConfiteriaRepo;
import com.unicine.repository.purchase.CompraRepo;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

import jakarta.validation.Valid;

/**
 * Implementacion del servicio de items de confiteria dentro de una compra.
 * 
 * Gestiona el registro, actualizacion, consulta y eliminacion de items,
 * validando la existencia de la compra y la confiteria asociadas.
 */
@Service
@Validated
public class CompraConfiteriaServicioImp implements CompraConfiteriaServicio {

    private final CompraConfiteriaRepo compraConfiteriaRepo;
    private final CompraRepo compraRepo;
    private final ConfiteriaRepo confiteriaRepo;

    public CompraConfiteriaServicioImp(CompraConfiteriaRepo compraConfiteriaRepo,
                                       CompraRepo compraRepo,
                                       ConfiteriaRepo confiteriaRepo) {
        this.compraConfiteriaRepo = compraConfiteriaRepo;
        this.compraRepo = compraRepo;
        this.confiteriaRepo = confiteriaRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia del item de confiteria que se esta buscando.
     * Lanza ResourceNotFoundException si no se encuentra.
     */
    private void validarExiste(Optional<CompraConfiteria> compraConfiteria) {
        if (compraConfiteria.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND);
        }
    }

    /**
     * Metodo para comprobar que la lista de items no este vacia.
     * Lanza ResourceNotFoundException si la lista esta vacia.
     */
    private void validarExiste(List<CompraConfiteria> compraConfiterias) {
        if (compraConfiterias.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND);
        }
    }

    /**
     * Valida que la compra exista en la base de datos.
     */
    private void validarCompraExiste(Integer codigo) {
        Optional<Compra> compra = compraRepo.findById(codigo);
        if (compra.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_PURCHASE_NOT_FOUND);
        }
    }

    /**
     * Valida que la confiteria exista en la base de datos.
     */
    private void validarConfiteriaExiste(Integer codigo) {
        Optional<Confiteria> confiteria = confiteriaRepo.findById(codigo);
        if (confiteria.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND);
        }
    }

    /**
     * Metodo para validar la confirmacion de la eliminacion.
     */
    private void comprobarConfirmacion(boolean confirmacion) throws Exception {
        if (!confirmacion) {
            throw new BusinessRuleException(PurchaseErrorCatalog.DOMAIN_PURCHASE_DELETE_DELETE_NOT_CONFIRMED);
        }
    }

    /**
     * Valida que la compra y la confiteria asociadas al item existan.
     */
    private void validarRelaciones(CompraConfiteria compraConfiteria) {
        validarCompraExiste(compraConfiteria.getCompra().getCodigo());
        validarConfiteriaExiste(compraConfiteria.getConfiteria().getCodigo());
    }

    // SECTION: Implementacion de servicios CRUD

    @Override
    public CompraConfiteria registrar(@Valid CompraConfiteria compraConfiteria) {
        validarRelaciones(compraConfiteria);
        return compraConfiteriaRepo.save(compraConfiteria);
    }

    @Override
    public CompraConfiteria actualizar(@Valid CompraConfiteria compraConfiteria) {
        Optional<CompraConfiteria> buscado = compraConfiteriaRepo.findById(compraConfiteria.getCodigo());
        validarExiste(buscado);
        validarRelaciones(compraConfiteria);
        return compraConfiteriaRepo.save(compraConfiteria);
    }

    @Override
    public void eliminar(@Valid CompraConfiteria compraConfiteria, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);
        compraConfiteriaRepo.delete(compraConfiteria);
    }

    @Override
    public Optional<CompraConfiteria> obtener(Integer codigo) {
        Optional<CompraConfiteria> buscado = compraConfiteriaRepo.findById(codigo);
        validarExiste(buscado);
        return buscado;
    }

    @Override
    public List<CompraConfiteria> listar() {
        return compraConfiteriaRepo.findAll();
    }

    @Override
    public List<CompraConfiteria> listarPaginado() {
        return compraConfiteriaRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<CompraConfiteria> listarPorCompra(Integer codigoCompra) {
        validarCompraExiste(codigoCompra);
        List<CompraConfiteria> items = compraConfiteriaRepo.findByCompraCodigo(codigoCompra);
        validarExiste(items);
        return items;
    }

    @Override
    public List<CompraConfiteria> listarPorConfiteria(Integer codigoConfiteria) {
        validarConfiteriaExiste(codigoConfiteria);
        List<CompraConfiteria> items = compraConfiteriaRepo.findByConfiteriaCodigo(codigoConfiteria);
        validarExiste(items);
        return items;
    }

    @Override
    public Double calcularTotalPorCompra(Integer codigoCompra) {
        validarCompraExiste(codigoCompra);
        return compraConfiteriaRepo.calcularTotalPorCompra(codigoCompra);
    }
}
