package pt.tecnico.TheCorkApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import com.google.gson.JsonObject;
import pt.tecnico.SecureServer;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
public class TheCorkApiController {

    private SecureServer _secureServer;

    public TheCorkApiController(){
        _secureServer = new SecureServer();
        _secureServer.InitializeConnection();
        System.out.println("LIGA O SERVER");
        _secureServer.InitializeDB();
    }

    @GetMapping("/login")
    public boolean login(@RequestParam(name="user", required=true) String user, @RequestParam(name="password", required=true) String pass) {
        boolean result = _secureServer.sendQueryLogin(user, pass);
        return result;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }

    @GetMapping("/updateCard")
    public String getCard(@RequestParam(name="name", required=true) String name,
                          @RequestParam(name="card", required=true) String card,
                          @RequestParam(name="validity", required=true) String validity,
                          @RequestParam(name="code", required=true) String code){
        boolean fromBank = _secureServer.RcvSendMsg(name, card, validity, code);
        if(fromBank)
             _secureServer.updateQueryCard(name, card, validity, code);
        return "accept";
    }

    @GetMapping("/getCard")
    public boolean getCard(@RequestParam(name="name", required=true) String name) {
        boolean result = _secureServer.sendQueryCard(name);
        return result;
    }
}