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
import com.unicine.service.showing.FuncionEsquemaServicio;
import com.unicine.transfer.dto.request.FuncionEsquemaRequest;
import com.unicine.transfer.dto.response.FuncionEsquemaResponse;
import com.unicine.util.pagination.PaginadoManual;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller de esquemas de funcion — ocupacion de sillas por funcion.
 * Lectura publica (la usa la cartelera); escritura solo ADMIN o ADMINISTRADOR_TEATRO.
 */
@RestController
@RequestMapping("/api/funcion-esquemas")
@Validated
@Tag(name = "Esquemas de función", description = "Ocupación de sillas por función")
public class FuncionEsquemaController {

    private final FuncionEsquemaServicio esquemaServicio;

    public FuncionEsquemaController(FuncionEsquemaServicio esquemaServicio) {
        this.esquemaServicio = esquemaServicio;
    }

    // SECTION: Escritura protegida

    @PostMapping
    @Operation(summary = "Registrar esquema", description = "Solo ADMIN o ADMINISTRADOR_TEATRO.")
    public ResponseEntity<FuncionEsquemaResponse> registrar(
            @Valid @RequestBody FuncionEsquemaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(esquemaServicio.registrar(request));
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar esquema", description = "Solo ADMIN o ADMINISTRADOR_TEATRO.")
    public ResponseEntity<FuncionEsquemaResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody FuncionEsquemaRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        request.setCodigo(codigo);
        return ResponseEntity.ok(esquemaServicio.actualizar(request));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar esquema — requiere ?confirmacion=true", description = "Solo ADMIN.")
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
        esquemaServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura publica

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener esquema por código")
    public ResponseEntity<FuncionEsquemaResponse> obtener(
            @PathVariable @Positive Integer codigo) throws Exception {
        return esquemaServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar esquemas", description = "Lectura pública. Filtros: ?funcion= | ?page=&size=")
    public ResponseEntity<List<FuncionEsquemaResponse>> listar(
            @RequestParam(required = false) Integer funcion,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {
        List<FuncionEsquemaResponse> esquemas = esquemaServicio.listar();
        if (funcion != null) {
            esquemas = esquemas.stream()
                    .filter(e -> funcion.equals(e.getFuncionCodigo()))
                    .toList();
        }
        return ResponseEntity.ok(PaginadoManual.paginar(esquemas, page, size));
    }

    // !SECTION
}
