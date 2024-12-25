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

import com.unicine.entidades.AdministradorTeatro;
import com.unicine.repo.AdministradorTeatroRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AdministradorTeatroTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    @Autowired
    private AdministradorTeatroRepo administradorTeatroRepo;

    // SECTION: Consultas basicas para la base de datos
    
    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        AdministradorTeatro adminTeatro = new AdministradorTeatro(1778000000, "Gomez", "Carlos", "carlos.gomez@uqvirtual.edu.co", "P]V8y1`I)U3)}29.");

        AdministradorTeatro guardado = administradorTeatroRepo.save(adminTeatro);

        Assertions.assertEquals("Gomez", guardado.getNombre());

        System.out.println("\n" + "Registro guardado:");
        
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        AdministradorTeatro guardado = administradorTeatroRepo.findById(1119000000).orElse(null);

        System.out.println(guardado);

        guardado.setNombre("Camilo");

        AdministradorTeatro actualizado = administradorTeatroRepo.save(guardado);

        Assertions.assertEquals("Camilo", actualizado.getNombre());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        AdministradorTeatro buscado = administradorTeatroRepo.findById(1119000000).orElse(null);

        System.out.println(buscado);

        administradorTeatroRepo.delete(buscado);

        AdministradorTeatro verificacion = administradorTeatroRepo.findById(1119000000).orElse(null);

        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Optional<AdministradorTeatro> buscado = administradorTeatroRepo.findById(1119000000);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");
        
        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<AdministradorTeatro> administradores = administradorTeatroRepo.findAll();

        Assertions.assertEquals(6, administradores.size());

        System.out.println("\n" + "Listado de registros:");

        for (AdministradorTeatro admin : administradores) {
            System.out.println(admin);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<AdministradorTeatro> administradores = administradorTeatroRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, administradores.size());

        System.out.println("\n" + "Listado de registros paginado:");

        for (AdministradorTeatro admin : administradores) {
            System.out.println(admin);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOrdenado() {

        List<AdministradorTeatro> administradores = administradorTeatroRepo.findAll(Sort.by("nombre"));

        Assertions.assertEquals(6, administradores.size());

        System.out.println("\n" + "Listado de registros ordenado:");

        for (AdministradorTeatro admin : administradores) {
            System.out.println(admin);
        }
    }

    // SECTION: Colsultas personalizadas

    @Test
    @Sql("classpath:dataset.sql")
    public void findByCorreo() {
            
        Optional<AdministradorTeatro> buscado = administradorTeatroRepo.findByCorreo("jhona.belloc@uqvirtual.edu.co");

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido por correo:");

        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void comprobarAutenticacion() {
            
        Optional<AdministradorTeatro> buscado = administradorTeatroRepo.comprobarAutenticacion("jhona.belloc@uqvirtual.edu.co", "fe5i/PFsjWU0/+4VjImKacbXbnsiQ07+L49lGB5bq4fQ5u5lMiNXljo0s+oSV73N");

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro autenticado:");
        
        System.out.println(buscado.orElse(null));
    }

}
