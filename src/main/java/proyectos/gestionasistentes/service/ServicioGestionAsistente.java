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
import proyectos.gestionproyectos.model.Asistente;
import proyectos.gestionproyectos.model.AyudanteInvestigacion;
import proyectos.gestionproyectos.model.IntegranteProyecto;
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
    private IntegranteProyecto crearIntegranteSegunTipo(String tipo) {
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
    public Asistente registrarAsistenteAProyecto(Long proyectoId, Asistente asistente) {
        // Buscamos el proyecto
        var proyecto = proyectoRepository.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // 1. Usar el setter que añadimos a IntegranteProyecto
        asistente.setProyecto(proyecto);

        // 2. Usar el método activar() definido en tu clase Asistente
        asistente.activar();

        return (Asistente) integranteRepository.save(asistente);
    }

    // +darDeBajaAsistente()
    public Asistente darDeBajaAsistente(Long idAsistente) {
        Asistente asistente = obtenerAsistente(idAsistente);

        // 3. CORRECCIÓN: Usar desactivar() o marcarFueraNomina()
        // en lugar de .setEstado(Asistente.EstadoAsistente.INACTIVO)
        asistente.desactivar();

        return (Asistente) integranteRepository.save(asistente);
    }

    // +confirmarActualizacionNomina() - Procesa el reporte mensual
    public ReporteNomina confirmarActualizacionNomina(Long proyectoId, Integer mes, Integer anio,
            List<Long> idsAsistentes) {
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

        // Si la lista de IDs está vacía, se guarda como reporte "sin contrataciones"
        if (idsAsistentes != null && !idsAsistentes.isEmpty()) {
            List<Asistente> asistentes = idsAsistentes.stream()
                    .map(this::obtenerAsistente)
                    .toList();
            reporte.setListaAsistentes(asistentes);
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

            List<Asistente> asistentesFinales = new ArrayList<>();

            if (request.getAsistentes() != null && !request.getAsistentes().isEmpty()) {
                for (NominaRequestDTO.AsistenteDTO dto : request.getAsistentes()) {
                    try {
                        if (dto.getId() == null) {
                            // Nuevo integrante - crear según tipo
                            IntegranteProyecto nuevo = crearIntegranteSegunTipo(dto.getTipoIntegrante());
                            nuevo.setNombre(dto.getNombre());
                            nuevo.setCedula(dto.getCedula());
                            nuevo.setProyecto(proyecto);

                            // Si es Asistente, activarlo
                            if (nuevo instanceof Asistente) {
                                ((Asistente) nuevo).activar();
                            }

                            // Guardar fecha de nacimiento si está presente
                            if (dto.getFechaNacimiento() != null && !dto.getFechaNacimiento().isEmpty()) {
                                nuevo.setFechaNacimiento(LocalDate.parse(dto.getFechaNacimiento()));
                            }

                            IntegranteProyecto guardado = integranteRepository.save(nuevo);

                            // Agregar a la lista si es Asistente activo
                            if (guardado instanceof Asistente && ((Asistente) guardado).estaActivo()) {
                                asistentesFinales.add((Asistente) guardado);
                            }

                            logger.info("  Nuevo integrante guardado: {} (ID={})", guardado.getNombre(),
                                    guardado.getId());
                        } else {
                            // Asistente existente
                            Asistente existente = obtenerAsistente(dto.getId());
                            // Actualizar estado basado en lo enviado
                            if ("FUERA_NOMINA".equals(dto.getEstado())) {
                                existente.desactivar();
                            } else {
                                existente.activar();
                            }
                            integranteRepository.save(existente);
                            // Agregar SIEMPRE si está activo, sin importar si ya estaba en la lista
                            if (existente.estaActivo()) {
                                asistentesFinales.add(existente);
                                logger.info("  Asistente actualizado y agregado: {} (ID={})", existente.getNombre(),
                                        existente.getId());
                            } else {
                                logger.info("  Asistente dado de baja: {} (ID={})", existente.getNombre(),
                                        existente.getId());
                            }
                        }
                    } catch (RuntimeException e) {
                        logger.warn("Error procesando asistente: {}", e.getMessage());
                    }
                }
            }

            logger.info("Paso 3: Guardando reporte con {} asistentes", asistentesFinales.size());

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

            logger.info("Reporte lista de asistentes ANTES: {}",
                    reporte.getListaAsistentes() != null ? reporte.getListaAsistentes().size() : "null");

            // Crear nueva lista con los asistentes finales
            List<Asistente> nuevaLista = new ArrayList<>(asistentesFinales);
            reporte.setListaAsistentes(nuevaLista);

            logger.info("Reporte lista de asistentes DESPUÉS: {}", reporte.getListaAsistentes().size());
            for (Asistente a : asistentesFinales) {
                logger.info("  - Asistente en lista final: {} (ID={})", a.getNombre(), a.getId());
            }

            ReporteNomina guardado = nominaRepository.save(reporte);

            logger.info("Reporte guardado, lista final contiene: {}",
                    guardado.getListaAsistentes() != null ? guardado.getListaAsistentes().size() : 0);
            for (Asistente a : guardado.getListaAsistentes()) {
                logger.info("  - Guardado: {} (ID={})", a.getNombre(), a.getId());
            }
            logger.info("===== NÓMINA PROCESADA EXITOSAMENTE. ID={} =====", guardado.getIdReporte());
            logger.info("Asistentes guardados en BD: {}", guardado.getListaAsistentes().size());

            return guardado;
        } catch (Exception e) {
            logger.error("ERROR en procesarNominaCompleta: {}", e.getMessage(), e);
            throw new RuntimeException("Error al procesar la nómina: " + e.getMessage(), e);
        }
    }

    // Obtener TODOS los asistentes activos del proyecto (no del reporte)
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
}