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
import src.application.controllers.IncidenceService;
import src.application.controllers.MantenimientoService;
import src.application.controllers.MapService;
import src.domain.*;

import java.util.ArrayList;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest(classes={Application.class})

public class MapServiceTest {

  @Autowired
  protected EspacioRepository espacioRepositorio;

  @Autowired
  protected IncidenciaRepository incidenciaRepositorio;

  @Autowired
  protected IncidenceService incidenceService;

  @Autowired
  protected MapService mapService;

  @Autowired
  protected MantenimientoRepository mantenimientoRepository;

  @Autowired
  protected MantenimientoService mantenimientoService;

  @Test
  public void getInfoTest() {
    Espacio e = new Espacio("CRE.1065.00.020","PASILLO","TORRES QUEVEDO","PASILLO",false,"0.0",null);
    Espacio buscado = espacioRepositorio.findEspacioByID(e.getId());
    assert(e.getId().equals(buscado.getId()));
  }

  @Test
  public void guardarHoraTest() {
    Espacio espacio = espacioRepositorio.findEspacioByID("CRE.1065.00.020");
    Horario horarioOld = espacio.getHorario();
    ArrayList<CeldaHorario> chArray = horarioOld.getHorasLunes();
    CeldaHorario chOld = null;
    for (CeldaHorario c : chArray) {
      if (c.getHoraDeInicio() == 8) {
        chOld = c;
      }
    }
    Boolean result = false;
    if (chOld == null) {
      result = espacioRepositorio.addActividadAlHorario(espacio.getId(), "Lunes", 8, "De prueba");
      assert(result == true);
      result = espacioRepositorio.deleteActividadDelHorario(espacio.getId(), "Lunes", 8);
      assert(result == true);
    } else {
      result = espacioRepositorio.deleteActividadDelHorario(espacio.getId(), "Lunes", 8);
      assert(result == true);
      result = espacioRepositorio.addActividadAlHorario(espacio.getId(), "Lunes", 8, "De prueba");
      assert(result == true);
      result = espacioRepositorio.deleteActividadDelHorario(espacio.getId(), "Lunes", 8);
      assert(result == true);
      result = espacioRepositorio.addActividadAlHorario(espacio.getId(), "Lunes", chOld.getHoraDeInicio(), chOld.getActividad());
      assert(result == true);
    }
  }

  @Test
  public void testGetInfoDeEspacio() throws JSONException {
      HttpServletRequest mockRequest = mock(HttpServletRequest.class);
      Mockito.when(mockRequest.getHeader("id")).thenReturn("CRE.1065.00.020");

      ResponseEntity<String> response = mapService.getInfoDeEspacio(mockRequest);

      String expected = "\"Exito obteniendo espacio\"";
      JSONAssert.assertEquals(expected, response.getBody(), false);
      System.out.println(response.getBody());
  }

  @Test
  public void testGuardarHora() throws JSONException {
      HttpServletRequest mockRequest = mock(HttpServletRequest.class);
      Mockito.when(mockRequest.getParameter("idEspacio")).thenReturn("CRE.1065.00.020");
      Mockito.when(mockRequest.getParameter("dia")).thenReturn("Jueves");
      Mockito.when(mockRequest.getParameter("hora")).thenReturn("11");
      Mockito.when(mockRequest.getParameter("actividad")).thenReturn("Test de guardar hora");

      ResponseEntity<String> response = mapService.guardarHora(mockRequest);

      String expected = "\"Exito actualizando horario\"";
      JSONAssert.assertEquals(expected, response.getBody(), false);
      System.out.println(response.getBody());
  }

  @Test
  public void testVerHorarioDeEspacio() throws JSONException {
      Localizacion l = new Localizacion("CRE.1200.00.050","10",15,1,"00");
      Incidencia i = new Incidencia("01", "TestVerHorEsp", "desc", "PENDIENTE","prueba","worker1",l);
      String idIncidencia = incidenciaRepositorio.addIncidenciaTest(i);

      Incidencia incidencia2 = new Incidencia(idIncidencia ,"TestVerHorEsp","desc","ACEPTADA","prueba","worker1",l);
      System.out.println(incidenciaRepositorio.pendienteToAceptada(incidencia2));

      HttpServletRequest mockRequest = mock(HttpServletRequest.class);
      Mockito.when(mockRequest.getHeader("idIncidencia")).thenReturn(idIncidencia);

      ResponseEntity<String> response = mapService.verHorarioDeEspacio(mockRequest);

      String expected = "\"Exito obteniendo el horario del espacio\"";
      JSONAssert.assertEquals(expected, response.getBody(), false);
      System.out.println(response.getBody());

      incidenciaRepositorio.deleteIncidenciaByID(idIncidencia);
  }

  @Test
  public void testObtenerCoordsEspacio() throws JSONException {
      HttpServletRequest mockRequest = mock(HttpServletRequest.class);
      Mockito.when(mockRequest.getHeader("idEspacio")).thenReturn("CRE.1065.00.020");

      ResponseEntity<String> response = mapService.obtenerCoordsEspacio(mockRequest);

      String expected = "\"Exito obteniendo el id del espacio\"";
      System.out.println(response.getBody());
      JSONAssert.assertEquals(expected, response.getBody(), false);

  }

    @Test
    public void testBuscarEspacioByCaracteristicas() throws JSONException {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Mockito.when(mockRequest.getHeader("idEspacio")).thenReturn("CRE.1065.00.020");

        ResponseEntity<String> response = mapService.buscarEspacioByCaracteristicas(mockRequest);

        String expected = "\"Espacio encontrado\"";
        System.out.println(response.getBody());
        JSONAssert.assertEquals(expected, response.getBody(), false);
    }

}