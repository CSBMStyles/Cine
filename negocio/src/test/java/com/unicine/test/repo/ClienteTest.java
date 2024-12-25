package com.unicine.test.repo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

import com.unicine.entidades.Cliente;
import com.unicine.repo.ClienteRepo;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ClienteTest {

    /* NOTE: En las pruebas de unitarias o de integracion se menciona que se debe comprobar el resultado con el Assertions, pero no esta de mas imprimir el resultado para verificar visualmente que se esta obteniendo lo esperado */

    // NOTE: El autowired se utiliza para inyectar dependencias, donde se instancia por si solo la clase que se necesita
    @Autowired
    private ClienteRepo clienteRepo;

    // SECTION: Consultas basicas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        // Creamos un mapa de imágenes
        Map<String, String> imagenes = new HashMap<>();
        imagenes.put("http://example.com/imagen-1.jpg", "perfil");

        // Crear la lista de teléfonos
        ArrayList<String> telefonos = new ArrayList<>();
        telefonos.add("3160369165");

        LocalDate fechaNacimiento = LocalDate.of(1990, 10, 10);

        Cliente cliente = new Cliente(1004000066, "Juan", "Parra", "juan@gmail.com", "78!Kz9'Aovr1>`A5", false, fechaNacimiento, imagenes, telefonos);

        Cliente guardado = clienteRepo.save(cliente);

        Assertions.assertEquals("Juan", guardado.getNombre());

        System.out.println("\n" + "Registro guardado:");

        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        // NOTE: El or else es para evitar el <NullPointerException> en caso de no existir
        Cliente guardado = clienteRepo.findById(1009000011).orElse(null);

        System.out.println(guardado);

        guardado.setNombre("Juan");

        Cliente actualizado = clienteRepo.save(guardado);

        Assertions.assertEquals("Juan", actualizado.getNombre());

        System.out.println("\n" + "Registro actualizado:");

        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        Cliente buscado = clienteRepo.findById(1009000011).orElse(null);

        System.out.println(buscado);

        clienteRepo.delete(buscado);

        Cliente verificacion = clienteRepo.findById(1009000011).orElse(null);

        // NOTE: Verificar que el cliente fue eliminado, donde se usar or else para evitar el <NullPointerException>
        Assertions.assertNull(verificacion);

        System.out.println("\n" + "Registro eliminado:");

        System.out.println(verificacion);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        // NOTE: El Optional es un contenedor que puede o no contener un valor no nulo, esto para evitar el <NullPointerException>
        // REVIEW: Se recomienda usar la primera forma, sin el Optional y mejor aplicar el "orElse" para evitar el <NullPointerException>
        Optional<Cliente> buscado = clienteRepo.findById(1009000011);

        Assertions.assertTrue(buscado.isPresent());

        System.out.println("\n" + "Registro obtenido:");

        System.out.println(buscado.orElse(null));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        List<Cliente> clientes = clienteRepo.findAll();

        Assertions.assertEquals(5, clientes.size());

        System.out.println("\n" + "Listado de registros:");

        for (Cliente c : clientes) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPaginado() {

        List<Cliente> clientes = clienteRepo.findAll(PageRequest.of(0, 3)).toList();

        Assertions.assertEquals(3, clientes.size());

        System.out.println("\n" + "Listado de registros paginados:");

        for (Cliente c : clientes) {
            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarOdenado() {

        List<Cliente> clientes = clienteRepo.findAll(Sort.by("nombre"));

        Assertions.assertEquals(5, clientes.size());

        System.out.println("\n" + "Listado de registros ordenados:");

        for (Cliente c : clientes) {
            System.out.println(c);
        }
    }

    // SECTION: Consultas personalizadas para la base de datos

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerClienteCorreo(){

        Cliente cliente = clienteRepo.findByCorreo("pepe@hotmail.com").orElse(null);

        Assertions.assertEquals("pepe@hotmail.com", cliente.getCorreo());

        System.out.println("\n" + "Registro obtenido:");

        System.out.println(cliente);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerClienteEstado(){

        List<Cliente> clientes = clienteRepo.findByEstado(true);

        for (Cliente c : clientes) {
            Assertions.assertTrue(c.getEstado());

            System.out.println(c);
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void comprobarAutenticacion(){

        Optional<Cliente> cliente = clienteRepo.comprobarAutenticacion("pepe@hotmail.com", "fe5i/PFsjWU0/+4VjImKacbXbnsiQ07+L49lGB5bq4fQ5u5lMiNXljo0s+oSV73N");

        Assertions.assertTrue(cliente.isPresent());

        System.out.println("\n" + "Registro autenticado:");

        System.out.println(cliente);
    }
}
