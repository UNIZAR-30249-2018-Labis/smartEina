package src;

import java.sql.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import src.domain.User;
import src.repository.UserRepository;

@RestController
public class SignUpController {

    @Autowired
    protected UserRepository userRepository;

    @RequestMapping(value = "/signUp", method = RequestMethod.POST)
    public ResponseEntity<String> signUp(@RequestParam("user") String username,
                                         @RequestParam("email") String email,
                                         @RequestParam("pass") String password,
                                         @RequestParam("repass") String rePassword,
                                         HttpServletRequest request) {
        if (username == null || username.trim().equals("")) {
            return new ResponseEntity<String> ("\"Nombre de usuario invalido.\"", HttpStatus.BAD_REQUEST);
        } else if (email == null || email.trim().equals("")) {
            return new ResponseEntity<String> ("\"Email invalido.\"", HttpStatus.BAD_REQUEST);
        } else if (password == null || password.trim().equals("")) {
            return new ResponseEntity<String> ("\"Contraseña invalida.\"", HttpStatus.BAD_REQUEST);
        } else if (rePassword == null || !rePassword.equals(password)) {
            return new ResponseEntity<String> ("\"Las dos contraseñas no coinciden.\"", HttpStatus.BAD_REQUEST);
        } else {
            // Miramos si el usuario existe
            if (userRepository.findByName(username) != null) {
                return new ResponseEntity<String> ("\"El usuario ya existe.\"", HttpStatus.BAD_REQUEST);
            } else {
                User user = new User(username, email, password, "basico", new Date(System.currentTimeMillis()));
                if (userRepository.addUser(user)) {
                    return new ResponseEntity<String> ("\"Usuario creado correctamente.\"", HttpStatus.CREATED);
                } else return new ResponseEntity<String> ("\"Ha habido un error al crear el usuario.\"", HttpStatus.BAD_REQUEST);

            }
        }
    }
}
