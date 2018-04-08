package src.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import src.domain.User;

@Repository
public class UserRepositoryImplementation implements UserRepository{

    // Faltara la conexion con JDBC
    @Autowired
    protected JdbcTemplate jdbc;

    private static final RowMapper<User> rowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new User(rs.getString("username").trim(), rs.getString("pass").trim(), rs.getString("email").trim(),
                rs.getString("type").trim(), rs.getDate("created"));
        }
    };

    public UserRepositoryImplementation (JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    // Resto de metodos
    public User findByName(String name) {
      try {
        String SQL = "SELECT * FROM public.tb_users WHERE username = ?";
        User user = jdbc.queryForObject(SQL, new Object[]{name}, rowMapper);
        return user;
      }
      catch (EmptyResultDataAccessException e) {
        return null; //Hay que capturar la excepción para devolver null en el caso de que no exista el user
      }
    }

    public User findByEmail(String email) {
        try {
            String SQL = "SELECT * FROM public.tb_users WHERE email = ?";
            User user = jdbc.queryForObject(SQL, new Object[]{email}, rowMapper);
            return user;
        }
        catch (EmptyResultDataAccessException e) {
            return null; //Hay que capturar la excepción para devolver null en el caso de que no exista el user
        }
    }

    public Boolean addUser(User user) {
        String SQL;

        if (findByName(user.getName()) != null) {
            return false;
        } else {
            //Si no encuentra un user lanza una excepción, añadimos
            SQL = "INSERT INTO public.tb_users(\n"
                    + "\tcreated, email, pass, username, type)\n"
                    + "\tVALUES (?, ?, ?, ?, ?);";
            if (jdbc.update(SQL,user.getCreated(),user.getEmail(),
                    user.getPass(),user.getName(),user.getType()) == 0) {
                return false;
            } else return true;
        }
    }

    public Boolean deleteUser(String name) {
        String SQL;
        SQL = "DELETE FROM public.tb_users WHERE username = ?";
        if (jdbc.update(SQL, name) == 0) {
            return false;
        } else return true;
    }
}
