package com.unicine.api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.movie.ComentarioServicio;
import com.unicine.transfer.dto.request.ComentarioRequest;
import com.unicine.transfer.dto.response.ComentarioResponse;
import com.unicine.util.pagination.PaginadoManual;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller de comentarios — resenas publicas + reacciones.
 * Lectura abierta; escritura propia o ADMIN (el servicio valida asistencia).
 */
@RestController
@RequestMapping("/api/comentarios")
@Validated
@Tag(name = "Comentarios", description = "Reseñas de películas y reacciones")
public class ComentarioController {

    private final ComentarioServicio comentarioServicio;

    public ComentarioController(ComentarioServicio comentarioServicio) {
        this.comentarioServicio = comentarioServicio;
    }

    // SECTION: Escritura propia o ADMIN

    @PostMapping
    @Operation(summary = "Registrar comentario",
            description = "Cliente comenta con su propia cédula (se fuerza del principal salvo ADMIN). Requiere haber asistido.")
    public ResponseEntity<ComentarioResponse> registrar(
            @Valid @RequestBody ComentarioRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            request.setClienteCedula(principal.getCedula());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(comentarioServicio.registrar(request));
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar comentario", description = "Propio o ADMIN.")
    public ResponseEntity<ComentarioResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody ComentarioRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var actual = comentarioServicio.obtener(codigo);
        if (actual.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!principal.esAdministrador()
                && !principal.getCedula().equals(actual.get().getCliente().getCedula())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        request.setCodigo(codigo);
        if (!principal.esAdministrador()) {
            request.setClienteCedula(principal.getCedula());
        }
        return ResponseEntity.ok(comentarioServicio.actualizar(request));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar comentario — requiere ?confirmacion=true", description = "Propio o ADMIN.")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer codigo,
            @RequestParam boolean confirmacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        var actual = comentarioServicio.obtener(codigo);
        if (actual.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if (!principal.esAdministrador()
                && !principal.getCedula().equals(actual.get().getCliente().getCedula())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        comentarioServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{codigo}/like")
    @Operation(summary = "Dar like", description = "Requiere autenticación.")
    public ResponseEntity<ComentarioResponse> darLike(
            @PathVariable @Positive Integer codigo,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(comentarioServicio.darLike(codigo));
    }

    @PostMapping("/{codigo}/dislike")
    @Operation(summary = "Dar dislike", description = "Requiere autenticación.")
    public ResponseEntity<ComentarioResponse> darDislike(
            @PathVariable @Positive Integer codigo,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(comentarioServicio.darDislike(codigo));
    }

    // !SECTION
    // SECTION: Lectura publica

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener comentario por código")
    public ResponseEntity<ComentarioResponse> obtener(@PathVariable @Positive Integer codigo) throws Exception {
        return comentarioServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar comentarios",
            description = "Lectura pública. Filtros: ?pelicula=&cliente= | ?page=&size=")
    public ResponseEntity<List<ComentarioResponse>> listar(
            @RequestParam(required = false) Integer pelicula,
            @RequestParam(required = false) Integer cliente,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) throws Exception {
        if (pelicula != null) {
            return ResponseEntity.ok(comentarioServicio.listarPorPelicula(pelicula));
        }
        if (cliente != null) {
            return ResponseEntity.ok(comentarioServicio.listarPorCliente(cliente));
        }
        if (page != null || size != null) {
            return ResponseEntity.ok(PaginadoManual.paginar(comentarioServicio.listar(), page, size));
        }
        return ResponseEntity.ok(comentarioServicio.listar());
    }

    // !SECTION
}
