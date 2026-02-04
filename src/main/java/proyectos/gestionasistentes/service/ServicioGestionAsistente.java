package proyectos.gestionasistentes.service;

import proyectos.gestionasistentes.dto.NominaRequestDTO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import proyectos.gestionasistentes.model.ReporteNomina;
import proyectos.gestionasistentes.repository.NominaRepository;
import jakarta.transaction.Transactional;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import proyectos.gestionproyectos.model.Asistente;
import proyectos.gestionproyectos.model.AyudanteInvestigacion;
import proyectos.gestionproyectos.model.IntegranteProyecto;
import proyectos.gestionproyectos.model.Proyecto;
import proyectos.gestionproyectos.model.TecnicoInvestigacion;
import proyectos.gestionproyectos.repository.IntegranteRepository;
import proyectos.gestionproyectos.repository.ProyectoRepository;

@Service
public class ServicioGestionAsistente {

    private static final Logger logger = LoggerFactory.getLogger(ServicioGestionAsistente.class);

    @Autowired
    private IntegranteRepository integranteRepository;

    @Autowired
    private NominaRepository nominaRepository;

    @Autowired
    private ProyectoRepository proyectoRepository;

    /**
     * Crea una instancia del tipo de integrante correcto según el tipo especificado
     */
    public IntegranteProyecto crearIntegranteSegunTipo(String tipo) {
        if (tipo == null || tipo.isEmpty()) {
            return new Asistente(); // Por defecto
        }

        switch (tipo.toUpperCase()) {
            case "AYUDANTE":
                return new AyudanteInvestigacion();
            case "TECNICO":
                return new TecnicoInvestigacion();
            case "ASISTENTE":
            default:
                return new Asistente();
        }
    }

    // +registrarAsistenteAProyecto()
    public IntegranteProyecto registrarAsistenteAProyecto(Long proyectoId, IntegranteProyecto integrante) {
        // Buscamos el proyecto
        var proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        integrante.setProyecto(proyecto);
        integrante.activar();

        return integranteRepository.save(integrante);
    }

    // +darDeBajaAsistente() - Ahora funciona con cualquier tipo de integrante
    @Transactional
    public IntegranteProyecto darDeBajaAsistente(Long idAsistente) {
        logger.info("Intentando dar de baja integrante id={}", idAsistente);

        IntegranteProyecto integrante = integranteRepository.findById(idAsistente)
                .orElseThrow(() -> new IllegalArgumentException("Integrante no encontrado"));

        integrante.desactivar();
        IntegranteProyecto guardado = integranteRepository.save(integrante);

        logger.info("Integrante id={} marcado como {}", guardado.getId(), guardado.getEstado());
        return guardado;
    }

    // +confirmarActualizacionNomina() - Procesa el reporte mensual
    public ReporteNomina confirmarActualizacionNomina(Long proyectoId, Integer mes, Integer anio,
            List<Long> idsAsistentes, String actualizadoPor) {
        if (mes == null || anio == null)
            throw new IllegalArgumentException("Mes y año obligatorios");

        // Buscamos si ya existe un reporte para ese proyecto en ese mes/año
        ReporteNomina reporte = nominaRepository.buscarPorProyectoMesAnio(proyectoId, mes, anio)
                .orElse(new ReporteNomina());

        reporte.setMes(mes);
        reporte.setAnio(anio);
        reporte.setFechaRegistro(LocalDate.now());

        // El proyecto es clave para filtrar reportes por director
        var proyecto = proyectoRepository.findById(proyectoId).get();
        reporte.setProyecto(proyecto);

        // Registrar quién actualizó la nómina (correo)
        reporte.setActualizadoPor(actualizadoPor);

        // Si la lista de IDs está vacía, se guarda como reporte "sin contrataciones"
        if (idsAsistentes != null && !idsAsistentes.isEmpty()) {
            List<IntegranteProyecto> integrantes = idsAsistentes.stream()
                    .map(id -> integranteRepository.findById(id)
                            .orElseThrow(() -> new RuntimeException("Integrante no encontrado")))
                    .toList();
            reporte.setListaIntegrantes(integrantes);
        }

        reporte.setEstado("COMPLETO"); // Se marca como actualizado satisfactoriamente
        return nominaRepository.save(reporte);
    }

