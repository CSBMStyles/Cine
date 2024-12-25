package com.unicine.test.repo;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import com.unicine.entidades.Administrador;
import com.unicine.repo.AdministradorRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AdministradorTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private AdministradorRepo administradorRepo;

    // SECTION: Consultas basicas para la base de datos
    
    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        Administrador administrador = new Administrador(1002000000, "Esteban", "Castaño", "estaban.castaño@uqvirtual.edu.co", "[99j'Ud&3£n:;z77");

        Administrador guardado = administradorRepo.save(administrador);

        Assertions.assertEquals("Esteban", guardado.getNombre());

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        Administrador guardado = administradorRepo.findById(1001000000).orElse(null);

        System.out.println(guardado);

        guardado.setNombre("Rafael");

        Administrador actualizado = administradorRepo.save(guardado);

        Assertions.assertEquals("Rafael", actualizado.getNombre());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Administrador buscado = administradorRepo.findById(1001000000).orElse(null);

        System.out.println(buscado);

        administradorRepo.delete(buscado);

        Administrador verificacion = administradorRepo.findById(1002000000).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<Administrador> buscado = administradorRepo.findById(1001000000);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Administrador> administradores = administradorRepo.findAll();

        Assertions.assertEquals(1, administradores.size());

        System.out.println("\n" + "Listado de registros:");

        for (Administrador admin : administradores) {
            System.out.println(admin);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Administrador> administradores = administradorRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(1, administradores.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (Administrador admin : administradores) {
            System.out.println(admin);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<Administrador> administradores = administradorRepo.findAll(Sort.by("nombre"));

        Assertions.assertEquals(1, administradores.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (Administrador admin : administradores) {
            System.out.println(admin);
        }
    }

    // SECTION: Consultas personalizadas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void findByCorreo() {
            
        Optional<Administrador> buscado = administradorRepo.findByCorreo("cristianbarrera100@gmail.com");

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido por correo:");

        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void comprobarAutenticacion() {
            
        Optional<Administrador> buscado = administradorRepo.comprobarAutenticacion("cristianbarrera100@gmail.com", "fe5i/PFsjWU0/+4VjImKacbXbnsiQ07+L49lGB5bq4fQ5u5lMiNXljo0s+oSV73N");

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro autenticado:");
        
        System.out.println(buscado.orElse(null));
    }
}
