package src;



import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import src.repository.UserRepository;
import src.domain.User;

@RestController
public class LogInController {

    @Autowired
    protected UserRepository userRepository;

    @RequestMapping(value = "/logIn", method = RequestMethod.GET)
    public ResponseEntity<String> logIn(HttpServletRequest request) {
        String username = request.getHeader("user");
        String password = request.getHeader("pass");

        if (username == null || password == null) {
            return new ResponseEntity<String>("\"Usuario o contraseña nulo \"", HttpStatus.BAD_REQUEST);
        } else {
            User u = userRepository.findByName(username);
            if (u == null) {
                return new ResponseEntity<String>("\"El usuario no existe \"", HttpStatus.BAD_REQUEST);
            } else {
                if (u.getPass().equals(password)) {
                    // Creamos la sesion o la mierda que sea

                    return null;
                } else {
                    return new ResponseEntity<String>("\"Contraseña incorrecta \"", HttpStatus.BAD_REQUEST);
                }
            }
        }
    }
}
