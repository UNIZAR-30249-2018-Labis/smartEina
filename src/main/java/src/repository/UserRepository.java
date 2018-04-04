package src.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import src.domain.User;

public class UserRepository {

    // Faltara la conexion con JDBC
    protected JdbcTemplate jdbc;

    @Autowired
    public UserRepository (JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    // Resto de metodos
    public User findByName(String name) {

        // Hacemos la query

        return null;
    }

}
