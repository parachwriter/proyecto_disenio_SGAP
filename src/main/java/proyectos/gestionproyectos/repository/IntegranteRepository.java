package proyectos.gestionproyectos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyectos.gestionproyectos.model.IntegranteProyecto;

public interface IntegranteRepository extends JpaRepository<IntegranteProyecto, Long> {
}