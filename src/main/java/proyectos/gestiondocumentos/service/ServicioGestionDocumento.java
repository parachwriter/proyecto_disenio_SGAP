package proyectos.gestiondocumentos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import proyectos.gestiondocumentos.dto.ValidacionDocumentoDTO;
import proyectos.gestiondocumentos.model.Documento;
import proyectos.gestiondocumentos.model.PlanificacionProyecto;
import proyectos.gestiondocumentos.model.AvanceProyecto;
import proyectos.gestiondocumentos.repository.DocumentoRepository;
import proyectos.gestionasistentes.model.ReporteNomina;
import proyectos.gestionasistentes.repository.NominaRepository;
import proyectos.gestionproyectos.model.PeriodoAcademico;
import proyectos.gestionproyectos.model.Proyecto;
import proyectos.gestionproyectos.repository.ProyectoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        PeriodoAcademico periodoSolicitado = parsearCodigo(periodoAcademico);

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
     * Requisito: Debe existir al menos una nómina registrada para el proyecto
     * Para periodos posteriores al primero, también se requieren las nóminas de
     * periodos anteriores completas
     */
    private ValidacionDocumentoDTO validarPlanificacion(Long proyectoId, LocalDate fechaInicio,
            PeriodoAcademico periodoSolicitado) {
        ValidacionDocumentoDTO resultado = new ValidacionDocumentoDTO();

        System.out.println("========== VALIDACIÓN PLANIFICACIÓN ==========");
        System.out.println("DEBUG PLANIFICACION - Proyecto ID: " + proyectoId);
        System.out.println("DEBUG PLANIFICACION - Fecha inicio proyecto: " + fechaInicio);

        // Ya no necesitamos validación base genérica, la validación detallada
        // por periodo calculará exactamente cuántas nóminas faltan

        // Verificar si el periodo solicitado es el primer periodo del proyecto
        PeriodoAcademico primerPeriodoProyecto = obtenerPeriodoDeFecha(fechaInicio);

        System.out.println("DEBUG PLANIFICACION - Primer periodo proyecto: " + primerPeriodoProyecto.getCodigo());
        System.out.println("DEBUG PLANIFICACION - Periodo solicitado: " + periodoSolicitado.getCodigo());

        // Lista para acumular periodos con nóminas faltantes
        List<String> periodosFaltantes = new ArrayList<>();

        // REGLA: Para el PRIMER PERIODO del proyecto, la primera planificación
        // se puede subir SIN requisito de nóminas (el proyecto apenas inicia)
        // Para periodos posteriores: exigir todas las nóminas de periodos anteriores
        // COMPLETAS
        if (periodoSolicitado.compareTo(primerPeriodoProyecto) == 0) {
            // Es el primer periodo del proyecto - SE PERMITE SUBIR SIN NÓMINAS
            System.out.println("DEBUG PLANIFICACION - Es el primer periodo, se permite subir sin requisito de nóminas");
            resultado.setPuedeCargar(true);
            resultado.setMensaje("Puede cargar la Planificación del primer periodo del proyecto.");
            return resultado;
        } else {
            // Es un periodo posterior al primero
            // Generar todos los periodos desde el primero hasta el solicitado (exclusive)
            PeriodoAcademico periodoActual = primerPeriodoProyecto;
            int iteracion = 0;
            while (periodoActual.compareTo(periodoSolicitado) < 0) {
                iteracion++;
                System.out.println("DEBUG PLANIFICACION - Iteración " + iteracion + ": Validando periodo "
                        + periodoActual.getCodigo());

                // Contar nóminas de este periodo mes por mes
                int nominasEncontradas = contarNominasPeriodo(proyectoId, periodoActual, fechaInicio,
                        primerPeriodoProyecto);
                int nominasEsperadas = calcularNominasEsperadas(periodoActual, fechaInicio, primerPeriodoProyecto);

                if (nominasEncontradas < nominasEsperadas) {
                    periodosFaltantes.add(periodoActual.getCodigo() + " (registradas: " + nominasEncontradas + "/"
                            + nominasEsperadas + ")");
                    System.out.println(
                            "DEBUG PLANIFICACION - Periodo " + periodoActual.getCodigo() + " agregado a faltantes");
                }

                periodoActual = periodoActual.siguiente();
                System.out.println("DEBUG PLANIFICACION - Siguiente periodo: " + periodoActual.getCodigo());
            }
        }

        System.out.println("DEBUG PLANIFICACION - Total periodos faltantes: " + periodosFaltantes.size());

        // Si hay periodos con nóminas faltantes, construir mensaje detallado
        if (!periodosFaltantes.isEmpty()) {
            resultado.setPuedeCargar(false);
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("No puede cargar la Planificación para el periodo ").append(periodoSolicitado.getCodigo())
                    .append(".\n\n");
            mensaje.append("Le faltan registrar reportes de nómina confirmados en los siguientes periodos:\n\n");
            for (String periodoFaltante : periodosFaltantes) {
                mensaje.append("• ").append(periodoFaltante).append("\n");
            }
            mensaje.append("\nNota: Solo cuentan las nóminas con estado 'Completo', no las pendientes.");
            resultado.setMensaje(mensaje.toString());
            return resultado;
        }

        resultado.setPuedeCargar(true);
        resultado.setMensaje("Todas las nóminas requeridas están registradas. Puede cargar la Planificación.");
        return resultado;
    }

    /**
     * Cuenta el total de nóminas COMPLETAS (confirmadas) para un proyecto.
     * NO cuenta las nóminas con estado PENDIENTE.
     */
    private int contarTotalNominasCompletasProyecto(Long proyectoId) {
        int cantidad = nominaRepository.contarNominasCompletasProyecto(proyectoId);
        System.out.println("DEBUG - Proyecto " + proyectoId + " total nóminas COMPLETAS: " + cantidad);
        return cantidad;
    }

    /**
     * @deprecated Usar contarTotalNominasCompletasProyecto en su lugar
     */
    @SuppressWarnings("unused")
    private int contarTotalNominasProyecto(Long proyectoId) {
        List<ReporteNomina> nominas = nominaRepository.findByProyectoId(proyectoId);
        int cantidad = (nominas != null) ? nominas.size() : 0;
        System.out.println("DEBUG - Proyecto " + proyectoId + " total nóminas (todas): " + cantidad);
        return cantidad;
    }

    /**
     * @deprecated Usar contarTotalNominasCompletasProyecto en su lugar
     */
    @SuppressWarnings("unused")
    private boolean tieneAlMenosUnaNomina(Long proyectoId) {
        List<ReporteNomina> nominas = nominaRepository.findByProyectoId(proyectoId);
        boolean tieneNominas = nominas != null && !nominas.isEmpty();
        System.out.println("DEBUG - Proyecto " + proyectoId + " tiene nóminas: " + tieneNominas +
                " (cantidad: " + (nominas != null ? nominas.size() : 0) + ")");
        return tieneNominas;
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

        System.out.println("========== VALIDACIÓN AVANCE ==========");
        System.out.println("DEBUG AVANCE - Proyecto ID: " + proyectoId);

        // Ya no necesitamos validación base genérica, la validación detallada
        // por periodo calculará exactamente cuántas nóminas faltan

        // Verificar que el periodo solicitado no sea futuro
        PeriodoAcademico periodoActual = obtenerPeriodoActual();
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
        PeriodoAcademico primerPeriodoProyecto = obtenerPeriodoDeFecha(fechaInicio);

        // 1. Validar periodos anteriores al seleccionado (completos)
        PeriodoAcademico periodo = primerPeriodoProyecto;
        while (periodo.compareTo(periodoSolicitado) < 0) {
            int nominasEncontradas = contarNominasPeriodo(proyectoId, periodo, fechaInicio, primerPeriodoProyecto);
            int nominasEsperadas = calcularNominasEsperadas(periodo, fechaInicio, primerPeriodoProyecto);

            if (nominasEncontradas < nominasEsperadas) {
                periodosFaltantes.add(
                        periodo.getCodigo() + " (registradas: " + nominasEncontradas + "/" + nominasEsperadas + ")");
            }

            periodo = periodo.siguiente();
        }

        // 2. Validar que el periodo seleccionado tenga TODAS sus nóminas completas
        int nominasPeriodoSeleccionado = contarNominasPeriodo(proyectoId, periodoSolicitado, fechaInicio,
                primerPeriodoProyecto);
        int nominasEsperadasPeriodoSeleccionado = calcularNominasEsperadas(periodoSolicitado, fechaInicio,
                primerPeriodoProyecto);

        if (nominasPeriodoSeleccionado < nominasEsperadasPeriodoSeleccionado) {
            periodosFaltantes.add(periodoSolicitado.getCodigo() + " (registradas: " + nominasPeriodoSeleccionado + "/"
                    + nominasEsperadasPeriodoSeleccionado + ")");
        }

        // Si hay periodos con nóminas faltantes, construir mensaje detallado
        if (!periodosFaltantes.isEmpty()) {
            resultado.setPuedeCargar(false);
            StringBuilder mensaje = new StringBuilder();
            mensaje.append("No puede cargar el Avance para el periodo ").append(periodoSolicitado.getCodigo())
                    .append(".\n\n");
            mensaje.append("Le faltan registrar reportes de nómina confirmados en los siguientes periodos:\n\n");
            for (String periodoFaltante : periodosFaltantes) {
                mensaje.append("• ").append(periodoFaltante).append("\n");
            }
            mensaje.append("\nNota: Solo cuentan las nóminas con estado 'Completo', no las pendientes.");
            resultado.setMensaje(mensaje.toString());
            return resultado;
        }

        resultado.setPuedeCargar(true);
        resultado.setMensaje("Todas las nóminas requeridas están completas. Puede cargar el Avance del periodo "
                + periodoSolicitado.getCodigo());
        return resultado;
    }

    /**
     * Cuenta cuántas nóminas existen para un proyecto en un periodo académico
     * específico
     * Valida mes por mes dentro del periodo, considerando la fecha de inicio del
     * proyecto
     */
    private int contarNominasPeriodo(Long proyectoId, PeriodoAcademico periodo, LocalDate fechaInicioProyecto,
            PeriodoAcademico primerPeriodoProyecto) {
        int count = 0;
        LocalDate fecha = periodo.getFechaInicio();

        // Si es el primer periodo del proyecto, empezar desde la fecha de inicio del
        // proyecto
        if (periodo.compareTo(primerPeriodoProyecto) == 0 && fechaInicioProyecto.isAfter(fecha)) {
            fecha = fechaInicioProyecto;
        }

        LocalDate fechaFin = periodo.getFechaFin();

        System.out.println("DEBUG - Contando nóminas COMPLETAS para periodo: " + periodo.getCodigo());
        System.out.println("DEBUG - Fecha inicio validación: " + fecha + ", Fecha fin: " + fechaFin);

        // Iterar mes por mes desde la fecha calculada hasta el fin del periodo
        while (!fecha.isAfter(fechaFin)) {
            int mes = fecha.getMonthValue();
            int anio = fecha.getYear();

            System.out.println("DEBUG - Buscando nómina COMPLETA: mes=" + mes + ", año=" + anio);

            // Buscar solo nóminas con estado COMPLETO (no pendientes)
            Optional<ReporteNomina> nominaOpt = nominaRepository.buscarNominaCompletaPorProyectoMesAnio(
                    proyectoId,
                    mes,
                    anio);

            if (nominaOpt.isPresent()) {
                count++;
                System.out.println("DEBUG - Nómina COMPLETA encontrada para mes " + mes + "/" + anio);
            } else {
                System.out.println("DEBUG - NO encontrada nómina COMPLETA para mes " + mes + "/" + anio
                        + " (puede estar pendiente o no existir)");
            }

            fecha = fecha.plusMonths(1);
        }

        System.out.println("DEBUG - Total nóminas COMPLETAS encontradas en " + periodo.getCodigo() + ": " + count);
        return count;
    }

    /**
     * Calcula cuántas nóminas se esperan en un periodo según la fecha de inicio del
     * proyecto
     * Si es el primer periodo, solo se cuentan los meses desde el inicio del
     * proyecto
     * Si es un periodo posterior, se esperan 6 meses completos
     */
    private int calcularNominasEsperadas(PeriodoAcademico periodo, LocalDate fechaInicioProyecto,
            PeriodoAcademico primerPeriodoProyecto) {
        // Si no es el primer periodo del proyecto, siempre se esperan 6 meses
        if (periodo.compareTo(primerPeriodoProyecto) != 0) {
            return 6;
        }

        // Es el primer periodo: contar meses desde inicio proyecto hasta fin de periodo
        LocalDate inicioConteo = fechaInicioProyecto;
        LocalDate finPeriodo = periodo.getFechaFin();

        int mesesEsperados = 0;
        LocalDate fecha = inicioConteo;
        while (!fecha.isAfter(finPeriodo)) {
            mesesEsperados++;
            fecha = fecha.plusMonths(1);
        }

        System.out.println("DEBUG - Nóminas esperadas para periodo " + periodo.getCodigo() + ": " + mesesEsperados);
        return mesesEsperados;
    }

    /**
     * Calcula cuántas nóminas se esperan desde el inicio del proyecto hasta el mes
     * ANTERIOR al actual
     * (no se puede exigir la nómina del mes en curso porque aún no ha terminado)
     * Esto se usa para el primer periodo del proyecto
     */
    private int calcularNominasEsperadasHastaHoy(PeriodoAcademico periodo, LocalDate fechaInicioProyecto,
            PeriodoAcademico primerPeriodoProyecto) {
        LocalDate fechaActual = LocalDate.now();
        // El mes anterior al actual (no se puede exigir nómina del mes en curso)
        LocalDate fechaLimite = fechaActual.minusMonths(1);

        // Si la fecha límite es anterior al inicio del proyecto, no se esperan nóminas
        if (fechaLimite.isBefore(fechaInicioProyecto)) {
            System.out.println("DEBUG - Fecha límite " + fechaLimite + " es anterior al inicio del proyecto "
                    + fechaInicioProyecto);
            return 0;
        }

        // Contar meses desde inicio proyecto hasta el mes anterior al actual
        // pero sin exceder el fin del periodo
        LocalDate finPeriodo = periodo.getFechaFin();
        LocalDate fechaFinConteo = fechaLimite.isBefore(finPeriodo) ? fechaLimite : finPeriodo;

        int mesesEsperados = 0;
        LocalDate fecha = fechaInicioProyecto;
        while (!fecha.isAfter(fechaFinConteo)) {
            mesesEsperados++;
            fecha = fecha.plusMonths(1);
        }

        System.out.println("DEBUG - Nóminas esperadas hasta hoy para periodo " + periodo.getCodigo() +
                " (desde " + fechaInicioProyecto + " hasta " + fechaFinConteo + "): " + mesesEsperados);
        return mesesEsperados;
    }

    /**
     * Guardar documento PDF subido por el director de proyecto
     * 
     * @param archivo          Archivo PDF subido
     * @param proyectoId       ID del proyecto
     * @param periodoAcademico Código del periodo académico (ej: "2025A")
     * @param tipoDocumento    Tipo: "PLANIFICACION" o "AVANCE"
     * @return Documento guardado en BD
     * @throws IOException
     */
    public Documento guardarDocumento(MultipartFile archivo, Long proyectoId, String periodoAcademico,
            String tipoDocumento) throws IOException {

        // 1. Validar que el proyecto existe
        Proyecto proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // 2. Crear directorio si no existe
        String directorioBase = "uploads/documentos";
        Path directorioPath = Paths.get(directorioBase);
        if (!Files.exists(directorioPath)) {
            Files.createDirectories(directorioPath);
        }

        // 3. Generar nombre único para el archivo
        String nombreOriginal = archivo.getOriginalFilename();
        String extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        String nombreUnico = UUID.randomUUID().toString() + extension;

        // 4. Guardar archivo físico
        Path rutaArchivo = directorioPath.resolve(nombreUnico);
        Files.copy(archivo.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

        // 5. Crear instancia del tipo correcto de documento
        Documento documento;

        // EN EL MÉTODO guardarDocumento:

        if ("PLANIFICACION".equals(tipoDocumento)) {
            PlanificacionProyecto planificacion = new PlanificacionProyecto();
            planificacion.setPeriodoAcademico(periodoAcademico);
            documento = planificacion;
        } else if ("AVANCE".equals(tipoDocumento)) {
            AvanceProyecto avance = new AvanceProyecto();
            avance.setPeriodoAcademico(periodoAcademico);
            // avance.setAprobado(false); <-- ¡BORRA ESTA LÍNEA TAMBIÉN!
            documento = avance;
        } else {
            throw new RuntimeException("Tipo de documento no válido: " + tipoDocumento);
        }

        // 6. Establecer propiedades comunes
        // IMPORTANTE: Guardamos el nombre único (UUID) para poder encontrar el archivo
        // pero mantenemos referencia al nombre original para mostrar al usuario
        documento.setNombre(nombreUnico); // Nombre del archivo físico (UUID)
        documento.setFormato("pdf");
        documento.setRutaAlmacenamiento(directorioBase + "|" + nombreOriginal); // ruta|nombreOriginal
        documento.setFecha(LocalDateTime.now());
        documento.setProyecto(proyecto);
        documento.setTipo(tipoDocumento); // Asegurar que el tipo se establece explícitamente

        // 7. Guardar en base de datos
        Documento documentoGuardado = documentoRepository.save(documento);

        System.out.println("Documento guardado: " + documentoGuardado.getNombre() +
                " (ID: " + documentoGuardado.getIdDocumento() + ") - Proyecto: " + proyecto.getNombre() +
                " - Periodo: " + periodoAcademico + " - Estado: Por aprobar");

        return documentoGuardado;
    }

    /**
     * Obtiene todos los documentos de un proyecto específico
     * 
     * @param proyectoId ID del proyecto
     * @return Lista de documentos del proyecto
     */
    public List<Documento> obtenerDocumentosDeProyecto(Long proyectoId) {
        // Verificar que el proyecto existe
        Optional<Proyecto> proyectoOpt = proyectoRepository.findById(proyectoId);
        if (!proyectoOpt.isPresent()) {
            throw new RuntimeException("Proyecto no encontrado con ID: " + proyectoId);
        }

        // Buscar todos los documentos del proyecto
        List<Documento> documentos = documentoRepository.findByProyectoId(proyectoId);

        // Asegurar que el campo 'tipo' esté correctamente establecido para cada
        // documento
        for (Documento doc : documentos) {
            if (doc.getTipo() == null || doc.getTipo().isEmpty()) {
                if (doc instanceof PlanificacionProyecto) {
                    doc.setTipo("PLANIFICACION");
                } else if (doc instanceof AvanceProyecto) {
                    doc.setTipo("AVANCE");
                }
            }
        }

        System.out.println("Documentos encontrados para proyecto " + proyectoId + ": " + documentos.size());

        return documentos;
    }

    /**
     * Obtener un documento por su ID
     */
    public Documento obtenerDocumentoPorId(Long idDocumento) {
        return documentoRepository.findById(idDocumento)
                .orElseThrow(() -> new RuntimeException("Documento no encontrado con ID: " + idDocumento));
    }

    /**
     * Guardar un documento actualizado (para aprobación/rechazo)
     */
    public void guardarDocumentoActualizado(Documento documento) {
        documentoRepository.save(documento);
    }

    // Método para la Jefa: Busca documentos pendientes de TODOS los proyectos
    public List<Documento> obtenerTodosLosPendientes() {
        return documentoRepository.findAll().stream()
                .filter(doc -> {
                    if (doc instanceof PlanificacionProyecto) {
                        return ((PlanificacionProyecto) doc).getAprobado() == null;
                    } else if (doc instanceof AvanceProyecto) {
                        return ((AvanceProyecto) doc).getAprobado() == null;
                    }
                    return false;
                })
                .peek(doc -> {
                    // Asegurar que el campo 'tipo' esté correctamente establecido
                    if (doc.getTipo() == null || doc.getTipo().isEmpty()) {
                        if (doc instanceof PlanificacionProyecto) {
                            doc.setTipo("PLANIFICACION");
                        } else if (doc instanceof AvanceProyecto) {
                            doc.setTipo("AVANCE");
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Obtener documentos respondidos (aprobados y rechazados) con filtros
     * opcionales
     * Para la Jefa de Departamento - Consultar Respuesta Documentos
     * 
     * @param proyectoId Filtro opcional por proyecto
     * @param periodo    Filtro opcional por periodo académico
     * @return Lista de documentos que han sido aprobados o rechazados
     */
    public List<Documento> obtenerDocumentosRespondidos(Long proyectoId, String periodo) {
        List<Documento> documentos;

        // Si hay filtro de proyecto, obtener solo de ese proyecto
        if (proyectoId != null) {
            documentos = documentoRepository.findByProyectoId(proyectoId);
        } else {
            documentos = documentoRepository.findAll();
        }

        return documentos.stream()
                // Filtrar solo documentos respondidos (aprobado != null)
                .filter(doc -> {
                    Boolean aprobado = null;
                    if (doc instanceof PlanificacionProyecto) {
                        aprobado = ((PlanificacionProyecto) doc).getAprobado();
                    } else if (doc instanceof AvanceProyecto) {
                        aprobado = ((AvanceProyecto) doc).getAprobado();
                    }
                    return aprobado != null; // Solo documentos que tienen respuesta
                })
                // Corregir fechas faltantes para rechazados y guardar
                .peek(doc -> {
                    boolean necesitaGuardar = false;
                    if (doc instanceof PlanificacionProyecto) {
                        PlanificacionProyecto plan = (PlanificacionProyecto) doc;
                        if (plan.getAprobado() != null && !plan.getAprobado() && plan.getFechaAprobacion() == null) {
                            plan.setFechaAprobacion(LocalDateTime.now());
                            necesitaGuardar = true;
                        }
                    } else if (doc instanceof AvanceProyecto) {
                        AvanceProyecto avance = (AvanceProyecto) doc;
                        if (avance.getAprobado() != null && !avance.getAprobado()
                                && avance.getFechaAprobacion() == null) {
                            avance.setFechaAprobacion(LocalDateTime.now());
                            necesitaGuardar = true;
                        }
                    }
                    if (necesitaGuardar) {
                        documentoRepository.save(doc);
                    }
                })
                // Filtrar por periodo si se especifica
                .filter(doc -> {
                    if (periodo == null || periodo.isEmpty()) {
                        return true;
                    }
                    String periodoDoc = null;
                    if (doc instanceof PlanificacionProyecto) {
                        periodoDoc = ((PlanificacionProyecto) doc).getPeriodoAcademico();
                    } else if (doc instanceof AvanceProyecto) {
                        periodoDoc = ((AvanceProyecto) doc).getPeriodoAcademico();
                    }
                    return periodo.equals(periodoDoc);
                })
                .peek(doc -> {
                    // Asegurar que el campo 'tipo' esté correctamente establecido
                    if (doc.getTipo() == null || doc.getTipo().isEmpty()) {
                        if (doc instanceof PlanificacionProyecto) {
                            doc.setTipo("PLANIFICACION");
                        } else if (doc instanceof AvanceProyecto) {
                            doc.setTipo("AVANCE");
                        }
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * Corrige las fechas de documentos rechazados que no tienen fecha de respuesta
     * (para datos históricos que fueron rechazados antes de la corrección del
     * código)
     * 
     * @return número de documentos corregidos
     */
    public int corregirFechasDocumentosRechazados() {
        List<Documento> todosDocumentos = documentoRepository.findAll();
        int corregidos = 0;

        for (Documento doc : todosDocumentos) {
            boolean modificado = false;

            if (doc instanceof PlanificacionProyecto) {
                PlanificacionProyecto plan = (PlanificacionProyecto) doc;
                // Si está rechazado (aprobado = false) pero no tiene fecha
                if (plan.getAprobado() != null && !plan.getAprobado() && plan.getFechaAprobacion() == null) {
                    plan.setFechaAprobacion(java.time.LocalDateTime.now());
                    modificado = true;
                }
            } else if (doc instanceof AvanceProyecto) {
                AvanceProyecto avance = (AvanceProyecto) doc;
                // Si está rechazado (aprobado = false) pero no tiene fecha
                if (avance.getAprobado() != null && !avance.getAprobado() && avance.getFechaAprobacion() == null) {
                    avance.setFechaAprobacion(java.time.LocalDateTime.now());
                    modificado = true;
                }
            }

            if (modificado) {
                documentoRepository.save(doc);
                corregidos++;
            }
        }

        System.out.println("Documentos rechazados corregidos: " + corregidos);
        return corregidos;
    }

    /**
     * Calcula el periodo académico correspondiente a una fecha específica
     */
    private static PeriodoAcademico obtenerPeriodoDeFecha(LocalDate fecha) {
        int mes = fecha.getMonthValue();
        int anio = fecha.getYear();

        // Periodo A: Marzo (3) - Agosto (8)
        if (mes >= 3 && mes <= 8) {
            return new PeriodoAcademico(anio, 'A');
        }
        // Periodo B: Septiembre (9) - Diciembre (12)
        else if (mes >= 9 && mes <= 12) {
            return new PeriodoAcademico(anio, 'B');
        }
        // Periodo B del año anterior: Enero (1) - Febrero (2)
        else {
            return new PeriodoAcademico(anio - 1, 'B');
        }
    }

    /**
     * Obtiene el periodo académico actual (basado en la fecha de hoy)
     */
    private static PeriodoAcademico obtenerPeriodoActual() {
        return obtenerPeriodoDeFecha(LocalDate.now());
    }

    /**
     * Parsea un código de periodo (ej: "2025A") a un objeto PeriodoAcademico
     */
    private static PeriodoAcademico parsearCodigo(String codigo) {
        if (codigo == null || codigo.length() < 5) {
            throw new IllegalArgumentException("Código de periodo inválido: " + codigo);
        }

        try {
            int anio = Integer.parseInt(codigo.substring(0, 4));
            char ciclo = codigo.charAt(4);
            return new PeriodoAcademico(anio, ciclo);
        } catch (Exception e) {
            throw new IllegalArgumentException("Código de periodo inválido: " + codigo, e);
        }
    }
}
