package src;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import src.application.controllers.IncidenciaService;
import src.application.controllers.MantenimientoService;
import src.domain.*;

import static org.mockito.Mockito.mock;


@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})
public class MantenimientoControllerTest {

    @Autowired
    protected IncidenciaRepository incidenciaRepository;

    @Autowired
    protected MantenimientoRepository mantenimientoRepository;

    @Autowired
    protected MantenimientoService  mantenimientoService;

    @Autowired
    protected EspacioRepository espacioRepository;

    @Autowired
    protected IncidenciaService incidenciaService;

    @Test
    public void testGetIncidenciaMantenimiento() throws JSONException {

        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "GetInfoMant", "desc", "PENDIENTE","prueba","worker1",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);

        Incidencia incidencia2 = new Incidencia(idIncidencia ,"GetInfoMant","desc","ACEPTADA","prueba","worker1",l);
        System.out.println(incidenciaRepository.pendienteToAceptada(incidencia2));

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("idIncidencia")).thenReturn(idIncidencia);
        Mockito.when(mockRequest.getHeader("idTrabajador")).thenReturn("worker1");

        ResponseEntity<String> response = incidenciaService.asignarIncidencia(idIncidencia,"worker1","Lunes","20");

        response = mantenimientoService.getIncidenciaMantenimiento(mockRequest);

        String expected = "\"Exito obteniendo incidencia mantenimiento.\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void testGetAllIncidenciasMantenimientoTrabajador() throws JSONException {
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "GetInfoMantTrab", "desc", "PENDIENTE","prueba","worker1",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);

        Incidencia incidencia2 = new Incidencia(idIncidencia ,"GetInfoMantTrab","desc","ACEPTADA","prueba","worker1",l);
        System.out.println(incidenciaRepository.pendienteToAceptada(incidencia2));

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("idTrabajador")).thenReturn("worker1");

        ResponseEntity<String> response = incidenciaService.asignarIncidencia(idIncidencia,"worker1","Lunes","16");

        response = mantenimientoService.getAllIncidenciasMantenimientoTrabajador(mockRequest);

        String expected = "\"Exito obteniendo todas las incidencia mantenimiento por trabajador.\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }

    @Test
    public void testGetAllIncidenciasMantenimientoEspacio() throws JSONException {
        Localizacion l = new Localizacion("CRE.1065.00.020","10",15,1,"00");
        Incidencia i = new Incidencia("01", "getInciMantEsp", "desc", "PENDIENTE","prueba","worker1",l);
        String idIncidencia = incidenciaRepository.addIncidenciaTest(i);

        Incidencia incidencia2 = new Incidencia(idIncidencia ,"getInciMantEsp","desc","ACEPTADA","prueba","worker1",l);
        System.out.println(incidenciaRepository.pendienteToAceptada(incidencia2));

        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("idEspacio")).thenReturn("10");

        ResponseEntity<String> response = incidenciaService.asignarIncidencia(idIncidencia,"worker1","Lunes","10");

        response = mantenimientoService.getAllIncidenciasMantenimientoEspacio(mockRequest);

        String expected = "\"Exito obteniendo todas las incidencia mantenimiento por espacio.\"";
        JSONAssert.assertEquals(expected, response.getBody(), false);
        System.out.println(response.getBody());

        incidenciaRepository.deleteIncidenciaByID(idIncidencia);
    }





}
