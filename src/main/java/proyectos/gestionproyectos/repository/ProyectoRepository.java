package proyectos.gestionproyectos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import proyectos.gestionproyectos.model.ProyectoInvestigacion;

public interface ProyectoRepository extends JpaRepository<ProyectoInvestigacion, Long> {
    // Busca proyectos comparando el correo institucional del director asignado
    List<ProyectoInvestigacion> findByDirectorCorreoInstitucional(String correo);
}