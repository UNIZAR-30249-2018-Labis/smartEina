package src.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import src.domain.User;

@Repository
public class UserRepositoryImplementation implements UserRepository{

    // Faltara la conexion con JDBC
    @Autowired
    protected JdbcTemplate jdbc;

    public UserRepositoryImplementation (JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    // Resto de metodos
    public User findByName(String name) {

        // Hacemos la query

        return null;
    }

    public Boolean addUser(User user) {

        // Hacemos la query

        return true;
    }

}
