package proyectos.gestionproyectos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Cambiado para soportar HTML
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyectos.gestionproyectos.model.ProyectoInvestigacion;
import proyectos.gestionproyectos.service.ServicioGestionProyecto;
import proyectos.gestionusuario.model.DirectorProyecto;

@RestController
@RequestMapping("/proyectos")
public class ProyectoController {
    @Autowired
    private ServicioGestionProyecto servicio;

    @PostMapping
    public String crearProyecto(@RequestBody ProyectoInvestigacion p) {
        // Extraemos el director que viene dentro del objeto proyecto
        DirectorProyecto director = p.getDirector();

        // Ahora enviamos AMBOS parámetros al servicio como él espera
        servicio.registrarProyecto(p, director);

        return "Proyecto y Director registrados correctamente";
    }

    @PostMapping("/{proyectoId}/integrantes/{integranteId}")
    public String asignarIntegrante(
            @PathVariable Long proyectoId,
            @PathVariable Long integranteId) {
        servicio.asignarIntegranteAProyecto(proyectoId, integranteId);
        return "Integrante asignado con éxito";
    }
}