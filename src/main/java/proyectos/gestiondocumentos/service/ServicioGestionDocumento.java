package proyectos.gestiondocumentos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyectos.gestiondocumentos.dto.ValidacionDocumentoDTO;
import proyectos.gestiondocumentos.model.Documento;
import proyectos.gestiondocumentos.repository.DocumentoRepository;
import proyectos.gestionasistentes.model.ReporteNomina;
import proyectos.gestionasistentes.repository.NominaRepository;
import proyectos.gestionproyectos.util.PeriodoAcademicoUtil;
import proyectos.gestionproyectos.model.PeriodoAcademico;
import proyectos.gestionproyectos.model.Proyecto;
import proyectos.gestionproyectos.repository.ProyectoRepository;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioGestionDocumento {

    @Autowired
    private DocumentoRepository documentoRepository;

    @Autowired
    private NominaRepository nominaRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    // Método del UML: cargarDocumento
    public void cargarDocumento(Documento documento) {
        if (documento.validarFormato()) {
            documentoRepository.save(documento);
            System.out.println(
                    "Documento guardado: " + documento.getNombre() + " (ID: " + documento.getIdDocumento() + ")");
        } else {
            System.err.println("Error: Formato inválido para " + documento.getNombre());
            // En un caso real, lanzarías: throw new RuntimeException("Formato inválido");
        }
    }

    // Método del UML: descargarDocumento
    public Documento descargarDocumento(Long idDocumento) {
        Optional<Documento> docOpt = documentoRepository.findById(idDocumento);
        if (docOpt.isPresent()) {
            Documento doc = docOpt.get();
            System.out.println("Descargando archivo desde ruta: " + doc.obtenerRutaCompleta());
            return doc;
        } else {
            System.out.println("Documento no encontrado.");
            return null;
        }
    }

    // Método del UML: agregarDocumentoAProyecto
    public void agregarDocumentoAProyecto(String idProyecto, Documento documento) {
        // En este diseño, los documentos (Memorando/Cronograma) ya tienen el idProyecto
        // como atributo.
        // Este método sirve como puente lógico.
        System.out.println("Vinculando documento al proyecto: " + idProyecto);
        cargarDocumento(documento);
    }

    /**
     * Valida si un proyecto puede cargar un documento según el tipo y periodo
     * académico
     * 
     * @param proyectoId       ID del proyecto
     * @param periodoAcademico Código del periodo (ej: "2025A", "2026B")
     * @param tipoDocumento    "PLANIFICACION" o "AVANCE"
     * @return ValidacionDocumentoDTO con el resultado de la validación
     */
    public ValidacionDocumentoDTO validarCargaDocumento(Long proyectoId, String periodoAcademico,
            String tipoDocumento) {
        ValidacionDocumentoDTO resultado = new ValidacionDocumentoDTO();
        resultado.setPeriodoAcademico(periodoAcademico);
        resultado.setTipoDocumento(tipoDocumento);

        // Obtener el proyecto
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        if (!proyectoOpt.isPresent()) {
            resultado.setPuedeCargar(false);
            resultado.setMensaje("Proyecto no encontrado");
            return resultado;
        }

        Proyecto proyecto = proyectoOpt.get();
        LocalDate fechaInicio = LocalDate.parse(proyecto.getFechaInicio());
        LocalDate fechaActual = LocalDate.now();

        // Obtener el periodo solicitado
        PeriodoAcademico periodoSolicitado = PeriodoAcademicoUtil.parsearCodigo(periodoAcademico);

        if ("PLANIFICACION".equals(tipoDocumento)) {
            return validarPlanificacion(proyectoId, fechaInicio, periodoSolicitado);
        } else if ("AVANCE".equals(tipoDocumento)) {
            return validarAvance(proyectoId, fechaInicio, fechaActual, periodoSolicitado);
        } else {
            resultado.setPuedeCargar(false);
            resultado.setMensaje("Tipo de documento inválido");
            return resultado;
        }
    }

    /**
     * Valida si se puede cargar una Planificación de Proyecto
     * Requisito: Todas las nóminas de periodos anteriores deben estar completas
     */
    private ValidacionDocumentoDTO validarPlanificacion(Long proyectoId, LocalDate fechaInicio,
            PeriodoAcademico periodoSolicitado) {
        ValidacionDocumentoDTO resultado = new ValidacionDocumentoDTO();

        // Verificar si el periodo solicitado es el primer periodo del proyecto
        PeriodoAcademico primerPeriodoProyecto = PeriodoAcademicoUtil.obtenerPeriodoDeFecha(fechaInicio);

        if (periodoSolicitado.compareTo(primerPeriodoProyecto) == 0) {
            // Es el primer periodo del proyecto, no hay periodos anteriores que validar
            resultado.setPuedeCargar(true);
            resultado.setMensaje("Es el primer periodo del proyecto. Puede cargar la Planificación.");
            return resultado;
        }

        // Obtener todos los periodos desde el inicio del proyecto hasta el periodo
        // anterior al solicitado
        PeriodoAcademico periodoAnterior = periodoSolicitado.anterior();
        List<PeriodoAcademico> periodosAnteriores = PeriodoAcademicoUtil.obtenerPeriodosEntreFechas(
                fechaInicio,
                periodoAnterior.getFechaFin());

        // Verificar que todas las nóminas de esos periodos estén completas
        for (PeriodoAcademico periodo : periodosAnteriores) {
            LocalDate inicioPeriodo = periodo.getFechaInicio();
            LocalDate finPeriodo = periodo.getFechaFin();

            // Contar cuántos meses tiene este periodo
            int mesesEnPeriodo = 6; // Cada periodo académico tiene 6 meses

            // Contar cuántas nóminas existen para este proyecto en este periodo
            List<ReporteNomina> nominas = nominaRepository.findByProyectoIdAndMesAnio(
                    proyectoId,
                    inicioPeriodo.getMonthValue(),
                    inicioPeriodo.getYear(),
                    finPeriodo.getMonthValue(),
                    finPeriodo.getYear());

            if (nominas.size() < mesesEnPeriodo) {
                resultado.setPuedeCargar(false);
                resultado.setMensaje(
                        "No puede cargar Planificación para el periodo " + periodoSolicitado.getCodigo() +
                                ". Faltan nóminas del periodo " + periodo.getCodigo() +
                                ". Nóminas registradas: " + nominas.size() + "/" + mesesEnPeriodo);
                return resultado;
            }
        }

        resultado.setPuedeCargar(true);
        resultado
                .setMensaje("Todas las nóminas de periodos anteriores están completas. Puede cargar la Planificación.");
        return resultado;
    }

    /**
     * Valida si se puede cargar un Avance de Proyecto
     * Requisito: Todas las nóminas de periodos anteriores completas + todas las
     * nóminas del periodo seleccionado completas (6 meses)
     * Si el periodo seleccionado es el actual y no ha terminado, NO se puede cargar
     */
    private ValidacionDocumentoDTO validarAvance(Long proyectoId, LocalDate fechaInicio, LocalDate fechaActual,
            PeriodoAcademico periodoSolicitado) {
        ValidacionDocumentoDTO resultado = new ValidacionDocumentoDTO();

        // Verificar que el periodo solicitado no sea futuro
        PeriodoAcademico periodoActual = PeriodoAcademicoUtil.obtenerPeriodoActual();
        if (periodoSolicitado.compareTo(periodoActual) > 0) {
            resultado.setPuedeCargar(false);
            resultado.setMensaje("No puede cargar un Avance para un periodo futuro");
            return resultado;
        }

        // Si el periodo seleccionado es el actual, verificar que ya haya terminado
        if (periodoSolicitado.compareTo(periodoActual) == 0) {
            // El periodo actual aún no ha terminado
            resultado.setPuedeCargar(false);
            resultado.setMensaje(
                    "No puede cargar un Avance para el periodo actual " + periodoSolicitado.getCodigo() +
                            " porque aún no ha finalizado. Debe esperar a que termine el periodo.");
            return resultado;
        }

        // Lista para acumular todos los periodos con nóminas faltantes
        List<String> periodosFaltantes = new ArrayList<>();
        PeriodoAcademico primerPeriodoProyecto = PeriodoAcademicoUtil.obtenerPeriodoDeFecha(fechaInicio);

        // 1. Validar periodos anteriores al seleccionado (completos)
        if (periodoSolicitado.compareTo(primerPeriodoProyecto) > 0) {
            PeriodoAcademico periodoAnteriorAlSeleccionado = periodoSolicitado.anterior();
            List<PeriodoAcademico> periodosAnteriores = PeriodoAcademicoUtil.obtenerPeriodosEntreFechas(
                    fechaInicio,
                    periodoAnteriorAlSeleccionado.getFechaFin());

            for (PeriodoAcademico periodo : periodosAnteriores) {
                LocalDate inicioPeriodo = periodo.getFechaInicio();
                LocalDate finPeriodo = periodo.getFechaFin();

                int mesesEnPeriodo = 6;
                List<ReporteNomina> nominas = nominaRepository.findByProyectoIdAndMesAnio(
                        proyectoId,
                        inicioPeriodo.getMonthValue(),
                        inicioPeriodo.getYear(),
                        finPeriodo.getMonthValue(),
                        finPeriodo.getYear());

                if (nominas.size() < mesesEnPeriodo) {
                    periodosFaltantes.add(periodo.getCodigo() + " (registradas: " + nominas.size() + "/6)");
                }
            }
        }

        // 2. Validar que el periodo seleccionado tenga TODAS sus nóminas completas (6 meses)
        LocalDate inicioPeriodoSeleccionado = periodoSolicitado.getFechaInicio();
        LocalDate finPeriodoSeleccionado = periodoSolicitado.getFechaFin();
        int mesesEnPeriodo = 6;

        List<ReporteNomina> nominasPeriodoSeleccionado = nominaRepository.findByProyectoIdAndMesAnio(
                proyectoId,
                inicioPeriodoSeleccionado.getMonthValue(),
                inicioPeriodoSeleccionado.getYear(),
                finPeriodoSeleccionado.getMonthValue(),
                finPeriodoSeleccionado.getYear());

        if (nominasPeriodoSeleccionado.size() < mesesEnPeriodo) {
            periodosFaltantes.add(periodoSolicitado.getCodigo() + " (registradas: " + nominasPeriodoSeleccionado.size() + "/6)");
        }

        // Si hay periodos con nóminas faltantes, construir mensaje detallado
        if (!periodosFaltantes.isEmpty()) {
            resultado.setPuedeCargar(false);
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("No puede cargar Avance para el periodo ").append(periodoSolicitado.getCodigo())
                    .append(".\n\nFaltan nóminas completas en los siguientes periodos:\n");
            for (String periodoFaltante : periodosFaltantes) {
                mensaje.append("• ").append(periodoFaltante).append("\n");
            }
            mensaje.append("\nDebe completar todas las nóminas antes de cargar el documento.");
            resultado.setMensaje(mensaje.toString());
            return resultado;
        }

        resultado.setPuedeCargar(true);
        resultado.setMensaje("Todas las nóminas requeridas están completas. Puede cargar el Avance del periodo " + periodoSolicitado.getCodigo());
        return resultado;
    }

        resultado.setPuedeCargar(true);
        resultado.setMensaje("Todas las nóminas requeridas están completas. Puede cargar el Avance del periodo "
                + periodoSolicitado.getCodigo());
        return resultado;
    }}