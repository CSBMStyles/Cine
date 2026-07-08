package com.unicine.service.movie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.movie.PeliculaDisposicion;
import com.unicine.entity.movie.composed.PeliculaDisposicionCompuesta;
import com.unicine.enums.movie.EstadoPelicula;
import com.unicine.repository.movie.PeliculaDisposicionRepo;
import com.unicine.transfer.dto.request.PeliculaDisposicionRequest;
import com.unicine.transfer.dto.response.PeliculaDisposicionResponse;
import com.unicine.transfer.mapper.PeliculaDisposicionMapper;
import com.unicine.util.validation.catalog.domain.MovieErrorCatalog;
import com.unicine.exception.ResourceNotFoundException;

@Service
@Validated
public class PeliculaDisposicionServicioImp implements PeliculaDisposicionServicio {

    private final PeliculaDisposicionRepo peliculaDisposicionRepo;
    private final EstadoPeliculaService estadoPeliculaServicio;
    private final PeliculaDisposicionMapper peliculaDisposicionMapper;

    public PeliculaDisposicionServicioImp(PeliculaDisposicionRepo peliculaDisposicionRepo,
                                          EstadoPeliculaService estadoPeliculaServicio,
                                          PeliculaDisposicionMapper peliculaDisposicionMapper) {
        this.peliculaDisposicionRepo = peliculaDisposicionRepo;
        this.estadoPeliculaServicio = estadoPeliculaServicio;
        this.peliculaDisposicionMapper = peliculaDisposicionMapper;
    }

    // SECTION: Metodos de soporte

    private void ingresoEstadoIncial(PeliculaDisposicion peliculaDisposicion) {

        peliculaDisposicion.setEstadoPelicula(EstadoPelicula.PENDIENTE);
    }

    private PeliculaDisposicion disposicionEstadoModificado(PeliculaDisposicion peliculaDisposicion) {

        return estadoPeliculaServicio.actualizarEstado(peliculaDisposicion);
    }

    private void validarExiste(Optional<PeliculaDisposicion> peliculaDisposicion) throws Exception {

        if (peliculaDisposicion.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_MOVIE_DISPOSITION_NOT_FOUND);
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
    }

    // SECTION: Implementacion de servicios

    @Override
    public PeliculaDisposicionResponse registrar(PeliculaDisposicionRequest request) throws Exception {

        PeliculaDisposicion peliculaDisposicion = peliculaDisposicionMapper.toEntity(request);

        ingresoEstadoIncial(peliculaDisposicion);

        PeliculaDisposicion guardado = peliculaDisposicionRepo.save(peliculaDisposicion);

        // TODO: emitir evento de dominio PELICULA_DISPOSICION_CREADA para reactividad futura (SSE/WebSockets)

        return peliculaDisposicionMapper.toResponse(guardado);
    }

    @Override
    public PeliculaDisposicionResponse actualizar(PeliculaDisposicionRequest request) throws Exception {

        PeliculaDisposicion peliculaDisposicion = peliculaDisposicionMapper.toEntity(request);

        PeliculaDisposicion actualizado = disposicionEstadoModificado(peliculaDisposicion);

        // TODO: emitir evento de dominio PELICULA_DISPOSICION_ESTADO_CAMBIADO para reactividad futura (SSE/WebSockets)

        return peliculaDisposicionMapper.toResponse(actualizado);
    }

    @Override
    public void actualizarEstadoPeliculas() {
        estadoPeliculaServicio.actualizarEstadosAutomaticamente();

        // TODO: emitir evento de dominio PELICULA_ESTADOS_ACTUALIZADOS para reactividad futura (SSE/WebSockets)
    }

    @Override
    public void eliminar(Integer peliculaCodigo, Integer ciudadCodigo, boolean confirmacion) throws Exception {

        comprobarConfirmacion(confirmacion);

        PeliculaDisposicionCompuesta codigo = new PeliculaDisposicionCompuesta(ciudadCodigo, peliculaCodigo);
        Optional<PeliculaDisposicion> buscado = peliculaDisposicionRepo.findById(codigo);
        validarExiste(buscado);
        peliculaDisposicionRepo.delete(buscado.get());
    }

    @Override
    public Optional<PeliculaDisposicionResponse> obtener(Integer peliculaCodigo, Integer ciudadCodigo) throws Exception {

        PeliculaDisposicionCompuesta codigo = new PeliculaDisposicionCompuesta(ciudadCodigo, peliculaCodigo);
        Optional<PeliculaDisposicion> buscado = peliculaDisposicionRepo.findById(codigo);

        validarExiste(buscado);

        return buscado.map(peliculaDisposicionMapper::toResponse);
    }

    @Override
    public List<PeliculaDisposicionResponse> listar() {
        return peliculaDisposicionMapper.toResponseList(peliculaDisposicionRepo.findAll());
    }

    @Override
    public List<PeliculaDisposicionResponse> listarRecomendacionPeliculaEstado(PeliculaDisposicionRequest request, EstadoPelicula estadoExcluido) {

        return peliculaDisposicionMapper.toResponseList(
                peliculaDisposicionRepo.listarDisposicionesPelicula(request.getPeliculaCodigo(), estadoExcluido));
    }

    @Override
    public List<PeliculaDisposicionResponse> listarPaginado() {

        return peliculaDisposicionMapper.toResponseList(peliculaDisposicionRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    @Override
    public List<PeliculaDisposicionResponse> listarAscendente() {

        return peliculaDisposicionMapper.toResponseList(peliculaDisposicionRepo.findAll(Sort.by("codigo").ascending()));
    }

    @Override
    public List<PeliculaDisposicionResponse> listarDescendente() {

        return peliculaDisposicionMapper.toResponseList(peliculaDisposicionRepo.findAll(Sort.by("codigo").descending()));
    }
}
