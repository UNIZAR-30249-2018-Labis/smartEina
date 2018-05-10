package src.application.controllers;

import com.google.gson.Gson;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import src.domain.Incidencia;
import src.domain.IncidenciaRepository;
import src.domain.Localizacion;

import java.util.ArrayList;

@RestController
public class IncidenceService {

    @Autowired
    protected IncidenciaRepository incidenciaRepository;

    @RequestMapping(value = "/obtenerIncidenciasDeUsuario", method = RequestMethod.GET)
    public ResponseEntity<String> getInfo(HttpServletRequest request) {
        String idUser = request.getHeader("idUser");

        ArrayList<Incidencia> a = incidenciaRepository.findAllIncidenciasByUser(idUser);
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();

        String json = gson.toJson(a);
        headers.add("Incidencias", json);
        return new ResponseEntity<String>("\"Exito obteniendo incidencias\"", headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/crearIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> crearIncidencia(@RequestParam("titulo") String titulo,
                                                  @RequestParam("descripcion") String desc,
                                                  @RequestParam("idUsuario") String idUsuario,
                                                  @RequestParam("x") float x,
                                                  @RequestParam("y") float y,
                                                  @RequestParam("planta") String planta,
                                                  @RequestParam("idEspacio") String idEspacio) {
        Localizacion localizacion = new Localizacion(null, idEspacio, x, y,planta);
        Incidencia incidencia = new Incidencia(null, titulo, desc, "PENDIENTE",idUsuario, "", localizacion);
        if (incidenciaRepository.addIncidencia(incidencia)) {
            return new ResponseEntity<String>("\"Se ha añadido la incidencia\"", HttpStatus.OK);
        } else return new ResponseEntity<String>("\"No se ha podido añadir la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/updateIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> updateIncidencia(@RequestParam("titulo") String titulo,
                                                   @RequestParam("descripcion") String desc,
                                                   @RequestParam("idIncidencia") String idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);

        if (incidenciaRepository.updateIncidenciaByID(new Incidencia(incidencia.getId(), titulo, desc, incidencia.getEstado(), incidencia.getIdUsuario(), incidencia.getIdTrabajador(), incidencia.getLocalizacion()))) {
            return new ResponseEntity<String>("\"Se ha modificado la incidencia correctamente\"", HttpStatus.OK);
        } else return new ResponseEntity<String>("\"No se ha modificado la incidencia correctamente\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/asignarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> asignarIncidencia(@RequestParam("idIncidencia") String idIncidencia,
                                                    @RequestParam("idTrabajador") String idTrabajador) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);

       if (incidenciaRepository.aceptadaToAsignada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "ASIGNADA", incidencia.getIdUsuario(), idTrabajador, incidencia.getLocalizacion()))) {
           return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
       } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/desAsignarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> desAsignarIncidencia(@RequestParam("idIncidencia") String idIncidencia,
                                                        @RequestParam("idTrabajador") String idTrabajador) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);

        if (incidenciaRepository.asignadaToAceptada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "ACEPTADA", incidencia.getIdUsuario(), "", incidencia.getLocalizacion()), idTrabajador)) {
            return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
        } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/finalizarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> terminarIncidencia(@RequestParam("idIncidencia") String idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);

        if (incidenciaRepository.asignadaToFinalizada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "FINALIZADA", incidencia.getIdUsuario(), incidencia.getIdTrabajador(), incidencia.getLocalizacion()))) {
            return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
        } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/aceptarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> aceptarIncidencia(@RequestParam("idIncidencia") String idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);
        if (incidenciaRepository.pendienteToAceptada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "ACEPTADA", incidencia.getIdUsuario(), incidencia.getIdTrabajador(), incidencia.getLocalizacion()))) {
            return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
        } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/rechazarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> rechazarIncidencia(@RequestParam("idIncidencia") String idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);
        if (incidenciaRepository.pendienteToRechazada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "RECHAZADA", incidencia.getIdUsuario(), incidencia.getIdTrabajador(), incidencia.getLocalizacion()))) {
            return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
        } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/incompletarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> incompletarIncidencia(@RequestParam("idIncidencia") String idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);
        if (incidenciaRepository.pendienteToIncompleta(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "INCOMPLETA", incidencia.getIdUsuario(), incidencia.getIdTrabajador(), incidencia.getLocalizacion()))) {
            return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
        } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/pendientarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> pendientarIncidencia(@RequestParam("idIncidencia") String idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);
        if (incidenciaRepository.incompletaToPendiente(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "PENDIENTE", incidencia.getIdUsuario(), incidencia.getIdTrabajador(), incidencia.getLocalizacion()))) {
            return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
        } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

}
