package com.unicine.test.security;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador solo para pruebas de la configuracion base de seguridad.
 * No es parte de la API productiva.
 */
@RestController
@RequestMapping("/security")
public class SecurityTestController {

    // SECTION: Endpoints de prueba

    @GetMapping("/public")
    public ResponseEntity<String> publica() {
        return ResponseEntity.ok("public");
    }

    @PostMapping("/public")
    public ResponseEntity<String> publicaPost() {
        return ResponseEntity.ok("public-post");
    }

    @GetMapping("/private")
    public ResponseEntity<String> privada() {
        return ResponseEntity.ok("private");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<String> admin() {
        return ResponseEntity.ok("admin");
    }

    // !SECTION
}
