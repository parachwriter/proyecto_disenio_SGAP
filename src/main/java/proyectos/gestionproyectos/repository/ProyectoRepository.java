package proyectos.gestionproyectos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import proyectos.gestionproyectos.model.ProyectoInvestigacion;

public interface ProyectoRepository extends JpaRepository<ProyectoInvestigacion, Long> {
}