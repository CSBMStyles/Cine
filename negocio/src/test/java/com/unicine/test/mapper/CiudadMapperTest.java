package com.unicine.test.mapper;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.unicine.entity.theater.Ciudad;
import com.unicine.transfer.dto.request.CiudadRequest;
import com.unicine.transfer.dto.response.CiudadResponse;
import com.unicine.transfer.mapper.CiudadMapper;

@SpringBootTest
public class CiudadMapperTest {

    @Autowired
    private CiudadMapper mapper;

    @Test
    public void toResponseMapeaCamposBasicos() {
        Ciudad ciudad = Ciudad.builder().nombre("Bogota").build();
        ciudad.setCodigo(1);

        CiudadResponse response = mapper.toResponse(ciudad);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1, response.getCodigo());
        Assertions.assertEquals("Bogota", response.getNombre());
    }

    @Test
    public void toResponseSoloExponeCamposDefinidosEnDto() {
        Ciudad ciudad = Ciudad.builder().nombre("Medellin").build();
        ciudad.setCodigo(2);

        CiudadResponse response = mapper.toResponse(ciudad);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.getCodigo());
        Assertions.assertEquals("Medellin", response.getNombre());
        // Note: teatros y peliculaDisposicion no estan en CiudadResponse para evitar ciclos
    }

    @Test
    public void toResponseListMapeaMultiplesElementos() {
        Ciudad c1 = Ciudad.builder().nombre("Cali").build();
        c1.setCodigo(1);
        Ciudad c2 = Ciudad.builder().nombre("Cartagena").build();
        c2.setCodigo(2);

        List<CiudadResponse> responses = mapper.toResponseList(List.of(c1, c2));

        Assertions.assertNotNull(responses);
        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals("Cali", responses.get(0).getNombre());
        Assertions.assertEquals("Cartagena", responses.get(1).getNombre());
    }

    @Test
    public void toEntityMapeaCamposBasicos() {
        CiudadRequest request = CiudadRequest.builder()
                .codigo(5)
                .nombre("Pasto")
                .build();

        Ciudad ciudad = mapper.toEntity(request);

        Assertions.assertNotNull(ciudad);
        Assertions.assertEquals(5, ciudad.getCodigo());
        Assertions.assertEquals("Pasto", ciudad.getNombre());
    }
}
