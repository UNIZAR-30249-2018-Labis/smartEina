package src.infrastructure.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import src.domain.CeldaHorario;
import src.domain.Horario;
import src.domain.HorarioRepository;
import src.domain.IncidenciaRepository;
import src.domain.Localizacion;

@Repository
public class HorarioRepositoryImplementation implements HorarioRepository {

    @Autowired
    protected JdbcTemplate jdbc;

    private static final RowMapper<CeldaHorario> rowMapper = new RowMapper<CeldaHorario>() {
        @Override
        public CeldaHorario mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CeldaHorario(rs.getString("idespacio").trim(), rs.getString("dia").trim(), rs.getInt("hora"),
                    rs.getString("actividad").trim());
        }
    };

    public HorarioRepositoryImplementation(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    @Override
    public Horario findHorario(String idEspacio) {
        ArrayList<CeldaHorario> horasLunes = findHorasDelDia(idEspacio, "Lunes");
        ArrayList<CeldaHorario> horasMartes = findHorasDelDia(idEspacio, "Martes");
        ArrayList<CeldaHorario> horasMiercoles = findHorasDelDia(idEspacio, "Miercoles");
        ArrayList<CeldaHorario> horasJueves = findHorasDelDia(idEspacio, "Jueves");
        ArrayList<CeldaHorario> horasViernes = findHorasDelDia(idEspacio, "Viernes");

        return new Horario (idEspacio, horasLunes, horasMartes, horasMiercoles, horasJueves, horasViernes);
    }

    @Override
    public Boolean addActividadAlHorario(String idEspacio, String dia, int horaInicio, String actividad) {
        // Obtenemos el horario
        Horario horario = findHorario(idEspacio);

        // Comprobamos si hay una actividad registrada a la misma hora y añadimos la actividad si se puede
        if (isHoraLibre(horario, dia, horaInicio)) {
            String SQL = "INSERT INTO public.tb_horarios(\n"
                    + "\t idespacio, dia, hora, actividad)\n"
                    + "\tVALUES (?, ?, ?, ?);";
            if (jdbc.update(SQL,idEspacio,dia,
                    horaInicio,actividad) == 0) {
                return false;
            } else return true;
        } else return false;
    }

    @Override
    public Boolean deleteActividadDelHorario(String idEspacio, String dia, int horaInicio) {
        Horario horario = findHorario(idEspacio);

        if (isHoraLibre(horario, dia, horaInicio)) {
            return false;
        } else {
            String SQL = "DELETE FROM public.tb_horarios WHERE idespacio = ? AND dia = ? AND hora = ?;";
            if (jdbc.update(SQL, idEspacio, dia, horaInicio) == 0) {
                return false;
            } else return true;
        }
    }

    @Override
    public Horario horarioDeEspacioDeIncidencia(String idEspacio) {
        return findHorario(idEspacio);
    }

    private boolean isHoraLibre(Horario horario, String dia, int hora) {
        // Obtenemos las horas del dia
        ArrayList<CeldaHorario> horas = null;
        switch (dia) {
            case "Lunes":
                horas = horario.getHorasLunes();
                break;
            case "Martes":
                horas = horario.getHorasMartes();
                break;
            case "Miercoles":
                horas = horario.getHorasMiercoles();
                break;
            case "Jueves":
                horas = horario.getHorasJueves();
                break;
            case "Viernes":
                horas = horario.getHorasViernes();
                break;
            default: return false;
        }

        for (CeldaHorario cH: horas) {
            if (cH.getHoraDeInicio() == hora) {
                return false;
            }
        }
        return true;
    }

    private ArrayList<CeldaHorario> findHorasDelDia(String idEspacio, String dia) {
        String SQL = "SELECT * FROM public.tb_horarios WHERE idespacio = ? AND dia = ?";
        List<CeldaHorario> listaHorario = jdbc.query(SQL,new Object[]{idEspacio, dia}, rowMapper);
        return (ArrayList<CeldaHorario>) listaHorario;
    }
}