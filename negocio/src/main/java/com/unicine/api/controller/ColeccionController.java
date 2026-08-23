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

import com.unicine.service.movie.ColeccionServicio;
import com.unicine.transfer.dto.request.ColeccionRequest;
import com.unicine.transfer.dto.response.ColeccionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller REST para Colección (relación Cliente-Película).
 * Clave compuesta: (cedula, codigoPelicula).
 */
@RestController
@RequestMapping("/api/colecciones")
@Validated
@Tag(name = "Colecciones", description = "Colecciones cliente-película")
public class ColeccionController {

    private final ColeccionServicio coleccionServicio;

    public ColeccionController(ColeccionServicio coleccionServicio) {
        this.coleccionServicio = coleccionServicio;
    }

    // SECTION: Escritura

    @PostMapping
    @Operation(summary = "Registrar colección")
    public ResponseEntity<ColeccionResponse> registrar(@Valid @RequestBody ColeccionRequest request) throws Exception {
        ColeccionResponse response = coleccionServicio.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping
    @Operation(summary = "Actualizar colección (por clave compuesta en body)")
    public ResponseEntity<ColeccionResponse> actualizar(@Valid @RequestBody ColeccionRequest request) throws Exception {
        ColeccionResponse response = coleccionServicio.actualizar(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{cedula}/{codigoPelicula}")
    @Operation(summary = "Eliminar colección — requiere ?confirmacion=true")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer cedula,
            @PathVariable @Positive Integer codigoPelicula,
            @RequestParam boolean confirmacion) throws Exception {
        coleccionServicio.eliminar(cedula, codigoPelicula, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura

    @GetMapping("/{cedula}/{codigoPelicula}")
    @Operation(summary = "Obtener colección por clave compuesta")
    public ResponseEntity<ColeccionResponse> obtener(
            @PathVariable @Positive Integer cedula,
            @PathVariable @Positive Integer codigoPelicula) throws Exception {
        return coleccionServicio.obtener(cedula, codigoPelicula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar colecciones",
            description = "Filtros: ?cliente= , ?pelicula= , ?page=&size=&sort=")
    public ResponseEntity<List<ColeccionResponse>> listar(
            @RequestParam(required = false) Integer cliente,
            @RequestParam(required = false) Integer pelicula,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) throws Exception {

        if (cliente != null) {
            return ResponseEntity.ok(coleccionServicio.listarPorCliente(cliente));
        }
        if (pelicula != null) {
            return ResponseEntity.ok(coleccionServicio.listarPorPelicula(pelicula));
        }

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
            return ResponseEntity.ok(coleccionServicio.listarPaginado(pageable));
        }

        return ResponseEntity.ok(coleccionServicio.listar());
    }

    // !SECTION
}
