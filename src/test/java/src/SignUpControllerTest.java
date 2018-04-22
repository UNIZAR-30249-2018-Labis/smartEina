package src;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import src.domain.User;
import src.domain.UserRepository;

import java.sql.Date;

@RunWith(SpringRunner.class)
@SpringBootTest

public class SignUpControllerTest {

    @Autowired
    protected UserRepository userRepository;

    /*
     * Test que comprueba el correcto funcionamiento del sistema al intentar añadir un usuario que no existia previamente
     */
    @Test
    public void testAddNonExistentUser() {
        User u = new User("testingUser2018","pass", "testingUser2018@gmail.com", "basico", new Date(System.currentTimeMillis()));
        boolean r1 = userRepository.addUser(u);
        assert(r1 == true);
        userRepository.deleteUser("testingUser2018");   // Borramos el usuario de prueba
    }

    /*
     * Test que comprueba el correcto funcionamiento del sistema al intentar añadir un usuario que ya existia previamente
     */
    @Test
    public void testAddExistentUser() {
        User u = new User("testingUser2018","pass", "testingUser2018@gmail.com", "basico", new Date(System.currentTimeMillis()));
        boolean r1 = userRepository.addUser(u);
        assert(r1 == true);
        boolean r2 = userRepository.addUser(u);
        assert(r2 == false);
        userRepository.deleteUser("testingUser2018");   // Borramos el usuario de prueba
    }
}
