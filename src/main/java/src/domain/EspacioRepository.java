package src.domain;

import java.util.ArrayList;

public interface EspacioRepository {
  //nombre,edificio,planta y horarios
  Espacio findEspacioByID(String id);

  Boolean addActividadAlHorario(String idEspacio, String dia, int horaInicio, String actividad);

  Boolean deleteActividadDelHorario(String idEspacio, String dia, int horaInicio);

  ArrayList<Espacio> findAllEspacios();

  Horario findHorario(String idEspacio);

  Boolean addCeldaHorarioAlHorario(String idEspacio, String dia, int horaInicio, String actividad);

  Boolean deleteCeldaHorarioDelHorario(String idEspacio, String dia, int horaInicio);

  Horario horarioDeEspacioDeIncidencia(String idEspacio);

  ArrayList<String> getCoordenadasByID(String idEspacio);
}
