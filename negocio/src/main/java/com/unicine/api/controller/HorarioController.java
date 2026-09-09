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
import com.unicine.service.showing.HorarioServicio;
import com.unicine.transfer.dto.request.HorarioRequest;
import com.unicine.transfer.dto.response.HorarioResponse;
import com.unicine.util.pagination.PaginadoManual;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller de horarios — lectura publica + administracion de programacion.
 * Solape devuelve 400 con codigo de dominio (contrato 4.0, ver 4.5.2).
 * Escritura solo ADMIN o ADMINISTRADOR_TEATRO.
 */
@RestController
@RequestMapping("/api/horarios")
@Validated
@Tag(name = "Horarios", description = "Programación de horarios por sala")
public class HorarioController {

    private final HorarioServicio horarioServicio;

    public HorarioController(HorarioServicio horarioServicio) {
        this.horarioServicio = horarioServicio;
    }

    // SECTION: Escritura protegida

    @PostMapping
    @Operation(summary = "Registrar horario",
            description = "Requiere ?salaCodigo=. Solo ADMIN o ADMINISTRADOR_TEATRO. Solape → 400.")
    public ResponseEntity<HorarioResponse> registrar(
            @Valid @RequestBody HorarioRequest request,
            @RequestParam @Positive Integer salaCodigo,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(horarioServicio.registrar(request, salaCodigo));
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar horario", description = "Solo ADMIN o ADMINISTRADOR_TEATRO. Solape → 400.")
    public ResponseEntity<HorarioResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody HorarioRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        request.setCodigo(codigo);
        return ResponseEntity.ok(horarioServicio.actualizar(request));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar horario — requiere ?confirmacion=true", description = "Solo ADMIN.")
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
        horarioServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura publica

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener horario por código")
    public ResponseEntity<HorarioResponse> obtener(@PathVariable @Positive Integer codigo) throws Exception {
        return horarioServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar horarios", description = "Lectura pública. ?page=&size=")
    public ResponseEntity<List<HorarioResponse>> listar(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        if (page != null || size != null) {
            return ResponseEntity.ok(PaginadoManual.paginar(horarioServicio.listar(), page, size));
        }
        return ResponseEntity.ok(horarioServicio.listar());
    }

    // !SECTION
}
