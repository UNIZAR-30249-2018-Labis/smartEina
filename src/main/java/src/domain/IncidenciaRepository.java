package src.domain;

import java.util.ArrayList;

public interface IncidenciaRepository {

  Incidencia findIncidenciaByID(String id);

  ArrayList<Incidencia> findAllIncidenciasByTrabajador(String idTrabajador);

  ArrayList<Incidencia> findAllIncidenciasByUser(String username);

  boolean addIncidencia(Incidencia incidencia);

  boolean updateIncidenciaByID(Incidencia incidencia);

  boolean deleteIncidenciaByID(String idIncidencia);

  boolean pendienteToIncompleta(Incidencia incidencia);

  boolean incompletaToPendiente(Incidencia incidencia);

  boolean pendienteToAceptada(Incidencia incidencia);

  boolean aceptadaToAsignada(Incidencia incidencia);

  boolean asignadaToAceptada(Incidencia incidencia, String idTrabajador);

  boolean asignadaToFinalizada(Incidencia incidencia);

  boolean pendienteToRechazada(Incidencia incidencia);

  Localizacion findLocalizacionByIDIncidencia(String idIncidencia);

  boolean addLocalizacion(String idIncidencia, float x, float y, String planta, String idEspacio);

  boolean deleteLocalizacionByIDIncidencia(String idIncidencia);

  ArrayList<Incidencia> findIncidenciasBySala(String idEspacio);

  ArrayList<Incidencia> findIncidenciasAceptadas();


  String addIncidenciaTest(Incidencia incidencia);
}
