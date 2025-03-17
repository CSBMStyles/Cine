package com.unicine.util.funtional.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ProcesadorImagen {

    /**
     * Metodo para leer una imagen
     * @param file Archivo de imagen a leer
     * @return buffered
     */
    public BufferedImage leerImagen(File file) throws IOException {

        try {
            BufferedImage image = ImageIO.read(file);

            if (image == null) {

                throw new IOException("No se pudo leer la imagen. Formato no soportado: " + file.getName());
            }
            return image;

        } catch (Exception e) {

            throw new IOException("Error al leer la imagen: " + file.getAbsolutePath() + ": " + e);
        }
    }

    /**
     * Metodo para leer una imagen desde un MultipartFile
     * @param file MultipartFile de imagen a leer
     * @return buffered
     */
    public BufferedImage leerImagen(MultipartFile file) throws IOException {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());

            if (image == null) {
                throw new IOException("No se pudo leer la imagen. Formato no soportado: " + file.getOriginalFilename());
            }
            return image;

        } catch (Exception e) {
            throw new IOException("Error al leer la imagen: " + file.getOriginalFilename() + ": " + e);
        }
    }

    /**
     * Metodo para convertir una imagen a formato webp
     * 
     * @param file Archivo de imagen a convertir
     * @param quality Calidad de la imagen
     * @return
     */
    public byte[] convertirFormato(File file, float quality) throws IOException {

        // Se lee la imagen original
        BufferedImage image = leerImagen(file);
        
        try {
            // Convertir la imagen a formato webp
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Configurar la calidad de la imagen
            Thumbnails.of(image)
                .scale(1.0)
                .outputQuality(quality)
                .outputFormat("webp")
                .toOutputStream(outputStream);
                
            // Retornar los bytes de la imagen convertida
            return outputStream.toByteArray();

        } catch (Exception e) {

            throw new IOException("Error al convertir la imagen: " + e.getMessage());
        }
    }

    /**
     * Metodo para convertir una imagen de MultipartFile a formato webp
     * 
     * @param file MultipartFile de imagen a convertir
     * @param quality Calidad de la imagen
     * @return
     */
    public byte[] convertirFormato(MultipartFile file, float quality) throws IOException {
        // Se lee la imagen original
        BufferedImage image = leerImagen(file);
        
        try {
            // Convertir la imagen a formato webp
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            // Configurar la calidad de la imagen
            Thumbnails.of(image)
                .scale(1.0)
                .outputQuality(quality)
                .outputFormat("webp")
                .toOutputStream(outputStream);
                
            // Retornar los bytes de la imagen convertida
            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new IOException("Error al convertir la imagen: " + e.getMessage());
        }
    }
}
