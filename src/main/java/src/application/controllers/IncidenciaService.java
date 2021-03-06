package src.application.controllers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpsParameters;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import src.application.domain.Email;
import src.application.domain.EmailRepository;
import src.application.domain.User;
import src.application.domain.UserRepository;
import src.domain.*;
import src.infrastructure.repository.MantenimientoRepositoryImplementation;

import java.util.ArrayList;

@RestController
public class IncidenciaService {

    @Autowired
    protected IncidenciaRepository incidenciaRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected EmailRepository emailRepository;

    @Autowired
    protected MantenimientoRepository mantenimientoRepository;

    @RequestMapping(value = "/obtenerIncidenciaByID", method = RequestMethod.GET)
    public ResponseEntity<String> getIncidencia(HttpServletRequest request) {
        String idIncidencia = request.getHeader("idIncidencia");

        Incidencia a = incidenciaRepository.findIncidenciaByID(idIncidencia);
        System.out.println(a.toString());
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();
        String json = gson.toJson(a);
        headers.add("Incidencias", json);
        return new ResponseEntity<String>("\"Exito obteniendo incidencias\"", headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/obtenerIncidenciasDeUsuario", method = RequestMethod.GET)
    public ResponseEntity<String> getIncidenciasUsuario(HttpServletRequest request) {
        String idUser = request.getHeader("idUser");

        ArrayList<Incidencia> a = incidenciaRepository.findAllIncidenciasByUser(idUser);
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();
        String json = gson.toJson(a);
        headers.add("Incidencias", json);
        return new ResponseEntity<String>("\"Exito obteniendo incidencias\"", headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/obtenerIncidenciasDeTrabajador", method = RequestMethod.GET)
    public ResponseEntity<String> getIncidenciasTrabajador(HttpServletRequest request) {
        String idUser = request.getHeader("idTrabajador");

        ArrayList<Incidencia> a = incidenciaRepository.findAllIncidenciasByTrabajador(idUser);
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();
        String json = gson.toJson(a);
        headers.add("Incidencias", json);
        return new ResponseEntity<String>("\"Exito obteniendo incidencias\"", headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/obtenerTodasIncidencias", method = RequestMethod.GET)
    public ResponseEntity<String> getAllIncidencias(HttpServletRequest request) {

        ArrayList<Incidencia> a = incidenciaRepository.findAllIncidencias();
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();

        String json = gson.toJson(a);
        headers.add("Incidencias", json);
        return new ResponseEntity<String>("\"Exito obteniendo incidencias\"", headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/obtenerIncidenciasDeEspacio", method = RequestMethod.GET)
    public ResponseEntity<String> getIncidenciasEspacio(HttpServletRequest request) {
        String idEspacio = request.getHeader("idEspacio");

        ArrayList<Incidencia> a = incidenciaRepository.findAllIncidenciasByEspacio(idEspacio);
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();

        String json = gson.toJson(a);
        headers.add("Incidencias", json);
        return new ResponseEntity<String>("\"Exito obteniendo incidencias\"", headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/obtenerIncidenciasDeEspacioAceptadas", method = RequestMethod.GET)
    public ResponseEntity<String> getIncidenciasEspacioAceptadas(HttpServletRequest request) {
        String idEspacio = request.getHeader("idEspacio");

        ArrayList<Incidencia> a = incidenciaRepository.findAllIncidenciasByEspacioAceptadas(idEspacio);
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();

        String json = gson.toJson(a);
        headers.add("Incidencias", json);
        return new ResponseEntity<String>("\"Exito obteniendo incidencias\"", headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/obtenerIncidenciasActivas", method = RequestMethod.GET)
    public ResponseEntity<String> getIncidenciasActivasYAsignadas(HttpServletRequest request) {
        ArrayList<Incidencia> a = incidenciaRepository.findAllIncidenciasActivas();
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();

        String json = gson.toJson(a);
        headers.add("Incidencias", json);
        return new ResponseEntity<String>("\"Exito obteniendo incidencias\"", headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/obtenerIncidenciasAceptadas", method = RequestMethod.GET)
    public ResponseEntity<String> getIncidenciasActivas(HttpServletRequest request) {
        ArrayList<Incidencia> a = incidenciaRepository.findAllIncidenciasAceptadas();
        Gson gson = new Gson();
        HttpHeaders headers = new HttpHeaders();

        String json = gson.toJson(a);
        headers.add("Incidencias", json);
        return new ResponseEntity<String>("\"Exito obteniendo incidencias\"", headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/obtenerIncidenciasCreadas", method = RequestMethod.GET)
    public ResponseEntity<String> getIncidenciasCreadas(HttpServletRequest request) {
        ArrayList<Incidencia> a = incidenciaRepository.findAllIncidenciasCreadas();
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
                                                    @RequestParam("idTrabajador") String idTrabajador,
                                                    @RequestParam("dia") String dia,
                                                    @RequestParam("hora") String hora) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);

        if (incidencia.getEstado().equals("ASIGNADA")) {
            return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia, porque ya estaba asignada\"", HttpStatus.BAD_REQUEST);
        }
       if (incidenciaRepository.aceptadaToAsignada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "ASIGNADA", incidencia.getIdUsuario(), idTrabajador, incidencia.getLocalizacion()))) {
           CeldaMantenimiento celda = new CeldaMantenimiento(incidencia.getLocalizacion().getIdEspacio(), idTrabajador, idIncidencia, dia, Integer.parseInt(hora));
           if (mantenimientoRepository.addCeldaMantenimiento(celda)) {
               return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado y se ha creado la celda\"", HttpStatus.OK);
           } else return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
       } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/desAsignarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> desAsignarIncidencia(@RequestParam("idIncidencia") String idIncidencia,
                                                        @RequestParam("idTrabajador") String idTrabajador) {

        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);
        CeldaMantenimiento celda = mantenimientoRepository.findCeldaMantenimientoByIDs(idTrabajador, idIncidencia);

        if (incidenciaRepository.asignadaToAceptada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "ACEPTADA", incidencia.getIdUsuario(), "", incidencia.getLocalizacion()), idTrabajador)) {
            if (mantenimientoRepository.deleteCeldaMantenimiento(celda)) {
                return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado y la celda borrada\"", HttpStatus.OK);
            } else return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
        } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/finalizarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> finalizarIncidencia(@RequestParam("idIncidencia") String idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);
        CeldaMantenimiento celda = mantenimientoRepository.findCeldaMantenimientoByIDs(incidencia.getIdTrabajador(), idIncidencia);

        if (incidenciaRepository.asignadaToFinalizada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "COMPLETADA", incidencia.getIdUsuario(), incidencia.getIdTrabajador(), incidencia.getLocalizacion()))) {
            if (mantenimientoRepository.deleteCeldaMantenimiento(celda)) {
                User user = userRepository.findByName(incidencia.getIdUsuario());
                Email email = new Email("admin.smartEina@gmail.com", user.getEmail(), "Incidencia \"" + incidencia.getId() + "\" completada!", "Su incidencia ha sido completada.");
                if (emailRepository.sendEmail(email)) {
                    return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
                } else return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
            } else return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
        } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/aceptarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> aceptarIncidencia(@RequestParam("idIncidencia") String idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);
        if (incidenciaRepository.pendienteToAceptada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "ACEPTADA", incidencia.getIdUsuario(), incidencia.getIdTrabajador(), incidencia.getLocalizacion()))) {
            User user = userRepository.findByName(incidencia.getIdUsuario());
            Email email = new Email("admin.smartEina@gmail.com", user.getEmail(), "Incidencia \"" + incidencia.getId() + "\" aceptada!", "Su incidencia ha sido aceptada.");
            if (emailRepository.sendEmail(email)) {
                return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
            } else return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
        } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/rechazarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> rechazarIncidencia(@RequestParam("idIncidencia") String idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);
        if (incidenciaRepository.pendienteToRechazada(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "RECHAZADA", incidencia.getIdUsuario(), incidencia.getIdTrabajador(), incidencia.getLocalizacion()))) {
            User user = userRepository.findByName(incidencia.getIdUsuario());
            Email email = new Email("admin.smartEina@gmail.com", user.getEmail(), "Incidencia \"" + incidencia.getId() + "\" rechazada!", "Su incidencia ha sido rechazada.");
            if (emailRepository.sendEmail(email)) {
                return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
            } else return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
        } return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/incompletarIncidencia", method = RequestMethod.POST)
    public ResponseEntity<String> incompletarIncidencia(@RequestParam("idIncidencia") String idIncidencia) {
        Incidencia incidencia = incidenciaRepository.findIncidenciaByID(idIncidencia);
        if (incidenciaRepository.pendienteToIncompleta(new Incidencia(incidencia.getId(),incidencia.getTitulo(), incidencia.getDesc(), "INCOMPLETA", incidencia.getIdUsuario(), incidencia.getIdTrabajador(), incidencia.getLocalizacion()))) {
            User user = userRepository.findByName(incidencia.getIdUsuario());
            Email email = new Email("admin.smartEina@gmail.com", user.getEmail(), "Incidencia \"" + incidencia.getId() + "\" pendiente de modificación!", "Es necesario que especifique mejor su incidencia.");
            if (emailRepository.sendEmail(email)) {
                return new ResponseEntity<String>("\"El estado de la incidencia ha sido actualizado\"", HttpStatus.OK);
            } else return new ResponseEntity<String>("\"No se ha podido actualizar el estado de la incidencia\"", HttpStatus.BAD_REQUEST);
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
