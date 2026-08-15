package com.unicine.util.funtional.image;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HexFormat;
import java.util.Locale;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.unicine.entity.image.interfaced.Imagenable;
import com.unicine.enums.image.TipoImagen;
import com.unicine.enums.image.TipoPropietarioImagen;
import com.unicine.util.config.ImagenIdentificadorConfig;

@Service
public class GeneradorNombreImagen {

    // SECTION: Constantes

    private static final String ALGORITMO_HMAC = "HmacSHA256";
    private static final int LONGITUD_HMAC = 8;
    private static final String PREFIJO_PELICULA = "PEL";
    private static final String PREFIJO_PERSONA = "PER";

    // !SECTION
    // SECTION: Dependencias

    private final ImagenIdentificadorConfig identificadorConfig;
    private final RefactorizadorRuta refactorizadorRuta;

    public GeneradorNombreImagen(ImagenIdentificadorConfig identificadorConfig,
                                 RefactorizadorRuta refactorizadorRuta) {
        this.identificadorConfig = identificadorConfig;
        this.refactorizadorRuta = refactorizadorRuta;
    }

    // !SECTION
    // SECTION: Metodos de negocio

    public String generar(String nombreOriginal, TipoPropietarioImagen tipoPropietario,
                          Integer codigoPropietario, TipoImagen tipoImagen, Integer orden,
                          Imagenable propietario) {
        validarDatos(tipoPropietario, codigoPropietario, tipoImagen, propietario);

        if (tipoPropietario == TipoPropietarioImagen.PELICULA) {
            return generarNombrePelicula(codigoPropietario, tipoImagen, orden);
        }

        if (esPersona(tipoPropietario)) {
            return generarNombrePersona(tipoPropietario, codigoPropietario, tipoImagen);
        }

        return refactorizadorRuta.nombrarArchivo(nombreOriginal, propietario);
    }

    public boolean usaNombreDeterminista(TipoPropietarioImagen tipoPropietario) {
        return tipoPropietario == TipoPropietarioImagen.PELICULA || esPersona(tipoPropietario);
    }

    // !SECTION
    // SECTION: Metodos auxiliares

    private String generarNombrePelicula(Integer codigoPelicula, TipoImagen tipoImagen, Integer orden) {
        if (orden == null || orden < 1) {
            throw new IllegalArgumentException("El orden es obligatorio para una imagen de película");
        }

        return String.format(
                Locale.ROOT,
                "%s-%d-%s-%02d",
                PREFIJO_PELICULA,
                codigoPelicula,
                tipoImagen.name(),
                orden
        );
    }

    private String generarNombrePersona(TipoPropietarioImagen tipoPropietario,
                                         Integer cedula, TipoImagen tipoImagen) {
        String material = tipoPropietario.name() + ":" + cedula;
        return PREFIJO_PERSONA + "-" + generarHmac(material) + "-" + tipoImagen.name();
    }

    private String generarHmac(String material) {
        String secret = identificadorConfig.getSecret();
        if (secret == null || secret.isBlank()) {
            throw new IllegalStateException("La propiedad IMAGE_IDENTIFIER_SECRET es obligatoria");
        }

        try {
            Mac mac = Mac.getInstance(ALGORITMO_HMAC);
            SecretKeySpec clave = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITMO_HMAC);
            mac.init(clave);
            byte[] resultado = mac.doFinal(material.getBytes(StandardCharsets.UTF_8));

            return HexFormat.of()
                    .formatHex(resultado)
                    .substring(0, LONGITUD_HMAC)
                    .toUpperCase(Locale.ROOT);
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("No se pudo generar el identificador seguro de la imagen", exception);
        }
    }

    private boolean esPersona(TipoPropietarioImagen tipoPropietario) {
        return tipoPropietario == TipoPropietarioImagen.CLIENTE
                || tipoPropietario == TipoPropietarioImagen.ADMINISTRADOR
                || tipoPropietario == TipoPropietarioImagen.ADMINISTRADOR_TEATRO;
    }

    private void validarDatos(TipoPropietarioImagen tipoPropietario, Integer codigoPropietario,
                              TipoImagen tipoImagen, Imagenable propietario) {
        if (tipoPropietario == null || codigoPropietario == null || tipoImagen == null) {
            throw new IllegalArgumentException("Los datos del propietario y tipo de imagen son obligatorios");
        }

        if (!usaNombreDeterminista(tipoPropietario) && propietario == null) {
            throw new IllegalArgumentException("El propietario es obligatorio para nombres heredados");
        }
    }

    // !SECTION
}
