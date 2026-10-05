package comfenalco.api.filter.sms.controller;

import comfenalco.api.filter.sms.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/public")
public class PublicController {


    private final JwtService jwtService;


    @Autowired
    public PublicController(
            JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @GetMapping("/token")
    public ResponseEntity<String> getToken(HttpServletRequest request) {

        String clientId = request.getRemoteAddr();

        String token = jwtService.generateToken(clientId);

        return ResponseEntity.ok(token);
    }

}
