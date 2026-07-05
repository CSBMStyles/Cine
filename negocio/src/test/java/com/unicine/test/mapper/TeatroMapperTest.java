package com.unicine.test.mapper;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.unicine.entity.theater.Ciudad;
import com.unicine.entity.theater.Teatro;
import com.unicine.entity.user.AdministradorTeatro;
import com.unicine.transfer.dto.request.TeatroRequest;
import com.unicine.transfer.dto.response.TeatroResponse;
import com.unicine.transfer.mapper.TeatroMapper;

@SpringBootTest
public class TeatroMapperTest {

    @Autowired
    private TeatroMapper mapper;

    @Test
    public void toResponseMapeaRelacionesAnidadas() {
        Ciudad ciudad = Ciudad.builder().nombre("Bogota").build();
        ciudad.setCodigo(1);

        AdministradorTeatro admin = AdministradorTeatro.builder()
                .cedula(123456)
                .nombre("Carlos")
                .apellido("Lopez")
                .correo("carlos@unicine.com")
                .password("Secret123!")
                .build();

        Teatro teatro = Teatro.builder()
                .direccion("Calle 26 # 10-20")
                .telefono("3001234567")
                .ciudad(ciudad)
                .administradorTeatro(admin)
                .build();
        teatro.setCodigo(10);

        TeatroResponse response = mapper.toResponse(teatro);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(10, response.getCodigo());
        Assertions.assertEquals("Calle 26 # 10-20", response.getDireccion());
        Assertions.assertEquals("3001234567", response.getTelefono());

        Assertions.assertNotNull(response.getCiudad());
        Assertions.assertEquals(1, response.getCiudad().getCodigo());
        Assertions.assertEquals("Bogota", response.getCiudad().getNombre());

        Assertions.assertNotNull(response.getAdministradorTeatro());
        Assertions.assertEquals(123456, response.getAdministradorTeatro().getCedula());
        Assertions.assertEquals("Carlos", response.getAdministradorTeatro().getNombre());
        // NOTE: password y lista de teatros no estan en AdministradorTeatroResponse
    }

    @Test
    public void toResponseSoloExponeCamposDefinidosEnDto() {
        Teatro teatro = Teatro.builder()
                .direccion("Av. Siempre Viva")
                .telefono("3001234567")
                .build();
        teatro.setCodigo(20);

        TeatroResponse response = mapper.toResponse(teatro);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(20, response.getCodigo());
        // NOTE: salas no estan en TeatroResponse para evitar ciclos
    }

    @Test
    public void toEntityMapeaCodigosDeRelaciones() {
        TeatroRequest request = TeatroRequest.builder()
                .codigo(10)
                .direccion("Calle 26 # 10-20")
                .telefono("3001234567")
                .ciudadCodigo(1)
                .administradorTeatroCedula(123456)
                .build();

        Teatro teatro = mapper.toEntity(request);

        Assertions.assertNotNull(teatro);
        Assertions.assertEquals(10, teatro.getCodigo());
        Assertions.assertEquals("Calle 26 # 10-20", teatro.getDireccion());
        Assertions.assertNotNull(teatro.getCiudad());
        Assertions.assertEquals(1, teatro.getCiudad().getCodigo());
        Assertions.assertNotNull(teatro.getAdministradorTeatro());
        Assertions.assertEquals(123456, teatro.getAdministradorTeatro().getCedula());
        Assertions.assertNull(teatro.getSalas(), "Las salas deben ignorarse en el request");
    }

    @Test
    public void toResponseListMapeaMultiplesElementos() {
        Teatro t1 = Teatro.builder().direccion("T1").telefono("3001234567").build();
        t1.setCodigo(1);
        Teatro t2 = Teatro.builder().direccion("T2").telefono("3001234567").build();
        t2.setCodigo(2);

        List<TeatroResponse> responses = mapper.toResponseList(List.of(t1, t2));

        Assertions.assertNotNull(responses);
        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals("T1", responses.get(0).getDireccion());
        Assertions.assertEquals("T2", responses.get(1).getDireccion());
    }
}
