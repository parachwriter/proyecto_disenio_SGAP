package proyectos.gestionasistentes.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyectos.gestionasistentes.model.ReporteNomina;
import proyectos.gestionasistentes.repository.NominaRepository;
import proyectos.gestionproyectos.model.Asistente;
import proyectos.gestionproyectos.model.IntegranteProyecto;
import proyectos.gestionproyectos.model.ProyectoInvestigacion;
import proyectos.gestionproyectos.repository.IntegranteRepository;
import proyectos.gestionproyectos.repository.ProyectoRepository;

@Service
public class ServicioGestionAsistente {

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    @Autowired
    private NominaRepository nominaRepository;

    // +ServicioGestionAsistente()
    public ServicioGestionAsistente() {
    }

    // =========================================================
    // 1) REGISTRO DE ASISTENTE POR DIRECTOR (con idProyecto)
    // =========================================================
    // (Se agrega este método para cumplir el flujo del UML)
    public Asistente registrarAsistente(Long idProyecto, Asistente asistente) {
        if (idProyecto == null) {
            throw new IllegalArgumentException("idProyecto es obligatorio");
        }
        if (asistente == null) {
            throw new IllegalArgumentException("Asistente no puede ser null");
        }

        ProyectoInvestigacion proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con id: " + idProyecto));

        // Por defecto, cuando se registra: NO está en nómina hasta que el director lo marque en el reporte
        if (asistente.getEstado() == null) {
            asistente.setEstado(Asistente.EstadoAsistente.FUERA_NOMINA);
        }

        // Como ProyectoInvestigacion tiene @OneToMany(cascade = ALL) con JoinColumn "proyecto_id",
        // agregar el asistente a la lista y guardar el proyecto crea la relación automáticamente.
        proyecto.getListaIntegrantes().add(asistente);
        proyectoRepository.save(proyecto);

        return asistente;
    }

    // +actualizarEstadoAsistente()
    public Asistente actualizarEstadoAsistente(Long idAsistente, Asistente.EstadoAsistente estado) {
        Asistente asistente = obtenerAsistente(idAsistente);
        asistente.setEstado(estado);
        return (Asistente) integranteRepository.save(asistente);
    }

    // =========================================================
    // 2) NÓMINA MENSUAL
    // =========================================================
    // +generarReporteNominaMensual()
    public ReporteNomina generarReporteNominaMensual(Integer mes, Integer anio, List<Long> idsAsistentes) {
        if (mes == null || anio == null) {
            throw new IllegalArgumentException("mes y anio son obligatorios");
        }
        if (idsAsistentes == null || idsAsistentes.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos 1 asistente");
        }

        ReporteNomina reporte = nominaRepository.findByMesAndAnio(mes, anio).orElse(new ReporteNomina());

        reporte.setMes(mes);
        reporte.setAnio(anio);
        reporte.setFechaRegistro(LocalDate.now());
        reporte.setEstado("INCOMPLETO");

        List<Asistente> asistentes = idsAsistentes.stream()
                .map(this::obtenerAsistente)
                .toList();

        // +actualizarEstadoNominaAsistentes()
        actualizarEstadoNominaAsistentes(asistentes);

        reporte.setListaAsistentes(asistentes);
        reporte.registrar();

        ReporteNomina guardado = nominaRepository.save(reporte);

        if (guardado.validarCompleto()) {
            guardado.setEstado("COMPLETO");
        } else {
            guardado.setEstado("INCOMPLETO");
        }

        return nominaRepository.save(guardado);
    }

    // +actualizarEstadoNominaAsistentes()
    public void actualizarEstadoNominaAsistentes(List<Asistente> listaAsistentes) {
        if (listaAsistentes == null) return;

        for (Asistente asistente : listaAsistentes) {
            asistente.setEstado(Asistente.EstadoAsistente.ACTIVO);
            integranteRepository.save(asistente);
        }
    }

    // +validarReporteMensualCumplido()
    public boolean validarReporteMensualCumplido(Integer mes, Integer anio) {
        if (mes == null || anio == null) return false;

        return nominaRepository
                .findByMesAndAnio(mes, anio)
                .map(ReporteNomina::validarCompleto)
                .orElse(false);
    }

    // =========================================================
    // 3) UTILIDAD
    // =========================================================
    private Asistente obtenerAsistente(Long idAsistente) {
        IntegranteProyecto integrante = integranteRepository.findById(idAsistente)
                .orElseThrow(() -> new RuntimeException("Integrante no encontrado con id: " + idAsistente));

        if (!(integrante instanceof Asistente)) {
            throw new RuntimeException("El integrante con id " + idAsistente + " no es un Asistente");
        }
        return (Asistente) integrante;
    }
}
