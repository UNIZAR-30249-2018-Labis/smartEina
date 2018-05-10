package src.infrastructure.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import src.domain.Incidencia;
import src.domain.IncidenciaRepository;
import src.domain.Localizacion;

@Repository
public class IncidenciaRepositoryImplementation implements IncidenciaRepository {

  @Autowired
  protected JdbcTemplate jdbc;

  public IncidenciaRepositoryImplementation (JdbcTemplate jdbcTemplate) {
    this.jdbc = jdbcTemplate;
  }

  private static final RowMapper<Incidencia> incidenciaMapper = new RowMapper<Incidencia>() {
    @Override
    public Incidencia mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new Incidencia(String.valueOf(rs.getInt("idIncidencia")).trim(),
          rs.getString("titulo").trim(), rs.getString("descripcion").trim(),
          rs.getString("estado").trim(),null, null,null);
    }
  };

  private static final RowMapper<Localizacion> localizacionMapper = new RowMapper<Localizacion>() {
    @Override
    public Localizacion mapRow(ResultSet rs, int rowNum) throws SQLException {
      return new Localizacion(rs.getString("idIncidencia"), rs.getString("idEspacio"), rs.getFloat("x"), rs.getFloat("y"), rs.getString("planta"));
    }
  };


  @Override
  public boolean addIncidencia(Incidencia incidencia) {
    final String SQL = "INSERT INTO public.tb_incidencias(\n"
          + "\t titulo, estado, descripcion)\n"
          + "\tVALUES (?, ?, ?);";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbc.update(new PreparedStatementCreator() {

      @Override
      public PreparedStatement createPreparedStatement(Connection connection)
              throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SQL,
                new String[] { "idIncidencia"});
        ps.setString(1, incidencia.getTitulo());
        ps.setString(2, incidencia.getEstado());
        ps.setString(3, incidencia.getDesc());
        return ps;
      }
    }, keyHolder);

    String idIncidencia = keyHolder.getKey().toString();

    String SQL_1 = "INSERT INTO public.tb_incidenciasuser(\n"
                  + "\t idUsuario, idIncidencia)\n"
                  + "\tVALUES(?,?)";
    if (jdbc.update(SQL_1, incidencia.getIdUsuario(), idIncidencia) == 0) {
      return false;
    }

    String SQL_2 = "INSERT INTO public.tb_localizacion(\n"
          + "\t idIncidencia, x, y, planta, idEspacio)\n"
          + "\tVALUES(?,?,?,?,?)";
    if (jdbc.update(SQL_2, idIncidencia, incidencia.getLocalizacion().getX(), incidencia.getLocalizacion().getY(), incidencia.getLocalizacion().getPlanta(), incidencia.getLocalizacion().getIdEspacio()) == 0) {
      return false;
    } else return true;
  }

  @Override
  public Incidencia findIncidenciaByID(String idIncidencia) {
    try {
      String SQL = "SELECT * FROM public.tb_incidencias WHERE idIncidencia = ?";
      Incidencia incidencia = jdbc.queryForObject(SQL, new Object[]{Integer.parseInt(idIncidencia)}, incidenciaMapper);

      SQL = "SELECT * FROM public.tb_incidenciasuser WHERE idIncidencia = ?";
      Map<String, Object> row = jdbc.queryForMap(SQL);
      String idUsuario = (String)row.get("idUsuario");

      SQL = "SELECT * FROM public.tb_incidenciastrabajador WHERE idIncidencia = ?";
      row = jdbc.queryForMap(SQL);
      String idTrabajador = (String)row.get("idTrabajador");

      Localizacion localizacion = findLocalizacionByIDIncidencia(idIncidencia);

      return new Incidencia(incidencia.getId(), incidencia.getTitulo(), incidencia.getDesc(), incidencia.getEstado(),idUsuario, idTrabajador, localizacion);
    }
    catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public ArrayList<Incidencia> findAllIncidenciasByTrabajador(String idTrabajador) {
    ArrayList<Incidencia> incidencias = new ArrayList<>();
    ArrayList<Incidencia> arrayIncidencias = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    try{
      String SQL = "SELECT * FROM public.tb_incidenciastrabajador WHERE idTrabajador = ?";
      List<Map<String, Object>> rows = jdbc.queryForList(SQL, new Object[] {Integer.parseInt(idTrabajador)});

      for (Map<String, Object> row: rows) {
        ids.add((String) row.get("idIncidencia"));
      }

      for (String s : ids) {
        Incidencia incidencia = findIncidenciaByID(s);
        incidencias.add(incidencia);
      }
      return arrayIncidencias;
    }
    catch (EmptyResultDataAccessException e){
    return null;
    }
  }

  @Override
  public ArrayList<Incidencia> findAllIncidenciasByUser(String idUsuario) {
    ArrayList<Incidencia> incidencias = new ArrayList<>();
    ArrayList<Incidencia> arrayIncidencias = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    try{
      String SQL = "SELECT * FROM public.tb_incidenciasusuario WHERE idUsuario = ?";
      List<Map<String, Object>> rows = jdbc.queryForList(SQL, new Object[] {Integer.parseInt(idUsuario)});

      for (Map<String, Object> row: rows) {
        ids.add((String) row.get("idIncidencia"));
      }

      for (String s : ids) {
        Incidencia incidencia = findIncidenciaByID(s);
        incidencias.add(incidencia);
      }

      return arrayIncidencias;
    }
    catch (EmptyResultDataAccessException e){
      return null;
    }
  }

  @Override
  public boolean updateIncidenciaByID(Incidencia incidencia) {
    String SQL =  "UPDATE public.tb_incidencias SET titulo = ? , desc = ? WHERE idIncidencia = ?";
    if (jdbc.update(SQL, incidencia.getTitulo(), incidencia.getDesc(), incidencia.getId()) == 0) {
      return false;
    } else return true;
  }

  @Override
  public boolean deleteIncidenciaByID(String idIncidencia) {
    String SQL = "DELETE FROM public.tb_incidencias WHERE idIncidencia = ?";

    if (jdbc.update(SQL, Integer.parseInt(idIncidencia)) == 0) {
      return false;
    }

    deleteLocalizacionByIDIncidencia(idIncidencia);

    SQL = "DELETE FROM public.tb_incidenciasuser WHERE idIncidencia = ?";
    if (jdbc.update(SQL, Integer.parseInt(idIncidencia)) == 0) {
      return false;
    }

    SQL = "DELETE FROM public.tb_incidenciasTrabajador WHERE idIncidencia = ?";
    if (jdbc.update(SQL, Integer.parseInt(idIncidencia)) == 0) {
      return false;
    } else return true;
  }

  @Override
  public boolean aceptadaToAsignada(Incidencia incidencia) {
    String SQL =  "UPDATE public.tb_incidencias SET estado = ?, idTrabajador = ? WHERE idIncidencia = ?";
    if (jdbc.update(SQL, incidencia.getEstado(), incidencia.getIdTrabajador(),Integer.parseInt(incidencia.getId())) == 0) {
      return false;
    }

    SQL = "INSERT INTO public.tb_incidenciasTrabajador(\n"
            + "\tidTrabajador, idIncidencia)\n"
            + "\tVALUES(?, ?)";
    if (jdbc.update(SQL, incidencia.getIdTrabajador(), Integer.parseInt(incidencia.getId())) == 0) {
      return false;
    } else return true;
  }

  @Override
  public boolean asignadaToFinalizada(Incidencia incidencia) {
    String SQL =  "UPDATE public.tb_incidencias SET estado = ? WHERE idIncidencia = ?";
    if (jdbc.update(SQL, incidencia.getEstado(), Integer.parseInt(incidencia.getId())) == 0) {
      return false;
    } else return true;
  }

  @Override
  public boolean asignadaToAceptada(Incidencia incidencia, String idTrabajador) {
    String SQL =  "UPDATE public.tb_incidencias SET estado = ? WHERE idIncidencia = ?";
    if (jdbc.update(SQL, incidencia.getEstado(), Integer.parseInt(incidencia.getId())) == 0) {
      return false;
    }

    SQL = "DELETE FROM public.tb_incidenciasTrabajador WHERE idIncidencia = ? AND idTrabajador = ?";
    if (jdbc.update(SQL, Integer.parseInt(incidencia.getId()), idTrabajador) == 0) {
      return false;
    } else return true;
  }

  @Override
  public boolean pendienteToAceptada(Incidencia incidencia) {
    String SQL =  "UPDATE public.tb_incidencias SET estado = ? WHERE idIncidencia = ?";
    if (jdbc.update(SQL, incidencia.getEstado(), Integer.parseInt(incidencia.getId())) == 0) {
      return false;
    } else return true;
  }

  @Override
  public boolean pendienteToRechazada(Incidencia incidencia) {
    String SQL =  "UPDATE public.tb_incidencias SET estado = ? WHERE idIncidencia = ?";
    if (jdbc.update(SQL, incidencia.getEstado(), Integer.parseInt(incidencia.getId())) == 0) {
      return false;
    } else return true;
  }

  @Override
  public boolean pendienteToIncompleta(Incidencia incidencia) {
    String SQL =  "UPDATE public.tb_incidencias SET estado = ? WHERE idIncidencia = ?";
    if (jdbc.update(SQL, incidencia.getEstado(), Integer.parseInt(incidencia.getId())) == 0) {
      return false;
    } else return true;
  }

  @Override
  public boolean incompletaToPendiente(Incidencia incidencia) {
    String SQL =  "UPDATE public.tb_incidencias SET estado = ? WHERE idIncidencia = ?";
    if (jdbc.update(SQL, incidencia.getEstado(), Integer.parseInt(incidencia.getId())) == 0) {
      return false;
    } else return true;
  }

  @Override
  public Localizacion findLocalizacionByIDIncidencia(String idIncidencia) {
    try {
      String SQL = "SELECT * FROM public.tb_localizacion WHERE idIncidencia = ?";
      Localizacion l = jdbc.queryForObject(SQL, new Object[]{Integer.parseInt(idIncidencia)}, localizacionMapper);
      return l;
    }
    catch (EmptyResultDataAccessException e) {
      return null;
    }
  }
  @Override
  public boolean addLocalizacion(String idIncidencia, float x, float y, String planta, String idEspacio) {
    String SQL;
    if (findLocalizacionByIDIncidencia(idIncidencia) != null) {
      return false;
    } else {
      SQL = "INSERT INTO public.tb_localizacion(\n"
              + "\tidIncidencia, x, y, planta, idEspacio)\n"
              + "\tVALUES (?, ?, ?, ?, ?);";
      if (jdbc.update(SQL, idIncidencia, x, y, planta, idEspacio) == 0) {
        return false;
      } else return true;
    }
  }

  @Override
  public boolean deleteLocalizacionByIDIncidencia(String idIncidencia) {
    String SQL;
    if (findLocalizacionByIDIncidencia(idIncidencia) != null) {
      return false;
    } else {
      SQL = "DELETE FROM public.tb_localizacion WHERE idIncidencia = ?";
      if (jdbc.update(SQL, idIncidencia) == 0) {
        return false;
      } else return true;
    }
  }


  // TODO: SIMPLEMENTE REVISAR LAS TABLAS DE incidenciasUsusario e incidenciasTrabajador
  @Override
  // Metodo para testear
  public String addIncidenciaTest(Incidencia incidencia) {
    final String SQL = "INSERT INTO public.tb_incidencias(\n"
            + "\t titulo, estado, descripcion)\n"
            + "\tVALUES (?, ?, ?);";

    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbc.update(new PreparedStatementCreator() {

      @Override
      public PreparedStatement createPreparedStatement(Connection connection)
              throws SQLException {
        PreparedStatement ps = connection.prepareStatement(SQL,
                new String[] { "idIncidencia"});
        ps.setString(1, incidencia.getTitulo());
        ps.setString(2, "PENDIENTE");
        ps.setString(3, incidencia.getDesc());
        return ps;
      }
    }, keyHolder);

    String idIncidencia = keyHolder.getKey().toString();

    String SQL_1 = "INSERT INTO public.tb_incidenciasuser(\n"
            + "\t idUsuario, idIncidencia)\n"
            + "\tVALUES(?,?)";
    if (jdbc.update(SQL_1, incidencia.getIdUsuario(), idIncidencia) == 0) {
      return "-1";
    }

    String SQL_2 = "INSERT INTO public.tb_localizacion(\n"
            + "\t idIncidencia, x, y, planta, idEspacio)\n"
            + "\tVALUES(?,?,?,?,?)";
    if (jdbc.update(SQL_2, idIncidencia, incidencia.getLocalizacion().getX(), incidencia.getLocalizacion().getY(), incidencia.getLocalizacion().getPlanta(), incidencia.getLocalizacion().getIdEspacio()) == 0) {
      return "-2";
    } else return keyHolder.getKey().toString();
  }

}
