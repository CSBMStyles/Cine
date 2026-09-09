package com.unicine.util.config;

import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.unicine.exception.handler.ApiError;
import com.unicine.exception.handler.ValidationErrorDetail;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

/**
 * Configuracion OpenAPI de UniCine (tarea 4.6).
 *
 * Define informacion del proyecto y el esquema de seguridad JWT por nombre.
 * El esquema {@code bearer-jwt} queda reservado para Fase 5: aun ningun
 * endpoint lo exige via {@code @SecurityRequirement} porque no hay JWT.
 * Verificacion: {@code OpenApiDocsTest} + {@code GET /v3/api-docs}.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "UniCine API",
                version = "4.6",
                description = "API REST de cartelera, compras y administracion. "
                        + "Errores uniformes ApiError con codigo de catalogo.",
                contact = @Contact(name = "UniCine"),
                license = @License(name = "Uso academico")),
        servers = @Server(url = "http://localhost:8080", description = "Local"))
@SecurityScheme(
        name = "bearer-jwt",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Reservado Fase 5. Hoy la API usa sesiones mock en tests; no enviar token aun.")
public class OpenApiConfig {

    /**
     * Registra ApiError y ValidationErrorDetail en components.schemas.
     * Ningun controller los retorna en firma (nacen en el handler),
     * asi springdoc igual los publica sin duplicar DTOs.
     */
    @Bean
    public OpenApiCustomizer apiErrorSchemas() {
        return openApi -> {
            registrarEsquema(openApi, ApiError.class);
            registrarEsquema(openApi, ValidationErrorDetail.class);
        };
    }

    private void registrarEsquema(io.swagger.v3.oas.models.OpenAPI openApi, Class<?> tipo) {
        io.swagger.v3.core.converter.ModelConverters.getInstance()
                .readAll(tipo)
                .forEach((nombre, esquema) -> {
                    boolean existe = openApi.getComponents().getSchemas() != null
                            && openApi.getComponents().getSchemas().containsKey(nombre);
                    if (!existe) {
                        openApi.getComponents().addSchemas(nombre, esquema);
                    }
                });
    }
}
