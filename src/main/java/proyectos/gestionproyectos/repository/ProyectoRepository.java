package proyectos.gestionproyectos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import proyectos.gestionproyectos.model.Proyecto;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    // Busca proyectos comparando el correo institucional del director asignado
    List<Proyecto> findByDirectorCorreoInstitucional(String correo);
}