package com.unicine.api.controller;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unicine.service.purchase.EntradaServicio;
import com.unicine.service.showing.FuncionServicio;
import com.unicine.transfer.dto.response.DetalleSillaResponse;
import com.unicine.transfer.dto.response.FuncionResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;

/**
 * Controller de cartelera — lectura publica + sillas ocupadas.
 */
@RestController
@RequestMapping("/api/funciones")
@Validated
@Tag(name = "Funciones", description = "Cartelera y funciones")
public class FuncionController {

    private final FuncionServicio funcionServicio;
    private final EntradaServicio entradaServicio;

    public FuncionController(FuncionServicio funcionServicio, EntradaServicio entradaServicio) {
        this.funcionServicio = funcionServicio;
        this.entradaServicio = entradaServicio;
    }

    // SECTION: Cartelera

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener función por código")
    public ResponseEntity<FuncionResponse> obtener(@PathVariable @Positive Integer codigo) throws Exception {
        return funcionServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar funciones", description = "Filtros: ?pelicula= & ?sala= — paginado ?page=0&size=10")
    public ResponseEntity<List<FuncionResponse>> listar(
            @RequestParam(required = false) Integer pelicula,
            @RequestParam(required = false) Integer sala,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size) {

        List<FuncionResponse> todas = funcionServicio.listar();

        if (pelicula != null || sala != null) {
            List<FuncionResponse> filtradas = todas.stream()
                    .filter(f -> pelicula == null || (f.getPelicula() != null && pelicula.equals(f.getPelicula().getCodigo())))
                    .filter(f -> sala == null || (f.getSala() != null && sala.equals(f.getSala().getCodigo())))
                    .toList();
            return ResponseEntity.ok(aplicarPaginado(filtradas, page, size));
        }

        return ResponseEntity.ok(aplicarPaginado(todas, page, size));
    }

    private List<FuncionResponse> aplicarPaginado(List<FuncionResponse> lista, Integer page, Integer size) {
        if (page == null && size == null) {
            return lista;
        }
        int p = page != null ? page : 0;
        int s = size != null ? size : 10;
        int from = Math.min(p * s, lista.size());
        int to = Math.min(from + s, lista.size());
        return lista.subList(from, to);
    }

    // !SECTION
    // SECTION: Sillas

    @GetMapping("/{codigo}/sillas-ocupadas")
    @Operation(summary = "Sillas ocupadas de la función")
    public ResponseEntity<List<DetalleSillaResponse>> sillasOcupadas(
            @PathVariable @Positive Integer codigo) throws Exception {
        funcionServicio.obtener(codigo).orElseThrow(() -> new com.unicine.exception.ResourceNotFoundException(
                com.unicine.util.validation.catalog.domain.ShowingErrorCatalog.DOMAIN_SHOWING_ENTITY_FUNCTION_NOT_FOUND));
        List<DetalleSillaResponse> ocupadas = entradaServicio.obtenerSillasOcupadas(codigo);
        return ResponseEntity.ok(ocupadas);
    }

    // !SECTION
}
