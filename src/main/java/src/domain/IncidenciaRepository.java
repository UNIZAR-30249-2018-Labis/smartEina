package src.domain;

import java.util.ArrayList;

public interface IncidenciaRepository {

  Incidencia findIncidenciaByID(String id);

  ArrayList<Incidencia> findAllIncidenciasByTrabajador(String idTrabajador);

  ArrayList<Incidencia> findAllIncidenciasByUser(String idUser);

  ArrayList<Incidencia> findAllIncidencias();

  ArrayList<Incidencia> findAllIncidenciasActivas();

  ArrayList<Incidencia> findAllIncidenciasAceptadas();

  ArrayList<Incidencia> findAllIncidenciasCreadas();

  ArrayList<Incidencia> findAllIncidenciasByEspacio(String idEspacio);

  ArrayList<Incidencia> findAllIncidenciasByEspacioAceptadas(String idEspacio);

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

  boolean deleteLocalizacionByIDIncidencia(String idIncidencia);

  String addIncidenciaTest(Incidencia incidencia);
}
