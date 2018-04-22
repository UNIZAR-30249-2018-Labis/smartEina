package src.domain;

public interface EspacioRepository {
  //nombre,edificio,planta y horarios
  Espacio findEspacioByID(String id);

  Boolean addActividadAlHorario(String idEspacio, String dia, int horaInicio, String actividad);

  Boolean deleteActividadDelHorario(String idEspacio, String dia, int horaInicio);
}
