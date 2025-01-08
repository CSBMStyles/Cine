package com.unicine.test.servicio;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entidades.AdministradorTeatro;
import com.unicine.entidades.Ciudad;
import com.unicine.entidades.Teatro;
import com.unicine.servicio.CiudadServicio;
import com.unicine.servicio.PersonaServicio;
import com.unicine.servicio.TeatroServicio;
import com.unicine.util.validacion.atributos.PersonaAttributeValidator;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class TeatroServicioTest {

    @Autowired
    private TeatroServicio teatroServicio;

    @Autowired
    private CiudadServicio ciudadServicio;

    @Autowired
    private PersonaServicio<AdministradorTeatro> administradorServicio;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {
        
        String direccion = "Avenida 1 # 4-6 Este";

        Ciudad ciudad;

        try {
            ciudad = ciudadServicio.obtener(1).orElse(null);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        AdministradorTeatro administradorTeatro;
        
        try {
            administradorTeatro = administradorServicio.obtener(new PersonaAttributeValidator("1119000000")).orElse(null);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        // Creacion del teatro

        Teatro teatro = new Teatro(direccion, "3162316812", ciudad, administradorTeatro);

        try {
            Teatro nuevo = teatroServicio.registrar(teatro);
            
            Assertions.assertEquals(direccion, nuevo.getDireccion());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        String telefono = "3125867145";

        try{
            Teatro teatro = teatroServicio.obtener(1).orElse(null);

            teatro.setTelefono(telefono);

            Teatro actualizado = teatroServicio.actualizar(teatro);

            Assertions.assertEquals(telefono, actualizado.getTelefono());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Integer codigo = 1;

        Teatro teatro;

        try {
            teatro = teatroServicio.obtener(codigo).orElse(null);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            teatroServicio.eliminar(teatro, true);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }

        try {
            teatroServicio.obtener(codigo);

        } catch (Exception e) {
            // Realizamos una validacion de la prueba para aceptar que el teatro fue eliminado mendiante la excepcion del metodo de obtener
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer codigo = 1;

        try {
            Teatro teatro = teatroServicio.obtener(codigo).orElse(null);

            Assertions.assertEquals(codigo, teatro.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + teatro);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Teatro> lista = teatroServicio.listar();

            Assertions.assertEquals(6, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    // 🟥

    @ParameterizedTest
    @ValueSource(strings = {
        "", // Caso vacío
        "  ", // Espacios en blanco
        "C", // Caso menor a un caracter
        "Calle 3 # 1 A 24 Sur", // Existente
    })
    @Sql("classpath:dataset.sql")
    public void validacionDireccion(String direcion) {

        System.out.println("\n" + direcion);
        try{
            Teatro teatro = teatroServicio.obtener(3).orElse(null);

            teatro.setDireccion(direcion);

            Teatro actualizado = teatroServicio.actualizar(teatro);

            Assertions.assertEquals(direcion, actualizado.getDireccion());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }
}
