package proyectos.gestiondocumentos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import proyectos.gestiondocumentos.dto.ValidacionDocumentoDTO;
import proyectos.gestiondocumentos.model.Documento;
import proyectos.gestiondocumentos.service.ServicioGestionDocumento;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
