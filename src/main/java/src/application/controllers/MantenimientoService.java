package src.application.controllers;

import com.google.gson.Gson;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import src.domain.CeldaMantenimiento;
import src.domain.Espacio;
import src.domain.MantenimientoRepository;

import java.util.ArrayList;

@RestController
public class MantenimientoService {

    @Autowired
    protected MantenimientoRepository mantenimientoRepository;

    @RequestMapping(value = "/getIncidenciaMantenimiento", method = RequestMethod.GET)
    public ResponseEntity<String> getIncidenciaMantenimiento(HttpServletRequest request) {
        String idTrabajador = request.getHeader("idTrabajador");
        String idIncidencia = request.getHeader("idIncidencia");

        CeldaMantenimiento celda = mantenimientoRepository.findCeldaMantenimientoByIDs(idTrabajador, idIncidencia);

        if (celda != null) {
            Gson gson = new Gson();
            String json = gson.toJson(celda);
            HttpHeaders headers = new HttpHeaders();

            headers.add("incidenciaMantenimiento", json);
            return new ResponseEntity<String>("\"Exito obteniendo incidencia mantenimiento.\"", headers, HttpStatus.OK);
        } else return new ResponseEntity<String>("\"Error obteniendo incidencia mantenimiento.\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/getAllIncidenciaMantenimientoTrabajador", method = RequestMethod.GET)
    public ResponseEntity<String> getAllIncidenciasMantenimientoTrabajador(HttpServletRequest request) {
        String idTrabajador = request.getHeader("idTrabajador");

        ArrayList<CeldaMantenimiento> celdas = mantenimientoRepository.findAllCeldasMantenimientoByTrabajador(idTrabajador);

        if (celdas != null) {
            Gson gson = new Gson();
            String json = gson.toJson(celdas);
            HttpHeaders headers = new HttpHeaders();

            headers.add("incidenciasMantenimiento", json);
            return new ResponseEntity<String>("\"Exito obteniendo todas las incidencia mantenimiento por trabajador.\"", headers, HttpStatus.OK);
        } else return new ResponseEntity<String>("\"Error obteniendo todas las incidencia mantenimiento por trabajador.\"", HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/getAllIncidenciaMantenimientoEspacio", method = RequestMethod.GET)
    public ResponseEntity<String> getAllIncidenciasMantenimientoEspacio(HttpServletRequest request) {
        String idEspacio = request.getHeader("idEspacio");

        ArrayList<CeldaMantenimiento> celdas = mantenimientoRepository.findAllCeldasMantenimientoByIdEspacio(idEspacio);

        System.out.println("CELDAS " + celdas.toString());
        if (celdas != null) {
            Gson gson = new Gson();
            String json = gson.toJson(celdas);
            HttpHeaders headers = new HttpHeaders();

            headers.add("incidenciasMantenimiento", json);
            return new ResponseEntity<String>("\"Exito obteniendo todas las incidencia mantenimiento por espacio.\"", headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("\"Error obteniendo todas las incidencia mantenimiento por espacio.\"", HttpStatus.BAD_REQUEST);
        }
    }
}
