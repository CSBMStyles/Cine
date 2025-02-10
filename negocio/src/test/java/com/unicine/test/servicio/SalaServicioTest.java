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

import com.unicine.entidades.DistribucionSilla;
import com.unicine.entidades.Teatro;
import com.unicine.entidades.Sala;
import com.unicine.servicio.DistribucionSillaServicio;
import com.unicine.servicio.SalaServicio;
import com.unicine.servicio.TeatroServicio;
import com.unicine.util.emuns.TipoSala;
import com.unicine.util.validaciones.atributos.DistribucionAtributoValidator;
import com.unicine.util.validaciones.atributos.SalaAtributoValidator;
import com.unicine.util.validaciones.atributos.TeatroAtributoValidator;

// IMPORTANT: El @Transactional se utiliza para que las pruebas no afecten la base de datos, es decir, que no se guarden los cambios realizados en las pruebas

@SpringBootTest
@Transactional
public class SalaServicioTest {

    @Autowired
    private SalaServicio salaServicio;

    @Autowired
    private TeatroServicio teatroServicio;

    @Autowired
    private DistribucionSillaServicio distribucionServicio;

    // 🟩

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {
        
        Teatro teatro;

        try {
            teatro = teatroServicio.obtener(new TeatroAtributoValidator("1")).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        DistribucionSilla distribucionSilla;
        
        try {
            distribucionSilla = distribucionServicio.obtener(new DistribucionAtributoValidator("1")).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }


        // Creacion del sala

        Sala sala = new Sala("ARX-01", TipoSala.valueOf("DX4"), teatro, distribucionSilla);

        try {
            Sala nuevo = salaServicio.registrar(sala);
            
            Assertions.assertEquals("ARX-01", nuevo.getNombre());

            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {

        String nombre = "P-01: Premier Dorada";

        try{
            Sala sala = salaServicio.obtener(new SalaAtributoValidator(1)).orElse(null);

            sala.setNombre(nombre);

            Sala actualizado = salaServicio.actualizar(sala);

            Assertions.assertEquals(nombre, actualizado.getNombre());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {

        SalaAtributoValidator validator = new SalaAtributoValidator(1);

        Sala sala;

        try {
            sala = salaServicio.obtener(validator).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            salaServicio.eliminar(sala, true);

        } catch (Exception e) { throw new RuntimeException(e); }

        try {
            salaServicio.obtener(validator);

        } catch (Exception e) {

            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer codigo = 1;

        try {
            Sala sala = salaServicio.obtener(new SalaAtributoValidator(codigo)).orElse(null);

            Assertions.assertEquals(codigo, sala.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + sala);

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {

        try {
            List<Sala> lista = salaServicio.listar();

            Assertions.assertEquals(6, lista.size());

            System.out.println("\n" + "Listado de registros:");

            lista.forEach(System.out::println);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    // 🟥

    @ParameterizedTest
    @ValueSource(strings = {
        "", // Caso vacío
        "  ", // Espacios en blanco
        "Atl", // Caso menor a un caracter
        "Atlantis", // Existente
    })
    @Sql("classpath:dataset.sql")
    public void validacionNombres(String nombre) {

        System.out.println("\n" + nombre);
        try{
            List<Sala> salas = salaServicio.obtenerNombresTeatro(new SalaAtributoValidator(nombre), 5);

            Assertions.assertEquals(1, salas.size());

            System.out.println("\n" + "Registros:" + "\n" + salas);

        } catch (Exception e) {
            
            Assertions.assertThrows(Exception.class, () -> {throw e;});

            System.out.println(e.getMessage());
        }
    }
}
