package com.unicine.test.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.unicine.entity.movie.Comentario;
import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.user.Cliente;
import com.unicine.exception.BusinessRuleException;
import com.unicine.exception.ResourceNotFoundException;
import com.unicine.repository.movie.ComentarioRepo;
import com.unicine.repository.movie.PeliculaRepo;
import com.unicine.repository.user.ClienteRepo;
import com.unicine.service.movie.ComentarioServicio;
import com.unicine.transfer.dto.request.ComentarioRequest;
import com.unicine.transfer.dto.response.ComentarioResponse;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class ComentarioServicioTest {

    @Autowired
    private ComentarioServicio comentarioServicio;

    @Autowired
    private ComentarioRepo comentarioRepo;

    @Autowired
    private ClienteRepo clienteRepo;

    @Autowired
    private PeliculaRepo peliculaRepo;

    private ComentarioRequest crearComentarioValidoRequest() {
        Cliente cliente = clienteRepo.findById(1008000022).orElse(null);
        Pelicula pelicula = peliculaRepo.findById(4).orElse(null);

        return ComentarioRequest.builder()
                .texto("Excelente pelicula, muy recomendada")
                .likes(0)
                .dislikes(0)
                .fecha(LocalDateTime.now())
                .clienteCedula(cliente.getCedula())
                .peliculaCodigo(pelicula.getCodigo())
                .build();
    }

    private ComentarioRequest crearComentarioSinEntradaRequest() {
        Cliente cliente = clienteRepo.findById(1009000011).orElse(null);
        Pelicula pelicula = peliculaRepo.findById(4).orElse(null);

        return ComentarioRequest.builder()
                .texto("No deberia poder comentar")
                .likes(0)
                .dislikes(0)
                .fecha(LocalDateTime.now())
                .clienteCedula(cliente.getCedula())
                .peliculaCodigo(pelicula.getCodigo())
                .build();
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrar() throws Exception {
        ComentarioRequest request = crearComentarioValidoRequest();

        ComentarioResponse guardado = comentarioServicio.registrar(request);

        Assertions.assertNotNull(guardado);
        Assertions.assertNotNull(guardado.getFecha());
        Assertions.assertEquals(0, guardado.getLikes());
        Assertions.assertEquals(0, guardado.getDislikes());

        System.out.println("\nComentario guardado:");
        System.out.println(guardado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void registrarSinEntrada() {
        ComentarioRequest request = crearComentarioSinEntradaRequest();

        Assertions.assertThrows(BusinessRuleException.class, () -> comentarioServicio.registrar(request));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void actualizar() throws Exception {
        ComentarioRequest request = crearComentarioValidoRequest();
        ComentarioResponse guardado = comentarioServicio.registrar(request);

        ComentarioRequest actualizacion = ComentarioRequest.builder()
                .codigo(guardado.getCodigo())
                .texto("Texto actualizado")
                .likes(guardado.getLikes())
                .dislikes(guardado.getDislikes())
                .fecha(guardado.getFecha())
                .clienteCedula(guardado.getCliente().getCedula())
                .peliculaCodigo(guardado.getPelicula().getCodigo())
                .build();

        ComentarioResponse actualizado = comentarioServicio.actualizar(actualizacion);

        Assertions.assertEquals("Texto actualizado", actualizado.getTexto());

        System.out.println("\nComentario actualizado:");
        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminar() throws Exception {
        ComentarioRequest request = crearComentarioValidoRequest();
        ComentarioResponse guardado = comentarioServicio.registrar(request);

        comentarioServicio.eliminar(guardado.getCodigo(), true);

        Optional<Comentario> verificacion = comentarioRepo.findById(guardado.getCodigo());
        Assertions.assertTrue(verificacion.isEmpty());

        System.out.println("\nComentario eliminado");
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void eliminarSinConfirmacion() throws Exception {
        ComentarioRequest request = crearComentarioValidoRequest();
        ComentarioResponse guardado = comentarioServicio.registrar(request);

        Assertions.assertThrows(BusinessRuleException.class, () -> comentarioServicio.eliminar(guardado.getCodigo(), false));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void obtenerInexistente() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> comentarioServicio.obtener(999));
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorPelicula() throws Exception {
        ComentarioRequest request = crearComentarioValidoRequest();
        comentarioServicio.registrar(request);

        List<ComentarioResponse> comentarios = comentarioServicio.listarPorPelicula(4);

        Assertions.assertEquals(1, comentarios.size());

        System.out.println("\nComentarios por pelicula:");
        comentarios.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void listarPorCliente() throws Exception {
        ComentarioRequest request = crearComentarioValidoRequest();
        comentarioServicio.registrar(request);

        List<ComentarioResponse> comentarios = comentarioServicio.listarPorCliente(1008000022);

        Assertions.assertEquals(1, comentarios.size());

        System.out.println("\nComentarios por cliente:");
        comentarios.forEach(System.out::println);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void darLike() throws Exception {
        ComentarioRequest request = crearComentarioValidoRequest();
        ComentarioResponse guardado = comentarioServicio.registrar(request);

        ComentarioResponse actualizado = comentarioServicio.darLike(guardado.getCodigo());

        Assertions.assertEquals(1, actualizado.getLikes());

        System.out.println("\nComentario con like:");
        System.out.println(actualizado);
    }

    @Test
    @Sql("classpath:dataset.sql")
    public void darDislike() throws Exception {
        ComentarioRequest request = crearComentarioValidoRequest();
        ComentarioResponse guardado = comentarioServicio.registrar(request);

        ComentarioResponse actualizado = comentarioServicio.darDislike(guardado.getCodigo());

        Assertions.assertEquals(1, actualizado.getDislikes());

        System.out.println("\nComentario con dislike:");
        System.out.println(actualizado);
    }
}