    // +obtenerMesesPendientes() - Lógica para reportes no realizados
    public List<String> obtenerMesesPendientes(Long proyectoId) {
        var proyecto = proyectoRepository.findById(proyectoId).get();
        LocalDate fin = LocalDate.parse(proyecto.getFechaFin());
        LocalDate hoy = LocalDate.now();

        // Determinamos hasta qué fecha evaluar (hoy o fin de proyecto)
        LocalDate limite = hoy.isBefore(fin) ? hoy : fin;

        // Aquí se compararía la secuencia de meses vs lo que existe en nominaRepository
        // Por brevedad, se retorna la lógica: si no hay reporte en tabla, es PENDIENTE
        return List.of("Mes Evaluado: " + limite.getMonth());
    }

    private Asistente obtenerAsistente(Long idAsistente) {
        return integranteRepository.findById(idAsistente)
                .map(i -> (Asistente) i)
                .orElseThrow(() -> new RuntimeException("Asistente no encontrado"));
    }

    public ReporteNomina procesarNominaCompleta(NominaRequestDTO request) {
        try {
            logger.info("===== INICIO PROCESAMIENTO DE NÓMINA =====");

            // VALIDACIONES BÁSICAS
            if (request == null || request.getProyectoId() == null) {
                throw new RuntimeException("Request o ID de proyecto nulo");
            }
            if (request.getMes() == null || request.getAnio() == null) {
                throw new RuntimeException("Mes y año son obligatorios");
            }

            logger.info("Paso 1: Buscando proyecto ID={}", request.getProyectoId());
            var proyecto = proyectoRepository.findById(request.getProyectoId())
                    .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));
            logger.info("Proyecto encontrado: {}", proyecto.getNombre());

            // PROCESAR ASISTENTES
            logger.info("Paso 2: Procesando {} asistentes",
                    request.getAsistentes() != null ? request.getAsistentes().size() : 0);

            List<IntegranteProyecto> integrantesFinales = new ArrayList<>();

            if (request.getAsistentes() != null && !request.getAsistentes().isEmpty()) {
                for (NominaRequestDTO.AsistenteDTO dto : request.getAsistentes()) {
                    try {
                        if (dto.getId() == null) {
                            // Nuevo integrante - crear según tipo
                            IntegranteProyecto nuevo = crearIntegranteSegunTipo(dto.getTipoIntegrante());
                            nuevo.setNombre(dto.getNombre());
                            nuevo.setCedula(dto.getCedula());
                            nuevo.setProyecto(proyecto);
                            nuevo.activar();

                            if ("FUERA_NOMINA".equals(dto.getEstado())) {
                                nuevo.desactivar();
                            }

                            // Guardar fecha de nacimiento si está presente
                            if (dto.getFechaNacimiento() != null && !dto.getFechaNacimiento().isEmpty()) {
                                nuevo.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
                            }

                            IntegranteProyecto guardado = integranteRepository.save(nuevo);

                            if (guardado.estaActivo()) {
                                integrantesFinales.add(guardado);
                                logger.info("  Nuevo integrante guardado y ACTIVO: {} (ID={}) tipo={} estado={} ",
                                        guardado.getNombre(), guardado.getId(), guardado.getClass().getSimpleName(),
                                        guardado.getEstado());
                            } else {
                                logger.info(
                                        "  Nuevo integrante guardado como FUERA_NOMINA: {} (ID={}) tipo={} estado={} ",
                                        guardado.getNombre(), guardado.getId(), guardado.getClass().getSimpleName(),
                                        guardado.getEstado());
                            }
                        } else {
                            // Integrante existente (cualquier tipo)
                            IntegranteProyecto existente = integranteRepository.findById(dto.getId())
                                    .orElseThrow(() -> new RuntimeException("Integrante no encontrado"));

                            if ("FUERA_NOMINA".equals(dto.getEstado())) {
                                existente.desactivar();
                            } else {
                                existente.activar();
                            }

                            IntegranteProyecto guardado = integranteRepository.save(existente);

                            if (guardado.estaActivo()) {
                                integrantesFinales.add(guardado);
                                logger.info("  Integrante actualizado y ACTIVO: {} (ID={}) tipo={} estado={} ",
                                        guardado.getNombre(), guardado.getId(), guardado.getClass().getSimpleName(),
                                        guardado.getEstado());
                            } else {
                                logger.info(
                                        "  Integrante marcado FUERA_NOMINA y excluido de la nómina: {} (ID={}) tipo={} estado={} ",
                                        guardado.getNombre(), guardado.getId(), guardado.getClass().getSimpleName(),
                                        guardado.getEstado());
                            }
                        }
                    } catch (RuntimeException e) {
                        logger.warn("Error procesando asistente: {}", e.getMessage());
                    }
                }
            }

