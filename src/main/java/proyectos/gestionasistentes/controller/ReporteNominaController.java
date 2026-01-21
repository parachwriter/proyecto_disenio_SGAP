package proyectos.gestionasistentes.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import proyectos.gestionasistentes.model.ReporteNomina;
import proyectos.gestionasistentes.service.ServicioGestionAsistente;
import proyectos.gestionproyectos.model.Asistente;
import proyectos.gestionproyectos.model.IntegranteProyecto;
import proyectos.gestionproyectos.repository.IntegranteRepository;

@RestController
@RequestMapping("/nomina")
@CrossOrigin(origins = "*")
public class ReporteNominaController {

    @Autowired
    private ServicioGestionAsistente servicio;

    @Autowired
    private IntegranteRepository integranteRepository;

    // 1) Listar asistentes (para el front)
    @GetMapping("/asistentes")
    public List<Asistente> listarAsistentes() {
        List<IntegranteProyecto> integrantes = integranteRepository.findAll();
        return integrantes.stream()
                .filter(i -> i instanceof Asistente)
                .map(i -> (Asistente) i)
                .toList();
    }

    // 2) Generar reporte mensual
    @PostMapping("/generar")
    public ReporteNomina generar(@RequestBody GenerarNominaRequest req) {
        return servicio.generarReporteNominaMensual(req.getMes(), req.getAnio(), req.getIdsAsistentes());
    }

    // 3) Validar reporte mensual cumplido
    @GetMapping("/validar")
    public Map<String, Object> validar(@RequestParam Integer mes, @RequestParam Integer anio) {
        boolean ok = servicio.validarReporteMensualCumplido(mes, anio);
        return Map.of("mes", mes, "anio", anio, "cumplido", ok);
    }

    // ===== DTO =====
    public static class GenerarNominaRequest {
        private Integer mes;
        private Integer anio;
        private List<Long> idsAsistentes;

        public Integer getMes() { return mes; }
        public void setMes(Integer mes) { this.mes = mes; }

        public Integer getAnio() { return anio; }
        public void setAnio(Integer anio) { this.anio = anio; }

        public List<Long> getIdsAsistentes() { return idsAsistentes; }
        public void setIdsAsistentes(List<Long> idsAsistentes) { this.idsAsistentes = idsAsistentes; }
    }
}
