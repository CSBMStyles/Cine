package com.unicine.util.funtional.image;

import org.springframework.stereotype.Service;

import com.unicine.entity.user.Persona;
import com.unicine.entity.image.interfaced.Imagenable;

@Service
public class RefactorizadorRuta {


    /**
     * Método para nombrar un archivo dependiendo de la entidad propietaria.
     * 
     * @param fileName
     * @param propietario
     * @return 
     */
    public String nombrarArchivo(String fileName, Imagenable propietario) {

        if (propietario instanceof Persona) {

            Persona persona = (Persona) propietario;

            return renombrarArchivoExtension(persona.getNombre() + "-" + persona.getApellido());

        } else {

            return renombrarArchivoExtension(fileName);
        }
    }

    /**
     * Método auxiliar para borrar la extensión de un nombre de archivo.
     *
     * @param fileName Nombre original del archivo.
     * @return Nombre del archivo sin la extensión.
     */
    public String renombrarArchivoExtension(String fileName) {

        // Obtener el nombre sin la extensión.
        int index = fileName.lastIndexOf('.');

        String baseName = index > 0 ? fileName.substring(0, index) : fileName;

        // Reemplazar espacios y caracteres especiales.
        return remplazarDenominacion(baseName);  
    }

    /**
     * Método auxiliar para reemplazar los espacios y caracteres especiales en el nombre del archivo.
     * Además convierte el resultado a formato título: solo la primera letra de cada palabra en mayúscula.
     *
     * @param fileName nombre del archivo original o carpeta
     * @return Nombre del archivo/carpeta normalizado en formato título con guiones.
     */
    public String remplazarDenominacion(String fileName) {

        // Normalizar a minúsculas para partir de una base uniforme.
        fileName = fileName.toLowerCase();

        // Reemplazar acentos y caracteres especiales del español por equivalentes ASCII.
        fileName = normalizarAcentos(fileName);

        // Reemplazar los espacios y guiones bajos por guiones.
        fileName = fileName.replace(" ", "-").replace("_", "-");
        
        // Eliminar cualquier caracter que no sea alfanumérico o alguno de los siguientes: '.', '_' o '-'.
        fileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "");

        // Aplicar formato título a cada segmento separado por guiones.
        return aplicarFormatoTitulo(fileName);
    }

    /**
     * Reemplaza vocales acentuadas y la eñe por sus equivalentes ASCII.
     *
     * @param input Texto a normalizar.
     * @return Texto sin acentos ni eñe.
     */
    private String normalizarAcentos(String input) {
        return input
                .replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace("ñ", "n")
                .replace("ü", "u");
    }

    /**
     * Método auxiliar para poner en mayúscula solo la primera letra de cada palabra,
     * manteniendo el resto en minúscula.
     *
     * Ejemplo: "de-toditos-rojo" → "De-Toditos-Rojo"
     *
     * @param input Texto separado por guiones.
     * @return Texto en formato título.
     */
    private String aplicarFormatoTitulo(String input) {

        if (input == null || input.isEmpty()) {
            return input;
        }

        String[] segmentos = input.split("-");
        StringBuilder resultado = new StringBuilder();

        for (int i = 0; i < segmentos.length; i++) {
            String segmento = segmentos[i];

            if (segmento.isEmpty()) {
                if (i > 0) {
                    resultado.append("-");
                }
                continue;
            }

            if (i > 0) {
                resultado.append("-");
            }

            resultado.append(Character.toUpperCase(segmento.charAt(0)));

            if (segmento.length() > 1) {
                resultado.append(segmento.substring(1));
            }
        }

        return resultado.toString();
    }


}
