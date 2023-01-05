package pt.tecnico.TheCorkApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.gson.JsonObject;
import pt.tecnico.SecureServer;

@RestController
public class TheCorkApiController {

    private SecureServer _secureServer;

    public TheCorkApiController(){
        _secureServer = new SecureServer();
        _secureServer.InitializeDB();
        //_secureServer.InitializeConnection();
    }


    @GetMapping("/login")
    public boolean login(@RequestParam(name="user", required=true) String user, @RequestParam(name="password", required=true) String pass) {
        System.out.println("i got here");
        boolean result = _secureServer.SendQueryLogin(user, pass);
        System.out.println(result);
        return result;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}