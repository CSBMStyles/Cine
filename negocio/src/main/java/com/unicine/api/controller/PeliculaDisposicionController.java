package com.unicine.api.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.unicine.service.movie.PeliculaDisposicionServicio;
import com.unicine.transfer.dto.request.PeliculaDisposicionRequest;
import com.unicine.transfer.dto.response.PeliculaDisposicionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller REST para Película-Disposición (cartelera por ciudad).
 * Clave compuesta: (peliculaCodigo, ciudadCodigo).
 */
@RestController
@RequestMapping("/api/pelicula-disposiciones")
@Validated
@Tag(name = "Disposiciones", description = "Disposición de películas por ciudad")
public class PeliculaDisposicionController {

    private final PeliculaDisposicionServicio disposicionServicio;

    public PeliculaDisposicionController(PeliculaDisposicionServicio disposicionServicio) {
        this.disposicionServicio = disposicionServicio;
    }

    // SECTION: Escritura

    @PostMapping
    @Operation(summary = "Registrar disposición")
    public ResponseEntity<PeliculaDisposicionResponse> registrar(
            @Valid @RequestBody PeliculaDisposicionRequest request) throws Exception {
        PeliculaDisposicionResponse response = disposicionServicio.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    @Operation(summary = "Actualizar disposición (clave compuesta en body)")
    public ResponseEntity<PeliculaDisposicionResponse> actualizar(
            @Valid @RequestBody PeliculaDisposicionRequest request) throws Exception {
        PeliculaDisposicionResponse response = disposicionServicio.actualizar(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{peliculaCodigo}/{ciudadCodigo}")
    @Operation(summary = "Eliminar disposición — requiere ?confirmacion=true")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer peliculaCodigo,
            @PathVariable @Positive Integer ciudadCodigo,
            @RequestParam boolean confirmacion) throws Exception {
        disposicionServicio.eliminar(peliculaCodigo, ciudadCodigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura

    @GetMapping("/{peliculaCodigo}/{ciudadCodigo}")
    @Operation(summary = "Obtener disposición por clave compuesta")
    public ResponseEntity<PeliculaDisposicionResponse> obtener(
            @PathVariable @Positive Integer peliculaCodigo,
            @PathVariable @Positive Integer ciudadCodigo) throws Exception {
        return disposicionServicio.obtener(peliculaCodigo, ciudadCodigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar disposiciones", description = "Soporta ?page=&size=&sort=")
    public ResponseEntity<List<PeliculaDisposicionResponse>> listar(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {

        if (page != null || size != null || (sort != null && !sort.isBlank())) {
            int p = page != null ? page : 0;
            int s = size != null ? size : 10;
            Pageable pageable;
            if (sort != null && !sort.isBlank()) {
                boolean desc = "desc".equalsIgnoreCase(direction);
                Sort sortObj = desc ? Sort.by(sort).descending() : Sort.by(sort).ascending();
                pageable = PageRequest.of(p, s, sortObj);
            } else {
                pageable = PageRequest.of(p, s);
            }
            return ResponseEntity.ok(disposicionServicio.listarPaginado(pageable));
        }

        return ResponseEntity.ok(disposicionServicio.listar());
    }

    // !SECTION
}
