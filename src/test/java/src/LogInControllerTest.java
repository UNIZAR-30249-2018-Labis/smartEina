package src;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import src.domain.User;
import src.repository.UserRepository;

import java.sql.Date;

@RunWith(SpringRunner.class)
@SpringBootTest

public class LogInControllerTest {

    @Autowired
    protected UserRepository userRepository;

    @Test
    public void testFindExistentUser() {
        User u = new User("testingUser2018","pass", "testingUser2018@gmail.com", "basico", new Date(System.currentTimeMillis()));
        boolean r1 = userRepository.addUser(u);
        assert(r1 == true);
        User u1 = userRepository.findByName(u.getName());
        assert(u1 != null);
        userRepository.deleteUser("testingUser2018");   // Borramos el usuario de prueba
    }

    @Test
    public void testFindNonExistentUser() {
        User u1 = userRepository.findByName("testingUser2018");
        assert(u1 == null);
    }

}
