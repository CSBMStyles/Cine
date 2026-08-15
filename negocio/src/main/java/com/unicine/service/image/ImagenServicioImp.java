package com.unicine.service.image;

import java.util.List;
import java.util.Optional;
import java.util.Comparator;
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
import com.unicine.enums.image.TipoImagen;
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
import com.unicine.util.funtional.image.GeneradorNombreImagen;
import com.unicine.util.funtional.image.RefactorizadorRuta;
import com.unicine.util.validation.catalog.domain.ImageErrorCatalog;

import io.imagekit.sdk.models.results.Result;
import io.imagekit.sdk.models.results.ResultFileVersionDetails;
import io.imagekit.sdk.models.results.ResultFileVersions;
import io.imagekit.sdk.models.results.ResultList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Servicio para persistir imagenes y sincronizarlas con ImageKit.
 */
@Service
@Validated
public class ImagenServicioImp implements ImagenServicio {

    private final ImagenRepo imagenRepo;
    private final ImageKitService imageKitService;
    private final RefactorizadorRuta refactorizadorRuta;
    private final GeneradorNombreImagen generadorNombreImagen;
    private final ImagenMapper imagenMapper;
    private final ClienteRepo clienteRepo;
    private final AdministradorRepo administradorRepo;
    private final AdministradorTeatroRepo administradorTeatroRepo;
    private final PeliculaRepo peliculaRepo;
    private final ConfiteriaRepo confiteriaRepo;

