package com.unicine.service.image;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import com.unicine.entity.image.Imagen;
import com.unicine.entity.user.Administrador;
import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.entity.user.Cliente;
import com.unicine.entity.confiteria.Confiteria;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.user.Persona;
import com.unicine.entity.image.interfaced.Imagenable;
import com.unicine.enums.image.TipoPropietarioImagen;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.exception.ValidationException;
import com.unicine.repository.confiteria.ConfiteriaRepo;
import com.unicine.repository.image.ImagenRepo;
import com.unicine.repository.movie.PeliculaRepo;
import com.unicine.repository.user.AdministradorRepo;
import com.unicine.repository.user.AdministradorTeatroRepo;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.transfer.dto.request.ImagenRequest;
import com.unicine.transfer.dto.response.ImagenResponse;
import com.unicine.transfer.dto.response.VersionArchivoResponse;
import com.unicine.transfer.mapper.ImagenMapper;
import com.unicine.util.funtional.image.RefactorizadorRuta;
import com.unicine.util.validation.catalog.domain.ImageErrorCatalog;

import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultFileVersionDetails;
import io.imagekit.sdk.models.results.ResultFileVersions;
import io.imagekit.sdk.models.results.ResultList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
@Validated
public class ImagenServicioImp implements ImagenServicio {

    private final ImagenRepo imagenRepo;
    private final ImageKitService imageKitService;
    private final RefactorizadorRuta refactorizadorRuta;
    private final ImagenMapper imagenMapper;
    private final ClienteRepo clienteRepo;
    private final AdministradorRepo administradorRepo;
    private final AdministradorTeatroRepo administradorTeatroRepo;
    private final PeliculaRepo peliculaRepo;
    private final ConfiteriaRepo confiteriaRepo;

    public ImagenServicioImp(ImagenRepo imagenRepo, ImageKitService imageKitService,
                             RefactorizadorRuta refactorizadorRuta, ImagenMapper imagenMapper,
                             ClienteRepo clienteRepo, AdministradorRepo administradorRepo,
                             AdministradorTeatroRepo administradorTeatroRepo,
                             PeliculaRepo peliculaRepo, ConfiteriaRepo confiteriaRepo) {
        this.imagenRepo = imagenRepo;
        this.imageKitService = imageKitService;
        this.refactorizadorRuta = refactorizadorRuta;
        this.imagenMapper = imagenMapper;
        this.clienteRepo = clienteRepo;
        this.administradorRepo = administradorRepo;
        this.administradorTeatroRepo = administradorTeatroRepo;
        this.peliculaRepo = peliculaRepo;
        this.confiteriaRepo = confiteriaRepo;
    }

    // SECTION: Metodos de soporte

    private void validarExiste(Optional<Imagen> imagen) throws Exception {
        if (imagen.isEmpty()) {
            throw new ResourceNotFoundException(ImageErrorCatalog.DOMAIN_IMAGE_ENTITY_IMAGE_NOT_FOUND);
        }
    }

    private void validarExisteImagen(Imagenable propietario) throws Exception {
        if (propietario instanceof Persona) {
            Persona persona = (Persona) propietario;
            Optional<Imagen> imagenRelacion = imagenRepo.findByPersona(persona.getCedula());
            if (imagenRelacion.isPresent()) {
                throw new ValidationException(ImageErrorCatalog.DOMAIN_IMAGE_DUPLICATE_PERSON_ALREADY_HAS_IMAGE);
            }
        }
    }

    private String constructorCarpeta(Imagenable propietario) {
        if (propietario instanceof Persona) {
            return "personas/" + propietario.getCarpetaPrefijo();
        } else {
            String nombreEntidad = refactorizadorRuta.remplazarDenominacion(propietario.getNombre());
            String subCarpeta = propietario.getSubCarpeta();
            if (subCarpeta != null) {
                return propietario.getCarpetaPrefijo() + "/" + subCarpeta + "/" + nombreEntidad;
            }
            return propietario.getCarpetaPrefijo() + "/" + nombreEntidad;
        }
    }

    private void comprobarConfirmacion(boolean confirmacion) {
        if (!confirmacion) {
            throw new RuntimeException("La eliminación no fue confirmada");
        }
    }

    private List<String> obtenerEliminadosId(List<Imagen> eliminados) {
        return eliminados.stream().map(Imagen::getCodigo).collect(Collectors.toList());
    }

    private List<String> obtenerImagenesCarpetaId(ResultList result) {
        return result.getResults().stream().map(response -> response.getFileId()).collect(Collectors.toList());
    }

    private void validarTamanoImagen(MultipartFile file) throws Exception {
        long tamanoMaximo = 5;
        double conversion = file.getSize() / Math.pow(1024.0, 2);
        if (conversion > tamanoMaximo) {
            throw new Exception("El tamaño de la imagen excede el límite permitido de 5 MB. Tamaño actual: " + String.format("%.2f", conversion) + " MB");
        }
    }

