package proyectos.gestionproyectos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyectos.gestionproyectos.model.Asistente;
import proyectos.gestionproyectos.model.IntegranteProyecto;

public interface IntegranteRepository extends JpaRepository<IntegranteProyecto, Long> {

    // Obtener todos los asistentes ACTIVOS de un proyecto
    // El estado está configurado como EnumType.STRING, así que comparamos con el
    // string
    @Query(value = "SELECT * FROM asistente a INNER JOIN integrante_proyecto ip ON a.id = ip.id WHERE ip.proyecto_id = :proyectoId AND a.estado = 'ACTIVO'", nativeQuery = true)
    List<Asistente> obtenerAsistentesActivosPorProyecto(@Param("proyectoId") Long proyectoId);
}