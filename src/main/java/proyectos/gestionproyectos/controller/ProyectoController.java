package proyectos.gestionproyectos.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proyectos.gestionproyectos.dto.ProyectoRequestDTO;
import proyectos.gestionproyectos.model.*;
import proyectos.gestionproyectos.service.ServicioGestionProyecto;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/proyectos")
public class ProyectoController {
    private static final Logger logger = LoggerFactory.getLogger(ProyectoController.class);

    @Autowired
    private ServicioGestionProyecto servicio;

    @PostMapping
    public ResponseEntity<String> crearProyecto(@RequestBody ProyectoRequestDTO dto) {
        try {
            // Validaciones
            if (dto.getDirector() == null) {
                return ResponseEntity.badRequest().body("Error: El proyecto debe tener un director asignado");
            }
            if (dto.getTipoProyecto() == null || dto.getTipoProyecto().isEmpty()) {
                return ResponseEntity.badRequest().body("Error: Debe especificar el tipo de proyecto");
            }
            if ("INVESTIGACION".equals(dto.getTipoProyecto()) &&
                    (dto.getSubtipoInvestigacion() == null || dto.getSubtipoInvestigacion().isEmpty())) {
                return ResponseEntity.badRequest().body("Error: Debe especificar el subtipo de investigación");
            }

            // Crear la instancia correcta según el tipo
            Proyecto proyecto = crearProyectoSegunTipo(dto);

            // Asignar datos comunes
            proyecto.setNombre(dto.getNombre());
            proyecto.setPresupuesto(dto.getPresupuesto());
            proyecto.setMaxAsistentes(dto.getMaxAsistentes());
            proyecto.setFechaInicio(dto.getFechaInicio());
            proyecto.setFechaFin(dto.getFechaFin());
            proyecto.setDirector(dto.getDirector());
            // Línea de investigación (opcional)
            proyecto.setLineaInvestigacion(dto.getLineaInvestigacion());

            // Calcular duración en meses
            if (dto.getFechaInicio() != null && dto.getFechaFin() != null) {
                proyecto.calcularDuracionMeses();
            }

            // Registrar proyecto
            servicio.registrarProyecto(proyecto);

            return ResponseEntity.ok("Proyecto " + obtenerNombreTipo(dto) +
                    " registrado correctamente. Se ha enviado un correo a " +
                    dto.getDirector().getCorreoInstitucional());
        } catch (Exception e) {
            logger.error("Error al crear proyecto: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error al crear proyecto: " + e.getMessage());
        }
    }

    private Proyecto crearProyectoSegunTipo(ProyectoRequestDTO dto) {
        switch (dto.getTipoProyecto()) {
            case "INVESTIGACION":
                return crearProyectoInvestigacion(dto.getSubtipoInvestigacion());
            case "VINCULACION":
                return new ProyectoVinculacion();
            case "TRANSICION":
                return new ProyectoTransicionTecnologica();
            default:
                throw new IllegalArgumentException("Tipo de proyecto no válido: " + dto.getTipoProyecto());
        }
    }

    private Proyecto crearProyectoInvestigacion(String subtipo) {
        switch (subtipo) {
            case "INTERNO":
                return new Interno();
            case "SEMILLA":
                return new Semilla();
            case "GRUPAL":
                return new Grupal();
            case "MULTIDISCIPLINARIO":
                return new Multidisciplinario();
            default:
                throw new IllegalArgumentException("Subtipo de investigación no válido: " + subtipo);
        }
    }

    private String obtenerNombreTipo(ProyectoRequestDTO dto) {
        if ("INVESTIGACION".equals(dto.getTipoProyecto())) {
            return "de Investigación (" + dto.getSubtipoInvestigacion() + ")";
        } else if ("VINCULACION".equals(dto.getTipoProyecto())) {
            return "de Vinculación";
        } else {
            return "de Transición Tecnológica";
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
    public ResponseEntity<?> listarPorDirector(@PathVariable String correo, @RequestParam(required = false) String tipo) {
        try {
            List<Proyecto> proyectos = servicio.obtenerProyectosPorDirector(correo);
            if (tipo != null && !tipo.isBlank()) {
                proyectos = proyectos.stream()
                    .filter(p -> {
                        if ("ProyectoInvestigacion".equals(tipo)) {
                            return p instanceof proyectos.gestionproyectos.model.ProyectoInvestigacion;
                        } else if ("ProyectoVinculacion".equals(tipo)) {
                            return p instanceof proyectos.gestionproyectos.model.ProyectoVinculacion;
                        } else if ("ProyectoTransicionTecnologica".equals(tipo)) {
                            return p instanceof proyectos.gestionproyectos.model.ProyectoTransicionTecnologica;
                        }
                        return true;
                    })
                    .collect(Collectors.toList());
            }
            return ResponseEntity.ok(proyectos != null ? proyectos : new ArrayList<>());
        } catch (Exception e) {
            logger.error("Error al listar proyectos: {}", e.getMessage(), e);
            return ResponseEntity.ok(new ArrayList<>()); // Retorna lista vacía en lugar de error
        }
    }

    // listar todos los proyectos
    @GetMapping
    public ResponseEntity<List<Proyecto>> listarTodos(
            @org.springframework.web.bind.annotation.RequestParam(required = false) String tipo) {
        try {
            // Mapear valores del frontend a patrones de discriminador
            String pattern = null;
            if (tipo != null && !tipo.isBlank()) {
                switch (tipo) {
                    case "ProyectoInvestigacion":
                        pattern = "INVESTIGACION%"; // cubre INVESTIGACION_*
                        break;
                    case "ProyectoVinculacion":
                        pattern = "VINCULACION";
                        break;
                    case "ProyectoTransicionTecnologica":
                        pattern = "TRANSICION_TECNOLOGICA";
                        break;
                    default:
                        // permitir búsquedas exactas por discriminator si se envía otro valor
                        pattern = tipo;
                }
            }

            List<Proyecto> proyectos = servicio.listarPorTipo(pattern);
            if (proyectos == null || proyectos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(proyectos);
        } catch (Exception e) {
            logger.error("Error al listar proyectos con filtro tipo={}: {}", tipo, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}