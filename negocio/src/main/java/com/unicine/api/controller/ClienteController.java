package com.unicine.api.controller;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.purchase.CompraServicio;
import com.unicine.service.user.ClienteServicio;
import com.unicine.transfer.dto.request.ClienteRequest;
import com.unicine.transfer.dto.response.ClienteResponse;
import com.unicine.transfer.dto.response.CompraResponse;
import com.unicine.util.pagination.PaginadoManual;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

/**
 * Controller para perfil propio y administracion de clientes.
 * Ownership: /me usa el principal; /{cedula} solo si cedula == principal o rol ADMIN.
 */
@RestController
@RequestMapping("/api/clientes")
@Validated
@Tag(name = "Clientes", description = "Perfil y administración de clientes")
public class ClienteController {

    private final ClienteServicio clienteServicio;
    private final CompraServicio compraServicio;

    public ClienteController(ClienteServicio clienteServicio, CompraServicio compraServicio) {
        this.clienteServicio = clienteServicio;
        this.compraServicio = compraServicio;
    }

    // SECTION: Perfil propio

    @GetMapping("/me")
    @Operation(summary = "Obtener mi perfil", description = "Requiere autenticación. Devuelve el cliente del principal sin password.")
    public ResponseEntity<ClienteResponse> obtenerMiPerfil(@AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        return clienteServicio.obtener(principal.getCedula())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    @Operation(summary = "Actualizar mi perfil", description = "Ignora cedula del body, usa la del principal. Valida duplicados y edad.")
    public ResponseEntity<ClienteResponse> actualizarMiPerfil(
            @AuthenticationPrincipal UsuarioPrincipal principal,
            @Valid @RequestBody ClienteRequest request) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        request.setCedula(principal.getCedula());
        request.setEstado(true);
        ClienteResponse response = clienteServicio.actualizar(request);
        return ResponseEntity.ok(response);
    }

    // !SECTION
    // SECTION: Administración protegida

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Solo rol ADMINISTRADOR. 401 sin auth, 403 sin rol.")
    public ResponseEntity<List<ClienteResponse>> listar(
            @AuthenticationPrincipal UsuarioPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        if (!principal.esAdministrador()) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(clienteServicio.listar());
    }

    @GetMapping("/{cedula}")
    @Operation(summary = "Obtener cliente por cédula", description = "Solo propio o ADMIN. Por ahora valida ownership manual.")
    public ResponseEntity<ClienteResponse> obtenerPorCedula(
            @PathVariable @Positive Integer cedula,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        // Si no es ADMIN y pide otro cedula -> 403
        if (!principal.esAdministrador() && !principal.getCedula().equals(cedula)) {
            return ResponseEntity.status(403).build();
        }
        return clienteServicio.obtener(cedula)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{cedula}")
    @Operation(summary = "Eliminar cliente — requiere ?confirmacion=true", description = "Solo propio o ADMIN.")
    public ResponseEntity<Void> eliminar(
            @PathVariable @Positive Integer cedula,
            @RequestParam boolean confirmacion,
            @AuthenticationPrincipal UsuarioPrincipal principal) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        if (!principal.esAdministrador() && !principal.getCedula().equals(cedula)) {
            return ResponseEntity.status(403).build();
        }
        clienteServicio.eliminar(cedula, confirmacion);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me/compras")
    @Operation(summary = "Historial de mis compras",
            description = "Alias de GET /api/compras?cliente=me. ?page=&size=&direction=asc|desc. "
                    + "Orden fechaCompra DESC. Vacío → 200 [].")
    public ResponseEntity<List<CompraResponse>> misCompras(
            @AuthenticationPrincipal UsuarioPrincipal principal,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String direction) throws Exception {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        try {
            List<CompraResponse> mias = compraServicio.obtenerComprasCliente(principal.getCedula()).stream()
                    .sorted(ordenHistorial(direction))
                    .toList();
            return ResponseEntity.ok(PaginadoManual.paginar(mias, page, size));
        } catch (com.unicine.exception.ResourceNotFoundException e) {
            return ResponseEntity.ok(List.of());
        }
    }

    private Comparator<CompraResponse> ordenHistorial(String direction) {
        Comparator<CompraResponse> porFecha = Comparator.comparing(
                CompraResponse::getFechaCompra, Comparator.nullsLast(LocalDateTime::compareTo))
                .thenComparing(CompraResponse::getCodigo, Comparator.nullsLast(Integer::compareTo));
        return "asc".equalsIgnoreCase(direction) ? porFecha : porFecha.reversed();
    }

    // !SECTION
}
