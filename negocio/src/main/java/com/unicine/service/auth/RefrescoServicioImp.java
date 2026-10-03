package com.unicine.service.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.auth.SesionRefresh;
import com.unicine.exception.AuthenticationException;
import com.unicine.repository.auth.SesionRefreshRepo;
import com.unicine.security.JwtServicio;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.transfer.dto.auth.ParTokensResponse;
import com.unicine.util.validation.catalog.domain.UserErrorCatalog;

/**
 * Sesiones con rotacion y revocacion en servidor (5.1.3).
 * El refresh viaja una sola vez: se guarda su hash SHA-256, nunca en claro.
 */
@Service
public class RefrescoServicioImp implements RefrescoServicio {

    // SECTION: Dependencias

    private final SesionRefreshRepo sesionRepo;

    private final JwtServicio jwtServicio;

    public RefrescoServicioImp(SesionRefreshRepo sesionRepo, JwtServicio jwtServicio) {
        this.sesionRepo = sesionRepo;
        this.jwtServicio = jwtServicio;
    }

    // !SECTION
    // SECTION: Sesiones

    @Override
    @Transactional
    public ParTokensResponse crearSesion(UsuarioPrincipal principal) {
        String refresco = jwtServicio.emitirRefresco(principal);
        guardarSesion(principal, refresco);
        return new ParTokensResponse(jwtServicio.emitirAcceso(principal), refresco, "Bearer");
    }

    @Override
    @Transactional
    public ParTokensResponse rotar(String refreshToken) {
        Jwt jwt;
        try {
            jwt = jwtServicio.validarRefresco(refreshToken);
        } catch (JwtException e) {
            throw new AuthenticationException(UserErrorCatalog.DOMAIN_USER_AUTH_TOKEN_INVALID, e);
        }
        SesionRefresh sesion = sesionRepo.findByTokenHash(hash(refreshToken))
                .orElseThrow(() -> new AuthenticationException(
                        UserErrorCatalog.DOMAIN_USER_AUTH_TOKEN_INVALID));
        if (Boolean.TRUE.equals(sesion.getRevocado()) || sesion.getExpiraEn().isBefore(Instant.now())) {
            revocarTodas(sesion.getCedula());
            throw new AuthenticationException(sesion.getExpiraEn().isBefore(Instant.now())
                    ? UserErrorCatalog.DOMAIN_USER_AUTH_TOKEN_EXPIRED
                    : UserErrorCatalog.DOMAIN_USER_AUTH_TOKEN_REVOKED);
        }
        sesion.setRevocado(true);
        sesionRepo.save(sesion);
        UsuarioPrincipal principal = jwtServicio.principalDesde(jwt);
        return crearSesion(principal);
    }

    @Override
    @Transactional
    public void cerrarSesion(String refreshToken) {
        sesionRepo.findByTokenHash(hash(refreshToken)).ifPresent(sesion -> {
            sesion.setRevocado(true);
            sesionRepo.save(sesion);
        });
    }

    // !SECTION
    // SECTION: Privados

    private void guardarSesion(UsuarioPrincipal principal, String refresco) {
        Jwt jwt = jwtServicio.validarRefresco(refresco);
        SesionRefresh sesion = new SesionRefresh();
        sesion.setTokenHash(hash(refresco));
        sesion.setCedula(principal.getCedula());
        sesion.setTipo(principal.getTipo());
        sesion.setExpiraEn(jwt.getExpiresAt());
        sesion.setRevocado(false);
        sesion.setCreadoEn(Instant.now());
        sesionRepo.save(sesion);
    }

    private void revocarTodas(Integer cedula) {
        sesionRepo.findByCedula(cedula).forEach(sesion -> {
            sesion.setRevocado(true);
            sesionRepo.save(sesion);
        });
    }

    static String hash(String token) {
        try {
            byte[] digesto = MessageDigest.getInstance("SHA-256")
                    .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digesto);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 no disponible", e);
        }
    }

    // !SECTION
}
