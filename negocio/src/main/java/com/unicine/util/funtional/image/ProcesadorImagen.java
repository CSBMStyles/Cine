package com.unicine.util.funtional.image;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.unicine.entity.confiteria.Confiteria;
import com.unicine.entity.image.interfaced.Imagenable;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.user.Persona;
import com.unicine.enums.image.TipoImagen;

import net.coobird.thumbnailator.Thumbnails;

@Service
public class ProcesadorImagen {

    /**
     * Perfil de procesamiento para una entidad imagenable.
     *
     * @param anchoMaximo Ancho máximo en píxeles; el alto se escala proporcionalmente.
     * @param calidad Calidad de compresión entre 0.0 y 1.0.
     */
    private record PerfilImagen(int anchoMaximo, float calidad) {}

    // Perfiles por tipo de entidad. Los valores pueden ajustarse según necesidades de diseño.
    private static final PerfilImagen PERFIL_CONFITERIA = new PerfilImagen(800, 0.7f);
    private static final PerfilImagen PERFIL_PELICULA_POSTER = new PerfilImagen(800, 0.8f);
    private static final PerfilImagen PERFIL_PELICULA_BANNER = new PerfilImagen(1200, 0.8f);
    private static final PerfilImagen PERFIL_PERSONA = new PerfilImagen(400, 0.7f);

    /**
     * Procesa una imagen según el tipo de propietario.
     * <p>
     * Para películas se utiliza el tipo {@link TipoImagen#POSTER} por defecto.
     *
     * @param file Archivo de imagen a procesar.
     * @param propietario Entidad propietaria de la imagen.
     * @return Bytes de la imagen procesada en formato WebP.
     */
    public byte[] procesar(MultipartFile file, Imagenable propietario) throws IOException {

        if (propietario instanceof Pelicula) {
            return procesar(file, propietario, TipoImagen.POSTER);
        }

        return convertirFormato(file, resolverPerfil(propietario));
    }

    /**
     * Procesa una imagen de película según su tipo (poster o banner).
     *
     * @param file Archivo de imagen a procesar.
     * @param propietario Película propietaria de la imagen.
     * @param tipo Tipo de imagen de película.
     * @return Bytes de la imagen procesada en formato WebP.
     */
    public byte[] procesar(MultipartFile file, Imagenable propietario, TipoImagen tipo) throws IOException {

        return convertirFormato(file, resolverPerfil(tipo));
    }

    /**
     * Resuelve el perfil de procesamiento para una entidad no película.
     */
    private PerfilImagen resolverPerfil(Imagenable propietario) {

        if (propietario instanceof Confiteria) {
            return PERFIL_CONFITERIA;
        }

        if (propietario instanceof Persona) {
            return PERFIL_PERSONA;
        }

        // Perfil conservador por defecto para cualquier otro tipo imagenable.
        return PERFIL_CONFITERIA;
    }

    /**
     * Resuelve el perfil de procesamiento para una película según su tipo.
     */
    private PerfilImagen resolverPerfil(TipoImagen tipo) {

        return switch (tipo) {
            case AVATAR -> PERFIL_PERSONA;
            case BANNER -> PERFIL_PELICULA_BANNER;
            case POSTER, GALERIA -> PERFIL_PELICULA_POSTER;
            case PRODUCTO -> PERFIL_CONFITERIA;
        };
    }

    /**
     * Formato de salida optimizado para entregar al CDN.
     */
    private static final String FORMATO_SALIDA = "webp";

    /**
     * Convierte una imagen a formato WebP aplicando redimensionamiento proporcional.
     *
     * @param file Archivo de imagen a convertir.
     * @param perfil Perfil con ancho máximo y calidad.
     * @return Bytes de la imagen convertida.
     */
    public byte[] convertirFormato(MultipartFile file, PerfilImagen perfil) throws IOException {

        return convertirFormato(file, perfil.anchoMaximo(), perfil.calidad());
    }

    /**
     * Convierte una imagen a formato WebP aplicando redimensionamiento proporcional.
     *
     * @param file Archivo de imagen a convertir.
     * @param anchoMaximo Ancho máximo en píxeles.
     * @param quality Calidad de compresión entre 0.0 y 1.0.
     * @return Bytes de la imagen convertida.
     */
    public byte[] convertirFormato(MultipartFile file, int anchoMaximo, float quality) throws IOException {

        BufferedImage image = leerImagen(file);

        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(image)
                .width(anchoMaximo)
                .outputQuality(quality)
                .outputFormat(FORMATO_SALIDA)
                .toOutputStream(outputStream);

            return outputStream.toByteArray();

        } catch (Throwable e) {
            // Fallback ante fallos de librerías nativas. Se prefiere devolver el archivo
            // original antes de romper el flujo de negocio.
            return file.getBytes();
        }
    }

    /**
     * Método base para convertir un archivo físico a WebP.
     * Mantiene la firma anterior para compatibilidad.
     *
     * @param file Archivo físico.
     * @param quality Calidad de compresión.
     * @return Bytes de la imagen convertida.
     * @deprecated Usar {@link #convertirFormato(MultipartFile, int, float)} o {@link #procesar(MultipartFile, Imagenable)}.
     */
    @Deprecated
    public byte[] convertirFormato(File file, float quality) throws IOException {

        try {
            BufferedImage image = leerImagen(file);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Thumbnails.of(image)
                .width(800)
                .outputQuality(quality)
                .outputFormat(FORMATO_SALIDA)
                .toOutputStream(outputStream);

            return outputStream.toByteArray();

        } catch (Throwable e) {
            return java.nio.file.Files.readAllBytes(file.toPath());
        }
    }

    /**
     * Lee una imagen desde un archivo físico.
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
     * Lee una imagen desde un MultipartFile.
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
}