    private Imagenable obtenerPropietario(ImagenRequest request) throws Exception {
        TipoPropietarioImagen tipo = request.getTipoPropietario();
        Integer codigo = request.getCodigoPropietario();

        return switch (tipo) {
            case CLIENTE -> clienteRepo.findById(codigo)
                    .orElseThrow(() -> new ResourceNotFoundException(ImageErrorCatalog.DOMAIN_IMAGE_ENTITY_IMAGE_NOT_FOUND));
            case ADMINISTRADOR -> administradorRepo.findById(codigo)
                    .orElseThrow(() -> new ResourceNotFoundException(ImageErrorCatalog.DOMAIN_IMAGE_ENTITY_IMAGE_NOT_FOUND));
            case ADMINISTRADOR_TEATRO -> administradorTeatroRepo.findById(codigo)
                    .orElseThrow(() -> new ResourceNotFoundException(ImageErrorCatalog.DOMAIN_IMAGE_ENTITY_IMAGE_NOT_FOUND));
            case PELICULA -> peliculaRepo.findById(codigo)
                    .orElseThrow(() -> new ResourceNotFoundException(ImageErrorCatalog.DOMAIN_IMAGE_ENTITY_IMAGE_NOT_FOUND));
            case CONFITERIA -> confiteriaRepo.findById(codigo)
                    .orElseThrow(() -> new ResourceNotFoundException(ImageErrorCatalog.DOMAIN_IMAGE_ENTITY_IMAGE_NOT_FOUND));
        };
    }

    private Imagenable obtenerPropietarioPorTipo(TipoPropietarioImagen tipo, Integer codigo) throws Exception {
        return obtenerPropietario(ImagenRequest.builder()
                .tipoPropietario(tipo)
                .codigoPropietario(codigo)
                .build());
    }

    private void asignarPropietario(Imagen imagen, Imagenable propietario) {
        if (propietario instanceof Cliente cliente) {
            imagen.setCliente(cliente);
        } else if (propietario instanceof Administrador administrador) {
            imagen.setAdministrador(administrador);
        } else if (propietario instanceof AdministradorTeatro administradorTeatro) {
            imagen.setAdministradorTeatro(administradorTeatro);
        } else if (propietario instanceof Pelicula pelicula) {
            imagen.setPelicula(pelicula);
        } else if (propietario instanceof Confiteria confiteria) {
            imagen.setConfiteria(confiteria);
        }
    }

    private ImagenResponse enriquecerResponse(Imagen imagen, Result result, TipoPropietarioImagen tipoPropietario, Integer codigoPropietario) {
        ImagenResponse response = imagenMapper.toResponse(imagen);
        response.setTipoPropietario(tipoPropietario.name());
        response.setCodigoPropietario(codigoPropietario);

        if (result != null) {
            response.setNombre(result.getName());
            response.setFilePath(result.getFilePath());
            response.setThumbnailUrl(result.getThumbnail());
            response.setFileType(result.getFileType());
            response.setAltura(result.getHeight());
            response.setAnchura(result.getWidth());
            response.setTamanio(result.getSize());
            if (result.getVersionInfo() != null && result.getVersionInfo().isJsonObject()) {
                JsonObject versionInfo = result.getVersionInfo().getAsJsonObject();
                if (versionInfo.has("id")) {
                    response.setVersionId(versionInfo.get("id").getAsString());
                }
                if (versionInfo.has("name")) {
                    response.setVersionName(versionInfo.get("name").getAsString());
                }
            }
        }
        return response;
    }

    // SECTION: Implementacion de servicios

    @Override
    public ImagenResponse registrar(ImagenRequest request, MultipartFile file) throws Exception {
        Imagenable propietario = obtenerPropietario(request);
        validarExisteImagen(propietario);
        validarTamanoImagen(file);

        Imagen imagen = imagenMapper.toEntity(request);
        asignarPropietario(imagen, propietario);

        String folder = constructorCarpeta(propietario);
        Result result = imageKitService.subirImagen(file, folder, propietario, false, request.getNombre(), request.getTipoImagenPelicula());

        imagen.setCodigo(result.getFileId());
        imagen.setUrl(result.getUrl());

        Imagen guardada = imagenRepo.save(imagen);
        return enriquecerResponse(guardada, result, request.getTipoPropietario(), request.getCodigoPropietario());
    }

    @Override
    public ImagenResponse actualizar(ImagenRequest request, MultipartFile file) throws Exception {
        validarTamanoImagen(file);
        Imagenable propietario = obtenerPropietario(request);

        Optional<Imagen> buscado = imagenRepo.findById(request.getCodigo());
        validarExiste(buscado);

        Imagen imagen = buscado.get();
        asignarPropietario(imagen, propietario);

        String folder = constructorCarpeta(propietario);
        Result result = imageKitService.actualizarImagen(file, imagen.getCodigo(), folder, propietario);

        imagen.setUrl(result.getUrl());

        Imagen actualizada = imagenRepo.save(imagen);
        return enriquecerResponse(actualizada, result, request.getTipoPropietario(), request.getCodigoPropietario());
    }

