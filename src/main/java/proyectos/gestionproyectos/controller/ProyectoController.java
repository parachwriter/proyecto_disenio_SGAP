package proyectos.gestionproyectos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // Cambiado para soportar HTML
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import proyectos.gestionproyectos.model.ProyectoInvestigacion;
import proyectos.gestionproyectos.service.ServicioGestionProyecto;

@RestController
@RequestMapping("/proyectos")
public class ProyectoController {
    @Autowired
    private ServicioGestionProyecto servicio;

    @PostMapping
    public ProyectoInvestigacion crearProyecto(@RequestBody ProyectoInvestigacion p) {
        return servicio.registrarProyecto(p);

    }

    @PostMapping("/{proyectoId}/integrantes/{integranteId}")
    public String asignarIntegrante(
            @PathVariable Long proyectoId,
            @PathVariable Long integranteId) {
        servicio.asignarIntegranteAProyecto(proyectoId, integranteId);
        return "Integrante asignado con éxito";
    }
}