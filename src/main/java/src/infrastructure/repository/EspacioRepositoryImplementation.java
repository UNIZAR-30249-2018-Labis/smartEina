package src.infrastructure.repository;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import src.domain.Espacio;
import src.domain.EspacioRepository;
import src.domain.Horario;
import src.domain.HorarioRepository;


@Repository
public class EspacioRepositoryImplementation implements EspacioRepository {

    @Autowired
    protected JdbcTemplate jdbc;
    @Autowired
    protected HorarioRepository horarioRepository;

    private static final RowMapper<Espacio> EspacioMapper = new RowMapper<Espacio>() {
        @Override
        public Espacio mapRow(ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id_espacio").trim();
            String nombre = rs.getString("id_centro").trim();
            String edificio = rs.getString("id_edificio").trim();
            String uso = rs.getString("tipo_de_uso").trim();
            String utc = rs.getString("id_utc").trim();
            int planta = Integer.parseInt(utc.substring(0, 1));

            return new Espacio(id, nombre, edificio, uso, false, planta, null);
        }
    };

    private static final RowMapper<String> edificioMapper = new RowMapper<String>() {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("edificio");
        }
    };

    private static final RowMapper<String> tipoDeUsoMapper = new RowMapper<String>() {
        @Override
        public String mapRow(ResultSet rs, int rowNum) throws SQLException {
            return rs.getString("tipo_de_uso");
        }
    };

    @Override
    public Boolean addActividadAlHorario(String idEspacio, String dia, int horaInicio, String actividad) {
        return horarioRepository.addActividadAlHorario(idEspacio, dia, horaInicio, actividad);
    }

    @Override
    public Boolean deleteActividadDelHorario(String idEspacio, String dia, int horaInicio) {
        return horarioRepository.deleteActividadDelHorario(idEspacio, dia, horaInicio);
    }

    @Override
    public Espacio findEspacioByID(String id) {
        try {
            String SQL = "SELECT * FROM public.tb_espacios WHERE id_espacio = ?";
            Espacio espacio = jdbc.queryForObject(SQL, new Object[]{id}, EspacioMapper);

            SQL = "SELECT * FROM public.tb_tipo_de_uso WHERE id = ?";
            String tipoDeUso = jdbc.queryForObject(SQL, new Object[]{Integer.parseInt(espacio.getTipoDeUso())}, tipoDeUsoMapper);

            SQL = "SELECT * FROM public.tb_edificios WHERE id_edificio = ?";
            String edificio = jdbc.queryForObject(SQL, new Object[]{espacio.getEdificio()}, edificioMapper);

            // Cogemos el horario asignado a ese espacio
            Horario horario = horarioRepository.findHorario(espacio.getId());

            return new Espacio(espacio.getId(), espacio.getNombre(), edificio, tipoDeUso, false, espacio.getPlanta() ,horario);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("Ha cascado la BD");
            return null;
        }
    }
}
