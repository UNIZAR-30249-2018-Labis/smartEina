package src;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.domain.EspacioEntity;
import src.repository.EspacioRepository;

@RestController
public class MapController {

  @Autowired
  protected EspacioRepository espacioRepository;

  @RequestMapping(value = "/getInfo", method = RequestMethod.GET)
  public ResponseEntity<String> getInfo(HttpServletRequest request) {
    float x = Float.parseFloat(request.getHeader("x"));
    float y = Float.parseFloat(request.getHeader("y"));
    String id = request.getHeader("id");

    //Comprobar que las coordenadas pertenece a dentro del mapa, ¿cómo?
    EspacioEntity e = espacioRepository.getInfoByID(id);
    System.out.println(e == null);
    HttpHeaders headers = new HttpHeaders();
    ObjectMapper mapper = new ObjectMapper();
    if (e == null) {
      return new ResponseEntity<>("Error", headers, HttpStatus.BAD_REQUEST);
    } else {
      try {
        String jsonInString = mapper.writeValueAsString(e);
        System.out.println(jsonInString);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
      return new ResponseEntity<>("", headers, HttpStatus.OK);
    }
  }

}
