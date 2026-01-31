package proyectos.gestionproyectos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyectos.gestionproyectos.model.Proyecto;

public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {
    // Busca proyectos comparando el correo institucional del director asignado
    List<Proyecto> findByDirectorCorreoInstitucional(String correo);

    @Query(value = "SELECT * FROM proyecto_investigacion WHERE tipo_proyecto LIKE :tipoPattern", nativeQuery = true)
    List<Proyecto> findByTipoProyectoPattern(@Param("tipoPattern") String tipoPattern);
}