package src.repository;

import src.domain.User;


public interface UserRepository {
    User findByName(String name);

    Boolean addUser(User user);

}
