package com.unicine.test.servicio;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entidades.PeliculaDisposicion;
import com.unicine.entidades.Pelicula;
import com.unicine.entidades.Ciudad;
import com.unicine.servicio.CiudadServicio;
import com.unicine.servicio.PeliculaDisposicionServicio;
import com.unicine.servicio.PeliculaServicio;
import com.unicine.util.emuns.EstadoPelicula;
import com.unicine.util.validaciones.atributos.CiudadAtributoValidator;
import com.unicine.util.validaciones.atributos.PeliculaAtributoValidator;

import java.util.List;

@SpringBootTest
@Transactional
public class PeliculaDisposicionServicioTest {

    @Autowired
    private PeliculaDisposicionServicio peliculaDisposicionServicio;

    @Autowired
    private PeliculaServicio peliculaServicio;

    @Autowired 
    private CiudadServicio ciudadServicio;

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() {

        EstadoPelicula estado = EstadoPelicula.CARTELERA;

        // Obtenemos la pelicula apartir del que se haya seleccionado, esta configuracion se recomienda que este en la misma interfaz de la creacion de pelicula
        Pelicula pelicula;
        try {
            pelicula = peliculaServicio.obtener(new PeliculaAtributoValidator(1)).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        // Obtenemos la ciudad apartir de una lista, puede automatizarse para que se seleccione la ciudad por apartir del administrador que lo crea y su ubicacion
        Ciudad ciudad;
        try {
            ciudad = ciudadServicio.obtener(new CiudadAtributoValidator(1)).orElse(null);

        } catch (Exception e) { throw new RuntimeException(e); }

        PeliculaDisposicion peliculaDisposicion = new PeliculaDisposicion(estado, pelicula, ciudad);

        try {
            PeliculaDisposicion nuevo = peliculaDisposicionServicio.registrar(peliculaDisposicion);
            
            Assertions.assertEquals(estado, nuevo.getEstadoPelicula());
            
            System.out.println("\n" + "Registro guardado:" + "\n" + nuevo);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() {
        try {
            PeliculaDisposicion peliculaDisposicion = peliculaDisposicionServicio.obtener(1).orElse(null);

            peliculaDisposicion.setEstadoPelicula(EstadoPelicula.PREVENTA);

            PeliculaDisposicion actualizado = peliculaDisposicionServicio.actualizar(peliculaDisposicion);

            Assertions.assertEquals(EstadoPelicula.PREVENTA, actualizado.getEstadoPelicula());

            System.out.println("\n" + "Registro actualizado:" + "\n" + actualizado);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() {
        
        Integer codigo = 1;

        try {
            PeliculaDisposicion peliculaDisposicion = peliculaDisposicionServicio.obtener(codigo).orElse(null);

            peliculaDisposicionServicio.eliminar(peliculaDisposicion, true);
            
            Assertions.assertThrows(Exception.class, () -> {
                peliculaDisposicionServicio.obtener(codigo);
            });

        } catch (Exception e) { throw new RuntimeException(e); }
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtener() {

        Integer codigo = 1;

        try {
            PeliculaDisposicion peliculaDisposicion = peliculaDisposicionServicio.obtener(codigo).orElse(null);

            Assertions.assertEquals(codigo, peliculaDisposicion.getCodigo());

            System.out.println("\n" + "Registro encontrado:" + "\n" + peliculaDisposicion);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listar() {
        try {
            List<PeliculaDisposicion> lista = peliculaDisposicionServicio.listar();
            Assertions.assertTrue(lista.size() > 0);
            System.out.println("\n" + "Listado de registros:");
            lista.forEach(System.out::println);

        } catch (Exception e) { throw new RuntimeException(e); }

    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarRecomendacionPeliculaEstado() {
        try {

            EstadoPelicula estado = EstadoPelicula.FUERA_CARTELERA;

            PeliculaDisposicion peliculaDisposicion = peliculaDisposicionServicio.obtener(1).orElse(null);

            List<PeliculaDisposicion> lista = peliculaDisposicionServicio.listarRecomendacionPeliculaEstado(peliculaDisposicion, estado);

            Assertions.assertTrue(lista.size() > 0);

            System.out.println("\n" + "Listado de recomendaciones:");

            lista.forEach(System.out::println);

        } catch (Exception e) { throw new RuntimeException(e); }

    }
}
