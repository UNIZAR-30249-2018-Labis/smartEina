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

@Repository
public class EspacioRepositoryImplementation implements EspacioRepository {

  @Autowired
  protected JdbcTemplate jdbc;

  private static final RowMapper<EspacioEntity> EspacioMapper = new RowMapper<EspacioEntity>() {
    @Override
    public EspacioEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
      String id = rs.getString("id_espacio").trim();
      String nombre = rs.getString("id_centro").trim();
      String edificio = rs.getString("id_edificio").trim();
      String uso = rs.getString("tipo_de_uso").trim();
      String utc = rs.getString("id_utc").trim();
      int planta = Integer.parseInt(utc.substring(0,1));

      return new EspacioEntity(id,nombre,edificio,uso,false,planta,null);
    }
  };

  private static final RowMapper<HorarioOV> HorarioMapper = new RowMapper<HorarioOV>() {
    @Override
    public HorarioOV mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new HorarioOV(rs.getString("idespacio"),rs.getString("dia"),rs.getInt("hora"),rs.getString("actividad"));
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

  public EspacioEntity getInfoByID(String id) {
    String SQL = "SELECT * FROM public.tb_espacios WHERE id_espacio = ?";
    String SQLHorario = "SELECT * FROM public.tb_horarios WHERE idespacio = ?";
    String SQLTipoDeUso = "SELECT * FROM public.tb_tipo_de_uso WHERE id = ?";
    String SQLedificio = "SELECT * FROM public.tb_edificios WHERE id_edificio = ?";
    try {

      EspacioEntity e = jdbc.queryForObject(SQL, new Object[]{id}, EspacioMapper);
      String tdu = jdbc.queryForObject(SQLTipoDeUso, new Object[]{Integer.parseInt(e.getTipoDeUso())}, tipoDeUsoMapper);
      String edificio = jdbc.queryForObject(SQLedificio, new Object[]{e.getEdificio()}, edificioMapper);
      List<HorarioOV> horario = jdbc.query(SQLHorario, new Object[]{id}, HorarioMapper);
      return new EspacioEntity(e.getId(),e.getNombre(),edificio,tdu,false,e.getPlanta(),new ArrayList<HorarioOV>(horario));
    } catch (EmptyResultDataAccessException e) {
      return null; //Hay que capturar la excepción para devolver null en el caso de que no exista el user
    }
  }

  public EspacioEntity getInfoByCoordinates(float x, float y,int planta) {
   //TODO: Saber como sacar un espacio segun las coordenadas
    //Mirar el QGIS con el servidor de mañas
    return null;
  }

}
