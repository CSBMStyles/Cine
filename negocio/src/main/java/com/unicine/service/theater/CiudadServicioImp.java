package com.unicine.service.theater;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.theater.Ciudad;
import com.unicine.repository.theater.CiudadRepo;
import com.unicine.transfer.dto.request.CiudadRequest;
import com.unicine.transfer.dto.response.CiudadResponse;
import com.unicine.transfer.mapper.CiudadMapper;

import jakarta.validation.Valid;
import com.unicine.util.validation.catalog.domain.TheaterErrorCatalog;
import com.unicine.exception.ResourceNotFoundException;

@Service
@Validated
public class CiudadServicioImp implements CiudadServicio {

    private final CiudadRepo ciudadRepo;
    private final CiudadMapper ciudadMapper;

    public CiudadServicioImp(CiudadRepo ciudadRepo, CiudadMapper ciudadMapper) {
        this.ciudadRepo = ciudadRepo;
        this.ciudadMapper = ciudadMapper;
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<Ciudad> ciudad) throws Exception {

        if (ciudad.isEmpty()) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_ENTITY_CITY_NOT_FOUND);
        }
    }

    private void validarExiste(List<Ciudad> ciudad) throws Exception {

        if (ciudad.isEmpty()) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_ENTITY_CITY_NOT_FOUND_BY_NAME);
        }
    }

    // !SECTION
    // SECTION: Implementacion de servicios

    @Override
    public CiudadResponse registrar(@Valid CiudadRequest request) throws Exception {
        Ciudad ciudad = ciudadMapper.toEntity(request);
        return ciudadMapper.toResponse(ciudadRepo.save(ciudad));
    }

    @Override
    public CiudadResponse actualizar(@Valid CiudadRequest request) throws Exception {
        Ciudad ciudad = ciudadMapper.toEntity(request);
        return ciudadMapper.toResponse(ciudadRepo.save(ciudad));
    }

    @Override
    public void eliminar(Integer codigo) throws Exception {
        Optional<Ciudad> buscado = ciudadRepo.findById(codigo);
        validarExiste(buscado);
        ciudadRepo.delete(buscado.get());
    }

    @Override
    public Optional<CiudadResponse> obtener(Integer codigo) throws Exception {
        Optional<Ciudad> buscado = ciudadRepo.findById(codigo);
        validarExiste(buscado);
        return buscado.map(ciudadMapper::toResponse);
    }

    @Override
    public List<CiudadResponse> obtenerNombre(String nombre) throws Exception {
        List<Ciudad> ciudades = ciudadRepo.findByNombre(nombre);
        validarExiste(ciudades);
        return ciudadMapper.toResponseList(ciudades);
    }

    @Override
    public List<CiudadResponse> listar() {
        return ciudadMapper.toResponseList(ciudadRepo.findAll());
    }

    @Override
    public List<CiudadResponse> listarPaginado() {
        return ciudadMapper.toResponseList(ciudadRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    @Override
    public List<CiudadResponse> listarAscendenteNombre() {
        return ciudadMapper.toResponseList(ciudadRepo.findAll(Sort.by("nombre").ascending()));
    }

    @Override
    public List<CiudadResponse> listarDescendenteNombre() {
        return ciudadMapper.toResponseList(ciudadRepo.findAll(Sort.by("nombre").descending()));
    }
    // !SECTION
}
