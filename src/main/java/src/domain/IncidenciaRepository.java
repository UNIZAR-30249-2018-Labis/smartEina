package src.domain;

public interface IncidenciaRepository {

  Incidencia findIncidenciaByID(String id);

  boolean  addIncidencia(Incidencia i);

  boolean updateIncidenciaByID(Incidencia i);

  boolean deleteIncidenciaByID(String id);

  boolean pendienteToIncompleta(String id);

  boolean incompletaToPendiente(String id);

  boolean pendienteToAceptada(String id);

  boolean aceptadaToAsignada(String id,String idTrabajador);

  boolean asignadaToCompletada(String id);

  boolean pendienteToRechazada(String id);



}
