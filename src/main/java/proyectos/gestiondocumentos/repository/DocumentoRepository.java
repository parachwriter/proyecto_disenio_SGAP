package proyectos.gestiondocumentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyectos.gestiondocumentos.model.Documento;

import java.util.List;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    // Al usar herencia JOINED, guardar un Documento aquí automáticamente
    // lo inserta en la tabla correcta (memorandos o cronogramas).

    /**
     * Buscar todos los documentos de un proyecto específico
     * 
     * @param proyectoId ID del proyecto
     * @return Lista de documentos del proyecto
     */
    List<Documento> findByProyectoId(Long proyectoId);
}