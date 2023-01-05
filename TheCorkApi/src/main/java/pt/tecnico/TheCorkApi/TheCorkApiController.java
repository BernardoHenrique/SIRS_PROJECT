package pt.tecnico.TheCorkApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;

import pt.tecnico.SecureServer;

@RestController
public class TheCorkApiController {

    private SecureServer _secureServer;

    public TheCorkApiController(){
        _secureServer = new SecureServer();
        _secureServer.InitializeDB();
        _secureServer.InitializeConnection();
    }

@GetMapping("/login")
public boolean getSum(@RequestParam string a, @RequestParam string b) {

    return _secureServer.SendQueryLogin(a, b);
}

    @GetMapping("/hello")
    public String hello() {
        return "Hello World!";
    }
}
