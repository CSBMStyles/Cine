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

import com.unicine.enums.confiteria.CategoriaConfiteria;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.confiteria.ConfiteriaServicio;
import com.unicine.transfer.dto.request.ConfiteriaRequest;
import com.unicine.transfer.dto.response.ConfiteriaResponse;
import com.unicine.util.pagination.PaginadoManual;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller de confiteria — catalogo publico + administracion.
 * Lectura abierta; escritura solo ADMIN o ADMINISTRADOR_TEATRO.
 */
@RestController
@RequestMapping("/api/confiterias")
@Validated
@Tag(name = "Confiterías", description = "Catálogo y administración de confitería")
public class ConfiteriaController {

    private final ConfiteriaServicio confiteriaServicio;

    public ConfiteriaController(ConfiteriaServicio confiteriaServicio) {
        this.confiteriaServicio = confiteriaServicio;
    }

    // SECTION: Escritura protegida

    @PostMapping
    @Operation(summary = "Registrar confitería", description = "Solo ADMIN o ADMINISTRADOR_TEATRO.")
    public ResponseEntity<ConfiteriaResponse> registrar(
            @Valid @RequestBody ConfiteriaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(confiteriaServicio.registrar(request));
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar confitería", description = "Solo ADMIN o ADMINISTRADOR_TEATRO.")
    public ResponseEntity<ConfiteriaResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody ConfiteriaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        request.setCodigo(codigo);
        return ResponseEntity.ok(confiteriaServicio.actualizar(request));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar confitería — requiere ?confirmacion=true", description = "Solo ADMIN.")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer codigo,
            @RequestParam boolean confirmacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        confiteriaServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura publica

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener confitería por código")
    public ResponseEntity<ConfiteriaResponse> obtener(@PathVariable @Positive Integer codigo) throws Exception {
        return confiteriaServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar confitería",
            description = "Catálogo público. Filtros: ?categoria=SNACK&nombre=crispeta | ?page=&size=")
    public ResponseEntity<List<ConfiteriaResponse>> listar(
            @RequestParam(required = false) CategoriaConfiteria categoria,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) throws Exception {
        if (categoria != null) {
            return ResponseEntity.ok(confiteriaServicio.listarPorCategoria(categoria));
        }
        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(confiteriaServicio.buscarPorNombre(nombre));
        }
        if (page != null || size != null) {
            return ResponseEntity.ok(PaginadoManual.paginar(confiteriaServicio.listar(), page, size));
        }
        return ResponseEntity.ok(confiteriaServicio.listar());
    }

    // !SECTION
}
