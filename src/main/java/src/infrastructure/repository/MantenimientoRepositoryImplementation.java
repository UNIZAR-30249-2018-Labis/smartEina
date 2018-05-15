package src.infrastructure.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import src.domain.CeldaMantenimiento;
import src.domain.Espacio;
import src.domain.MantenimientoRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class MantenimientoRepositoryImplementation implements MantenimientoRepository{

    @Autowired
    protected JdbcTemplate jdbc;


    private static final RowMapper<CeldaMantenimiento> celdaMantenimientoMapper = new RowMapper<CeldaMantenimiento>() {
        @Override
        public CeldaMantenimiento mapRow(ResultSet rs, int rowNum) throws SQLException {
            String idTrabajador = rs.getString("idTrabajador").trim();
            String idIncidencia = Integer.toString(rs.getInt("idIncidencia"));
            String dia = rs.getString("dia").trim();
            Integer hora = rs.getInt("hora");

            return new CeldaMantenimiento("", idTrabajador, idIncidencia, dia, hora);
        }
    };

    @Override
    public CeldaMantenimiento findCeldaMantenimientoByIDs(String idTrabajador, String idIncidencia) {
        String SQL = "SELECT * FROM public.tb_incidenciastrabajador WHERE idTrabajador = ? AND idIncidencia = ?";
        try {
            CeldaMantenimiento celda = jdbc.queryForObject(SQL, new Object[]{idTrabajador, Integer.parseInt(idIncidencia)}, celdaMantenimientoMapper);

            SQL = "SELECT * FROM public.tb_localizacion WHERE idIncidencia = ?";
            Map<String, Object> row = jdbc.queryForMap(SQL, Integer.parseInt(idIncidencia));
            String idEspacio =(String) row.get("idEspacio");

            return new CeldaMantenimiento(idEspacio, celda.getIdTrabajador(), celda.getIdIncidencia(), celda.getDia(), celda.getHora());
        } catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public ArrayList<CeldaMantenimiento> findAllCeldasMantenimientoByTrabajador(String idTrabajador) {
        ArrayList<CeldaMantenimiento> celdas = new ArrayList<>();
        String SQL = "SELECT * FROM public.tb_incidenciastrabajador WHERE idTrabajador = ?";
        try {
            List<CeldaMantenimiento> listaCeldas = jdbc.query(SQL, new Object[] {idTrabajador}, celdaMantenimientoMapper);

            SQL = "SELECT * FROM public.tb_localizacion WHERE idIncidencia = ?";
            for (CeldaMantenimiento c: listaCeldas) {
                Map<String, Object> row = jdbc.queryForMap(SQL, Integer.parseInt(c.getIdIncidencia()));
                String idEspacio =(String) row.get("idEspacio");
                celdas.add(new CeldaMantenimiento(idEspacio, c.getIdTrabajador(),c.getIdIncidencia(),c.getDia(),c.getHora()));
            }
            return celdas;
        }catch(EmptyResultDataAccessException e) {
            return celdas;
        }
    }

    @Override
    public ArrayList<CeldaMantenimiento> findAllCeldasMantenimientoByIdEspacio(String idEspacio) {
        ArrayList<String> ids = new ArrayList<>();
        ArrayList<CeldaMantenimiento> celdas = new ArrayList<>();
        String SQL = "SELECT * FROM public.tb_localizacion WHERE idEspacio = ?";
        try {
            List<Map<String, Object>> rows = jdbc.queryForList(SQL, new Object[]{idEspacio});

            for (Map row: rows) {
                ids.add(Integer.toString((Integer) row.get("idIncidencia")));
            }

            SQL = "SELECT * FROM public.tb_incidenciastrabajador WHERE idIncidencia = ?";

            for (String id: ids) {
               CeldaMantenimiento celda = jdbc.queryForObject(SQL, new Object[] {Integer.parseInt(id)}, celdaMantenimientoMapper);
               celdas.add(new CeldaMantenimiento(idEspacio, celda.getIdTrabajador(), celda.getIdIncidencia(), celda.getDia(), celda.getHora()));
           }
           return celdas;
        } catch(EmptyResultDataAccessException e) {
            return celdas;
        }
    }

    @Override
    public boolean addCeldaMantenimiento(CeldaMantenimiento celda) {
        String SQL = "INSERT INTO public.tb_incidenciastrabajador(\n"
                + "\t idTrabajador, idIncidencia, dia, hora)\n"
                + "\t VALUES(?, ? , ?, ?);";
        if (jdbc.update(SQL, celda.getIdTrabajador(), Integer.parseInt(celda.getIdIncidencia()), celda.getDia(), celda.getHora()) == 0) {
            return false;
        } else return true;
    }

    @Override
    public boolean deleteCeldaMantenimiento(CeldaMantenimiento celda) {
        String SQL = "DELETE FROM public.tb_incidenciastrabajador WHERE idIncidencia = ? AND idTrabajador = ?";
        if (jdbc.update(SQL, Integer.parseInt(celda.getIdIncidencia()), celda.getIdTrabajador()) == 0) {
            return false;
        } else return true;
    }

}
