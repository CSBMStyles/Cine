package com.unicine.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.unicine.enums.image.TipoPropietarioImagen;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.image.ImagenServicio;
import com.unicine.transfer.dto.request.ImagenRequest;
import com.unicine.transfer.dto.response.ImagenResponse;
import com.unicine.transfer.dto.response.VersionArchivoResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Controller de imagenes — gateway ImageKit detras del servicio.
 * Lectura publica; escritura autenticada con ownership por rol (5.2 B5).
 * Limite 5MB (spring.servlet.multipart). Nunca expone el SDK en responses.
 */
@RestController
@RequestMapping("/api/imagenes")
@Validated
@Tag(name = "Imágenes", description = "Carga y gestión de imágenes (ImageKit)")
public class ImagenController {

    private final ImagenServicio imagenServicio;

    public ImagenController(ImagenServicio imagenServicio) {
        this.imagenServicio = imagenServicio;
    }

    // SECTION: 2️⃣ Funciones del Cliente

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir imagen",
            description = "Partes: datos (JSON ImagenRequest) + file (JPG/PNG/WEBP, max 5MB). "
                    + "CLIENTE/ADMINISTRADOR_TEATRO solo su propia imagen; ADMIN global cualquiera.")
    public ResponseEntity<ImagenResponse> subir(
            @RequestPart("datos") @Valid ImagenRequest request,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!puedeGestionar(principal, request.getTipoPropietario(), request.getCodigoPropietario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(imagenServicio.registrar(request, file));
    }

    @PutMapping(value = "/{codigo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Reemplazar imagen",
            description = "Partes: datos + file. Dueño de la imagen o ADMIN.")
    public ResponseEntity<ImagenResponse> reemplazar(
            @PathVariable String codigo,
            @RequestPart("datos") @Valid ImagenRequest request,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ImagenResponse existente = obtenerImagenExistente(codigo);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        TipoPropietarioImagen tipo = tipoPropietario(existente);
        if (!puedeGestionar(principal, tipo, existente.getCodigoPropietario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        // El dueño se toma de la imagen persistida, no del body: evita cambiar de propietario.
        request.setCodigo(codigo);
        request.setTipoPropietario(tipo);
        request.setCodigoPropietario(existente.getCodigoPropietario());
        return ResponseEntity.ok(imagenServicio.actualizar(request, file));
    }

    // !SECTION
    // SECTION: 1️⃣ Funciones del Administrador

    @PostMapping("/{codigo}/restaurar")
    @Operation(summary = "Restaurar versión",
            description = "Requiere ?versionId=. Dueño de la imagen o ADMIN. Compensación ante fallo externo incluida.")
    public ResponseEntity<ImagenResponse> restaurar(
            @PathVariable String codigo,
            @RequestParam String versionId,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ImagenResponse existente = obtenerImagenExistente(codigo);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        TipoPropietarioImagen tipo = tipoPropietario(existente);
        if (!puedeGestionar(principal, tipo, existente.getCodigoPropietario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ImagenRequest request = ImagenRequest.builder()
                .codigo(codigo)
                .tipoPropietario(tipo)
                .codigoPropietario(existente.getCodigoPropietario())
                .build();
        return ResponseEntity.ok(imagenServicio.restaurar(request, versionId));
    }

    @PostMapping("/{codigo}/renombrar")
    @Operation(summary = "Renombrar imagen",
            description = "Requiere ?nombre=. Dueño de la imagen o ADMIN.")
    public ResponseEntity<ImagenResponse> renombrar(
            @PathVariable String codigo,
            @RequestParam String nombre,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ImagenResponse existente = obtenerImagenExistente(codigo);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        TipoPropietarioImagen tipo = tipoPropietario(existente);
        if (!puedeGestionar(principal, tipo, existente.getCodigoPropietario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        ImagenRequest request = ImagenRequest.builder()
                .codigo(codigo)
                .tipoPropietario(tipo)
                .codigoPropietario(existente.getCodigoPropietario())
                .build();
        return ResponseEntity.ok(imagenServicio.renombrar(request, nombre));
    }

    // !SECTION
    // SECTION: 2️⃣ Funciones del Cliente

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar imagen — requiere ?confirmacion=true",
            description = "Dueño de la imagen o ADMIN.")
    public ResponseEntity<Void> eliminar(
            @PathVariable String codigo,
            @RequestParam boolean confirmacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ImagenResponse existente = obtenerImagenExistente(codigo);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        if (!puedeGestionar(principal, tipoPropietario(existente), existente.getCodigoPropietario())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        imagenServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: 2️⃣ Funciones del Cliente

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener metadata por fileId")
    public ResponseEntity<ImagenResponse> obtener(@PathVariable String codigo) throws Exception {
        return imagenServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar fileIds por propietario",
            description = "Requiere ?tipoPropietario=CLIENTE|ADMINISTRADOR|ADMINISTRADOR_TEATRO|PELICULA|CONFITERIA&codigoPropietario=")
    public ResponseEntity<List<String>> listarPorPropietario(
            @RequestParam TipoPropietarioImagen tipoPropietario,
            @RequestParam Integer codigoPropietario) throws Exception {
        return ResponseEntity.ok(imagenServicio.listar(tipoPropietario, codigoPropietario));
    }

    @GetMapping("/{codigo}/versiones")
    @Operation(summary = "Listar versiones del archivo")
    public ResponseEntity<List<VersionArchivoResponse>> listarVersiones(
            @PathVariable String codigo) throws Exception {
        return ResponseEntity.ok(imagenServicio.listarVersiones(codigo));
    }

    // !SECTION
    // SECTION: Metodos de soporte

    /**
     * Carga la imagen que se va a modificar. Devuelve null cuando no existe para
     * que el controller responda 404 sin depender del servicio.
     */
    private ImagenResponse obtenerImagenExistente(String codigo) throws Exception {
        return imagenServicio.obtener(codigo).orElse(null);
    }

    /**
     * Ownership de imagenes (5.2 B5): ADMIN global gestiona cualquier propietario;
     * CLIENTE y ADMINISTRADOR_TEATRO solo su propia imagen. Propietarios de catalogo
     * (PELICULA, CONFITERIA) quedan reservados al ADMIN.
     */
    private boolean puedeGestionar(UsuarioPrincipal principal, TipoPropietarioImagen tipo,
            Integer codigoPropietario) {
        if (principal.esAdministrador()) {
            return true;
        }
        if (tipo == null || codigoPropietario == null) {
            return false;
        }
        return switch (tipo) {
            case CLIENTE -> principal.getTipo() == TipoUsuario.CLIENTE
                    && principal.getCedula().equals(codigoPropietario);
            case ADMINISTRADOR_TEATRO -> principal.getTipo() == TipoUsuario.ADMINISTRADOR_TEATRO
                    && principal.getCedula().equals(codigoPropietario);
            default -> false;
        };
    }

    private TipoPropietarioImagen tipoPropietario(ImagenResponse imagen) {
        if (imagen.getTipoPropietario() == null) {
            return null;
        }
        try {
            return TipoPropietarioImagen.valueOf(imagen.getTipoPropietario());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    // !SECTION
}
