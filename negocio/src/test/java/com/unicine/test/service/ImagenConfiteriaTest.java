package com.unicine.test.service;

import java.io.File;
import java.nio.file.Files;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.entity.image.Imagen;
import com.unicine.service.confiteria.ConfiteriaServicio;
import com.unicine.service.image.ImagenServicio;

@SpringBootTest
@Transactional
public class ImagenConfiteriaTest {

    @Autowired
    private ImagenServicio imagenServicio;

    @Autowired
    private ConfiteriaServicio confiteriaServicio;

    @Test
    @Sql("classpath:dataset.sql")
    public void subirImagenConfiteria() {

        MultipartFile file;

        try {
            File fileOriginal = new File("../image/confiteria/snaks/DE TODITOS ROJO 45G.png");

            byte[] contenido = Files.readAllBytes(fileOriginal.toPath());

            file = new MockMultipartFile("imagen", fileOriginal.getName(), "image/png", contenido);

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            throw new RuntimeException(e);
        }

        Confiteria confiteria;

        try {
            confiteria = confiteriaServicio.obtener(15).orElse(null);
            Assertions.assertNotNull(confiteria, "La confiteria no debe ser nula");

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            throw new RuntimeException(e);
        }

        Imagen imagen = new Imagen();
        imagen.setConfiteria(confiteria);

        try {
            Imagen resultado = imagenServicio.registrar(imagen, file, confiteria);

            Assertions.assertNotNull(resultado, "La imagen resultante no debe ser nula");
            Assertions.assertNotNull(resultado.getCodigo(), "El codigo de la imagen no debe ser nulo");

            System.out.println("ID IMAGEN: " + resultado.getCodigo());
            System.out.println("URL IMAGEN: " + resultado.getUrl());

        } catch (Exception e) {
            System.out.println("Mensaje de error: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
