package src.infrastructure.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import src.domain.Incidencia;
import src.domain.IncidenciaRepository;
import src.domain.Localizacion;
import src.domain.LocalizacionRepository;

@Repository
public class IncidenciaRepositoryImplementation implements IncidenciaRepository {

  @Autowired
  protected JdbcTemplate jdbc;

  @Autowired
  protected LocalizacionRepository LocalizacionRepository;


  public IncidenciaRepositoryImplementation (JdbcTemplate jdbcTemplate) {
    this.jdbc = jdbcTemplate;
  }

  private static final RowMapper<Incidencia> incidenciaMapper = new RowMapper<Incidencia>() {
    @Override
    public Incidencia mapRow(ResultSet rs, int rowNum) throws SQLException {
      Localizacion l = new Localizacion(rs.getString("id_espacio").trim(),
          rs.getFloat("x"),rs.getFloat("y"),rs.getInt("planta"));
      return new Incidencia(rs.getString("idIncidencia").trim(),
          rs.getString("titulo").trim(), rs.getString("descripcion").trim(),
          rs.getString("estado").trim(),l);
    }
  };


  @Override
  public boolean addIncidencia(Incidencia i) {
    String SQL,buscar;

    if (findIncidenciaByID(i.getId()) != null) {
      return false;
    } else {
      //Si no encuentra un user lanza una excepción, añadimos

      SQL = "INSERT INTO public.tb_incidencias(\n"
          + "\ttitulo, estado, descripcion, idLocalizacion)\n"
          + "\tVALUES (?, ?, ?, ?);";
        int idLocalizacion = LocalizacionRepository.getIDofLocalizacion(i.getLocalizacion());
        if(idLocalizacion == -1) {
          LocalizacionRepository.addLocalizacion(i.getLocalizacion());
          idLocalizacion = LocalizacionRepository.getIDofLocalizacion(i.getLocalizacion());
        }


      if (jdbc.update(SQL,i.getTitulo(),
          i.getEstado(),i.getDesc(),idLocalizacion) == 0) {
        return false;
      } else {return true;}
    }
  }


  @Override
  public boolean deleteIncidenciaByID(String id) {
    String SQL;
    SQL = "DELETE FROM public.tb_incidencias WHERE idIncidencia = ?";
    if (jdbc.update(SQL, Integer.parseInt(id)) == 0) {
      return false;
    } else return true;
  }

  @Override
  public Incidencia findIncidenciaByID(String id) {
    try {
      String SQL = "SELECT p.idIncidencia,p.titulo,p.estado,p.descripcion,l.x,l.y,l.planta,l.id_espacio FROM public.tb_incidencias p,public.tb_localizacion l WHERE p.idIncidencia = ? AND l.id = p.idLocalizacion ";
      Incidencia incidencia = jdbc.queryForObject(SQL, new Object[]{Integer.parseInt(id)}, incidenciaMapper);
      return incidencia;
    }
    catch (EmptyResultDataAccessException e) {
      return null; //Hay que capturar la excepción para devolver null en el caso de que no exista la incidencia
    }
  }


  @Override
  public boolean updateIncidenciaByID(Incidencia i) {
    try {
      int idLocalizacion = LocalizacionRepository.getIDofLocalizacion(i.getLocalizacion());
      String SQL = "UPDATE public.tb_incidencias "
          + "SET titulo = ?, estado = ?,descripcion = ? ,idLocalizacion = ?"
          + "WHERE idIncidencia = ? ";
       jdbc.update(SQL, new Object[]{i.getTitulo(),
          i.getEstado(),i.getDesc(),idLocalizacion,Integer.parseInt(i.getId())});
      return true;
    }
    catch (Exception e) {
      return false; //Hay que capturar la excepción para devolver null en el caso de que no exista la incidencia
    }
  }


  @Override
  public boolean pendienteToIncompleta(String id) {
    return false;
  }

  @Override
  public boolean incompletaToPendiente(String id) {
    return false;
  }

  @Override
  public boolean pendienteToAceptada(String id) {
    return false;
  }

  @Override
  public boolean aceptadaToAsignada(String id, String idTrabajador) {
    return false;
  }

  @Override
  public boolean asignadaToCompletada(String id) {
    return false;
  }

  @Override
  public boolean pendienteToRechazada(String id) {
    return false;
  }
}
