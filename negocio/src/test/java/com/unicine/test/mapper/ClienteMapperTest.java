package com.unicine.test.mapper;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.unicine.entity.image.Imagen;
import com.unicine.entity.user.Cliente;
import com.unicine.transfer.dto.request.ClienteRequest;
import com.unicine.transfer.dto.response.ClienteResponse;
import com.unicine.transfer.mapper.ClienteMapper;

@SpringBootTest
public class ClienteMapperTest {

    @Autowired
    private ClienteMapper mapper;

    @Test
    public void toResponseMapeaCamposHeradosYPropios() {
        Cliente cliente = Cliente.builder()
                .cedula(987654)
                .nombre("Ana")
                .apellido("Garcia")
                .correo("ana@unicine.com")
                .password("Secret123!")
                .estado(true)
                .fechaNacimiento(LocalDate.of(1995, 5, 15))
                .telefonos(List.of("+573001234567"))
                .build();

        ClienteResponse response = mapper.toResponse(cliente);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(987654, response.getCedula());
        Assertions.assertEquals("Ana", response.getNombre());
        Assertions.assertEquals("Garcia", response.getApellido());
        Assertions.assertEquals("ana@unicine.com", response.getCorreo());
        Assertions.assertEquals(true, response.getEstado());
        Assertions.assertEquals(LocalDate.of(1995, 5, 15), response.getFechaNacimiento());
        Assertions.assertEquals(List.of("+573001234567"), response.getTelefonos());
        // Note: password no esta en ClienteResponse por seguridad
    }

    @Test
    public void toResponseMapeaImagenAnidada() {
        Imagen imagen = new Imagen();
        imagen.setCodigo("IMG-001");
        imagen.setUrl("https://cdn.unicine.com/1.jpg");

        Cliente cliente = Cliente.builder()
                .cedula(111)
                .nombre("Luis")
                .apellido("Ruiz")
                .correo("luis@unicine.com")
                .password("Secret123!")
                .estado(true)
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
        cliente.setImagen(imagen);

        ClienteResponse response = mapper.toResponse(cliente);

        Assertions.assertNotNull(response.getImagen());
        Assertions.assertEquals("IMG-001", response.getImagen().getCodigo());
        Assertions.assertEquals("https://cdn.unicine.com/1.jpg", response.getImagen().getUrl());
    }

    @Test
    public void toResponseSoloExponeCamposDefinidosEnDto() {
        Cliente cliente = Cliente.builder()
                .cedula(222)
                .nombre("Pedro")
                .apellido("Mendez")
                .correo("pedro@unicine.com")
                .password("Secret123!")
                .estado(true)
                .fechaNacimiento(LocalDate.of(1988, 8, 8))
                .build();

        ClienteResponse response = mapper.toResponse(cliente);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(222, response.getCedula());
        // Note: compras, cuponClientes y colecciones no estan en ClienteResponse
    }

    @Test
    public void toEntityMapeaCamposYOmiteRelacionesInversas() {
        ClienteRequest request = ClienteRequest.builder()
                .cedula(333)
                .nombre("Maria")
                .apellido("Torres")
                .correo("maria@unicine.com")
                .password("Secret123!")
                .estado(false)
                .fechaNacimiento(LocalDate.of(2000, 2, 20))
                .telefonos(List.of("+573009876543"))
                .build();

        Cliente cliente = mapper.toEntity(request);

        Assertions.assertNotNull(cliente);
        Assertions.assertEquals(333, cliente.getCedula());
        Assertions.assertEquals("Maria", cliente.getNombre());
        Assertions.assertEquals("Secret123!", cliente.getPassword());
        Assertions.assertNull(cliente.getCompras());
        Assertions.assertNull(cliente.getCuponClientes());
        Assertions.assertNull(cliente.getColecciones());
    }

    @Test
    public void toResponseListMapeaMultiplesElementos() {
        Cliente c1 = Cliente.builder().cedula(1).nombre("A").apellido("B").correo("a@b.com").password("Secret123!").estado(true).fechaNacimiento(LocalDate.now().minusYears(20)).build();
        Cliente c2 = Cliente.builder().cedula(2).nombre("C").apellido("D").correo("c@d.com").password("Secret123!").estado(true).fechaNacimiento(LocalDate.now().minusYears(25)).build();

        List<ClienteResponse> responses = mapper.toResponseList(List.of(c1, c2));

        Assertions.assertEquals(2, responses.size());
        Assertions.assertEquals("A", responses.get(0).getNombre());
        Assertions.assertEquals("C", responses.get(1).getNombre());
    }
}
