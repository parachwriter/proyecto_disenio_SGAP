package proyectos.gestiondocumentos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyectos.gestiondocumentos.model.Documento;
import proyectos.gestiondocumentos.repository.DocumentoRepository;
import java.util.Optional;

@Service
public class ServicioGestionDocumento {

    @Autowired
    private DocumentoRepository documentoRepository;

    // Método del UML: cargarDocumento
    public void cargarDocumento(Documento documento) {
        if (documento.validarFormato()) {
            documentoRepository.save(documento);
            System.out.println("Documento guardado: " + documento.getNombre() + " (ID: " + documento.getIdDocumento() + ")");
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
        // En este diseño, los documentos (Memorando/Cronograma) ya tienen el idProyecto como atributo.
        // Este método sirve como puente lógico.
        System.out.println("Vinculando documento al proyecto: " + idProyecto);
        cargarDocumento(documento);
    }
}