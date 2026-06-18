package com.unicine.service.confiteria;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.enums.confiteria.CategoriaConfiteria;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.confiteria.ConfiteriaRepo;
import com.unicine.util.validation.catalog.domain.PurchaseErrorCatalog;

import jakarta.validation.Valid;

/**
 * Implementacion del servicio de confiteria con logica de negocio completa.
 * 
 * Gestiona el catalogo de productos clasificados por categoria
 * y permite consultas por nombre y tipo de producto.
 */
@Service
@Validated
public class ConfiteriaServicioImp implements ConfiteriaServicio {

    private final ConfiteriaRepo confiteriaRepo;

    public ConfiteriaServicioImp(ConfiteriaRepo confiteriaRepo) {
        this.confiteriaRepo = confiteriaRepo;
    }

    // SECTION: Metodos de soporte

    /**
     * Metodo para comprobar la presencia de la confiteria que se esta buscando.
     * Lanza ResourceNotFoundException si no se encuentra.
     */
    private void validarExiste(Optional<Confiteria> confiteria) throws Exception {
        if (confiteria.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND);
        }
    }

    /**
     * Metodo para comprobar que la lista de confiterias no este vacia.
     * Lanza ResourceNotFoundException si la lista esta vacia.
     */
    private void validarExiste(List<Confiteria> confiterias) throws Exception {
        if (confiterias.isEmpty()) {
            throw new ResourceNotFoundException(PurchaseErrorCatalog.DOMAIN_PURCHASE_ENTITY_CONFECTIONERY_NOT_FOUND);
        }
    }

    /**
     * Metodo para validar la confirmacion de la eliminacion.
     */
    private void comprobarConfirmacion(boolean confirmacion) throws Exception {
        if (!confirmacion) {
            throw new RuntimeException("La eliminacion no fue confirmada");
        }
    }

    // SECTION: Implementacion de servicios CRUD

    @Override
    public Confiteria registrar(@Valid Confiteria confiteria) throws Exception {
        return confiteriaRepo.save(confiteria);
    }

    @Override
    public Confiteria actualizar(@Valid Confiteria confiteria) throws Exception {
        Optional<Confiteria> buscado = confiteriaRepo.findById(confiteria.getCodigo());
        validarExiste(buscado);
        return confiteriaRepo.save(confiteria);
    }

    @Override
    public void eliminar(@Valid Confiteria confiteria, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);
        confiteriaRepo.delete(confiteria);
    }

    @Override
    public Optional<Confiteria> obtener(Integer codigo) throws Exception {
        Optional<Confiteria> buscado = confiteriaRepo.findById(codigo);
        validarExiste(buscado);
        return buscado;
    }

    @Override
    public List<Confiteria> listar() {
        return confiteriaRepo.findAll();
    }

    @Override
    public List<Confiteria> listarPaginado() {
        return confiteriaRepo.findAll(PageRequest.of(0, 10)).toList();
    }

    // SECTION: Implementacion de metodos de negocio

    @Override
    public List<Confiteria> listarPorCategoria(CategoriaConfiteria categoria) throws Exception {
        List<Confiteria> confiterias = confiteriaRepo.findByCategoria(categoria);
        validarExiste(confiterias);
        return confiterias;
    }

    @Override
    public List<Confiteria> buscarPorNombre(String nombre) throws Exception {
        List<Confiteria> confiterias = confiteriaRepo.buscarPorNombre(nombre);
        validarExiste(confiterias);
        return confiterias;
    }
}
