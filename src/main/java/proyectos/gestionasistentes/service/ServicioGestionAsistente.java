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
    private IntegranteRepository integranteRepo;

    @Autowired
    private NominaRepository nominaRepo;

    // +ServicioGestionAsistente()
    public ServicioGestionAsistente() {
        // Constructor vacío (Spring lo maneja)
    }

    // +registrarAsistente()
    public Asistente registrarAsistente(Asistente asistente) {
        if (asistente == null) {
            throw new IllegalArgumentException("Asistente no puede ser null");
        }
        // Por defecto, si no viene estado, se marca ACTIVO
        if (asistente.getEstado() == null) {
            asistente.setEstado(Asistente.EstadoAsistente.ACTIVO);
        }
        return (Asistente) integranteRepo.save(asistente);
    }

    // +actualizarEstadoAsistente()
    public Asistente actualizarEstadoAsistente(Long idAsistente, Asistente.EstadoAsistente nuevoEstado) {
        Asistente asistente = obtenerAsistentePorId(idAsistente);
        asistente.setEstado(nuevoEstado);
        return (Asistente) integranteRepo.save(asistente);
    }

    // +generarReporteNominaMensual()
    public ReporteNomina generarReporteNominaMensual(Integer mes, Integer anio, List<Asistente> listaAsistentes) {
        if (mes == null || anio == null) {
            throw new IllegalArgumentException("mes y anio son obligatorios");
        }

        // Si ya existe, lo actualizamos (decisión práctica)
        ReporteNomina reporte = nominaRepo.findByMesAndAnio(mes, anio).orElse(new ReporteNomina());

        reporte.setMes(mes);
        reporte.setAnio(anio);
        reporte.setListaAsistentes(listaAsistentes);

        // Fecha y estado base según el diagrama
        if (reporte.getFechaRegistro() == null) {
            reporte.setFechaRegistro(LocalDate.now());
        }
        if (reporte.getEstado() == null || reporte.getEstado().isBlank()) {
            reporte.setEstado("REGISTRADO");
        }

        // Registrar en objeto (método del diagrama)
        reporte.registrar();

        // Guardar reporte
        ReporteNomina guardado = nominaRepo.save(reporte);

        // Actualizar estados de nómina para los asistentes del reporte
        actualizarEstadoNominaAsistentes(listaAsistentes);

        // Si está incompleto, lo marcamos
        if (!guardado.validarCompleto()) {
            guardado.setEstado("INCOMPLETO");
            guardado = nominaRepo.save(guardado);
        } else {
            guardado.setEstado("COMPLETO");
            guardado = nominaRepo.save(guardado);
        }

        return guardado;
    }

    // +actualizarEstadoNominaAsistentes()
    public void actualizarEstadoNominaAsistentes(List<Asistente> listaAsistentes) {
        if (listaAsistentes == null) return;

        for (Asistente a : listaAsistentes) {
            if (a == null) continue;

            // Si está en el reporte, lo consideramos en nómina => ACTIVO
            if (a.getEstado() == null || a.getEstado() == Asistente.EstadoAsistente.FUERA_NOMINA) {
                a.setEstado(Asistente.EstadoAsistente.ACTIVO);
            }

            integranteRepo.save(a);
        }
    }

    // +validarReporteMensualCumplido()
    public boolean validarReporteMensualCumplido(Integer mes, Integer anio) {
        return nominaRepo.findByMesAndAnio(mes, anio)
                .map(r -> r.validarCompleto())
                .orElse(false);
    }

    // ===== Helper interno (no afecta al diagrama) =====
    private Asistente obtenerAsistentePorId(Long idAsistente) {
        return integranteRepo.findById(idAsistente)
                .map(i -> {
                    if (i instanceof Asistente a) return a;
                    throw new RuntimeException("El integrante con id " + idAsistente + " no es Asistente");
                })
                .orElseThrow(() -> new RuntimeException("Asistente no encontrado con id: " + idAsistente));
    }
}
