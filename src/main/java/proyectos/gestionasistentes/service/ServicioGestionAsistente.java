package proyectos.gestionasistentes.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyectos.gestionasistentes.model.ReporteNomina;
import proyectos.gestionasistentes.repository.NominaRepository;
import proyectos.gestionproyectos.model.Asistente;
import proyectos.gestionproyectos.repository.IntegranteRepository;

@Service
public class ServicioGestionAsistente {

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private NominaRepository nominaRepository;

    // +ServicioGestionAsistente()
    public ServicioGestionAsistente() {
    }

    // +registrarAsistente()
    public Asistente registrarAsistente(Asistente asistente) {
        if (asistente == null) {
            throw new IllegalArgumentException("Asistente no puede ser null");
        }
        if (asistente.getEstado() == null) {
            asistente.setEstado(Asistente.EstadoAsistente.ACTIVO);
        }
        return (Asistente) integranteRepository.save(asistente);
    }

    // +actualizarEstadoAsistente()
    public Asistente actualizarEstadoAsistente(Long idAsistente, Asistente.EstadoAsistente estado) {
        Asistente asistente = obtenerAsistente(idAsistente);
        asistente.setEstado(estado);
        return (Asistente) integranteRepository.save(asistente);
    }

    // +generarReporteNominaMensual()
    public ReporteNomina generarReporteNominaMensual(Integer mes, Integer anio, List<Asistente> listaAsistentes) {
        ReporteNomina reporte = nominaRepository
                .findByMesAndAnio(mes, anio)
                .orElse(new ReporteNomina());

        reporte.setMes(mes);
        reporte.setAnio(anio);
        reporte.setListaAsistentes(listaAsistentes);

        if (reporte.getFechaRegistro() == null) {
            reporte.setFechaRegistro(LocalDate.now());
        }

        reporte.registrar();

        ReporteNomina guardado = nominaRepository.save(reporte);

        actualizarEstadoNominaAsistentes(listaAsistentes);

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
        return nominaRepository
                .findByMesAndAnio(mes, anio)
                .map(ReporteNomina::validarCompleto)
                .orElse(false);
    }

    // ===== Método de apoyo interno =====
    private Asistente obtenerAsistente(Long idAsistente) {
        return integranteRepository.findById(idAsistente)
                .map(i -> (Asistente) i)
                .orElseThrow(() ->
                        new RuntimeException("Asistente no encontrado con id: " + idAsistente));
    }
}
