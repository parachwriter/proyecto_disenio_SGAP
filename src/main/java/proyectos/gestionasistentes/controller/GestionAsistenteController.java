package proyectos.gestionasistentes.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    // 0) OBTENER TODAS LAS NÓMINAS (endpoint raíz)
    @GetMapping
    public List<ReporteNomina> obtenerTodasLasNominas() {
        return servicio.obtenerTodasLasNominas();
    }

    // ✅ Debug: si entras por navegador a /registrar, el navegador hace GET (no
    // POST)
    @GetMapping("/asistentes/registrar")
    public String registrarAsistenteGet() {
        return "Estas entrando por GET. Debe ser POST /nomina/asistentes/registrar";
    }

    // 1) LISTAR ASISTENTES (devuelve todos los integrantes de proyectos, incluyendo
    // técnicos y ayudantes). Mapea a DTO que incluye nombre e id del proyecto.
    @GetMapping("/asistentes")
    public List<IntegranteDTO> listarAsistentes() {
        return integranteRepository.findAll().stream()
                .map(IntegranteDTO::new)
                .collect(Collectors.toList());
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
                req.getIdsAsistentes(), req.getActualizadoPor());
    }

    // 4) VALIDAR REPORTE NOMINA
    @GetMapping("/validar")
    public Map<String, Object> validar(@RequestParam Integer mes, @RequestParam Integer anio) {
        return Map.of("mes", mes, "anio", anio, "cumplido", true);
    }

    // 5) OBTENER ASISTENTE POR ID
    @GetMapping("/asistentes/{id}")
    public org.springframework.http.ResponseEntity<?> obtenerAsistente(@PathVariable Long id) {
        return integranteRepository.findById(id)
                .map(i -> {
                    if (i instanceof Asistente) {
                        return org.springframework.http.ResponseEntity.ok((Asistente) i);
                    }
                    return org.springframework.http.ResponseEntity.badRequest().body("No es un asistente");
                })
                .orElse(org.springframework.http.ResponseEntity.notFound().build());
    }

    // 6) ELIMINAR ASISTENTE
    @DeleteMapping("/asistentes/{id}")
    public org.springframework.http.ResponseEntity<?> eliminarAsistente(@PathVariable Long id) {
        try {
            servicio.darDeBajaAsistente(id);
            return org.springframework.http.ResponseEntity.noContent().build();
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 8) OBTENER NÓMINAS POR PROYECTO (con filtro opcional por quien actualizó)
    @GetMapping("/proyecto/{proyectoId}")
    public List<ReporteNomina> obtenerNominasPorProyecto(@PathVariable Long proyectoId,
            @RequestParam(required = false) String actualizadoPor) {
        return servicio.obtenerNominasPorProyecto(proyectoId, actualizadoPor);
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
        private String actualizadoPor;

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

        public String getActualizadoPor() {
            return actualizadoPor;
        }

        public void setActualizadoPor(String actualizadoPor) {
            this.actualizadoPor = actualizadoPor;
        }
    }

    // DTO para exponer integrandes con información mínima del proyecto
    public static class IntegranteDTO {
        private Long id;
        private String nombre;
        private String cedula;
        private String tipo;
        private Map<String, Object> proyecto;
        private String estado;
        private String fechaNacimiento;

        public IntegranteDTO(IntegranteProyecto i) {
            this.id = i.getId();
            this.nombre = i.getNombre();
            this.cedula = i.getCedula();
            this.tipo = i.getTipo();
            if (i.getProyecto() != null) {
                this.proyecto = Map.of("id", i.getProyecto().getId(), "nombre", i.getProyecto().getNombre());
            } else {
                this.proyecto = null;
            }
            this.estado = i.getEstado() != null ? i.getEstado().name() : null;
            this.fechaNacimiento = i.getFechaNacimiento() != null ? i.getFechaNacimiento().toString() : null;
        }

        public Long getId() {
            return id;
        }

        public String getNombre() {
            return nombre;
        }

        public String getCedula() {
            return cedula;
        }

        public String getTipo() {
            return tipo;
        }

        public Map<String, Object> getProyecto() {
            return proyecto;
        }

        public String getEstado() {
            return estado;
        }

        public String getFechaNacimiento() {
            return fechaNacimiento;
        }
    }
}
