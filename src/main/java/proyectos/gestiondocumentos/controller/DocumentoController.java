package proyectos.gestiondocumentos.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyectos.gestiondocumentos.dto.ValidacionDocumentoDTO;
import proyectos.gestiondocumentos.model.AvanceProyecto;
import proyectos.gestiondocumentos.model.Documento;
import proyectos.gestiondocumentos.model.PlanificacionProyecto;
import proyectos.gestiondocumentos.service.ServicioGestionDocumento;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    @Autowired
    private ServicioGestionDocumento servicioGestionDocumento;

    /**
     * Endpoint para validar si un proyecto puede cargar un documento
     * 
     * @param proyectoId       ID del proyecto
     * @param periodoAcademico Código del periodo académico (ej: "2025A")
     * @param tipoDocumento    Tipo de documento: "PLANIFICACION" o "AVANCE"
     * @return ValidacionDocumentoDTO con resultado de la validación
     */
    @GetMapping("/validar-carga")
    public ResponseEntity<ValidacionDocumentoDTO> validarCargaDocumento(
            @RequestParam Long proyectoId,
            @RequestParam String periodoAcademico,
            @RequestParam String tipoDocumento) {

        ValidacionDocumentoDTO resultado = servicioGestionDocumento.validarCargaDocumento(
                proyectoId,
                periodoAcademico,
                tipoDocumento);

        return ResponseEntity.ok(resultado);
    }

    /**
     * Endpoint para cargar un documento PDF
     * 
     * @param archivo          Archivo PDF a cargar
     * @param proyectoId       ID del proyecto
     * @param periodoAcademico Código del periodo académico (ej: "2025A")
     * @param tipoDocumento    Tipo de documento: "PLANIFICACION" o "AVANCE"
     * @return Map con idDocumento y mensaje de confirmación
     */
    @PostMapping("/cargar")
    public ResponseEntity<?> cargarDocumento(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam Long proyectoId,
            @RequestParam String periodoAcademico,
            @RequestParam String tipoDocumento) {

        try {
            // Validar nombre del archivo
            String nombreArchivo = archivo.getOriginalFilename();
            if (nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".pdf")) {
                return ResponseEntity.badRequest().body("Solo se permiten archivos PDF");
            }

            // Validar tamaño (10MB máximo)
            if (archivo.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("El archivo excede el tamaño máximo de 10MB");
            }

            // Guardar documento
            Documento documentoGuardado = servicioGestionDocumento.guardarDocumento(
                    archivo, proyectoId, periodoAcademico, tipoDocumento);

            // Respuesta
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("idDocumento", documentoGuardado.getIdDocumento());
            respuesta.put("mensaje", "Documento cargado exitosamente. Estado: Por aprobar");
            respuesta.put("nombreArchivo", documentoGuardado.getNombre());
            respuesta.put("tipo", tipoDocumento);
            respuesta.put("periodo", periodoAcademico);

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al cargar el documento: " + e.getMessage());
        }
    }

    /**
     * Endpoint para obtener todos los documentos de un proyecto
     * 
     * @param proyectoId ID del proyecto
     * @return Lista de documentos del proyecto
     */
    @GetMapping("/proyecto/{proyectoId}")
    public ResponseEntity<List<Documento>> obtenerDocumentosProyecto(@PathVariable Long proyectoId) {
        try {
            List<Documento> documentos = servicioGestionDocumento.obtenerDocumentosDeProyecto(proyectoId);
            return ResponseEntity.ok(documentos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

        // ==============================================
    // NUEVOS ENDPOINTS PARA APROBACIÓN/RECHAZO
    // ==============================================

    /**
     * Obtener un documento específico por ID
     */
    @GetMapping("/{idDocumento}")
    public ResponseEntity<?> obtenerDocumento(@PathVariable Long idDocumento) {
        try {
            // Primero necesitamos agregar un método en el servicio para obtener documento por ID
            Documento documento = servicioGestionDocumento.obtenerDocumentoPorId(idDocumento);
            return ResponseEntity.ok(documento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener documento: " + e.getMessage());
        }
    }

    /**
     * Aprobar un documento
     */
    @PutMapping("/{idDocumento}/aprobar")
    public ResponseEntity<?> aprobarDocumento(
            @PathVariable Long idDocumento,
            @RequestParam(required = false) String observaciones) {
        
        try {
            Documento documento = servicioGestionDocumento.obtenerDocumentoPorId(idDocumento);
            
            // Usar los métodos que YA EXISTEN en las clases
            if (documento instanceof PlanificacionProyecto) {
                ((PlanificacionProyecto) documento).aprobar(
                    observaciones != null ? observaciones : "Documento aprobado sin observaciones"
                );
            } else if (documento instanceof AvanceProyecto) {
                ((AvanceProyecto) documento).aprobar(
                    observaciones != null ? observaciones : "Documento aprobado sin observaciones"
                );
            } else {
                // Solo PlanificacionProyecto y AvanceProyecto pueden ser aprobados
                return ResponseEntity.badRequest().body("Este tipo de documento no puede ser aprobado");
            }
            
            // Guardar los cambios
            servicioGestionDocumento.guardarDocumentoActualizado(documento);
            
            // Respuesta exitosa
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Documento aprobado exitosamente");
            respuesta.put("idDocumento", documento.getIdDocumento());
            respuesta.put("estado", "APROBADO");
            respuesta.put("fechaAprobacion", 
                documento instanceof PlanificacionProyecto ?
                ((PlanificacionProyecto) documento).getFechaAprobacion() :
                ((AvanceProyecto) documento).getFechaAprobacion()
            );
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al aprobar documento: " + e.getMessage());
        }
    }

    /**
     * Rechazar un documento
     */
    @PutMapping("/{idDocumento}/rechazar")
    public ResponseEntity<?> rechazarDocumento(
            @PathVariable Long idDocumento,
            @RequestParam String observaciones) {
        
        try {
            // Validar que las observaciones no estén vacías
            if (observaciones == null || observaciones.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Las observaciones son obligatorias para rechazar un documento");
            }
            
            Documento documento = servicioGestionDocumento.obtenerDocumentoPorId(idDocumento);
            
            // Usar los métodos que YA EXISTEN en las clases
            if (documento instanceof PlanificacionProyecto) {
                ((PlanificacionProyecto) documento).rechazar(observaciones);
            } else if (documento instanceof AvanceProyecto) {
                ((AvanceProyecto) documento).rechazar(observaciones);
            } else {
                return ResponseEntity.badRequest().body("Este tipo de documento no puede ser rechazado");
            }
            
            // Guardar los cambios
            servicioGestionDocumento.guardarDocumentoActualizado(documento);
            
            // Respuesta exitosa
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Documento rechazado");
            respuesta.put("idDocumento", documento.getIdDocumento());
            respuesta.put("estado", "RECHAZADO");
            respuesta.put("observaciones", observaciones);
            
            return ResponseEntity.ok(respuesta);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al rechazar documento: " + e.getMessage());
        }
    }

    @GetMapping("/proyecto/{proyectoId}/estado/{estado}")
    public ResponseEntity<?> obtenerDocumentosPorEstado(
            @PathVariable Long proyectoId,
            @PathVariable String estado) {

        try {
            List<Documento> todosDocumentos = servicioGestionDocumento
                    .obtenerDocumentosDeProyecto(proyectoId);

            List<Documento> documentosFiltrados = todosDocumentos.stream()
                    .filter(doc -> {
                        if (doc instanceof PlanificacionProyecto) {
                            PlanificacionProyecto plan = (PlanificacionProyecto) doc;
                            return filtrarPorEstado(plan.getAprobado(), estado);
                        } else if (doc instanceof AvanceProyecto) {
                            AvanceProyecto avance = (AvanceProyecto) doc;
                            return filtrarPorEstado(avance.getAprobado(), estado);
                        }
                        return false;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(documentosFiltrados);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al filtrar documentos: " + e.getMessage());
        }
    }

    private boolean filtrarPorEstado(Boolean aprobado, String estado) {
        switch (estado.toUpperCase()) {
            case "POR_APROBAR":
                return aprobado == null;
            case "APROBADOS":
                return aprobado != null && aprobado;
            case "RECHAZADOS":
                return aprobado != null && !aprobado;
            default:
                return false;
        }
    }

    // ==============================================
    //  ENDPOINT FALTANTE PARA LA JEFA DE DEPARTAMENTO
    // ==============================================

    /**
     * Obtener TODOS los documentos pendientes de aprobar (de todos los proyectos)
     */
    @GetMapping("/pendientes-globales")
    public ResponseEntity<List<Documento>> listarPendientesGlobales() {
        try {
            List<Documento> pendientes = servicioGestionDocumento.obtenerTodosLosPendientes();
            if (pendientes.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(pendientes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
