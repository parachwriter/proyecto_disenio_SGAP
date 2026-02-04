package proyectos.gestiondocumentos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyectos.gestiondocumentos.dto.ValidacionDocumentoDTO;
import proyectos.gestiondocumentos.model.AvanceProyecto;
import proyectos.gestiondocumentos.model.Documento;
import proyectos.gestiondocumentos.model.PlanificacionProyecto;
import proyectos.gestiondocumentos.service.ServicioGestionDocumento;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
            // ========== VALIDACIÓN DE REGLAS DE NEGOCIO (NÓMINAS) ==========
            // Validar que las nóminas requeridas estén completas antes de permitir la carga
            ValidacionDocumentoDTO validacion = servicioGestionDocumento.validarCargaDocumento(
                    proyectoId, periodoAcademico, tipoDocumento);

            if (!validacion.isPuedeCargar()) {
                return ResponseEntity.badRequest().body("No se puede cargar el documento: " + validacion.getMensaje());
            }

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
            // Primero necesitamos agregar un método en el servicio para obtener documento
            // por ID
            Documento documento = servicioGestionDocumento.obtenerDocumentoPorId(idDocumento);
            return ResponseEntity.ok(documento);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener documento: " + e.getMessage());
        }
    }

    /**
     * Endpoint para ver/descargar el archivo PDF de un documento
     * Se usa para mostrar el PDF en el visor del navegador
     */
    @GetMapping("/{idDocumento}/ver")
    public ResponseEntity<Resource> verDocumentoPDF(@PathVariable Long idDocumento) {
        try {
            Documento documento = servicioGestionDocumento.obtenerDocumentoPorId(idDocumento);
            if (documento == null) {
                System.err.println("DEBUG - Documento no encontrado con ID: " + idDocumento);
                return ResponseEntity.notFound().build();
            }

            System.out.println("DEBUG - Documento encontrado: " + documento.getNombre());
            System.out.println("DEBUG - Ruta almacenamiento: " + documento.getRutaAlmacenamiento());

            // Obtener la ruta del archivo
            String rutaArchivo = documento.obtenerRutaCompleta();
            System.out.println("DEBUG - Intentando abrir archivo: " + rutaArchivo);

            Path path = Paths.get(rutaArchivo);
            Resource resource = new UrlResource(path.toUri());

            // Si no existe, intentar buscar el archivo por nombre en el directorio
            if (!resource.exists() || !resource.isReadable()) {
                System.out.println("DEBUG - Archivo no encontrado en ruta principal, buscando alternativas...");

                // El nombre en BD podría ser el original, pero el archivo físico es UUID
                // Intentar extraer el directorio base
                String dirBase = documento.getRutaAlmacenamiento();
                if (dirBase != null && dirBase.contains("|")) {
                    dirBase = dirBase.split("\\|")[0];
                }
                if (dirBase == null || dirBase.isEmpty()) {
                    dirBase = "uploads/documentos";
                }

                // Intentar con la ruta base + nombre
                Path pathAlternativo = Paths.get(dirBase, documento.getNombre());
                System.out.println("DEBUG - Intentando ruta alternativa: " + pathAlternativo.toString());
                resource = new UrlResource(pathAlternativo.toUri());

                if (!resource.exists() || !resource.isReadable()) {
                    System.err.println("ERROR - No se puede leer el archivo en ninguna ruta");
                    System.err.println("  - Ruta original: " + rutaArchivo);
                    System.err.println("  - Ruta alternativa: " + pathAlternativo.toString());
                    return ResponseEntity.notFound().build();
                }
            }

            // Obtener nombre original para el header
            String nombreOriginal = documento.getNombreOriginal();
            System.out.println("DEBUG - Sirviendo archivo, nombre para descarga: " + nombreOriginal);

            // Devolver el PDF para visualización en el navegador
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreOriginal + "\"")
                    .body(resource);

        } catch (Exception e) {
            System.err.println("Error al servir PDF: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Aprobar un documento con PDF firmado
     */
    @PostMapping("/{idDocumento}/aprobar")
    public ResponseEntity<?> aprobarDocumento(
            @PathVariable Long idDocumento,
            @RequestParam(required = false) String observaciones,
            @RequestParam("archivoFirmado") MultipartFile archivoFirmado) {

        try {
            // Validar que se haya subido el archivo firmado
            if (archivoFirmado == null || archivoFirmado.isEmpty()) {
                return ResponseEntity.badRequest().body("Debe cargar el documento firmado para aprobar");
            }

            // Validar que sea PDF
            String nombreArchivo = archivoFirmado.getOriginalFilename();
            if (nombreArchivo == null || !nombreArchivo.toLowerCase().endsWith(".pdf")) {
                return ResponseEntity.badRequest().body("El documento firmado debe ser un archivo PDF");
            }

            Documento documento = servicioGestionDocumento.obtenerDocumentoPorId(idDocumento);

            // Guardar el archivo firmado
            String directorioFirmados = "uploads/documentos/firmados";
            Path directorioPath = Paths.get(directorioFirmados);
            if (!Files.exists(directorioPath)) {
                Files.createDirectories(directorioPath);
            }

            String extension = nombreArchivo.substring(nombreArchivo.lastIndexOf("."));
            String nombreUnico = "firmado_" + idDocumento + "_" + System.currentTimeMillis() + extension;
            Path rutaArchivo = directorioPath.resolve(nombreUnico);
            Files.copy(archivoFirmado.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            String rutaDocumentoFirmado = directorioFirmados + "/" + nombreUnico;

            // Aprobar con la ruta del documento firmado
            if (documento instanceof PlanificacionProyecto) {
                ((PlanificacionProyecto) documento).aprobar(
                        observaciones != null ? observaciones : "Documento aprobado",
                        rutaDocumentoFirmado);
            } else if (documento instanceof AvanceProyecto) {
                ((AvanceProyecto) documento).aprobar(
                        observaciones != null ? observaciones : "Documento aprobado",
                        rutaDocumentoFirmado);
            } else {
                return ResponseEntity.badRequest().body("Este tipo de documento no puede ser aprobado");
            }

            // Guardar los cambios
            servicioGestionDocumento.guardarDocumentoActualizado(documento);

            // Respuesta exitosa
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Documento aprobado exitosamente con firma");
            respuesta.put("idDocumento", documento.getIdDocumento());
            respuesta.put("estado", "APROBADO");
            respuesta.put("documentoFirmado", rutaDocumentoFirmado);
            respuesta.put("fechaAprobacion",
                    documento instanceof PlanificacionProyecto
                            ? ((PlanificacionProyecto) documento).getFechaAprobacion()
                            : ((AvanceProyecto) documento).getFechaAprobacion());

            return ResponseEntity.ok(respuesta);

        } catch (Exception e) {
            e.printStackTrace();
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
                return ResponseEntity.badRequest()
                        .body("Las observaciones son obligatorias para rechazar un documento");
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
    // ENDPOINT FALTANTE PARA LA JEFA DE DEPARTAMENTO
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

    /**
     * Obtener documentos respondidos (aprobados y rechazados) con filtros
     * opcionales
     * Para la Jefa de Departamento - Consultar Respuesta Documentos
     */
    @GetMapping("/respondidos")
    public ResponseEntity<?> listarDocumentosRespondidos(
            @RequestParam(required = false) Long proyectoId,
            @RequestParam(required = false) String periodo) {
        try {
            List<Documento> documentos = servicioGestionDocumento.obtenerDocumentosRespondidos(proyectoId, periodo);
            return ResponseEntity.ok(documentos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al obtener documentos: " + e.getMessage());
        }
    }

    /**
     * Endpoint para corregir fechas de documentos rechazados que no tienen fecha
     * (corrección de datos históricos)
     */
    @PostMapping("/admin/corregir-fechas-rechazo")
    public ResponseEntity<?> corregirFechasRechazo() {
        try {
            int corregidos = servicioGestionDocumento.corregirFechasDocumentosRechazados();
            return ResponseEntity.ok("Se corrigieron " + corregidos + " documentos rechazados sin fecha.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    /**
     * Endpoint para descargar el documento firmado (solo disponible si está
     * aprobado)
     */
    @GetMapping("/{idDocumento}/firmado")
    public ResponseEntity<Resource> descargarDocumentoFirmado(@PathVariable Long idDocumento) {
        try {
            Documento documento = servicioGestionDocumento.obtenerDocumentoPorId(idDocumento);
            if (documento == null) {
                return ResponseEntity.notFound().build();
            }

            // Obtener la ruta del documento firmado según el tipo
            String rutaFirmado = null;
            if (documento instanceof PlanificacionProyecto) {
                PlanificacionProyecto plan = (PlanificacionProyecto) documento;
                if (plan.getAprobado() == null || !plan.getAprobado()) {
                    return ResponseEntity.badRequest().build();
                }
                rutaFirmado = plan.getDocumentoFirmado();
            } else if (documento instanceof AvanceProyecto) {
                AvanceProyecto avance = (AvanceProyecto) documento;
                if (avance.getAprobado() == null || !avance.getAprobado()) {
                    return ResponseEntity.badRequest().build();
                }
                rutaFirmado = avance.getDocumentoFirmado();
            }

            if (rutaFirmado == null || rutaFirmado.isEmpty()) {
                System.err.println("No hay documento firmado para el documento ID: " + idDocumento);
                return ResponseEntity.notFound().build();
            }

            Path path = Paths.get(rutaFirmado);
            Resource resource = new UrlResource(path.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                System.err.println("No se puede leer el documento firmado: " + rutaFirmado);
                return ResponseEntity.notFound().build();
            }

            // Nombre para la descarga
            String nombreDescarga = "FIRMADO_" + documento.getNombreOriginal();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreDescarga + "\"")
                    .body(resource);

        } catch (Exception e) {
            System.err.println("Error al servir documento firmado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
