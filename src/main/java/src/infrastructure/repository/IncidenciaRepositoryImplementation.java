package src.infrastructure.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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

  private static final RowMapper<Localizacion> LocalizacionMapper = new RowMapper<Localizacion>() {
    @Override
    public Localizacion mapRow(ResultSet rs, int rowNum) throws SQLException {
      Localizacion l = new Localizacion(rs.getString("id_espacio").trim(),
          rs.getFloat("x"),rs.getFloat("y"),rs.getInt("planta"));
      return l;
    }
  };

  @Override
  public int addIncidencia(String titulo,String desc,String estado,String idTrabajador,Localizacion localizacion) {
    String SQL,buscar;
    KeyHolder keyHolder = new GeneratedKeyHolder();
      //Si no encuentra un user lanza una excepción, añadimos

      SQL = "INSERT INTO public.tb_incidencias(\n"
          + "\ttitulo, estado, descripcion, idLocalizacion)\n"
          + "\tVALUES (?, ?, ?, ?);";
         int idLocalizacion = LocalizacionRepository.getIDofLocalizacion(localizacion);
        if(idLocalizacion == -1) {
          LocalizacionRepository.addLocalizacion(localizacion);
          idLocalizacion = LocalizacionRepository.getIDofLocalizacion(localizacion);
        }
       final int idLoca = idLocalizacion;

    if(jdbc.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(SQL, new String[]{"idincidencia"});
      ps.setString(1, titulo);
      ps.setString(2, estado);
      ps.setString(3, desc);
      ps.setInt(4, idLoca);
      return ps;
    }, keyHolder) == 0){
      return -1;
    }
    else {
      //Como se ha añadido la incidencia, hay que añadir el id del trabajador
      String insertIncidencias = "INSERT INTO public.tb_trabajadoreincidencias(\n"
          + "\tidTrabajador,idIncidencia)\n"
          + "\tVALUES (?, ?);";
      jdbc.update(insertIncidencias,Integer.parseInt(idTrabajador),(int)keyHolder.getKey());
      return (int)keyHolder.getKey();
    }

  }

  @Override
  public boolean deleteIncidenciaByID(String id) {
    String deleteIncidencia,deleteIncidenciaYTrabajador;
    deleteIncidencia = "DELETE FROM public.tb_incidencias WHERE idIncidencia = ?";
    deleteIncidenciaYTrabajador = "DELETE FROM public.tb_trabajadorEIncidencias "
        + "WHERE idIncidencia = ?";
    if (jdbc.update(deleteIncidenciaYTrabajador, Integer.parseInt(id)) == 0 &&
        jdbc.update(deleteIncidencia, Integer.parseInt(id)) == 0) {
      return false;
    } else {
      return true;
    }
  }

  @Override
  public Incidencia findIncidenciaByID(String id) {
    try {
      String SQL = "SELECT p.idIncidencia,p.titulo,p.estado,p.descripcion,l.x,l.y,l.planta,l.id_espacio "
          + "FROM public.tb_incidencias p,public.tb_localizacion l "
          + "WHERE p.idIncidencia = ? AND l.id = p.idLocalizacion ";
      Incidencia incidencia = jdbc.queryForObject(SQL, new Object[]{Integer.parseInt(id)}, incidenciaMapper);
      return incidencia;
    }
    catch (EmptyResultDataAccessException e) {
      return null; //Hay que capturar la excepción para devolver null en el caso de que no exista la incidencia
    }
  }

  @Override
  public ArrayList<Incidencia> findIncidenciaOfTrabajador(String idTrabajador) {
    try{
      String SQL = "SELECT p.idIncidencia,p.titulo,p.estado,p.descripcion,p.idLocalizacion,l.x,l.y,l.planta,l.id_espacio "
          + "FROM public.tb_incidencias p,public.tb_trabajador t,public.tb_trabajadorEIncidencias tei,public.tb_localizacion l  "
          + "WHERE t.id = ? AND t.id = tei.idTrabajador AND p.idIncidencia = tei.idIncidencia"
          + " AND l.id = p.idLocalizacion";
      List<Incidencia> incidencias = jdbc.query(
          SQL,
          new Object[] {Integer.parseInt(idTrabajador)},
          incidenciaMapper);
      return new ArrayList<>(incidencias);
    }
    catch (EmptyResultDataAccessException e){
    return null;
    }
  }

  @Override
  public ArrayList<Incidencia> findIncidenciaCreadaByUser(String username) {
    try{
      String SQL = "SELECT p.idIncidencia,p.titulo,p.estado,p.descripcion,p.idLocalizacion,l.x,l.y,l.planta,l.id_espacio "
          + "FROM public.tb_incidencias p,public.tb_users u,tb_incidenciasCreadasXUsuario icxu,public.tb_localizacion l  "
          + "WHERE u.username = ? AND u.username = icxu.username AND p.idIncidencia = icxu.idIncidencia"
          + " AND l.id = p.idLocalizacion";
      List<Incidencia> incidencias = jdbc.query(
          SQL,
          new Object[] {username},
          incidenciaMapper);
      return new ArrayList<>(incidencias);
    }
    catch (EmptyResultDataAccessException e){
      return null;
    }
  }

  @Override
  public boolean updateIncidenciaByID(String id,String titulo,String desc,String estado,String idTrabajador,Localizacion localizacion) {
    try {
      int idLocalizacion = LocalizacionRepository.getIDofLocalizacion(localizacion);
      String SQL = "UPDATE public.tb_incidencias "
          + "SET titulo = ?, estado = ?,descripcion = ? ,idLocalizacion = ?"
          + "WHERE idIncidencia = ? ";
       if(jdbc.update(SQL, new Object[]{titulo,
          estado,desc,idLocalizacion,Integer.parseInt(id)}) == 0) {
         return false;
       }
       else{
         return true;
       }
    }
    catch (Exception e) {
      return false; //Hay que capturar la excepción para devolver null en el caso de que no exista la incidencia
    }
  }

  @Override
  public boolean pendienteToIncompleta(String id) {
    try {
      String SQL = "UPDATE public.tb_incidencias "
          +"SET estado = ?"
          + "WHERE idIncidencia = ? ";
      jdbc.update(SQL, new Object[]{"INCOMPLETA",Integer.parseInt(id)});
      return true;
    }
    catch (Exception e) {
      return false; //Hay que capturar la excepción para devolver null en el caso de que no exista la incidencia
    }
  }

  @Override
  public boolean incompletaToPendiente(String id) {
    try {
      String SQL = "UPDATE public.tb_incidencias "
          +"SET estado = ?"
          + "WHERE idIncidencia = ? ";
      jdbc.update(SQL, new Object[]{"PENDIENTE",Integer.parseInt(id)});
      return true;
    }
    catch (Exception e) {
      return false; //Hay que capturar la excepción para devolver null en el caso de que no exista la incidencia
    }
  }

  @Override
  public boolean pendienteToAceptada(String id) {
    try {
      String SQL = "UPDATE public.tb_incidencias "
          +"SET estado = ?"
          + "WHERE idIncidencia = ? ";
      jdbc.update(SQL, new Object[]{"ACEPTADA",Integer.parseInt(id)});
      return true;
    }
    catch (Exception e) {
      return false; //Hay que capturar la excepción para devolver null en el caso de que no exista la incidencia
    }
  }

  @Override
  public boolean aceptadaToAsignada(String id, String idTrabajador) {
    try {
      String SQL = "UPDATE public.tb_incidencias "
          +"SET estado = ?"
          + "WHERE idIncidencia = ? ";
      jdbc.update(SQL, new Object[]{"ASIGNADA",Integer.parseInt(id)});
      SQL = "UPDATE public.tb_trabajadorEIncidencias "
          +"SET idTrabajador = ?"
          + "WHERE idIncidencia = ? ";
      jdbc.update(SQL, new Object[]{Integer.parseInt(idTrabajador),Integer.parseInt(id)});
      return true;
    }
    catch (Exception e) {
      return false; //Hay que capturar la excepción para devolver null en el caso de que no exista la incidencia
    }
  }

  @Override
  public boolean asignadaToCompletada(String id) {
    try {
      String SQL = "UPDATE public.tb_incidencias "
          +"SET estado = ?"
          + "WHERE idIncidencia = ? ";
      jdbc.update(SQL, new Object[]{"COMPLETADA",Integer.parseInt(id)});
      return true;
    }
    catch (Exception e) {
      return false; //Hay que capturar la excepción para devolver null en el caso de que no exista la incidencia
    }
  }

  @Override
  public boolean pendienteToRechazada(String id) {
    try {
      String SQL = "UPDATE public.tb_incidencias "
          +"SET estado = ?"
          + "WHERE idIncidencia = ? ";
      jdbc.update(SQL, new Object[]{"RECHAZADA",Integer.parseInt(id)});
      return true;
    }
    catch (Exception e) {
      return false; //Hay que capturar la excepción para devolver null en el caso de que no exista la incidencia
    }
  }

  @Override
  public Localizacion findLocalizacionOfIncidencia(String id) {
    try {
      String SQL =
          "SELECT l.x,l.y,l.planta,l.id_espacio "
              + "FROM public.tb_incidencias p,public.tb_localizacion l "
              + "WHERE p.idIncidencia = ? AND l.id = p.idLocalizacion ";
      Localizacion l = jdbc.queryForObject(
          SQL,
          new Object[] {Integer.parseInt(id)},
          LocalizacionMapper);
      return l;
    }
    catch(Exception e){
      return null;
    }
  }
}
