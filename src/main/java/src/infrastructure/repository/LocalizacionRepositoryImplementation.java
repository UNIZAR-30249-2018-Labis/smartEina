package src.infrastructure.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import src.domain.Localizacion;
import src.domain.LocalizacionRepository;

@Repository
public class LocalizacionRepositoryImplementation implements LocalizacionRepository {

  @Autowired
  protected JdbcTemplate jdbc;

  private static final RowMapper<Localizacion> rowMapper = new RowMapper<Localizacion>() {
    @Override
    public Localizacion mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new Localizacion(rs.getString("idEspacio").trim(), rs.getFloat("x"), rs.getFloat("y"),
          rs.getInt("planta"));
    }
  };
  @Override
  public Localizacion findLocalizacion(String idEspacio) {
    try {
      String SQL = "SELECT * FROM public.tb_localizacion WHERE id_espacio = ?";
      Localizacion l = jdbc.queryForObject(SQL, new Object[]{idEspacio}, rowMapper);
      return l;
    }
    catch (EmptyResultDataAccessException e) {
      return null; //Hay que capturar la excepción para devolver null en el caso de que no exista el user
    }
  }

  @Override
  public boolean addLocalizacion(Localizacion l) {
    String SQL;

    if (findLocalizacion(l.getIdEspacio()) != null) {
      return false;
    } else {
      //Si no encuentra un user lanza una excepción, añadimos
      SQL = "INSERT INTO public.tb_localizacion(\n"
          + "\tx, y, planta, id_espacio)\n"
          + "\tVALUES (?, ?, ?, ?);";
      if (jdbc.update(SQL,l.getX(),l.getY(),
          l.getPlanta(),l.getIdEspacio()) == 0) {
        return false;
      } else return true;
    }
  }

  @Override
  public int getIDofLocalizacion(Localizacion l) {
    String buscar = "SELECT id from tb_localizacion where id_espacio = ? and x = ?  and y = ? and planta = ?";
    int idLocalizacion;
    try {
      idLocalizacion = jdbc.queryForObject(buscar, new Object[]{l.getIdEspacio(),
          l.getX(), l.getY(), l.getPlanta()}, Integer.class);
    }
    catch (EmptyResultDataAccessException e){
      idLocalizacion = -1;
    }

    return  idLocalizacion;
  }

  @Override
  public boolean deleteLocalizacionByID(Localizacion l ) {
    String SQL;
    SQL = "DELETE FROM public.tb_localizacion WHERE id_espacio = ? and x = ?  and y = ? and planta = ?";
    if (jdbc.update(SQL, l.getIdEspacio(),
        l.getX(), l.getY(), l.getPlanta()) == 0) {
      return false;
    } else return true;
  }
}
