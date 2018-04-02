package src;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LogInController {

    @RequestMapping("/logIn")
    public void logIn(@RequestParam Map<String,String> requestParams) {
        System.out.println(requestParams.get("user"));
        System.out.println(requestParams.get("pass"));
    }
}
