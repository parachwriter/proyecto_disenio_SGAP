package proyectos.gestionproyectos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin; // Cambiado para soportar HTML
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import proyectos.gestionproyectos.model.ProyectoInvestigacion;
import proyectos.gestionproyectos.service.ServicioGestionProyecto;

@CrossOrigin(origins = "*") // Permite peticiones desde cualquier origen para pruebas

@RestController
@RequestMapping("/proyectos")
public class ProyectoController {
    @Autowired
    private ServicioGestionProyecto servicio;

    @PostMapping
    public String crearProyecto(@RequestBody ProyectoInvestigacion p) {
        // Verificación rápida antes de procesar
        if (p.getDirector() == null) {
            return "Error: El proyecto debe tener un director asignado";
        }

        // El servicio se encarga de llamar a ServicioGestionUsuario
        // para crear las credenciales y guardar al director.
        servicio.registrarProyecto(p);
        return "Proyecto y Director registrados correctamente. Se ha enviado un correo a "
                + p.getDirector().getCorreoInstitucional();
    }

    @PostMapping("/{proyectoId}/integrantes/{integranteId}")
    public String asignarIntegrante(
            @PathVariable Long proyectoId,
            @PathVariable Long integranteId) {
        servicio.asignarIntegranteAProyecto(proyectoId, integranteId);
        return "Integrante asignado con éxito";
    }
}