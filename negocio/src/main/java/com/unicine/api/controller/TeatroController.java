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

import com.unicine.service.theater.TeatroServicio;
import com.unicine.transfer.dto.request.TeatroRequest;
import com.unicine.transfer.dto.response.TeatroResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller REST para Teatro.
 */
@RestController
@RequestMapping("/api/teatros")
@Validated
@Tag(name = "Teatros", description = "Gestión de teatros")
public class TeatroController {

    private final TeatroServicio teatroServicio;

    public TeatroController(TeatroServicio teatroServicio) {
        this.teatroServicio = teatroServicio;
    }

    // SECTION: Escritura

    @PostMapping
    @Operation(summary = "Registrar teatro")
    public ResponseEntity<TeatroResponse> registrar(@Valid @RequestBody TeatroRequest request) throws Exception {
        TeatroResponse response = teatroServicio.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar teatro")
    public ResponseEntity<TeatroResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody TeatroRequest request) throws Exception {
        request.setCodigo(codigo);
        TeatroResponse response = teatroServicio.actualizar(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar teatro — requiere ?confirmacion=true")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer codigo,
            @RequestParam boolean confirmacion) throws Exception {
        teatroServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener teatro por código")
    public ResponseEntity<TeatroResponse> obtener(@PathVariable @Positive Integer codigo) throws Exception {
        return teatroServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar teatros", description = "Soporta ?page=&size=&sort=codigo&direction=asc/desc")
    public ResponseEntity<List<TeatroResponse>> listar(
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
            return ResponseEntity.ok(teatroServicio.listarPaginado(pageable));
        }

        return ResponseEntity.ok(teatroServicio.listar());
    }

    // !SECTION
}
