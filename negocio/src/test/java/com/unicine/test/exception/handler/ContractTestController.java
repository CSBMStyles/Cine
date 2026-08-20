package com.unicine.test.exception.handler;

import com.unicine.exception.AuthenticationException;
import com.unicine.exception.AuthorizationException;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ExternalServiceException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.exception.ValidationException;
import com.unicine.util.validation.catalog.domain.ImageErrorCatalog;
import com.unicine.util.validation.catalog.domain.ShowingErrorCatalog;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contract")
public class ContractTestController {

    @GetMapping("/success")
    ResponseEntity<ContractResponse> success() {
        return ResponseEntity.ok(new ContractResponse("ok"));
    }

    @PostMapping("/success")
    ResponseEntity<ContractResponse> create() {
        return ResponseEntity.status(HttpStatus.CREATED).body(new ContractResponse("created"));
    }

    @PutMapping("/success/{codigo}")
    ResponseEntity<ContractResponse> update(@PathVariable Integer codigo) {
        return ResponseEntity.ok(new ContractResponse("updated"));
    }

    @DeleteMapping("/success/{codigo}")
    ResponseEntity<Void> delete(@PathVariable Integer codigo) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/not-found")
    void notFound() {
        throw new ResourceNotFoundException(ShowingErrorCatalog.DOMAIN_SHOWING_ENTITY_SCHEDULE_NOT_FOUND);
    }

    @GetMapping("/business-rule")
    void businessRule() {
        throw new BusinessRuleException(ShowingErrorCatalog.DOMAIN_SHOWING_BUSINESS_RULE_SCHEDULE_OVERLAP);
    }

    @GetMapping("/authentication")
    void authentication() {
        throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_INVALID_CREDENTIALS);
    }

    @GetMapping("/authorization")
    void authorization() {
        throw new AuthorizationException(UserErrorCatalog.DOMAIN_USER_AUTH_ACTION_NOT_PERMITTED);
    }

    @GetMapping("/conflict")
    void conflict() {
        throw new ValidationException(UserErrorCatalog.DOMAIN_USER_DUPLICATE_EMAIL_ALREADY_REGISTERED);
    }

    @GetMapping("/external")
    void external() {
        throw new ExternalServiceException(ImageErrorCatalog.DOMAIN_IMAGE_EXTERNAL_UPLOAD_ERROR, "provider");
    }

    @GetMapping("/unexpected")
    void unexpected() {
        throw new IllegalStateException("detalle interno");
    }

    @PostMapping("/body")
    ResponseEntity<ContractResponse> body(@RequestBody @Valid ContractRequest request) {
        return ResponseEntity.ok(new ContractResponse(request.nombre()));
    }

    @GetMapping("/method")
    String method(@RequestParam(name = "codigo") @NotBlank(message = "codigo requerido") String codigo) {
        return codigo;
    }

    record ContractRequest(@NotBlank(message = "nombre requerido") String nombre) {
    }

    record ContractResponse(String value) {
    }
}
