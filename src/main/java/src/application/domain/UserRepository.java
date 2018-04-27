package src.application.domain;

public interface UserRepository {

    User findByName(String name);

    User findByEmail(String email);

    Boolean addUser(User user);

    Boolean deleteUser(String name);

}
