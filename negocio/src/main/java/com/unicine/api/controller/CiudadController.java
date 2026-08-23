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

import com.unicine.service.theater.CiudadServicio;
import com.unicine.transfer.dto.request.CiudadRequest;
import com.unicine.transfer.dto.response.CiudadResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Controller REST para el agregado Ciudad.
 * Delega toda regla de negocio a {@link CiudadServicio}.
 */
@RestController
@RequestMapping("/api/ciudades")
@Validated
@Tag(name = "Ciudades", description = "Gestión de ciudades — catálogo y administración")
public class CiudadController {

    private final CiudadServicio ciudadServicio;

    public CiudadController(CiudadServicio ciudadServicio) {
        this.ciudadServicio = ciudadServicio;
    }

    // SECTION: Escritura

    @PostMapping
    @Operation(summary = "Registrar ciudad", description = "Crea una nueva ciudad. Requiere autenticación.")
    public ResponseEntity<CiudadResponse> registrar(@Valid @RequestBody CiudadRequest request) throws Exception {
        CiudadResponse response = ciudadServicio.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar ciudad")
    public ResponseEntity<CiudadResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody CiudadRequest request) throws Exception {
        request.setCodigo(codigo);
        CiudadResponse response = ciudadServicio.actualizar(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar ciudad")
    public ResponseEntity<Void> eliminar(@PathVariable @Positive Integer codigo) throws Exception {
        ciudadServicio.eliminar(codigo);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener ciudad por código")
    public ResponseEntity<CiudadResponse> obtener(@PathVariable @Positive Integer codigo) throws Exception {
        return ciudadServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar ciudades",
            description = "Soporta filtros: ?nombre=Armenia | ?page=0&size=10&sort=nombre&direction=asc | sin params lista completa")
    public ResponseEntity<List<CiudadResponse>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) throws Exception {

        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(ciudadServicio.obtenerNombre(nombre));
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
            return ResponseEntity.ok(ciudadServicio.listarPaginado(pageable));
        }

        return ResponseEntity.ok(ciudadServicio.listar());
    }

    // !SECTION
}
