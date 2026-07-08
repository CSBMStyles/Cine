package com.unicine.service.movie;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.unicine.entity.movie.Pelicula;
import com.unicine.repository.movie.PeliculaRepo;
import com.unicine.transfer.dto.request.PeliculaRequest;
import com.unicine.transfer.dto.response.PeliculaResponse;
import com.unicine.transfer.mapper.PeliculaMapper;
import com.unicine.util.validation.catalog.domain.MovieErrorCatalog;
import com.unicine.exception.ResourceNotFoundException;

@Service
@Validated
public class PeliculaServicioImp implements PeliculaServicio {

    private final PeliculaRepo peliculaRepo;
    private final PeliculaMapper peliculaMapper;

    public PeliculaServicioImp(PeliculaRepo peliculaRepo, PeliculaMapper peliculaMapper) {
        this.peliculaRepo = peliculaRepo;
        this.peliculaMapper = peliculaMapper;
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<Pelicula> pelicula) throws Exception {

        if (pelicula.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_MOVIE_NOT_FOUND);
        }
    }

    private void validarExiste(List<Pelicula> pelicula) throws Exception {

        if (pelicula.isEmpty()) {
            throw new ResourceNotFoundException(MovieErrorCatalog.DOMAIN_MOVIE_ENTITY_MOVIES_NOT_FOUND_BY_NAME);
        }
    }

    private void validarExisteNombre(Pelicula pelicula) throws Exception {

        Optional<Pelicula> existe = peliculaRepo.obtenerPeliculaNombre(pelicula.getNombre());

        if (existe.isPresent()) {
            throw new RuntimeException(MovieErrorCatalog.DOMAIN_MOVIE_DUPLICATE_MOVIE_ALREADY_EXISTS.getMessage());
        }
    }

    private void validarRepiteNombre(Pelicula pelicula) throws Exception {

        Optional<Pelicula> existe = peliculaRepo.obtenerNombreExcluido(pelicula.getNombre(), pelicula.getCodigo());

        if (existe.isPresent()) {
            throw new RuntimeException(MovieErrorCatalog.DOMAIN_MOVIE_DUPLICATE_MOVIE_NAME_ALREADY_EXISTS.getMessage());
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) throws Exception {

        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
    }

    private void copiarPelicula(Pelicula origen, Pelicula destino) {
        destino.setGeneros(origen.getGeneros());
        destino.setNombre(origen.getNombre());
        destino.setRepartos(origen.getRepartos());
        destino.setSinopsis(origen.getSinopsis());
        destino.setUrlTrailer(origen.getUrlTrailer());
        destino.setPuntuacion(origen.getPuntuacion());
        destino.setRestriccionEdad(origen.getRestriccionEdad());
    }

    // SECTION: Implementacion de servicios

    @Override
    public PeliculaResponse registrar(PeliculaRequest request) throws Exception {

        Pelicula pelicula = peliculaMapper.toEntity(request);

        validarExisteNombre(pelicula);

        return peliculaMapper.toResponse(peliculaRepo.save(pelicula));
    }

    @Override
    public PeliculaResponse actualizar(PeliculaRequest request) throws Exception {

        Pelicula pelicula = peliculaMapper.toEntity(request);

        Optional<Pelicula> existente = peliculaRepo.findById(pelicula.getCodigo());
        validarExiste(existente);

        Pelicula actual = existente.get();
        copiarPelicula(pelicula, actual);

        validarRepiteNombre(actual);

        return peliculaMapper.toResponse(peliculaRepo.save(actual));
    }

    @Override
    public void eliminar(Integer codigo, boolean confirmacion) throws Exception {

        comprobarConfirmacion(confirmacion);

        Optional<Pelicula> buscado = peliculaRepo.findById(codigo);
        validarExiste(buscado);
        peliculaRepo.delete(buscado.get());
    }

    @Override
    public Optional<PeliculaResponse> obtener(Integer codigo) throws Exception {

        Optional<Pelicula> buscado = peliculaRepo.findById(codigo);

        validarExiste(buscado);

        return buscado.map(peliculaMapper::toResponse);
    }

    @Override
    public List<PeliculaResponse> obtenerNombrePeliculas(String nombre) throws Exception {

        List<Pelicula> peliculas = peliculaRepo.buscarNombres(nombre);

        validarExiste(peliculas);

        return peliculaMapper.toResponseList(peliculas);
    }

    @Override
    public List<PeliculaResponse> listar() {
        return peliculaMapper.toResponseList(peliculaRepo.findAll());
    }

    @Override
    public List<PeliculaResponse> listarPaginado() {

        return peliculaMapper.toResponseList(peliculaRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    @Override
    public List<PeliculaResponse> listarAscendente() {

        return peliculaMapper.toResponseList(peliculaRepo.findAll(Sort.by("codigo").ascending()));
    }

    @Override
    public List<PeliculaResponse> listarDescendente() {

        return peliculaMapper.toResponseList(peliculaRepo.findAll(Sort.by("codigo").descending()));
    }
}
