package proyectos.gestionproyectos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import proyectos.gestionproyectos.model.Asistente;
import proyectos.gestionproyectos.model.IntegranteProyecto;

public interface IntegranteRepository extends JpaRepository<IntegranteProyecto, Long> {

    // Obtener todos los integrantes ACTIVOS de un proyecto
    // Ahora todos los tipos de integrantes tienen campo estado en la clase padre
    // Incluye registros con estado ACTIVO o NULL (para datos legacy)
    @Query("SELECT i FROM IntegranteProyecto i " +
            "WHERE i.proyecto.id = :proyectoId " +
            "AND (i.estado = 'ACTIVO' OR i.estado IS NULL)")
    List<IntegranteProyecto> obtenerIntegrantesActivosPorProyecto(@Param("proyectoId") Long proyectoId);

    // Mantener el método antiguo para compatibilidad
    @Query(value = "SELECT * FROM asistente a INNER JOIN integrante_proyecto ip ON a.id = ip.id WHERE ip.proyecto_id = :proyectoId AND a.estado = 'ACTIVO'", nativeQuery = true)
    List<Asistente> obtenerAsistentesActivosPorProyecto(@Param("proyectoId") Long proyectoId);
}