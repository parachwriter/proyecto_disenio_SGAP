package proyectos.gestionusuario.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Necesario para respuestas HTTP correctas
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping; // Nuevo
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyectos.gestionusuario.model.DirectorProyecto; // Importar modelo
import proyectos.gestionusuario.repository.DirectorProyectoRepository; // Importar repo
import proyectos.gestionusuario.service.ServicioGestionUsuario;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class LoginController {

    @Autowired
    private ServicioGestionUsuario servicioUsuario;

    // Inyectamos el repositorio SOLO para lecturas (listar), cumpliendo el principio de segregación
    @Autowired
    private DirectorProyectoRepository directorRepo;

    // 1. LOGIN (Tu código original intacto)
    @PostMapping("/login")
    public String login(@RequestBody Map<String, String> credenciales) {
        String usuario = credenciales.get("usuario");
        String password = credenciales.get("password");
        return servicioUsuario.autenticarUsuario(usuario, password);
    }

    // 2. NUEVO: REGISTRAR DIRECTOR
    // Reutiliza TU lógica existente en ServicioGestionUsuario.crearCredencialesDirector
    @PostMapping("/directores")
    public ResponseEntity<?> registrarDirector(@RequestBody DirectorProyecto director) {
        try {
            // El controlador solo actúa de puente (Fachada) hacia el servicio
            DirectorProyecto nuevoDirector = servicioUsuario.crearCredencialesDirector(director);
            return ResponseEntity.ok("Director registrado con éxito. Credenciales enviadas a: " +
                    nuevoDirector.getCorreoInstitucional());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al registrar director: " + e.getMessage());
        }
    }

    // 3. NUEVO: LISTAR DIRECTORES
    // Usa TU repositorio existente que ya tiene el método findAll() heredado
    @GetMapping("/directores")
    public List<DirectorProyecto> listarDirectores() {
        return directorRepo.findAll();
    }
}