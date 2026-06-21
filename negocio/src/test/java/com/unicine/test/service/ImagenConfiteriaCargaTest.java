package com.unicine.test.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
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
@Disabled("Tests para carga manual de imagenes a ImageKit. Ejecutar individualmente cuando se necesiten regenerar IDs/URLs.")
public class ImagenConfiteriaCargaTest {

    @Autowired
    private ImagenServicio imagenServicio;

    @Autowired
    private ConfiteriaServicio confiteriaServicio;

    private static final String RUTA_BASE = "image/confiteria";

    private Imagen subirImagenConfiteria(String rutaRelativa, Integer confiteriaId, String nombreConfiteria) throws Exception {

        Path rutaBase = Paths.get(System.getProperty("user.dir"))
                .getParent() // subir de 'negocio' a 'Cine'
                .resolve(RUTA_BASE)
                .toAbsolutePath()
                .normalize();

        File fileOriginal = rutaBase.resolve(rutaRelativa).toFile();

        if (!fileOriginal.exists()) {
            throw new RuntimeException("No existe el archivo: " + fileOriginal.getAbsolutePath());
        }

        byte[] contenido = Files.readAllBytes(fileOriginal.toPath());

        MultipartFile file = new MockMultipartFile("imagen", fileOriginal.getName(), "image/png", contenido);

        Confiteria confiteria = confiteriaServicio.obtener(confiteriaId)
                .orElseThrow(() -> new RuntimeException("No se encontro la confiteria con id " + confiteriaId));

        Imagen imagen = new Imagen();
        imagen.setConfiteria(confiteria);

        Imagen resultado = imagenServicio.registrar(imagen, file, confiteria);

        Assertions.assertNotNull(resultado, "La imagen resultante no debe ser nula");
        Assertions.assertNotNull(resultado.getCodigo(), "El codigo de la imagen no debe ser nulo");

        System.out.println("===== " + nombreConfiteria + " =====");
        System.out.println("ARCHIVO: " + rutaRelativa);
        System.out.println("CONFITERIA_ID: " + confiteriaId);
        System.out.println("IMAGEN_ID: " + resultado.getCodigo());
        System.out.println("IMAGEN_URL: " + resultado.getUrl());
        System.out.println("==============================");

        return resultado;
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirComboMega() throws Exception { subirImagenConfiteria("snaks/Combo Mega.png", 1, "Combo Mega"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirComboSencillo() throws Exception { subirImagenConfiteria("snaks/Combo Simple.png", 2, "Combo Sencillo"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirComboPareja() throws Exception { subirImagenConfiteria("snaks/Combo Parejas.png", 3, "Combo Pareja"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirCrispetaGrande() throws Exception { subirImagenConfiteria("snaks/Crispetas Grandes.png", 4, "Crispeta Grande"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirCrispetaMediana() throws Exception { subirImagenConfiteria("snaks/Crispetas Medianas.png", 5, "Crispeta Mediana"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirGaseosa() throws Exception { subirImagenConfiteria("bebida/Coca Cola.png", 6, "Gaseosa 600ml"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirAgua() throws Exception { subirImagenConfiteria("bebida/Botella Agua.png", 7, "Agua"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirPerroCaliente() throws Exception { subirImagenConfiteria("snaks/Perro Caliente.png", 8, "Perro Caliente"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirNachosQueso() throws Exception { subirImagenConfiteria("snaks/Doritos Queso.png", 9, "Nachos con Queso"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirChocorramo() throws Exception { subirImagenConfiteria("dulce/Chocorramo.png", 10, "Chocorramo"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirSupercoco() throws Exception { subirImagenConfiteria("dulce/Super Coco.png", 11, "Supercoco"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirJet() throws Exception { subirImagenConfiteria("dulce/Chocolatina Jet.png", 12, "Jet"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirHeladoArequipe() throws Exception { subirImagenConfiteria("otros/Helado Arequipe.png", 13, "Helado de Arequipe"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirCafe() throws Exception { subirImagenConfiteria("bebida/Café.png", 14, "Cafe"); }

    @Test
    @Sql("classpath:dataset.sql")
    public void subirDeToditosRojo() throws Exception { subirImagenConfiteria("snaks/De Toditos Rojo.png", 15, "De Toditos Rojo"); }
}
