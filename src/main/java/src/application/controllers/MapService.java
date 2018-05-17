package src.application.controllers;

import com.google.gson.Gson;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.domain.*;

import java.util.ArrayList;

@RestController
public class MapService {

  @Autowired
  protected EspacioRepository espacioRepository;

  @Autowired
  protected IncidenciaRepository incidenciaRepository;

  @Autowired
  protected MantenimientoRepository mantenimientoRepository;

  @RequestMapping(value = "/espacios", method = RequestMethod.GET)
  public ResponseEntity<String> getInfoDeEspacio(HttpServletRequest request) {
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
      return new ResponseEntity<String>("\"Exito obteniendo espacio\"", headers, HttpStatus.OK);
    }
  }

  @RequestMapping(value = "/guardarHora", method = RequestMethod.POST)
  public ResponseEntity<String> guardarHora(HttpServletRequest request) {
    String idEspacio = request.getParameter("idEspacio");
    String dia = request.getParameter("dia");
    String hora = request.getParameter("hora");
    String actividad = request.getParameter("actividad");

    ArrayList<CeldaMantenimiento> celdas = mantenimientoRepository.findAllCeldasMantenimientoByIdEspacio(idEspacio);

    // Comprobamos si algun trabajador se habia asignado algo ahi
    if (celdas.size() > 0) {
      for (CeldaMantenimiento celda: celdas) {
        if (celda.getHora() == Integer.parseInt(hora)) {
          Incidencia incidencia = incidenciaRepository.findIncidenciaByID(celda.getIdIncidencia());
          if (incidenciaRepository.asignadaToAceptada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "ACEPTADA", incidencia.getIdUsuario(), "", incidencia.getLocalizacion()), celda.getIdTrabajador())) {
            if (mantenimientoRepository.deleteCeldaMantenimiento(celda)) {
              break;
            } else return new ResponseEntity<String>("\"No se ha podido crear la hora\"", HttpStatus.BAD_REQUEST);
          } return new ResponseEntity<String>("\"No se ha podido crear la hora\"", HttpStatus.BAD_REQUEST);
        }
      }
    }
    espacioRepository.deleteActividadDelHorario(idEspacio,dia,Integer.parseInt(hora));
    if (!actividad.equals("")) {
      espacioRepository.addActividadAlHorario(idEspacio, dia, Integer.parseInt(hora), actividad);
    }
    return new ResponseEntity<String>("\"Exito actualizando horario\"", HttpStatus.OK);
  }

  @RequestMapping(value = "/verHorarioDeEspacio", method = RequestMethod.GET)
  public ResponseEntity<String> verHorarioDeEspacio(HttpServletRequest request) {
    String idIncidencia = request.getHeader("idIncidencia");
    Incidencia i = incidenciaRepository.findIncidenciaByID(idIncidencia);
    Horario h = espacioRepository.findHorarioDeEspacioDeIncidencia(i.getLocalizacion().getIdEspacio());
    Gson gson = new Gson();
    String json = gson.toJson(h);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Horario",json);
    return new ResponseEntity<String>("\"Exito obteniendo el horario del espacio\"", headers, HttpStatus.OK);
  }


  @RequestMapping(value = "/obtenerCoordsDeEspacio", method = RequestMethod.GET)
  public ResponseEntity<String> obtenerCoordsEspacio(HttpServletRequest request) {
    String idEspacio = request.getHeader("idEspacio");

    ArrayList<String> result = espacioRepository.getCoordenadasByID(idEspacio);

    Gson gson = new Gson();
    String json = gson.toJson(result);
    HttpHeaders headers = new HttpHeaders();
    headers.add("Coordenadas", json);
    return new ResponseEntity<String>("\"Exito obteniendo el id del espacio\"", headers, HttpStatus.OK);
  }

  @RequestMapping(value = "/obtenerDatosGeograficosEspacio", method = RequestMethod.GET)
  public ResponseEntity<String> obtenerDatosGeograficosEspacio(HttpServletRequest request) {
    String idEspacio = request.getHeader("idEspacio");
    Espacio espacio = espacioRepository.findEspacioByID(idEspacio);

    if (espacio == null) {
      return new ResponseEntity<String>("\"El espacio buscado no existe\"",HttpStatus.BAD_REQUEST);
    } else {
      ArrayList<String> result = espacioRepository.getCoordenadasByID(idEspacio);
      result.add(espacio.getPlanta());
      Gson gson = new Gson();
      String json = gson.toJson(result);
      HttpHeaders headers = new HttpHeaders();
      headers.add("datos", json);
      return new ResponseEntity<String>("\"Espacio encontrado\"", headers, HttpStatus.OK);
    }
  }
}
