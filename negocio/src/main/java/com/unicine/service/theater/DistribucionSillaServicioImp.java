package com.unicine.service.theater;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.theater.DistribucionSilla;
import com.unicine.repository.theater.DistribucionSillaRepo;
import com.unicine.transfer.dto.request.DistribucionSillaRequest;
import com.unicine.transfer.dto.response.DistribucionSillaResponse;
import com.unicine.transfer.mapper.DistribucionSillaMapper;

import jakarta.validation.Valid;
import com.google.gson.Gson;
import com.unicine.util.validation.catalog.domain.TheaterErrorCatalog;
import com.unicine.exception.ResourceNotFoundException;

@Service
@Validated
public class DistribucionSillaServicioImp implements DistribucionSillaServicio {

    private final DistribucionSillaRepo distribucionRepo;
    private final DistribucionSillaMapper distribucionMapper;

    public DistribucionSillaServicioImp(DistribucionSillaRepo distribucionRepo, DistribucionSillaMapper distribucionMapper) {
        this.distribucionRepo = distribucionRepo;
        this.distribucionMapper = distribucionMapper;
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<DistribucionSilla> distribucion) throws Exception {

        if (distribucion.isEmpty()) {
            throw new ResourceNotFoundException(TheaterErrorCatalog.DOMAIN_THEATER_ENTITY_SEAT_DISTRIBUTION_NOT_FOUND);
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
   }

    private void reemplazarDatos(DistribucionSilla distribucion) {

        Gson gson = new Gson();
        String[][] matriz = gson.fromJson(distribucion.getEsquema(), String[][].class);

        int filas = matriz.length;
        int columnas = (filas > 0) ? matriz[0].length : 0;

        int totalSillas = (int) Arrays.stream(matriz)
                .flatMap(Arrays::stream)
                .filter(silla -> silla != null && !silla.trim().isEmpty())
                .count();

        distribucion.setFilas(filas);
        distribucion.setColumnas(columnas);
        distribucion.setTotalSillas(totalSillas);
    }

     // SECTION: Implementacion de servicios

    @Override
    public DistribucionSillaResponse registrar(@Valid DistribucionSillaRequest request) throws Exception {

        DistribucionSilla distribucion = distribucionMapper.toEntity(request);
        reemplazarDatos(distribucion);

        return distribucionMapper.toResponse(distribucionRepo.save(distribucion));
    }

    @Override
    public DistribucionSillaResponse actualizar(@Valid DistribucionSillaRequest request) throws Exception {

        DistribucionSilla distribucion = distribucionMapper.toEntity(request);
        reemplazarDatos(distribucion);
        
        return distribucionMapper.toResponse(distribucionRepo.save(distribucion));
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception { 
        
        comprobarConfirmacion(confirmacion);

        Optional<DistribucionSilla> buscado = distribucionRepo.findById(codigo);
        validarExiste(buscado);
        distribucionRepo.delete(buscado.get());
    }

    @Override
    public Optional<DistribucionSillaResponse> obtener(Integer codigo) throws Exception {

        Optional<DistribucionSilla> buscado = distribucionRepo.findById(codigo);

        validarExiste(buscado);

        return buscado.map(distribucionMapper::toResponse);
    }

    @Override
    public List<DistribucionSillaResponse> listar() { 
        return distribucionMapper.toResponseList(distribucionRepo.findAll()); 
    }

    @Override
    public List<DistribucionSillaResponse> listarPaginado() { 

        return distribucionMapper.toResponseList(distribucionRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    @Override
    public List<DistribucionSillaResponse> listarAscendente() { 
        
        return distribucionMapper.toResponseList(distribucionRepo.findAll(Sort.by("codigo").ascending()));
    }

    @Override
    public List<DistribucionSillaResponse> listarDescendente() { 
        
        return distribucionMapper.toResponseList(distribucionRepo.findAll(Sort.by("codigo").descending()));
    }
}
