package hello;

/**
 * Created by Javier on 14/08/2017.
 */

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;


@RunWith(SpringRunner.class)
@WebMvcTest(value = ApiController.class, secure = false)
public class ApiControllerTest {

    @Autowired
    private MockMvc mockMvc;



    @Test
    public void calcularContextosVacio() throws Exception {
        String body="";


        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/calcularContextos").content(body)
                .contentType(MediaType.TEXT_PLAIN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        System.out.println("Lo que devuelve: "+response.getContentAsString());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());


    }

    @Test
    public void calcularContextosOk() throws Exception {
        String body="Prueba de texto";


        // Send course as body to /students/Student1/courses
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/calcularContextos").content(body)
                .contentType(MediaType.TEXT_PLAIN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        System.out.println("Lo que devuelve: "+response.getContentAsString());

        assertEquals(HttpStatus.OK.value(), response.getStatus());


    }

    @Test
    public void calcularComplejidadDevuelveInt() throws Exception {
        String body="Prueba de texto";


        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/calcularComplejidad").content(body)
                .contentType(MediaType.ALL);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        System.out.println("Lo que devuelve: "+response.getContentAsString());
        boolean numerico=false;
        try
        {
            Integer.parseInt(response.getContentAsString());
            numerico=true;
        } catch(Exception e)
        {
            System.out.println(e.getMessage());
            numerico=false;
        }

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertTrue(numerico);


    }

    @Test
    public void significado2Ok() throws Exception {


        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/significado2?palabra=karateca");

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        System.out.println("Lo que devuelve: "+response.getContentAsString());

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String expected = "[{\"contexto\":\"SignificadoDF\",\"texto\":\"1. Persona que practica el kárate. Ejemplo de uso: Los karatecas van vestidos de blanco cuando van a luchar.\"}]";

        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);


    }

    @Test
    public void convertirTexto2Ok() throws Exception {
        String body="Prueba de texto";


        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/convertirTexto2?puntuacion=1&sinonimos=1&pasivas=1").content(body)
                .contentType(MediaType.TEXT_PLAIN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();

        String expected="{\"complejidad\":{\"antes\":0,\"despues\":0,\"calculada\":false}}";
        System.out.println("Lo que devuelve: "+response.getContentAsString());
        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);

        assertEquals(HttpStatus.OK.value(), response.getStatus());


    }

    @Test
    public void convertirTextoVacio() throws Exception {
        String body="";


        // Send course as body to /students/Student1/courses
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("http://localhost:8080/api/convertirTexto2?puntuacion=1&sinonimos=1&pasivas=1").content(body)
                .contentType(MediaType.TEXT_PLAIN);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();

        MockHttpServletResponse response = result.getResponse();


        System.out.println("Lo que devuelve: "+response.getContentAsString());


        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());


    }



}
