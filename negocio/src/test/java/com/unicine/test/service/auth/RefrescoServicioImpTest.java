package com.unicine.test.service.auth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import com.unicine.entity.auth.SesionRefresh;
import com.unicine.enums.user.TipoUsuario;
import com.unicine.exception.AuthenticationException;
import com.unicine.repository.auth.SesionRefreshRepo;
import com.unicine.security.JwtServicio;
import com.unicine.security.UsuarioPrincipal;
import com.unicine.service.auth.RefrescoServicioImp;
import com.unicine.transfer.dto.auth.ParTokensResponse;

@ExtendWith(MockitoExtension.class)
public class RefrescoServicioImpTest {

    @Mock
    private SesionRefreshRepo sesionRepo;

    @Mock
    private JwtServicio jwtServicio;

    @InjectMocks
    private RefrescoServicioImp refrescoServicio;

    private static String hash(String token) throws Exception {
        Method metodo = RefrescoServicioImp.class.getDeclaredMethod("hash", String.class);
        metodo.setAccessible(true);
        return (String) metodo.invoke(null, token);
    }

    private UsuarioPrincipal principal() {
        return new UsuarioPrincipal(1009000011, "pepe@test.com", null,
                TipoUsuario.CLIENTE, List.of());
    }

    private Jwt jwtValido() {
        Jwt jwt = org.mockito.Mockito.mock(Jwt.class);
        org.mockito.Mockito.lenient().when(jwt.getExpiresAt())
                .thenReturn(Instant.now().plusSeconds(3600));
        return jwt;
    }

    private SesionRefresh sesionVigente(String hash) {
        SesionRefresh sesion = new SesionRefresh();
        sesion.setTokenHash(hash);
        sesion.setCedula(1009000011);
        sesion.setTipo(TipoUsuario.CLIENTE);
        sesion.setExpiraEn(Instant.now().plusSeconds(3600));
        sesion.setRevocado(false);
        sesion.setCreadoEn(Instant.now());
        return sesion;
    }

    @Test
    public void rotarRevocaAnteriorYEmitePar() throws Exception {
        String viejo = "refresh-viejo";
        Jwt jwt = jwtValido();
        when(jwtServicio.validarRefresco(any())).thenReturn(jwt);
        when(sesionRepo.findByTokenHash(hash(viejo))).thenReturn(Optional.of(sesionVigente(hash(viejo))));
        when(jwtServicio.principalDesde(jwt)).thenReturn(principal());
        when(jwtServicio.emitirRefresco(any())).thenReturn("refresh-nuevo");
        when(jwtServicio.emitirAcceso(any())).thenReturn("access-nuevo");
        when(sesionRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ParTokensResponse par = refrescoServicio.rotar(viejo);

        Assertions.assertEquals("access-nuevo", par.getAccessToken());
        Assertions.assertEquals("refresh-nuevo", par.getRefreshToken());
    }

    @Test
    public void reusoRevocadoRevocaTodoY401() throws Exception {
        String robado = "refresh-robado";
        Jwt jwt = jwtValido();
        when(jwtServicio.validarRefresco(any())).thenReturn(jwt);
        SesionRefresh revocada = sesionVigente(hash(robado));
        revocada.setRevocado(true);
        when(sesionRepo.findByTokenHash(hash(robado))).thenReturn(Optional.of(revocada));
        when(sesionRepo.findByCedula(1009000011)).thenReturn(List.of(revocada));

        AuthenticationException e = Assertions.assertThrows(AuthenticationException.class,
                () -> refrescoServicio.rotar(robado));

        Assertions.assertEquals("DOMAIN_USER_AUTH_TOKEN_REVOKED", e.getErrorCode());
        verify(sesionRepo).findByCedula(1009000011);
    }

    @Test
    public void desconocidoEs401() {
        when(sesionRepo.findByTokenHash(any())).thenReturn(Optional.empty());

        Assertions.assertThrows(AuthenticationException.class,
                () -> refrescoServicio.rotar("desconocido"));
    }

    @Test
    public void logoutEsIdempotente() {
        refrescoServicio.cerrarSesion("inexistente");

        verify(sesionRepo).findByTokenHash(any());
    }
}
