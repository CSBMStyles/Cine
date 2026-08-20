package com.unicine.test.util.funtional.image;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.unicine.enums.image.TipoImagen;
import com.unicine.enums.image.TipoPropietarioImagen;
import com.unicine.util.config.ImagenIdentificadorConfig;
import com.unicine.util.funtional.image.GeneradorNombreImagen;
import com.unicine.util.funtional.image.RefactorizadorRuta;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GeneradorNombreImagenTest {

    private GeneradorNombreImagen generador;

    @BeforeEach
    void configurarGenerador() {
        ImagenIdentificadorConfig config = new ImagenIdentificadorConfig();
        config.setSecret("test-image-identifier-secret");
        generador = new GeneradorNombreImagen(config, new RefactorizadorRuta());
    }

    @Test
    void debeGenerarNombreDeterministaParaPelicula() {
        String nombre = generador.generar(
                "Ratatouille.webp",
                TipoPropietarioImagen.PELICULA,
                6,
                TipoImagen.POSTER,
                1,
                null
        );

        assertEquals("PEL-6-POSTER-01", nombre);
    }

    @Test
    void debeGenerarHmacEstableSinExponerCedula() {
        String primerNombre = generador.generar(
                "Camila.png",
                TipoPropietarioImagen.ADMINISTRADOR,
                1001000000,
                TipoImagen.AVATAR,
                null,
                null
        );
        String segundoNombre = generador.generar(
                "Otra-imagen.png",
                TipoPropietarioImagen.ADMINISTRADOR,
                1001000000,
                TipoImagen.AVATAR,
                null,
                null
        );
        String nombreOtroRol = generador.generar(
                "Camila.png",
                TipoPropietarioImagen.CLIENTE,
                1001000000,
                TipoImagen.AVATAR,
                null,
                null
        );

        assertEquals(primerNombre, segundoNombre);
        assertTrue(primerNombre.matches("PER-[0-9A-F]{8}-AVATAR"));
        assertFalse(primerNombre.contains("1001000000"));
        assertNotEquals(primerNombre, nombreOtroRol);
    }
}
