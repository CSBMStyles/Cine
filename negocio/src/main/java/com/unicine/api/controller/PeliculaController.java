package com.unicine.api.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

import com.unicine.service.movie.PeliculaServicio;
import com.unicine.transfer.dto.request.PeliculaRequest;
import com.unicine.transfer.dto.response.PeliculaResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller REST para Película.
 * GET público (permitAll), escritura requiere autenticación.
 *
 * Incluye demo del método QUERY (RFC 10008) — ver /query-demo.
 */
@RestController
@RequestMapping("/api/peliculas")
@Validated
@Tag(name = "Películas", description = "Catálogo de películas")
public class PeliculaController {

    private final PeliculaServicio peliculaServicio;

    public PeliculaController(PeliculaServicio peliculaServicio) {
        this.peliculaServicio = peliculaServicio;
    }

    // SECTION: Escritura

    @PostMapping
    @Operation(summary = "Registrar película")
    public ResponseEntity<PeliculaResponse> registrar(@Valid @RequestBody PeliculaRequest request) throws Exception {
        PeliculaResponse response = peliculaServicio.registrar(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar película")
    public ResponseEntity<PeliculaResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody PeliculaRequest request) throws Exception {
        request.setCodigo(codigo);
        PeliculaResponse response = peliculaServicio.actualizar(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar película — requiere ?confirmacion=true")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer codigo,
            @RequestParam boolean confirmacion) throws Exception {
        peliculaServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: Lectura

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener película por código")
    public ResponseEntity<PeliculaResponse> obtener(@PathVariable @Positive Integer codigo) throws Exception {
        return peliculaServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar películas",
            description = "Filtros: ?nombre= , ?page=0&size=10&sort=codigo&direction=asc")
    public ResponseEntity<List<PeliculaResponse>> listar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) throws Exception {

        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(peliculaServicio.obtenerNombrePeliculas(nombre));
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
            return ResponseEntity.ok(peliculaServicio.listarPaginado(pageable));
        }

        return ResponseEntity.ok(peliculaServicio.listar());
    }

    // !SECTION
    // SECTION: Demo QUERY (RFC 10008) — lectura con body

    /**
     * Demo del método QUERY: búsqueda compleja con body JSON.
     * Hoy se expone como POST /query para compatibilidad Spring.
     * Cuando Spring soporte nativo QUERY, el mismo handler atenderá QUERY /api/peliculas/query.
     *
     * Ejemplo body: { "generos":["ACCION"], "restriccionEdad":18, "nombreParcial":"Avengers" }
     * Ventaja QUERY vs GET: body no va en URL/logs, es safe/idempotente/cacheable.
     */
    @PostMapping(value = "/query", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "[DEMO QUERY] Búsqueda compleja con body",
            description = "Equivalente a QUERY /api/peliculas/query (RFC 10008). "
                    + "Actualmente POST por compat Spring; en el futuro el mismo endpoint aceptará QUERY.")
    public ResponseEntity<List<PeliculaResponse>> buscarConBody(
            @RequestBody PeliculaFiltroRequest filtro) throws Exception {

        // Demo: filtrado simple por nombre si se provee; si no, lista completa
        if (filtro.getNombreParcial() != null && !filtro.getNombreParcial().isBlank()) {
            return ResponseEntity.ok(peliculaServicio.obtenerNombrePeliculas(filtro.getNombreParcial()));
        }
        return ResponseEntity.ok(peliculaServicio.listar());
    }

    /**
     * Handler genérico para el método QUERY cuando el contenedor lo permita.
     * Spring MVC aún no tiene RequestMethod.QUERY; por eso mapeamos sin método específico.
     * Si el servidor recibe QUERY /api/peliculas/query, aquí caerá.
     * Se mantiene para forward-compat; no interfiere con GET/POST existentes.
     */
    @RequestMapping(value = "/query", method = {}, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PeliculaResponse>> buscarConQueryMethod(
            @RequestBody(required = false) PeliculaFiltroRequest filtro) throws Exception {
        // Solo atiende si el método es QUERY; si es POST ya lo manejó buscarConBody
        // En MockMvc con method("QUERY") este handler responde.
        if (filtro != null && filtro.getNombreParcial() != null && !filtro.getNombreParcial().isBlank()) {
            return ResponseEntity.ok(peliculaServicio.obtenerNombrePeliculas(filtro.getNombreParcial()));
        }
        return ResponseEntity.ok(peliculaServicio.listar());
    }

    /**
     * DTO filtro para demo QUERY — no es entidad, solo query document.
     */
    public static class PeliculaFiltroRequest {
        private String nombreParcial;
        private Integer restriccionEdad;

        public String getNombreParcial() { return nombreParcial; }
        public void setNombreParcial(String nombreParcial) { this.nombreParcial = nombreParcial; }
        public Integer getRestriccionEdad() { return restriccionEdad; }
        public void setRestriccionEdad(Integer restriccionEdad) { this.restriccionEdad = restriccionEdad; }
    }

    // !SECTION
}
