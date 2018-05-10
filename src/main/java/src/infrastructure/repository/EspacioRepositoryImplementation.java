package src.infrastructure.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import src.domain.*;


@Repository
public class EspacioRepositoryImplementation implements EspacioRepository {

    @Autowired
    protected JdbcTemplate jdbc;

    private static final RowMapper<Espacio> espacioMapper = new RowMapper<Espacio>() {
        @Override
        public Espacio mapRow(ResultSet rs, int rowNum) throws SQLException {
            String id = rs.getString("id_espacio").trim();
            String nombre = rs.getString("id_centro").trim();
            String edificio = rs.getString("id_edificio").trim();
            String uso = rs.getString("tipo_de_uso").trim();
            String utc = rs.getString("id_utc").trim();
            String planta = utc.substring(0, 2);

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

    private static final RowMapper<CeldaHorario> celdaHorarioMapper = new RowMapper<CeldaHorario>() {
        @Override
        public CeldaHorario mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new CeldaHorario(rs.getString("idespacio").trim(), rs.getString("dia").trim(), rs.getInt("hora"),
                    rs.getString("actividad").trim());
        }
    };

    @Override
    public Boolean addActividadAlHorario(String idEspacio, String dia, int horaInicio, String actividad) {
        return addCeldaHorarioAlHorario(idEspacio, dia, horaInicio, actividad);
    }

    @Override
    public Boolean deleteActividadDelHorario(String idEspacio, String dia, int horaInicio) {
        return deleteCeldaHorarioDelHorario(idEspacio, dia, horaInicio);
    }

    @Override
    public Espacio findEspacioByID(String id) {
        try {
            String SQL = "SELECT * FROM public.tb_espacios WHERE id_espacio = ?";
            Espacio espacio = jdbc.queryForObject(SQL, new Object[]{id}, espacioMapper);

            SQL = "SELECT * FROM public.tb_tipo_de_uso WHERE id = ?";
            String tipoDeUso = jdbc.queryForObject(SQL, new Object[]{Integer.parseInt(espacio.getTipoDeUso())}, tipoDeUsoMapper);

            if (id.equals("Exterior")) {
                // Cogemos el horario asignado a ese espacio
                Horario horario = findHorario(espacio.getId());

                return new Espacio(espacio.getId(), espacio.getNombre(), espacio.getEdificio(), tipoDeUso, true, "00", horario);
            } else {
                SQL = "SELECT * FROM public.tb_edificios WHERE id_edificio = ?";
                String edificio = jdbc.queryForObject(SQL, new Object[]{espacio.getEdificio()}, edificioMapper);

                // Cogemos el horario asignado a ese espacio
                Horario horario = findHorario(espacio.getId());

                return new Espacio(espacio.getId(), espacio.getNombre(), edificio, tipoDeUso, false, espacio.getPlanta() ,horario);
            }

        } catch (EmptyResultDataAccessException e) {
            System.out.println("Ha cascado la BD");
            return null;
        }
    }

    @Override
    public ArrayList<Espacio> findAllEspacios() {
        ArrayList<Espacio> arrayEspacios = new ArrayList<>();

        String SQL = "SELECT * FROM public.tb_espacios";
        List<Espacio> listaEspacios = jdbc.query(SQL,new Object[]{}, espacioMapper);

        for (Espacio e: listaEspacios) {
            SQL = "SELECT * FROM public.tb_tipo_de_uso WHERE id = ?";
            String tipoDeUso = jdbc.queryForObject(SQL, new Object[]{Integer.parseInt(e.getTipoDeUso())}, tipoDeUsoMapper);

            SQL = "SELECT * FROM public.tb_edificios WHERE id_edificio = ?";
            String edificio = jdbc.queryForObject(SQL, new Object[]{e.getEdificio()}, edificioMapper);
            arrayEspacios.add(new Espacio(e.getId(), e.getNombre(), edificio, tipoDeUso, false, e.getPlanta(), null));
        }

        return arrayEspacios;
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
    public Boolean addCeldaHorarioAlHorario(String idEspacio, String dia, int horaInicio, String actividad) {
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
    public Boolean deleteCeldaHorarioDelHorario(String idEspacio, String dia, int horaInicio) {
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
        List<CeldaHorario> listaHorario = jdbc.query(SQL,new Object[]{idEspacio, dia}, celdaHorarioMapper);
        return (ArrayList<CeldaHorario>) listaHorario;
    }

}
