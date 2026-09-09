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
 * Todo requiere autenticacion; escritura valida ownership en servidor.
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

    // SECTION: Escritura multipart

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Subir imagen",
            description = "Partes: datos (JSON ImagenRequest) + file (JPG/PNG/WEBP, max 5MB). Ownership validado en servidor.")
    public ResponseEntity<ImagenResponse> subir(
            @RequestPart("datos") @Valid ImagenRequest request,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(imagenServicio.registrar(request, file));
    }

    @PutMapping(value = "/{codigo}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Reemplazar imagen", description = "Partes: datos + file. Requiere autenticación.")
    public ResponseEntity<ImagenResponse> reemplazar(
            @PathVariable String codigo,
            @RequestPart("datos") @Valid ImagenRequest request,
            @RequestPart("file") MultipartFile file,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        request.setCodigo(codigo);
        return ResponseEntity.ok(imagenServicio.actualizar(request, file));
    }

    @PostMapping("/{codigo}/restaurar")
    @Operation(summary = "Restaurar versión", description = "Requiere ?versionId=. Compensación ante fallo externo incluida.")
    public ResponseEntity<ImagenResponse> restaurar(
            @PathVariable String codigo,
            @RequestParam String versionId,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ImagenRequest request = ImagenRequest.builder().codigo(codigo).build();
        return ResponseEntity.ok(imagenServicio.restaurar(request, versionId));
    }

    @PostMapping("/{codigo}/renombrar")
    @Operation(summary = "Renombrar imagen", description = "Requiere ?nombre=.")
    public ResponseEntity<ImagenResponse> renombrar(
            @PathVariable String codigo,
            @RequestParam String nombre,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ImagenRequest request = ImagenRequest.builder().codigo(codigo).build();
        return ResponseEntity.ok(imagenServicio.renombrar(request, nombre));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar imagen — requiere ?confirmacion=true")
    public ResponseEntity<Void> eliminar(
            @PathVariable String codigo,
            @RequestParam boolean confirmacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        imagenServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener metadata por fileId")
    public ResponseEntity<ImagenResponse> obtener(
            @PathVariable String codigo,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return imagenServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar fileIds por propietario",
            description = "Requiere ?tipoPropietario=CLIENTE|ADMINISTRADOR|ADMINISTRADOR_TEATRO|PELICULA|CONFITERIA&codigoPropietario=")
    public ResponseEntity<List<String>> listarPorPropietario(
            @RequestParam TipoPropietarioImagen tipoPropietario,
            @RequestParam Integer codigoPropietario,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(imagenServicio.listar(tipoPropietario, codigoPropietario));
    }

    @GetMapping("/{codigo}/versiones")
    @Operation(summary = "Listar versiones del archivo")
    public ResponseEntity<List<VersionArchivoResponse>> listarVersiones(
            @PathVariable String codigo,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(imagenServicio.listarVersiones(codigo));
    }

    // !SECTION
}
