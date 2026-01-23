package proyectos.gestionproyectos.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proyectos.gestionproyectos.model.ProyectoInvestigacion;
import proyectos.gestionproyectos.service.ServicioGestionProyecto;

@CrossOrigin(origins = "*") // Permite peticiones desde cualquier origen para pruebas

@RestController
@RequestMapping("/proyectos")
public class ProyectoController {
    private static final Logger logger = LoggerFactory.getLogger(ProyectoController.class);

    @Autowired
    private ServicioGestionProyecto servicio;

    @PostMapping
    public ResponseEntity<String> crearProyecto(@RequestBody ProyectoInvestigacion p) {
        try {
            // Verificación rápida antes de procesar
            if (p.getDirector() == null) {
                return ResponseEntity.badRequest().body("Error: El proyecto debe tener un director asignado");
            }

            // El servicio se encarga de llamar a ServicioGestionUsuario
            // para crear las credenciales y guardar al director.
            servicio.registrarProyecto(p);
            return ResponseEntity.ok("Proyecto y Director registrados correctamente. Se ha enviado un correo a "
                    + p.getDirector().getCorreoInstitucional());
        } catch (Exception e) {
            logger.error("Error al crear proyecto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al crear proyecto: " + e.getMessage());
        }
    }

    @PostMapping("/{proyectoId}/integrantes/{integranteId}")
    public String asignarIntegrante(
            @PathVariable Long proyectoId,
            @PathVariable Long integranteId) {
        servicio.asignarIntegranteAProyecto(proyectoId, integranteId);
        return "Integrante asignado con éxito";
    }

    @GetMapping("/director/{correo}")
    public ResponseEntity<?> listarPorDirector(@PathVariable String correo) {
        try {
            List<ProyectoInvestigacion> proyectos = servicio.obtenerProyectosPorDirector(correo);
            return ResponseEntity.ok(proyectos != null ? proyectos : new ArrayList<>());
        } catch (Exception e) {
            logger.error("Error al listar proyectos: {}", e.getMessage(), e);
            return ResponseEntity.ok(new ArrayList<>()); // Retorna lista vacía en lugar de error
        }
    }
}