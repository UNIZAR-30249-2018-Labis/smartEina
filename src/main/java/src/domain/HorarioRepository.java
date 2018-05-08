package src.domain;

public interface HorarioRepository {

    Horario findHorario(String idEspacio);

    Boolean addActividadAlHorario(String idEspacio, String dia, int horaInicio, String actividad);

    Boolean deleteActividadDelHorario(String idEspacio, String dia, int horaInicio);

    Horario horarioDeEspacioDeIncidencia(String idEspacio);

}
