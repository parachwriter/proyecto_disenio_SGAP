package proyectos.gestionasistentes.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proyectos.gestionasistentes.dto.NominaRequestDTO;
import proyectos.gestionasistentes.model.ReporteNomina;
import proyectos.gestionasistentes.service.ServicioGestionAsistente;
import proyectos.gestionproyectos.model.Asistente;

@RestController
@RequestMapping("/api/nomina")
@CrossOrigin(origins = "*")
public class ReporteNominaController {
    private static final Logger logger = LoggerFactory.getLogger(ReporteNominaController.class);

    @Autowired
    private ServicioGestionAsistente servicioAsistente;

    // 1. Registrar un nuevo asistente a un proyecto específico
    @PostMapping("/registrar-asistente/{proyectoId}")
    public Asistente registrarAsistente(@PathVariable Long proyectoId, @RequestBody Asistente asistente) {
        return servicioAsistente.registrarAsistenteAProyecto(proyectoId, asistente);
    }

    // 2. Dar de baja (Cambiar estado a FUERA_NOMINA)
    @PutMapping("/dar-de-baja/{idAsistente}")
    public Asistente darDeBaja(@PathVariable Long idAsistente) {
        return servicioAsistente.darDeBajaAsistente(idAsistente);
    }

    // 3. Confirmar la nómina mensual y guardar el reporte
    @PostMapping("/confirmar")
    public ReporteNomina confirmarNomina(@RequestBody Map<String, Object> payload) {
        Long proyectoId = Long.valueOf(payload.get("proyectoId").toString());
        Integer mes = (Integer) payload.get("mes");
        Integer anio = (Integer) payload.get("anio");
        @SuppressWarnings("unchecked")
        List<Long> idsAsistentes = (List<Long>) payload.get("idsAsistentes");

        return servicioAsistente.confirmarActualizacionNomina(proyectoId, mes, anio, idsAsistentes);
    }

    @PostMapping("/confirmar-completo")
    public ResponseEntity<?> confirmarNominaCompleta(@RequestBody NominaRequestDTO request) {
        Map<String, Object> response = new HashMap<>();
        try {
            System.out.println("DEBUG: Iniciando procesamiento de nómina");
            System.out.println("DEBUG: Proyecto ID: " + request.getProyectoId());
            System.out.println(
                    "DEBUG: Asistentes recibidos: "
                            + (request.getAsistentes() != null ? request.getAsistentes().size() : 0));

            if (request.getAsistentes() != null) {
                for (NominaRequestDTO.AsistenteDTO a : request.getAsistentes()) {
                    System.out.println(
                            "  - ID: " + a.getId() + ", Nombre: " + a.getNombre() + ", Estado: " + a.getEstado());
                }
            }

            ReporteNomina resultado = servicioAsistente.procesarNominaCompleta(request);

            response.put("idReporte", resultado.getIdReporte());
            response.put("mensaje", "Nómina procesada exitosamente");
            response.put("proyecto", resultado.getProyecto().getNombre());
            response.put("asistentesGuardados", resultado.getListaAsistentes().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error en procesamiento: {}", e.getMessage(), e);
            response.put("error", "Error al procesar la nómina");
            response.put("mensaje", e.getMessage());
            response.put("tipo", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Obtener asistentes activos del proyecto
    @GetMapping("/asistentes-activos/{proyectoId}")
    public ResponseEntity<?> obtenerAsistentesActivos(@PathVariable Long proyectoId) {
        try {
            List<Asistente> asistentes = servicioAsistente.obtenerAsistentesActivosPorProyecto(proyectoId);
            return ResponseEntity.ok(asistentes);
        } catch (Exception e) {
            logger.error("Error obteniendo asistentes: {}", e.getMessage(), e);
            return ResponseEntity.ok(new ArrayList<>()); // Retorna lista vacía en caso de error
        }
    }
}