            logger.info("Paso 3: Guardando reporte con {} integrantes", integrantesFinales.size());

            // BUSCAR O CREAR REPORTE
            ReporteNomina reporte = nominaRepository
                    .buscarPorProyectoMesAnio(request.getProyectoId(), request.getMes(), request.getAnio())
                    .orElse(new ReporteNomina());

            // CONFIGURAR Y GUARDAR
            reporte.setProyecto(proyecto);
            reporte.setMes(request.getMes());
            reporte.setAnio(request.getAnio());
            reporte.setFechaRegistro(LocalDate.now());
            reporte.setEstado("COMPLETO");

            // Si el request incluye quien actualizó, persistirlo
            try {
                java.lang.reflect.Method m = request.getClass().getMethod("getActualizadoPor");
                if (m != null) {
                    Object val = m.invoke(request);
                    if (val instanceof String) {
                        reporte.setActualizadoPor((String) val);
                    }
                }
            } catch (NoSuchMethodException ignored) {
            }

            logger.info("Reporte lista de integrantes ANTES: {}",
                    reporte.getListaIntegrantes() != null ? reporte.getListaIntegrantes().size() : "null");

            // Crear nueva lista con los integrantes finales
            List<IntegranteProyecto> nuevaLista = new ArrayList<>(integrantesFinales);
            reporte.setListaIntegrantes(nuevaLista);

            logger.info("Reporte lista de integrantes DESPUÉS: {}", reporte.getListaIntegrantes().size());
            for (IntegranteProyecto i : integrantesFinales) {
                logger.info("  - Integrante en lista final: {} (ID={}) tipo={}",
                        i.getNombre(), i.getId(), i.getClass().getSimpleName());
            }

            ReporteNomina guardado = nominaRepository.save(reporte);

            logger.info("Reporte guardado, lista final contiene: {}",
                    guardado.getListaIntegrantes() != null ? guardado.getListaIntegrantes().size() : 0);
            for (IntegranteProyecto i : guardado.getListaIntegrantes()) {
                logger.info("  - Guardado: {} (ID={}) tipo={}",
                        i.getNombre(), i.getId(), i.getClass().getSimpleName());
            }
            logger.info("===== NÓMINA PROCESADA EXITOSAMENTE. ID={} =====", guardado.getIdReporte());
            logger.info("Integrantes guardados en BD: {}", guardado.getListaIntegrantes().size());

