package src.application.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.domain.Espacio;
import src.domain.EspacioRepository;
import src.domain.Horario;
import src.domain.HorarioRepository;
import src.domain.Incidencia;
import src.domain.IncidenciaRepository;
import src.domain.Localizacion;
import src.domain.LocalizacionRepository;

@RestController
public class MapController {

  @Autowired
  protected EspacioRepository espacioRepository;

  @Autowired
  protected IncidenciaRepository incidenciaRepository;

  @Autowired
  protected HorarioRepository horarioRepository;

  @Autowired
  protected LocalizacionRepository localizacionRepository;


  @RequestMapping(value = "/espacios", method = RequestMethod.GET)
  public ResponseEntity<String> getInfo(HttpServletRequest request) {
    String id = request.getHeader("id");
    Espacio e = espacioRepository.findEspacioByID(id);

    Gson gson = new Gson();
    HttpHeaders headers = new HttpHeaders();
    if (e == null) {
      // SI es null estamos en la calle, ya lo trataremos, devolveremos un objeto especial del tipo espacio
      return new ResponseEntity<String>("\"Error, el espacio no existe\"", HttpStatus.BAD_REQUEST);
    } else {
      String json = gson.toJson(e);
      headers.add("Espacio", json);
      System.out.println(json);
      return new ResponseEntity<String>("\"Exito obteniendo espacio\"", headers, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/guardarHora", method = RequestMethod.POST)
  public ResponseEntity<String> guardarHora(HttpServletRequest request) {
    String idEspacio = request.getParameter("idEspacio");
    String dia = request.getParameter("dia");
    String hora = request.getParameter("hora");
    String actividad = request.getParameter("actividad");
    espacioRepository.deleteActividadDelHorario(idEspacio,dia,Integer.parseInt(hora));
    if (!actividad.equals("")) {
      espacioRepository.addActividadAlHorario(idEspacio, dia, Integer.parseInt(hora), actividad);
    }
    return new ResponseEntity<String>("\"Exito actualizando horario\"", HttpStatus.OK);
  }

  @RequestMapping(value = "/verHorarioDeEspacio", method = RequestMethod.GET)
  public ResponseEntity<String> verHorarioDeEspacio(HttpServletRequest request)
      throws JsonProcessingException {
    String idIncidencia = request.getParameter("idIncidencia");
    Incidencia i = incidenciaRepository.findIncidenciaByID(idIncidencia);
    Horario h = horarioRepository.findHorario(i.getLocalizacion().getIdEspacio());
    ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String json = ow.writeValueAsString(h);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Horario",json);
    System.out.println(json);
    return new ResponseEntity<String>("\"Exito obteniendo el horario del espacio\"", headers, HttpStatus.OK);
  }

  @RequestMapping(value = "/terminarIncidencia", method = RequestMethod.GET)
  public ResponseEntity<String> terminarIncidencia(HttpServletRequest request)
  {
    String idIncidencia = request.getParameter("idIncidencia");
    //Incidencia i = incidenciaRepository.findIncidenciaByID(idIncidencia);
    boolean res = incidenciaRepository.asignadaToCompletada(idIncidencia);
    if(res) return new ResponseEntity<String>("\"Se ha modificado la incidencia correctamente\"", HttpStatus.OK);
    else return new ResponseEntity<String>("\"No se ha modificado la incidencia correctamente\"", HttpStatus.BAD_REQUEST);
  }

  @RequestMapping(value = "/crearIncidencia", method = RequestMethod.GET)
  public ResponseEntity<String> crearIncidencia(HttpServletRequest request)
  {
    String titulo = request.getParameter("titulo");
    String desc = request.getParameter("descripcion");
    String idEspacio = request.getParameter("idEspacio");
    //Incidencia i = incidenciaRepository.findIncidenciaByID(idIncidencia);
    Localizacion l = localizacionRepository.findLocalizacion(idEspacio);
    int index = incidenciaRepository.addIncidencia(titulo,desc,"PENDIENTE","",l);
    if(index != -1) return new ResponseEntity<String>("\"Se ha añadido la incidencia\"", HttpStatus.OK);
    else return new ResponseEntity<String>("\"No se ha podido añadir la incidencia\"", HttpStatus.BAD_REQUEST);
  }

  @RequestMapping(value = "/empezarIncidencia", method = RequestMethod.GET)
  public ResponseEntity<String> empezarIncidencia(HttpServletRequest request)
  {
    String idIncidencia = request.getParameter("idIncidencia");
    String idTrabajador = request.getParameter("idTrabajador");
    boolean res = incidenciaRepository.aceptadaToAsignada(idIncidencia,idTrabajador);
    if(res) return new ResponseEntity<String>("\"Se ha modificado la incidencia correctamente\"", HttpStatus.OK);
    else return new ResponseEntity<String>("\"No se ha modificado la incidencia correctamente\"", HttpStatus.BAD_REQUEST);
  }

  @RequestMapping(value = "/updateIncidencia", method = RequestMethod.GET)
  public ResponseEntity<String> updateIncidencia(HttpServletRequest request)
  {
    String idIncidencia = request.getParameter("idIncidencia");
    String titulo = request.getParameter("titulo");
    String desc = request.getParameter("descripcion");
    String estado = request.getParameter("estado");
    String idEspacio = request.getParameter("idEspacio");
    String idTrabajador = request.getParameter("idTrabajador");
    Localizacion l = localizacionRepository.findLocalizacion(idEspacio);
    boolean res = incidenciaRepository.updateIncidenciaByID(idIncidencia,titulo,desc,estado,idTrabajador,l);
    if(res) return new ResponseEntity<String>("\"Se ha modificado la incidencia correctamente\"", HttpStatus.OK);
    else return new ResponseEntity<String>("\"No se ha modificado la incidencia correctamente\"", HttpStatus.BAD_REQUEST);
  }

}