    @Override
    public ImagenResponse restaurar(ImagenRequest request, String versionId) throws Exception {
        Optional<Imagen> buscado = imagenRepo.findById(request.getCodigo());
        validarExiste(buscado);

        Imagen imagen = buscado.get();
        Result result = imageKitService.restaurarVersion(imagen.getCodigo(), versionId);
        imagen.setUrl(result.getUrl());

        Imagen restaurada = imagenRepo.save(imagen);
        return enriquecerResponse(restaurada, result, request.getTipoPropietario(), request.getCodigoPropietario());
    }

    @Override
    public ImagenResponse renombrar(ImagenRequest request, String nuevoNombre) throws Exception {
        Optional<Imagen> buscado = imagenRepo.findById(request.getCodigo());
        validarExiste(buscado);

        Imagenable propietario = obtenerPropietario(request);
        Imagen imagen = buscado.get();
        Result result = imageKitService.renombrarImagen(imagen.getCodigo(), nuevoNombre, propietario);
        imagen.setUrl(result.getUrl());

        Imagen renombrada = imagenRepo.save(imagen);
        return enriquecerResponse(renombrada, result, request.getTipoPropietario(), request.getCodigoPropietario());
    }

    @Override
    public void eliminar(String codigo, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);

        Optional<Imagen> buscado = imagenRepo.findById(codigo);
        validarExiste(buscado);

        imageKitService.eliminarImagen(codigo);
        imagenRepo.delete(buscado.get());
    }

    @Override
    public void eliminarMultiple(List<String> codigos, boolean confirmacion) throws Exception {
        comprobarConfirmacion(confirmacion);

        List<Imagen> imagenes = imagenRepo.findAllById(codigos);
        if (imagenes.isEmpty()) {
            throw new ResourceNotFoundException(ImageErrorCatalog.DOMAIN_IMAGE_ENTITY_IMAGE_NOT_FOUND);
        }

        imageKitService.eliminarImagenes(obtenerEliminadosId(imagenes));
        imagenRepo.deleteAll(imagenes);
    }

    @Override
    public Optional<ImagenResponse> obtener(String codigo) throws Exception {
        Optional<Imagen> buscado = imagenRepo.findById(codigo);
        validarExiste(buscado);

        Imagen imagen = buscado.get();
        TipoPropietarioImagen tipo = null;
        Integer codigoPropietario = null;

        if (imagen.getCliente() != null) {
            tipo = TipoPropietarioImagen.CLIENTE;
            codigoPropietario = imagen.getCliente().getCedula();
        } else if (imagen.getAdministrador() != null) {
            tipo = TipoPropietarioImagen.ADMINISTRADOR;
            codigoPropietario = imagen.getAdministrador().getCedula();
        } else if (imagen.getAdministradorTeatro() != null) {
            tipo = TipoPropietarioImagen.ADMINISTRADOR_TEATRO;
            codigoPropietario = imagen.getAdministradorTeatro().getCedula();
        } else if (imagen.getPelicula() != null) {
            tipo = TipoPropietarioImagen.PELICULA;
            codigoPropietario = imagen.getPelicula().getCodigo();
        } else if (imagen.getConfiteria() != null) {
            tipo = TipoPropietarioImagen.CONFITERIA;
            codigoPropietario = imagen.getConfiteria().getCodigo();
        }

        final TipoPropietarioImagen tipoFinal = tipo;
        final Integer codigoPropietarioFinal = codigoPropietario;
        return buscado.map(i -> enriquecerResponse(i, null, tipoFinal, codigoPropietarioFinal));
    }

    @Override
    public List<String> listar(TipoPropietarioImagen tipoPropietario, Integer codigoPropietario) throws Exception {
        Imagenable propietario = obtenerPropietarioPorTipo(tipoPropietario, codigoPropietario);
        String folder = constructorCarpeta(propietario);
        ResultList result = imageKitService.listarImagenes(folder);
        return obtenerImagenesCarpetaId(result);
    }

    @Override
    public List<VersionArchivoResponse> listarVersiones(String codigo) throws Exception {
        ResultFileVersions result = imageKitService.listarVersiones(codigo);
        List<ResultFileVersionDetails> listaRespuesta = result.getResultFileVersionDetailsList();

        return listaRespuesta.stream()
                .map(version -> VersionArchivoResponse.builder()
                        .fileId(version.getFileId())
                        .name(version.getUrl())
                        .updatedAt(version.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ImagenResponse> listarPaginado() {
        return imagenMapper.toResponseList(imagenRepo.findAll(PageRequest.of(0, 10)).toList());
    }

    @Override
    public List<ImagenResponse> listarAscendente() {
        return imagenMapper.toResponseList(imagenRepo.findAll(Sort.by("codigo").ascending()));
    }

    @Override
    public List<ImagenResponse> listarDescendente() {
        return imagenMapper.toResponseList(imagenRepo.findAll(Sort.by("codigo").descending()));
    }
}
