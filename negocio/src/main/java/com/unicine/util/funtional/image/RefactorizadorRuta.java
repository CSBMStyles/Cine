package com.unicine.util.funtional.image;

import org.springframework.stereotype.Service;

import com.unicine.entity.Persona;
import com.unicine.entity.interfaced.Imagenable;

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
     *
     * @param fileName nombre del archivo original o carpeta
     * @return Nombre del archivo/carpeta con espacios y caracteres especiales reemplazados.
     */
    public String remplazarDenominacion(String fileName) {

        // Reemplazar los espacios por guiones.
        fileName = fileName.replace(" ", "-").replace("_", "-");
        
        // Eliminar cualquier caracter que no sea alfanumérico o alguno de los siguientes: '.', '_' o '-'.
        fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "");

        return fileName;
    }


}
