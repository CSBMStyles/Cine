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

import com.unicine.service.theater.SalaServicio;
import com.unicine.transfer.dto.request.SalaRequest;
import com.unicine.transfer.dto.response.SalaResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

/**
 * Controller REST para Sala.
 */
@RestController
@RequestMapping("/api/salas")
@Validated
@Tag(name = "Salas", description = "Gestión de salas de cine")
public class SalaController {

    private final SalaServicio salaServicio;

    public SalaController(SalaServicio salaServicio) {
        this.salaServicio = salaServicio;
    }

    // SECTION: Escritura

    @PostMapping
    @Operation(summary = "Registrar sala")
    public ResponseEntity<SalaResponse> registrar(@Valid @RequestBody SalaRequest request) throws Exception {
        SalaResponse response = salaServicio.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar sala")
    public ResponseEntity<SalaResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody SalaRequest request) throws Exception {
        request.setCodigo(codigo);
        SalaResponse response = salaServicio.actualizar(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar sala — requiere ?confirmacion=true")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer codigo,
            @RequestParam boolean confirmacion) throws Exception {
        salaServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener sala por código")
    public ResponseEntity<SalaResponse> obtener(@PathVariable @Positive Integer codigo) throws Exception {
        return salaServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar salas",
            description = "Filtros: ?nombre= , ?teatro= (id teatro), ?nombre= & ?teatro=, ?page=&size=&sort=")
    public ResponseEntity<List<SalaResponse>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer teatro,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) throws Exception {

        if (nombre != null && !nombre.isBlank() && teatro != null) {
            return ResponseEntity.ok(salaServicio.obtenerNombresTeatro(nombre, teatro));
        }
        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(salaServicio.obtenerNombre(nombre));
        }
        if (teatro != null) {
            return ResponseEntity.ok(salaServicio.listar());
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
            return ResponseEntity.ok(salaServicio.listarPaginado(pageable));
        }

        return ResponseEntity.ok(salaServicio.listar());
    }

    @GetMapping("/teatro/{teatroCodigo}/sala/{codigo}")
    @Operation(summary = "Obtener sala por código y teatro")
    public ResponseEntity<SalaResponse> obtenerPorTeatro(
            @PathVariable @Positive Integer codigo,
            @PathVariable @Positive Integer teatroCodigo) throws Exception {
        return salaServicio.obtenerIdTeatro(codigo, teatroCodigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // !SECTION
}
