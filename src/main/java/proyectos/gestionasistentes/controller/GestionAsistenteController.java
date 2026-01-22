package proyectos.gestionasistentes.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import proyectos.gestionasistentes.model.ReporteNomina;
import proyectos.gestionasistentes.service.ServicioGestionAsistente;
import proyectos.gestionproyectos.model.Asistente;
import proyectos.gestionproyectos.repository.IntegranteRepository;

@RestController
@RequestMapping("/nomina")
@CrossOrigin(origins = "*")
public class GestionAsistenteController {

    @Autowired
    private ServicioGestionAsistente servicio;

    @Autowired
    private IntegranteRepository integranteRepository;

    // ✅ Prueba rápida para confirmar que el controller está cargado
    @GetMapping("/ping")
    public String ping() {
        return "OK-GestionAsistentes";
    }

    // ✅ Debug: si entras por navegador a /registrar, el navegador hace GET (no
    // POST)
    @GetMapping("/asistentes/registrar")
    public String registrarAsistenteGet() {
        return "Estas entrando por GET. Debe ser POST /nomina/asistentes/registrar";
    }

    // 1) LISTAR ASISTENTES
    @GetMapping("/asistentes")
    public List<Asistente> listarAsistentes() {
        return integranteRepository.findAll().stream()
                .filter(i -> i instanceof Asistente)
                .map(i -> (Asistente) i)
                .toList();
    }

    // 2) REGISTRAR ASISTENTE (POST)
    @PostMapping("/asistentes/registrar")
    public Asistente registrarAsistente(@RequestBody RegistrarAsistenteRequest req) {
        Asistente a = new Asistente();
        a.setCedula(req.getCedula());
        a.setNombre(req.getNombre());

        if (req.getFechaNacimiento() != null && !req.getFechaNacimiento().isBlank()) {
            a.setFechaNacimiento(LocalDate.parse(req.getFechaNacimiento())); // YYYY-MM-DD
        }

        return servicio.registrarAsistenteAProyecto(req.getIdProyecto(), a);
    }

    // 3) GENERAR REPORTE NOMINA
    @PostMapping("/generar")
    public ReporteNomina generar(@RequestBody GenerarNominaRequest req) {
        return servicio.confirmarActualizacionNomina(req.getIdProyecto(), req.getMes(), req.getAnio(),
                req.getIdsAsistentes());
    }

    // 4) VALIDAR REPORTE NOMINA
    @GetMapping("/validar")
    public Map<String, Object> validar(@RequestParam Integer mes, @RequestParam Integer anio) {
        return Map.of("mes", mes, "anio", anio, "cumplido", true);
    }

    // ===== DTOs =====
    public static class RegistrarAsistenteRequest {
        private Long idProyecto;
        private String cedula;
        private String nombre;
        private String fechaNacimiento;

        public Long getIdProyecto() {
            return idProyecto;
        }

        public void setIdProyecto(Long idProyecto) {
            this.idProyecto = idProyecto;
        }

        public String getCedula() {
            return cedula;
        }

        public void setCedula(String cedula) {
            this.cedula = cedula;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getFechaNacimiento() {
            return fechaNacimiento;
        }

        public void setFechaNacimiento(String fechaNacimiento) {
            this.fechaNacimiento = fechaNacimiento;
        }
    }

    public static class GenerarNominaRequest {
        private Long idProyecto;
        private Integer mes;
        private Integer anio;
        private List<Long> idsAsistentes;

        public Long getIdProyecto() {
            return idProyecto;
        }

        public void setIdProyecto(Long idProyecto) {
            this.idProyecto = idProyecto;
        }

        public Integer getMes() {
            return mes;
        }

        public void setMes(Integer mes) {
            this.mes = mes;
        }

        public Integer getAnio() {
            return anio;
        }

        public void setAnio(Integer anio) {
            this.anio = anio;
        }

        public List<Long> getIdsAsistentes() {
            return idsAsistentes;
        }

        public void setIdsAsistentes(List<Long> idsAsistentes) {
            this.idsAsistentes = idsAsistentes;
        }
    }
}
