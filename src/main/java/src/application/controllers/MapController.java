package src.application.controllers;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jdk.nashorn.internal.parser.JSONParser;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import src.domain.Espacio;
import src.domain.EspacioRepository;

@RestController
public class MapController {

  @Autowired
  protected EspacioRepository espacioRepository;

  @RequestMapping(value = "/espacios", method = RequestMethod.GET)
  public ResponseEntity<String> getInfo(HttpServletRequest request) {
    String id = request.getHeader("id");
    Espacio e = espacioRepository.findEspacioByID(id);

    Gson gson = new Gson();
    HttpHeaders headers = new HttpHeaders();
    if (e == null) {
      return new ResponseEntity<String>("\"Error, el espacio no existe\"", HttpStatus.BAD_REQUEST);
    } else {
      String json = gson.toJson(e);
      headers.add("Espacio", json);
      System.out.println(json);
      return new ResponseEntity<String>("\"Exito obteniendo espacio\"", headers, HttpStatus.OK);
    }
  }

}
