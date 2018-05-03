package src.domain;

import java.util.ArrayList;

public interface IncidenciaRepository {

  Incidencia findIncidenciaByID(String id);

  ArrayList<Incidencia> findIncidenciaOfTrabajador(String idTrabajador);

  int  addIncidencia(String titulo,String desc,String estado,String idTrabajador,Localizacion localizacion);

  boolean updateIncidenciaByID(String id,String titulo,String desc,String estado,String idTrabajador,Localizacion localizacion);

  Localizacion findLocalizacionOfIncidencia(String id);

  boolean deleteIncidenciaByID(String id);

  boolean pendienteToIncompleta(String id);

  boolean incompletaToPendiente(String id);

  boolean pendienteToAceptada(String id);

  boolean aceptadaToAsignada(String id,String idTrabajador);

  boolean asignadaToCompletada(String id);

  boolean pendienteToRechazada(String id);



}
