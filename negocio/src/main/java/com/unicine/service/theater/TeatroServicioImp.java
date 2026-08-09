package com.unicine.service.theater;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.theater.Teatro;
import com.unicine.repository.theater.TeatroRepo;
import com.unicine.transfer.dto.request.TeatroRequest;
import com.unicine.transfer.dto.response.TeatroResponse;
import com.unicine.transfer.mapper.TeatroMapper;

import jakarta.validation.Valid;
import com.unicine.util.validation.catalog.domain.TheaterErrorCatalog;
import com.unicine.exception.ResourceNotFoundException;

@Service
@Validated
public class TeatroServicioImp implements TeatroServicio {

    private final TeatroRepo teatroRepo;
    private final TeatroMapper teatroMapper;

    public TeatroServicioImp(TeatroRepo teatroRepo, TeatroMapper teatroMapper) {
        this.teatroRepo = teatroRepo;
        this.teatroMapper = teatroMapper;
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<Teatro> teatro) throws Exception {

        if (teatro.isEmpty()) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_ENTITY_THEATER_NOT_FOUND);
        }
    }

    private void validarExisteDireccion(Teatro teatro) throws Exception {

        Optional<Teatro> existe = teatroRepo.findByDireccion(teatro.getDireccion(), teatro.getCiudad().getCodigo());
       
        if (existe.isPresent()) {
            throw new RuntimeException("La dirección del teatro ya existe en la ciudad");
        }
    }

    private void validarRepiteDireccion(Teatro teatro) throws Exception {

        Optional<Teatro> existe = teatroRepo.buscarDireccionExcluido(teatro.getDireccion(), teatro.getCiudad().getCodigo(), teatro.getCodigo());
       
        if (existe.isPresent()) {
            throw new RuntimeException("La dirección del teatro ya existe en la ciudad");
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    // !SECTION
     // SECTION: Implementacion de servicios

    @Override
    public TeatroResponse registrar(@Valid TeatroRequest request) throws Exception { 

        Teatro teatro = teatroMapper.toEntity(request);
        validarExisteDireccion(teatro);

        return teatroMapper.toResponse(teatroRepo.save(teatro));
    }

    @Override
    public TeatroResponse actualizar(@Valid TeatroRequest request) throws Exception {

        Teatro teatro = teatroMapper.toEntity(request);
        validarRepiteDireccion(teatro);

        return teatroMapper.toResponse(teatroRepo.save(teatro));
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        Optional<Teatro> buscado = teatroRepo.findById(codigo);
        validarExiste(buscado);
        teatroRepo.delete(buscado.get());
    }

    @Override
    public Optional<TeatroResponse> obtener(Integer codigo) throws Exception {

        Optional<Teatro> buscado = teatroRepo.findById(codigo);

        validarExiste(buscado);

        return buscado.map(teatroMapper::toResponse);
    }

    @Override
    public List<TeatroResponse> listar() { 
        return teatroMapper.toResponseList(teatroRepo.findAll()); 
    }

    @Override
    public List<TeatroResponse> listarPaginado() { 

        return teatroMapper.toResponseList(teatroRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    @Override
    public List<TeatroResponse> listarAscendente() { 
        
        return teatroMapper.toResponseList(teatroRepo.findAll(Sort.by("codigo").ascending()));
    }

    @Override
    public List<TeatroResponse> listarDescendente() { 
        
        return teatroMapper.toResponseList(teatroRepo.findAll(Sort.by("codigo").descending()));
    }
     // !SECTION
}
