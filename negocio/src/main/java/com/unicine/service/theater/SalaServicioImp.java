package com.unicine.service.theater;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.theater.Sala;
import com.unicine.enums.theater.TipoSala;
import com.unicine.repository.theater.SalaRepo;
import com.unicine.transfer.dto.request.SalaRequest;
import com.unicine.transfer.dto.response.SalaResponse;
import com.unicine.transfer.mapper.SalaMapper;
import com.unicine.util.initializer.SalaPrecioInit;

import jakarta.validation.Valid;
import com.unicine.util.validation.catalog.domain.TheaterErrorCatalog;
import com.unicine.exception.ResourceNotFoundException;

@Service
@Validated
public class SalaServicioImp implements SalaServicio {

    private final SalaRepo salaRepo;
    private final SalaPrecioInit precioInitalizer;
    private final SalaMapper salaMapper;

    public SalaServicioImp(SalaRepo salaRepo, SalaPrecioInit preciosBaseConfig, SalaMapper salaMapper) {
        this.salaRepo = salaRepo;
        this.precioInitalizer = preciosBaseConfig;
        this.salaMapper = salaMapper;
    }

    @Override
    public Double obtenerPrecioBase(TipoSala tipoSala) {
        return precioInitalizer.obtenerPrecio(tipoSala);
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<Sala> sala) throws Exception {

        if (sala.isEmpty()) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_ENTITY_ROOM_NOT_FOUND);
        }
    }

    private void validarExiste(List<Sala> sala) throws Exception {

        if (sala.isEmpty()) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_ENTITY_ROOMS_NOT_FOUND_BY_NAME);
        }
    }

    private void validarExisteNombre(Sala sala) throws Exception {

        Optional<Sala> existe = salaRepo.buscarNombreValidacion(sala.getNombre(), sala.getTeatro().getCodigo());
       
        if (existe.isPresent()) {
            throw new RuntimeException("El nombre de la sala ya existe en el sala");
        }
    }

    private void validarRepiteNombre(Sala sala) throws Exception {

        Optional<Sala> existe = salaRepo.buscarNombreExcluido(sala.getNombre(), sala.getTeatro().getCodigo(), sala.getCodigo());
       
        if (existe.isPresent()) {
            throw new RuntimeException("El nombre de la sala ya existe en el sala");
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
    public SalaResponse registrar(@Valid SalaRequest request) throws Exception { 

        Sala sala = salaMapper.toEntity(request);
        validarExisteNombre(sala);

        return salaMapper.toResponse(salaRepo.save(sala));
    }

    @Override
    public SalaResponse actualizar(@Valid SalaRequest request) throws Exception {

        Sala sala = salaMapper.toEntity(request);
        validarRepiteNombre(sala);

        return salaMapper.toResponse(salaRepo.save(sala));
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        Optional<Sala> buscado = salaRepo.findById(codigo);
        validarExiste(buscado);
        salaRepo.delete(buscado.get());
    }

    @Override
    public Optional<SalaResponse> obtener(Integer codigo) throws Exception {

        Optional<Sala> buscado = salaRepo.findById(codigo);

        validarExiste(buscado);

        return buscado.map(salaMapper::toResponse);
    }

    @Override
    public List<SalaResponse> obtenerNombre(String nombre) throws Exception { 

        List<Sala> salas = salaRepo.buscarNombre(nombre);

        validarExiste(salas);

        return salaMapper.toResponseList(salas); 
    }

    @Override
    public Optional<SalaResponse> obtenerIdTeatro(Integer codigo, Integer teatroElegido) throws Exception { 

        Optional<Sala> sala = salaRepo.buscarIdTeatro(codigo, teatroElegido);

        validarExiste(sala);

        return sala.map(salaMapper::toResponse); 
    }

    @Override
    public List<SalaResponse> obtenerNombresTeatro(String nombre, Integer teatroElegido) throws Exception { 

        List<Sala> salas = salaRepo.buscarNombreTeatro(nombre, teatroElegido);

        validarExiste(salas);

        return salaMapper.toResponseList(salas); 
    }

    @Override
    public List<SalaResponse> listar() { 
        return salaMapper.toResponseList(salaRepo.findAll()); 
    }

    @Override
    public List<SalaResponse> listarPaginado() {

        return salaMapper.toResponseList(salaRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    @Override
    public List<SalaResponse> listarPaginado(Pageable pageable) {
        return salaMapper.toResponseList(salaRepo.findAll(pageable).toList());
    }

    @Override
    public List<SalaResponse> listarAscendente() { 
        
        return salaMapper.toResponseList(salaRepo.findAll(Sort.by("codigo").ascending()));
    }

    @Override
    public List<SalaResponse> listarDescendente() { 
        
        return salaMapper.toResponseList(salaRepo.findAll(Sort.by("codigo").descending()));
    }
    // !SECTION
}
