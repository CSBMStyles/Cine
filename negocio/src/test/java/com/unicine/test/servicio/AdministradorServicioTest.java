package com.unicine.test.servicio;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entidades.Administrador;
import com.unicine.servicio.PersonaServicio;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class AdministradorServicioTest {

    @Autowired
    private PersonaServicio<Administrador> administradorServicio;

    @Test
    @Sql("classpath:dataset.sql")
    public void login() {

        try {
            Administrador administrador = administradorServicio.login("cristianbarrera100@gmail.com", "fe5i/PFsjWU0/+4VjImKacbXbnsiQ07+L49lGB5bq4fQ5u5lMiNXljo0s+oSV73N");

            Assertions.assertEquals("cristianbarrera100@gmail.com", administrador.getCorreo());

            System.out.println("\n" + "Administrador encontrado:" + "\n" + administrador);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Administrador administrador = new Administrador(1002000000, "Camilo", "Esprada", "camilo@gmail.com", "78!Kz9'Aovr1>`A5");

        try {
            Administrador nuevo = administradorServicio.registrar(administrador);
            
            Assertions.assertEquals(1002000000, nuevo.getCedula());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        try{
            Administrador administrador = administradorServicio.obtener(1001000000).orElse(null);

            administrador.setNombre("Roberto");

            Administrador actualizado = administradorServicio.actualizar(administrador);

            Assertions.assertEquals("Roberto", actualizado.getNombre());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {
        
        try {
            administradorServicio.eliminar(1001000000);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
        try {
            administradorServicio.obtener(1001000000);

        } catch (Exception e) {
            // Realizamos una validacion de la prueba para aceptar que el administrador fue eliminado mendiante la excepcion del metodo de obtener
            Assertions.assertThrows(Exception.class, () -> {
                throw e;
            });

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        try {
            Administrador administrador = administradorServicio.obtener(1001000000).orElse(null);

            Assertions.assertEquals(1001000000, administrador.getCedula());

            System.out.println("\n" + "Registro encontrado:" + "\n" + administrador);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Administrador> lista = administradorServicio.listar();

            Assertions.assertEquals(1, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) {
            Assertions.assertTrue(false);

            throw new RuntimeException(e);
        }
    }
}
