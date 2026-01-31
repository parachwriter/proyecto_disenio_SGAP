package proyectos.gestionasistentes.controller;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
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
import proyectos.gestionproyectos.model.IntegranteProyecto;
import proyectos.gestionproyectos.model.Proyecto;
import proyectos.gestionproyectos.repository.ProyectoRepository;

@RestController
@RequestMapping("/api/nomina")
@CrossOrigin(origins = "*")
public class ReporteNominaController {
    private static final Logger logger = LoggerFactory.getLogger(ReporteNominaController.class);

    @Autowired
    private ServicioGestionAsistente servicioAsistente;

    @Autowired
    private ProyectoRepository proyectoRepository;

    // 1. Registrar un nuevo asistente a un proyecto específico
    @PostMapping("/registrar-asistente/{proyectoId}")
    public Asistente registrarAsistente(@PathVariable Long proyectoId, @RequestBody Asistente asistente) {
        return servicioAsistente.registrarAsistenteAProyecto(proyectoId, asistente);
    }

    // 2. Dar de baja (Cambiar estado a FUERA_NOMINA) - Ahora funciona con cualquier
    // tipo de integrante
    @PutMapping("/dar-de-baja/{idAsistente}")
    public IntegranteProyecto darDeBaja(@PathVariable Long idAsistente) {
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

        String actualizadoPor = payload.get("actualizadoPor") != null ? payload.get("actualizadoPor").toString() : null;
        return servicioAsistente.confirmarActualizacionNomina(proyectoId, mes, anio, idsAsistentes, actualizadoPor);
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
            response.put("asistentesGuardados", resultado.getListaIntegrantes().size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error en procesamiento: {}", e.getMessage(), e);
            response.put("error", "Error al procesar la nómina");
            response.put("mensaje", e.getMessage());
            response.put("tipo", e.getClass().getSimpleName());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Obtener TODOS los integrantes activos del proyecto (Asistentes, Ayudantes,
    // Técnicos)
    @GetMapping("/asistentes-activos/{proyectoId}")
    public ResponseEntity<?> obtenerAsistentesActivos(@PathVariable Long proyectoId) {
        try {
            List<IntegranteProyecto> integrantes = servicioAsistente.obtenerIntegrantesActivosPorProyecto(proyectoId);
            return ResponseEntity.ok(integrantes);
        } catch (Exception e) {
            logger.error("Error obteniendo integrantes: {}", e.getMessage(), e);
            return ResponseEntity.ok(new ArrayList<>()); // Retorna lista vacía en caso de error
        }
    }

    // Obtener fechas y años disponibles del proyecto
    @GetMapping("/fechas-proyecto/{proyectoId}")
    public ResponseEntity<?> obtenerFechasProyecto(@PathVariable Long proyectoId) {
        try {
            Proyecto proyecto = proyectoRepository.findById(proyectoId)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

            Map<String, Object> response = new HashMap<>();
            response.put("fechaInicio", proyecto.getFechaInicio());
            response.put("fechaFin", proyecto.getFechaFin());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error obteniendo fechas del proyecto: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // Obtener meses atrasados (sin reporte completado)
    @GetMapping("/meses-atrasados/{proyectoId}")
    public ResponseEntity<?> obtenerMesesAtrasados(@PathVariable Long proyectoId) {
        try {
            Proyecto proyecto = proyectoRepository.findById(proyectoId)
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

            // Obtener todos los reportes de nómina para este proyecto
            List<ReporteNomina> reportes = servicioAsistente.obtenerReportesProyecto(proyectoId);

            // Crear mapa de meses completados (año-mes -> completado)
            Map<String, Boolean> mesesCompletados = new HashMap<>();
            for (ReporteNomina reporte : reportes) {
                String clave = reporte.getAnio() + "-" + String.format("%02d", reporte.getMes());
                mesesCompletados.put(clave, true);
            }

            // Parsear fechas del proyecto
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            YearMonth fechaInicio = YearMonth.parse(proyecto.getFechaInicio(), formatter);
            YearMonth fechaFin = YearMonth.parse(proyecto.getFechaFin(), formatter);
            YearMonth ahora = YearMonth.now();

            // Generar lista de meses atrasados
            List<Map<String, Object>> mesesAtrasados = new ArrayList<>();
            YearMonth actual = fechaInicio;
            while (!actual.isAfter(fechaFin) && !actual.isAfter(ahora)) {
                String clave = actual.getYear() + "-" + String.format("%02d", actual.getMonthValue());
                if (!mesesCompletados.containsKey(clave)) {
                    Map<String, Object> mes = new HashMap<>();
                    mes.put("mes", actual.getMonthValue());
                    mes.put("anio", actual.getYear());
                    mes.put("etiqueta", actual.format(DateTimeFormatter.ofPattern("MMMM")));
                    mesesAtrasados.add(mes);
                }
                actual = actual.plusMonths(1);
            }

            return ResponseEntity.ok(mesesAtrasados);
        } catch (Exception e) {
            logger.error("Error obteniendo meses atrasados: {}", e.getMessage(), e);
            return ResponseEntity.ok(new ArrayList<>()); // Retorna lista vacía en caso de error
        }
    }
}