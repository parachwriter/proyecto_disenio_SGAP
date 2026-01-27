package proyectos.gestiondocumentos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyectos.gestiondocumentos.dto.ValidacionDocumentoDTO;
import proyectos.gestiondocumentos.service.ServicioGestionDocumento;

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
}
