package proyectos.gestiondocumentos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import proyectos.gestiondocumentos.model.Documento;

@Repository
public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    // Al usar herencia JOINED, guardar un Documento aquí automáticamente
    // lo inserta en la tabla correcta (memorandos o cronogramas).
}