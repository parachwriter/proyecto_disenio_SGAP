package proyectos.gestioncomunicacion.repository;

import com.tuempresa.gestioncomunicacion.model.Comunicado;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComunicacionRepository extends JpaRepository<Comunicado, Long> {
}
