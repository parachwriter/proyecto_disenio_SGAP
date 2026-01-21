package proyectos.gestionusuario.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyectos.gestionusuario.service.ServicioGestionUsuario;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private ServicioGestionUsuario servicioUsuario;

    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> credenciales) {
        String usuario = credenciales.get("usuario");
        String password = credenciales.get("password");

        // Retorna "ADMIN", "DIRECTOR" o "INVALIDO"
        return servicioUsuario.autenticarUsuario(usuario, password);
    }
}