package com.unicine.api.controller;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
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
import com.unicine.service.purchase.EntradaServicio;
import com.unicine.service.showing.FuncionServicio;
import com.unicine.service.theater.SalaServicio;
import com.unicine.transfer.dto.request.FuncionRequest;
import com.unicine.transfer.dto.response.DetalleSillaResponse;
import com.unicine.transfer.dto.response.FuncionResponse;
import com.unicine.util.pagination.PaginadoManual;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    private final SalaServicio salaServicio;

    public FuncionController(FuncionServicio funcionServicio, EntradaServicio entradaServicio,
            SalaServicio salaServicio) {
        this.funcionServicio = funcionServicio;
        this.entradaServicio = entradaServicio;
        this.salaServicio = salaServicio;
    }

    // SECTION: 3️⃣ Funciones del Administrador Teatro

    @PostMapping
    @Operation(summary = "Registrar función", description = "Solo ADMIN o ADMINISTRADOR_TEATRO de su teatro.")
    public ResponseEntity<FuncionResponse> registrar(
            @Valid @RequestBody FuncionRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Integer teatro = teatroDeSala(request.getSalaCodigo());
        if (teatro != null && fueraDeAlcance(principal, teatro)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(funcionServicio.registrar(request));
    }

    @PutMapping("/{codigo}")
    @Operation(summary = "Actualizar función", description = "Solo ADMIN o ADMINISTRADOR_TEATRO de su teatro.")
    public ResponseEntity<FuncionResponse> actualizar(
            @PathVariable @Positive Integer codigo,
            @Valid @RequestBody FuncionRequest request,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        request.setCodigo(codigo);
        Integer teatroExistente = funcionServicio.obtener(codigo)
                .map(f -> f.getSala() == null || f.getSala().getTeatro() == null
                        ? null
                        : f.getSala().getTeatro().getCodigo())
                .orElse(null);
        Integer teatroSolicitado = teatroDeSala(request.getSalaCodigo());
        if ((teatroExistente != null && fueraDeAlcance(principal, teatroExistente))
                || (teatroSolicitado != null && fueraDeAlcance(principal, teatroSolicitado))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(funcionServicio.actualizar(request));
    }

    @DeleteMapping("/{codigo}")
    @Operation(summary = "Eliminar función",
            description = "Solo ADMIN o ADMINISTRADOR_TEATRO de su teatro. Requiere ?confirmacion=true.")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer codigo,
            @RequestParam boolean confirmacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        if (!principal.esGestorCatalogo()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        var funcion = funcionServicio.obtener(codigo);
        if (funcion.isPresent()) {
            Integer teatro = funcion.get().getSala() == null || funcion.get().getSala().getTeatro() == null
                    ? null
                    : funcion.get().getSala().getTeatro().getCodigo();
            if (teatro != null && fueraDeAlcance(principal, teatro)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        funcionServicio.eliminar(codigo, confirmacion);
        return ResponseEntity.noContent().build();
    }

    // !SECTION
    // SECTION: *️⃣ Funciones Generales

    @GetMapping("/{codigo}")
    @Operation(summary = "Obtener función por código")
    public ResponseEntity<FuncionResponse> obtener(@PathVariable @Positive Integer codigo) throws Exception {
        return funcionServicio.obtener(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Listar funciones", description = "Cartelera pública. Filtros: ?pelicula=&sala=&ciudad=&fecha=YYYY-MM-DD (America/Bogota). Paginado ?page=0&size=10&sort=codigo&direction=asc|desc. Vacío → 200 []. Fecha inválida → 400.")
    public ResponseEntity<List<FuncionResponse>> listar(
            @RequestParam(required = false) Integer pelicula,
            @RequestParam(required = false) Integer sala,
            @RequestParam(required = false) Integer ciudad,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String direction) {

        List<FuncionResponse> filtradas = funcionServicio.listar().stream()
                .filter(f -> pelicula == null || (f.getPelicula() != null && pelicula.equals(f.getPelicula().getCodigo())))
                .filter(f -> sala == null || (f.getSala() != null && sala.equals(f.getSala().getCodigo())))
                .filter(f -> ciudad == null || ciudad.equals(extraerCiudadCodigo(f)))
                .filter(f -> fecha == null || fecha.equals(extraerFechaFuncion(f)))
                .sorted(ordenar(sort, direction))
                .toList();

        return ResponseEntity.ok(PaginadoManual.paginar(filtradas, page, size));
    }

    private Integer extraerCiudadCodigo(FuncionResponse funcion) {
        if (funcion.getSala() == null || funcion.getSala().getTeatro() == null
                || funcion.getSala().getTeatro().getCiudad() == null) {
            return null;
        }
        return funcion.getSala().getTeatro().getCiudad().getCodigo();
    }

    private LocalDate extraerFechaFuncion(FuncionResponse funcion) {
        if (funcion.getHorario() == null || funcion.getHorario().getFechaInicio() == null) {
            return null;
        }
        return funcion.getHorario().getFechaInicio().toLocalDate();
    }

    private Comparator<FuncionResponse> ordenar(String sort, String direction) {
        Comparator<FuncionResponse> porCodigo =
                Comparator.comparing(FuncionResponse::getCodigo, Comparator.nullsLast(Integer::compareTo));
        if (sort == null || sort.isBlank()) {
            return porCodigo;
        }
        return "desc".equalsIgnoreCase(direction) ? porCodigo.reversed() : porCodigo;
    }

    // !SECTION
    // SECTION: *️⃣ Funciones Generales

    @GetMapping("/{codigo}/sillas-ocupadas")
    @Operation(summary = "Sillas ocupadas de la función")
    public ResponseEntity<List<DetalleSillaResponse>> sillasOcupadas(
            @PathVariable @Positive Integer codigo) throws Exception {
        funcionServicio.obtener(codigo).orElseThrow(() -> new com.unicine.exception.ResourceNotFoundException(
                com.unicine.util.validation.catalog.domain.ShowingErrorCatalog.DOMAIN_SHOWING_ENTITY_FUNCTION_NOT_FOUND));
        List<DetalleSillaResponse> ocupadas = entradaServicio.obtenerSillasOcupadas(codigo);
        return ResponseEntity.ok(ocupadas);
    }

    /**
     * Alcance por teatro (5.2 B4): el ADMIN opera en todos; el resto solo en
     * los teatros de su claim teatroIds. Teatro desconocido niega por defecto.
     */
    private boolean fueraDeAlcance(UsuarioPrincipal principal, Integer teatroCodigo) {
        if (principal.esAdministrador()) {
            return false;
        }
        List<Integer> teatros = principal.getTeatroIds();
        return teatroCodigo == null || teatros == null || !teatros.contains(teatroCodigo);
    }

    private Integer teatroDeSala(Integer salaCodigo) throws Exception {
        if (salaCodigo == null) {
            return null;
        }
        return salaServicio.obtener(salaCodigo)
                .map(s -> s.getTeatro() == null ? null : s.getTeatro().getCodigo())
                .orElse(null);
    }

    // !SECTION
}
