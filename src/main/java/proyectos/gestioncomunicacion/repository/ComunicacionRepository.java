package proyectos.gestioncomunicacion.repository;

import proyectos.gestioncomunicacion.model.Comunicado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComunicacionRepository extends JpaRepository<Comunicado, Long> {
}
