package src.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import src.domain.EspacioEntity;
import src.domain.HorarioOV;
import src.domain.User;

@Repository
public class HorarioRepositoryImplementation implements HorarioRepository {
    
    private JdbcTemplate jdbc;

    private static final RowMapper<HorarioOV> rowMapper = new RowMapper<HorarioOV>() {
        @Override
        public HorarioOV mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new HorarioOV(rs.getString("idespacio").trim(), rs.getString("dia").trim(), rs.getInt("hora"),
                    rs.getString("actividad").trim());
        }
    };

    public HorarioRepositoryImplementation(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    @Override
    public Boolean addHorario(HorarioOV horario) {

        if(estaEspacioLibre(horario.getIdEspacio(),horario.getDia(),horario.getHoraInicio())) {
            String SQL = "INSERT INTO public.tb_horarios(idespacio,dia,hora,actividad) VALUES (?,?,?,?)";
            if (jdbc.update(SQL,horario.getIdEspacio(),horario.getDia(),horario.getHoraInicio(),horario.getActividad()) != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Boolean deleteHorario(String idEspacio, String dia, int hora) {
        if(!estaEspacioLibre(idEspacio,dia,hora)) {
            String SQL = "DELETE FROM public.tb_horarios WHERE idespacio = ? AND dia = ? AND hora = ?";
            if (jdbc.update(SQL, idEspacio, dia, hora) != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List <HorarioOV> viewHorario(String idEspacio, String dia) {
        String SQL = "SELECT * FROM public.tb_horarios WHERE idespacio = ? AND dia = ?";
        List <HorarioOV> horarios = jdbc.query(SQL, rowMapper, idEspacio, dia);
        return horarios;
    }

    @Override
    public List<Integer> viewHorarioLibre(String idEspacio, String dia) {
        List <HorarioOV> horariosOcupados = viewHorario(idEspacio, dia);
        List <Integer> horasLibres = new ArrayList<>();
        //Rellena la lista con todas horas libres posibles
        for (int hora = 7; hora < 21; hora++) {
            horasLibres.add(hora);
        }
        for (HorarioOV horarioOcupado : horariosOcupados) {
            horasLibres.remove(new Integer(horarioOcupado.getHoraInicio()));
        }
        return horasLibres;
    }

    @Override
    public Boolean estaEspacioLibre(String idEspacio, String dia, int hora) {
        Integer horariosEnEseMomento = jdbc.queryForObject("SELECT count(*) FROM public.tb_horarios WHERE idespacio = ? AND dia = ? AND hora = ?", Integer.class, idEspacio,dia,hora);
        return horariosEnEseMomento != null && horariosEnEseMomento == 0;
    }
}