    public ImagenServicioImp(ImagenRepo imagenRepo, ImageKitService imageKitService,
                             RefactorizadorRuta refactorizadorRuta, GeneradorNombreImagen generadorNombreImagen,
                             ImagenMapper imagenMapper,
                             ClienteRepo clienteRepo, AdministradorRepo administradorRepo,
                             AdministradorTeatroRepo administradorTeatroRepo,
                             PeliculaRepo peliculaRepo, ConfiteriaRepo confiteriaRepo) {
        this.imagenRepo = imagenRepo;
        this.imageKitService = imageKitService;
        this.refactorizadorRuta = refactorizadorRuta;
        this.generadorNombreImagen = generadorNombreImagen;
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

    private TipoImagen resolverTipoImagen(ImagenRequest request, Imagenable propietario) {
        if (request.getTipoImagen() != null) {
            return request.getTipoImagen();
        }

        if (propietario instanceof Persona) {
            return TipoImagen.AVATAR;
        }

        if (propietario instanceof Pelicula) {
            return TipoImagen.POSTER;
        }

        return TipoImagen.PRODUCTO;
    }

    private void asignarMetadatosRegistro(Imagen imagen, Imagenable propietario) {
        if (!(propietario instanceof Pelicula pelicula)) {
            imagen.setOrden(null);
            imagen.setPrincipal(null);
            return;
        }

        Integer maxOrden = Optional.ofNullable(imagenRepo.findMaxOrdenByPelicula(pelicula.getCodigo()))
                .orElse(0);
        long totalImagenes = imagenRepo.countByPeliculaCodigo(pelicula.getCodigo());
        int siguienteOrden = Math.toIntExact(Math.max(maxOrden, totalImagenes) + 1);

        imagen.setOrden(siguienteOrden);
        imagen.setPrincipal(siguienteOrden == 1);
    }

    private Optional<Imagen> buscarSlotExistente(Imagenable propietario, TipoImagen tipoImagen) {
        if (!(propietario instanceof Pelicula pelicula) || !esSlotUnico(tipoImagen)) {
            return Optional.empty();
        }

        return imagenRepo.findFirstByPeliculaCodigoAndTipoImagenOrderByOrdenAsc(
                pelicula.getCodigo(), tipoImagen);
    }

    private boolean esSlotUnico(TipoImagen tipoImagen) {
        return tipoImagen == TipoImagen.POSTER || tipoImagen == TipoImagen.BANNER;
    }

    private Comparator<Imagen> comparadorOrden() {
        return Comparator.comparing(
                        Imagen::getOrden,
                        Comparator.nullsLast(Integer::compareTo))
                .thenComparing(Imagen::getCodigo);
    }

    private List<String> obtenerIdsOrdenados(List<Imagen> imagenes) {
        return imagenes.stream()
                .sorted(comparadorOrden())
                .map(Imagen::getCodigo)
                .collect(Collectors.toList());
    }

    private void promoverSiguientePrincipal(Imagen eliminada) {
        if (!(eliminada.getPelicula() != null && Boolean.TRUE.equals(eliminada.getPrincipal()))) {
            return;
        }

        imagenRepo.findByPeliculaCodigo(eliminada.getPelicula().getCodigo()).stream()
                .filter(imagen -> !imagen.getCodigo().equals(eliminada.getCodigo()))
                .sorted(comparadorOrden())
                .findFirst()
                .ifPresent(siguiente -> {
                    siguiente.setPrincipal(true);
                    imagenRepo.save(siguiente);
                });
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

    private ImagenResponse actualizarImagenExistente(Imagen imagen, ImagenRequest request,
                                                     MultipartFile file, Imagenable propietario,
                                                     TipoImagen tipoImagen) throws Exception {
        asignarPropietario(imagen, propietario);
        imagen.setTipoImagen(tipoImagen);

        String folder = constructorCarpeta(propietario);
        Result result = imageKitService.actualizarImagen(file, imagen.getCodigo(), folder, propietario, tipoImagen);

        imagen.setUrl(result.getUrl());

        Imagen actualizada = imagenRepo.save(imagen);
        return enriquecerResponse(actualizada, result, request.getTipoPropietario(), request.getCodigoPropietario());
    }

    private ImagenResponse enriquecerResponse(Imagen imagen, Result result, TipoPropietarioImagen tipoPropietario, Integer codigoPropietario) {
        ImagenResponse response = imagenMapper.toResponse(imagen);
        response.setTipoPropietario(tipoPropietario.name());
        response.setCodigoPropietario(codigoPropietario);
        completarDatosArchivo(response, result);
        return response;
    }

    /**
     * Completa la respuesta con los metadatos devueltos por ImageKit.
     */
    private void completarDatosArchivo(ImagenResponse response, Result result) {
        if (result == null) {
            return;
        }

        response.setNombre(result.getName());
        response.setFilePath(result.getFilePath());
        response.setThumbnailUrl(result.getThumbnail());
        response.setFileType(result.getFileType());
        response.setAltura(result.getHeight());
        response.setAnchura(result.getWidth());
        response.setTamanio(result.getSize());
        asignarVersion(response, result.getVersionInfo());
    }

    private void asignarVersion(ImagenResponse response, JsonElement versionInfo) {
        if (versionInfo == null || !versionInfo.isJsonObject()) {
            return;
        }

        JsonObject datosVersion = versionInfo.getAsJsonObject();
        if (datosVersion.has("id")) {
            response.setVersionId(datosVersion.get("id").getAsString());
        }
        if (datosVersion.has("name")) {
            response.setVersionName(datosVersion.get("name").getAsString());
        }
    }

    // !SECTION
    // SECTION: Implementacion de servicios

    @Override
    public ImagenResponse registrar(ImagenRequest request, MultipartFile file) throws Exception {
        Imagenable propietario = obtenerPropietario(request);
        validarExisteImagen(propietario);
        validarTamanoImagen(file);

        TipoImagen tipoImagen = resolverTipoImagen(request, propietario);

        Optional<Imagen> slotExistente = buscarSlotExistente(propietario, tipoImagen);
        if (slotExistente.isPresent()) {
            return actualizarImagenExistente(slotExistente.get(), request, file, propietario, tipoImagen);
        }

        Imagen imagen = imagenMapper.toEntity(request);
        asignarPropietario(imagen, propietario);
        imagen.setTipoImagen(tipoImagen);
        asignarMetadatosRegistro(imagen, propietario);

        String folder = constructorCarpeta(propietario);
        String nombreArchivo = generadorNombreImagen.generar(
                request.getNombre(),
                request.getTipoPropietario(),
                request.getCodigoPropietario(),
                tipoImagen,
                imagen.getOrden(),
                propietario
        );
        boolean nombreEstable = generadorNombreImagen.usaNombreDeterminista(request.getTipoPropietario());
        Result result = imageKitService.subirImagen(
                file,
                folder,
                propietario,
                false,
                nombreArchivo,
                tipoImagen,
                nombreEstable
        );

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

        TipoImagen tipoImagen = imagen.getTipoImagen() == null
                ? resolverTipoImagen(request, propietario)
                : imagen.getTipoImagen();

        return actualizarImagenExistente(imagen, request, file, propietario, tipoImagen);
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

        Imagen imagen = buscado.get();
        imageKitService.eliminarImagen(codigo);
        promoverSiguientePrincipal(imagen);
        imagenRepo.delete(imagen);
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

        if (propietario instanceof Pelicula pelicula) {
            return obtenerIdsOrdenados(imagenRepo.findByPeliculaCodigo(pelicula.getCodigo()));
        }

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
    // !SECTION
}
