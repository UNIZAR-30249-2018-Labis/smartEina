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
import src.domain.*;

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
                new String[] { "idincidencia"});
        ps.setString(1, incidencia.getTitulo());
        ps.setString(2, incidencia.getEstado());
        ps.setString(3, incidencia.getDesc());
        return ps;
      }
    }, keyHolder);

    String idIncidencia = keyHolder.getKey().toString();

    String SQL_1 = "INSERT INTO public.tb_incidenciasuser(\n"
                  + "\t iduser, idIncidencia)\n"
                  + "\tVALUES(?,?)";
    if (jdbc.update(SQL_1, incidencia.getIdUsuario(), Integer.parseInt(idIncidencia)) == 0) {
      return false;
    }

    String SQL_2 = "INSERT INTO public.tb_localizacion(\n"
          + "\t idIncidencia, x, y, planta, idEspacio)\n"
          + "\tVALUES(?,?,?,?,?)";
    if (jdbc.update(SQL_2, Integer.parseInt(idIncidencia), incidencia.getLocalizacion().getX(), incidencia.getLocalizacion().getY(), incidencia.getLocalizacion().getPlanta(), incidencia.getLocalizacion().getIdEspacio()) == 0) {
      return false;
    } else return true;
  }

  @Override
  public Incidencia findIncidenciaByID(String idIncidencia) {
    String SQL;
    Incidencia incidencia;
    String idUsuario,idTrabajador;
    Map<String, Object> row;
    try {
      SQL = "SELECT * FROM public.tb_incidencias WHERE idIncidencia = ?";
      incidencia = jdbc.queryForObject(SQL, new Object[]{Integer.parseInt(idIncidencia)}, incidenciaMapper);
    }
    catch (EmptyResultDataAccessException e) {
      return null;
    }

    SQL = "SELECT * FROM public.tb_incidenciasuser WHERE idIncidencia = ?";
    row = jdbc.queryForMap(SQL, Integer.parseInt(idIncidencia));
    idUsuario = (String) row.get("iduser");
    //Hay que hacer un try-catch  porque puede que una incidencia NO esté asociada
    //a ningún trabajador
    try {
      SQL = "SELECT * FROM public.tb_incidenciastrabajador WHERE idincidencia = ?";
      row = jdbc.queryForMap(SQL, Integer.parseInt(idIncidencia));
      idTrabajador = (String) row.get("idtrabajador");
    }
    catch (EmptyResultDataAccessException e) {
      idTrabajador = "";
    }
    Localizacion localizacion = findLocalizacionByIDIncidencia(idIncidencia);

    return new Incidencia(incidencia.getId(), incidencia.getTitulo(), incidencia.getDesc(), incidencia.getEstado(),idUsuario, idTrabajador, localizacion);
  }

  @Override
  public ArrayList<Incidencia> findAllIncidenciasByTrabajador(String idTrabajador) {
    ArrayList<Incidencia> incidencias = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    try{
      String SQL = "SELECT * FROM public.tb_incidenciastrabajador WHERE idTrabajador = ?";
      List<Map<String, Object>> rows = jdbc.queryForList(SQL, new Object[] {idTrabajador});

      for (Map<String, Object> row: rows) {
        Integer idEntero = (Integer)row.get("idIncidencia");
        ids.add(String.valueOf(idEntero));
      }

      for (String s : ids) {
        Incidencia incidencia = findIncidenciaByID(s);
        incidencias.add(incidencia);
      }
      return incidencias;
    }
    catch (EmptyResultDataAccessException e){
    return null;
    }
  }

  @Override
  public ArrayList<Incidencia> findAllIncidenciasByUser(String idUsuario) {
    System.out.println("USUARIO INCIDENCIA: " + idUsuario);
    ArrayList<Incidencia> incidencias = new ArrayList<>();
    ArrayList<String> ids = new ArrayList<>();
    try{
      String SQL = "SELECT * FROM public.tb_incidenciasuser WHERE iduser = ?";
      List<Map<String, Object>> rows = jdbc.queryForList(SQL, new Object[] {idUsuario});

      for (Map<String, Object> row: rows) {
        ids.add(String.valueOf(row.get("idIncidencia")));
      }
      for (String s : ids) {
        Incidencia incidencia = findIncidenciaByID(s);
        incidencias.add(incidencia);
      }
      return incidencias;
    }
    catch (EmptyResultDataAccessException e){
      return null;
    }
  }

  @Override
  public ArrayList<Incidencia> findAllIncidenciasActivas() {
    ArrayList<Incidencia> incidenciaPreTrabajador = new ArrayList<>();
    ArrayList<Incidencia> incidencias = new ArrayList<>();
    try {
      String SQL = "SELECT * FROM public.tb_incidencias WHERE estado = 'ACEPTADA' OR estado = 'ASIGNADA'";
      List<Incidencia> list = jdbc.query(SQL, new Object[] {}, incidenciaMapper);

      SQL = "SELECT * FROM public.tb_incidenciasuser WHERE idIncidencia = ?";

      for (Incidencia i: list) {
        Map<String, Object> row = jdbc.queryForMap(SQL, Integer.parseInt(i.getId()));
        Localizacion localizacion = findLocalizacionByIDIncidencia(i.getId());
        incidenciaPreTrabajador.add(new Incidencia(i.getId(),i.getTitulo(), i.getDesc(), i.getEstado(),(String) row.get("idUser"),null,localizacion));
      }
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
    // Buscamos trabajadores
    for (Incidencia i: incidenciaPreTrabajador) {
      if (i.getEstado().equals("ASIGNADA")) {
        String SQL = "SELECT * FROM public.tb_incidenciastrabajador WHERE idIncidencia = ?";
        try {
          Map<String, Object> row = jdbc.queryForMap(SQL, Integer.parseInt(i.getId()));
          String idTrabajador = (String) row.get("IdTrabajador");
          incidencias.add(new Incidencia(i.getId(), i.getTitulo(), i.getDesc(), i.getEstado(), i.getIdUsuario(), idTrabajador, i.getLocalizacion()));
        } catch( EmptyResultDataAccessException e) {
          incidencias.add(new Incidencia(i.getId(), i.getTitulo(), i.getDesc(), i.getEstado(), i.getIdUsuario(), "", i.getLocalizacion()));
        }
      } else {
        incidencias.add(new Incidencia(i.getId(), i.getTitulo(), i.getDesc(), i.getEstado(), i.getIdUsuario(), "", i.getLocalizacion()));
      }
    }
    return incidencias;
  }

  @Override
  public ArrayList<Incidencia> findAllIncidenciasAceptadas() {
    ArrayList<Incidencia> incidenciaPreTrabajador = new ArrayList<>();
    ArrayList<Incidencia> incidencias = new ArrayList<>();
    try {
      String SQL = "SELECT * FROM public.tb_incidencias WHERE estado = 'ACEPTADA'";
      List<Incidencia> list = jdbc.query(SQL, new Object[] {}, incidenciaMapper);

      SQL = "SELECT * FROM public.tb_incidenciasuser WHERE idIncidencia = ?";

      for (Incidencia i: list) {
        Map<String, Object> row = jdbc.queryForMap(SQL, Integer.parseInt(i.getId()));
        Localizacion localizacion = findLocalizacionByIDIncidencia(i.getId());
        incidenciaPreTrabajador.add(new Incidencia(i.getId(),i.getTitulo(), i.getDesc(), i.getEstado(),(String) row.get("idUser"),null,localizacion));
      }
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
    // Buscamos trabajadores
    for (Incidencia i: incidenciaPreTrabajador) {
      incidencias.add(new Incidencia(i.getId(), i.getTitulo(), i.getDesc(), i.getEstado(), i.getIdUsuario(), "", i.getLocalizacion()));
    }
    return incidencias;
  }

  @Override
  public ArrayList<Incidencia> findAllIncidenciasCreadas() {

    ArrayList<Incidencia> incidencias = new ArrayList<>();
    try {
      String SQL = "SELECT * FROM public.tb_incidencias WHERE estado = 'PENDIENTE'";
      List<Incidencia> list = jdbc.query(SQL, new Object[] {}, incidenciaMapper);

      SQL = "SELECT * FROM public.tb_incidenciasuser WHERE idIncidencia = ?";

      for (Incidencia i: list) {
        Map<String, Object> row = jdbc.queryForMap(SQL, Integer.parseInt(i.getId()));
        Localizacion localizacion = findLocalizacionByIDIncidencia(i.getId());
        incidencias.add(new Incidencia(i.getId(),i.getTitulo(), i.getDesc(), i.getEstado(),(String) row.get("idUser"),"",localizacion));
      }
    } catch (EmptyResultDataAccessException e) {
      return null;
    }

    return incidencias;
  }

  @Override
  public ArrayList<Incidencia> findAllIncidencias() {
    ArrayList<Incidencia> incidencias = new ArrayList<>();
    ArrayList<String> ids = new ArrayList();
    try{
      String SQL = "SELECT * FROM public.tb_incidenciasuser";
      List<Map<String, Object>> rows = jdbc.queryForList(SQL, new Object[] {});

      for (Map<String, Object> row: rows) {
        ids.add(String.valueOf(row.get("idIncidencia")));
      }

      for (String s : ids) {
        Incidencia incidencia = findIncidenciaByID(s);
        incidencias.add(incidencia);
      }

      return incidencias;
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public ArrayList<Incidencia> findAllIncidenciasByEspacio(String idEspacio) {
    ArrayList<Incidencia> incidencias = new ArrayList<>();
    String SQL = "SELECT * FROM public.tb_localizacion WHERE idEspacio = ?";
    try {
      List<Map<String, Object>> list = jdbc.queryForList(SQL, idEspacio);

      System.out.println("Lista ids " + list.toString());

      for(Map row: list) {
        Incidencia incidencia = findIncidenciaByID(((Integer) row.get("idIncidencia")).toString());
        if (incidencia.getEstado().equals("ACEPTADA") || incidencia.getEstado().equals("ASIGNADA")) {
          incidencias.add(incidencia);
        }
      }

    } catch (EmptyResultDataAccessException e) {
      return null;
    }
    return incidencias;
  }

  @Override
  public ArrayList<Incidencia> findAllIncidenciasByEspacioAceptadas(String idEspacio) {
    ArrayList<Incidencia> incidencias = new ArrayList<>();
    String SQL = "SELECT * FROM public.tb_localizacion WHERE idEspacio = ?";
    try {
      List<Map<String, Object>> list = jdbc.queryForList(SQL, idEspacio);

      for(Map row: list) {
        Incidencia incidencia = findIncidenciaByID(((Integer) row.get("idIncidencia")).toString());
        if (incidencia.getEstado().equals("ACEPTADA")) {
          incidencias.add(incidencia);
        }
      }

    } catch (EmptyResultDataAccessException e) {
      return null;
    }
    return incidencias;
  }

  @Override
  public boolean updateIncidenciaByID(Incidencia incidencia) {
    String SQL =  "UPDATE public.tb_incidencias SET titulo = ? , descripcion = ? WHERE idIncidencia = ?";
    if (jdbc.update(SQL, incidencia.getTitulo(), incidencia.getDesc(), Integer.parseInt(incidencia.getId())) == 0) {
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

    //Borramos de la tabla de users e incidencias
    SQL = "DELETE FROM public.tb_incidenciasuser WHERE idIncidencia = ?";

    jdbc.update(SQL, Integer.parseInt(idIncidencia));

    //Borramos de la tabla de incidencias asociadas a trabajador
    SQL = "DELETE FROM public.tb_incidenciasTrabajador WHERE idIncidencia = ?";

    jdbc.update(SQL, Integer.parseInt(idIncidencia));

    //Borramos de la tabla de localizaciones
    SQL = "DELETE FROM public.tb_localizacion WHERE idIncidencia = ?";

    jdbc.update(SQL, Integer.parseInt(idIncidencia));
    return true;
  }

  @Override
  public boolean aceptadaToAsignada(Incidencia incidencia) {
    String SQL =  "UPDATE public.tb_incidencias SET estado = ? WHERE idIncidencia = ?";
    if (jdbc.update(SQL, incidencia.getEstado(),Integer.parseInt(incidencia.getId())) == 0) {
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
            new String[] { "idincidencia"});
        ps.setString(1, incidencia.getTitulo());
        ps.setString(2, incidencia.getEstado());
        ps.setString(3, incidencia.getDesc());
        return ps;
      }
    }, keyHolder);

    String idIncidencia = keyHolder.getKey().toString();

    String SQL_1 = "INSERT INTO public.tb_incidenciasuser(\n"
            + "\t iduser, idIncidencia)\n"
            + "\tVALUES(?,?)";
    if (jdbc.update(SQL_1, incidencia.getIdUsuario(), Integer.parseInt(idIncidencia)) == 0) {
      return "-1";
    }

    String SQL_2 = "INSERT INTO public.tb_localizacion(\n"
            + "\t idIncidencia, x, y, planta, idEspacio)\n"
            + "\tVALUES(?,?,?,?,?)";
    if (jdbc.update(SQL_2, Integer.parseInt(idIncidencia), incidencia.getLocalizacion().getX(), incidencia.getLocalizacion().getY(), incidencia.getLocalizacion().getPlanta(), incidencia.getLocalizacion().getIdEspacio()) == 0) {
      return "-2";
    } else return keyHolder.getKey().toString();
  }

}
