package com.unicine.test.mapper;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.unicine.entity.movie.Pelicula;
import com.unicine.entity.showing.Funcion;
import com.unicine.entity.showing.FuncionEsquema;
import com.unicine.entity.showing.Horario;
import com.unicine.entity.theater.Sala;
import com.unicine.enums.movie.FormatoPelicula;
import com.unicine.transfer.dto.request.FuncionRequest;
import com.unicine.transfer.dto.response.FuncionResponse;
import com.unicine.transfer.mapper.FuncionMapper;

@SpringBootTest
public class FuncionMapperTest {

    @Autowired
    private FuncionMapper mapper;

    @Test
    public void toResponseMapeaCamposYRelacionesAnidadas() {
        Sala sala = Sala.builder().nombre("Sala 1").build();
        sala.setCodigo(1);

        Horario horario = Horario.builder()
                .fechaInicio(LocalDateTime.of(2026, 7, 4, 18, 0))
                .fechaFin(LocalDateTime.of(2026, 7, 4, 20, 0))
                .build();
        horario.setCodigo(2);

        Pelicula pelicula = Pelicula.builder().nombre("Inception").build();
        pelicula.setCodigo(3);

        FuncionEsquema esquema = FuncionEsquema.builder().build();
        esquema.setCodigo(4);

        Funcion funcion = Funcion.builder()
                .formato(FormatoPelicula.DOBLADO)
                .sala(sala)
                .horario(horario)
                .pelicula(pelicula)
                .build();
        funcion.setCodigo(5);
        funcion.setPrecio(15000.0);
        funcion.setFuncionEsquema(esquema);

        FuncionResponse response = mapper.toResponse(funcion);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(5, response.getCodigo());
        Assertions.assertEquals(15000.0, response.getPrecio());
        Assertions.assertEquals(FormatoPelicula.DOBLADO, response.getFormato());

        Assertions.assertNotNull(response.getSala());
        Assertions.assertEquals(1, response.getSala().getCodigo());

        Assertions.assertNotNull(response.getHorario());
        Assertions.assertEquals(2, response.getHorario().getCodigo());

        Assertions.assertNotNull(response.getPelicula());
        Assertions.assertEquals(3, response.getPelicula().getCodigo());

        Assertions.assertNotNull(response.getFuncionEsquema());
        Assertions.assertEquals(4, response.getFuncionEsquema().getCodigo());
    }

    @Test
    public void toResponseSoloExponeCamposDefinidosEnDto() {
        Funcion funcion = Funcion.builder()
                .formato(FormatoPelicula.SUBTITULADO)
                .build();
        funcion.setCodigo(6);

        FuncionResponse response = mapper.toResponse(funcion);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(6, response.getCodigo());
        // NOTE: compras no estan en FuncionResponse para evitar ciclos
    }

    @Test
    public void toEntityMapeaCodigosDeRelaciones() {
        FuncionRequest request = FuncionRequest.builder()
                .codigo(5)
                .precio(15000.0)
                .formato(FormatoPelicula.DOBLADO)
                .salaCodigo(1)
                .horarioCodigo(2)
                .peliculaCodigo(3)
                .build();

        Funcion funcion = mapper.toEntity(request);

        Assertions.assertNotNull(funcion);
        Assertions.assertEquals(5, funcion.getCodigo());
        Assertions.assertEquals(15000.0, funcion.getPrecio());
        Assertions.assertEquals(FormatoPelicula.DOBLADO, funcion.getFormato());
        Assertions.assertNotNull(funcion.getSala());
        Assertions.assertEquals(1, funcion.getSala().getCodigo());
        Assertions.assertNotNull(funcion.getHorario());
        Assertions.assertEquals(2, funcion.getHorario().getCodigo());
        Assertions.assertNotNull(funcion.getPelicula());
        Assertions.assertEquals(3, funcion.getPelicula().getCodigo());
        Assertions.assertNull(funcion.getCompras());
    }

    @Test
    public void toResponseListMapeaMultiplesElementos() {
        Funcion f1 = Funcion.builder().formato(FormatoPelicula.DOBLADO).build();
        f1.setCodigo(1);
        Funcion f2 = Funcion.builder().formato(FormatoPelicula.SUBTITULADO).build();
        f2.setCodigo(2);

        List<FuncionResponse> responses = mapper.toResponseList(List.of(f1, f2));

        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals(FormatoPelicula.DOBLADO, responses.get(0).getFormato());
        Assertions.assertEquals(FormatoPelicula.SUBTITULADO, responses.get(1).getFormato());
    }
}