            return guardado;
        } catch (Exception e) {
            logger.error("ERROR en procesarNominaCompleta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar la nómina: " + e.getMessage(), e);
        }
    }

    // Obtener TODOS los integrantes activos del proyecto (no del reporte)
    public List<IntegranteProyecto> obtenerIntegrantesActivosPorProyecto(Long proyectoId) {
        logger.info("Obteniendo integrantes activos del proyecto ID: {}", proyectoId);
        List<IntegranteProyecto> integrantes = integranteRepository.obtenerIntegrantesActivosPorProyecto(proyectoId);
        logger.info("Se encontraron {} integrantes activos en el proyecto", integrantes.size());
        for (IntegranteProyecto i : integrantes) {
            logger.info("  - {} (ID={}) tipo={}", i.getNombre(), i.getId(), i.getClass().getSimpleName());
        }
        return integrantes;
    }

    // Mantener método antiguo para compatibilidad
    public List<Asistente> obtenerAsistentesActivosPorProyecto(Long proyectoId) {
        logger.info("Obteniendo asistentes activos del proyecto ID: {}", proyectoId);
        List<Asistente> asistentes = integranteRepository.obtenerAsistentesActivosPorProyecto(proyectoId);
        logger.info("Se encontraron {} asistentes activos en el proyecto", asistentes.size());
        return asistentes;
    }

    // Obtener todos los reportes de nómina de un proyecto
    public List<ReporteNomina> obtenerReportesProyecto(Long proyectoId) {
        return nominaRepository.obtenerReportesProyecto(proyectoId);
    }

    /**
     * Genera en BD los reportes pendientes (estado PENDIENTE) para cada mes desde
     * fechaInicio del proyecto hasta min(fechaFin, hoy), sin duplicar los ya
     * existentes.
     */
    @Transactional
    public void generarPendientesProyecto(Proyecto proyecto) {
        if (proyecto == null)
            return;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        YearMonth inicio = parseYearMonthSafe(proyecto.getFechaInicio(), formatter);
        YearMonth fin = parseYearMonthSafe(proyecto.getFechaFin(), formatter);
        YearMonth limite = YearMonth.now().isBefore(fin) ? YearMonth.now() : fin;

        YearMonth actual = inicio;
        while (!actual.isAfter(limite)) {
            int mes = actual.getMonthValue();
            int anio = actual.getYear();

            boolean existe = nominaRepository.buscarPorProyectoMesAnio(proyecto.getId(), mes, anio).isPresent();
            if (!existe) {
                ReporteNomina pendiente = new ReporteNomina();
                pendiente.setProyecto(proyecto);
                pendiente.setMes(mes);
                pendiente.setAnio(anio);
                pendiente.setEstado("PENDIENTE");
                pendiente.setFechaRegistro(null);
                pendiente.setListaIntegrantes(new ArrayList<>());
                nominaRepository.save(pendiente);
                logger.info("Creada nómina pendiente en BD para proyecto={} mes={} anio={}", proyecto.getId(), mes,
                        anio);
            }

            actual = actual.plusMonths(1);
        }
    }

    private YearMonth parseYearMonthSafe(String dateStr, DateTimeFormatter formatter) {
        try {
            if (dateStr == null || dateStr.isBlank()) {
                return YearMonth.now();
            }
            return YearMonth.parse(dateStr, formatter);
        } catch (Exception ex) {
            return YearMonth.now();
        }
    }

    // Obtener nóminas de un proyecto, opcionalmente filtradas por quien las
    // actualizó
    public List<ReporteNomina> obtenerNominasPorProyecto(Long proyectoId, String actualizadoPor) {
        if (actualizadoPor == null || actualizadoPor.isBlank()) {
            return nominaRepository.findByProyectoId(proyectoId);
        }
        return nominaRepository.findByProyectoIdAndActualizadoPor(proyectoId, actualizadoPor);
    }

    // Obtener todas las nóminas
    public List<ReporteNomina> obtenerTodasLasNominas() {
        return nominaRepository.findAll();
    }

    // Obtener nóminas por proyecto
    public List<ReporteNomina> obtenerNominasPorProyecto(Long proyectoId) {
        return nominaRepository.findByProyectoId(proyectoId);
    }
}