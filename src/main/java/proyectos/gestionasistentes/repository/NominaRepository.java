package proyectos.gestionasistentes.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import proyectos.gestionasistentes.model.ReporteNomina;

@Repository
public interface NominaRepository extends JpaRepository<ReporteNomina, Long> {

    // Consulta con FETCH JOIN para cargar eagerly los integrantes
    @Query("SELECT DISTINCT r FROM ReporteNomina r LEFT JOIN FETCH r.listaIntegrantes WHERE r.proyecto.id = :idProyecto AND r.mes = :mes AND r.anio = :anio")
    Optional<ReporteNomina> buscarPorProyectoMesAnio(
            @Param("idProyecto") Long idProyecto,
            @Param("mes") Integer mes,
            @Param("anio") Integer anio);

    // Obtener todos los reportes de un proyecto
    @Query("SELECT r FROM ReporteNomina r WHERE r.proyecto.id = :idProyecto ORDER BY r.anio DESC, r.mes DESC")
    List<ReporteNomina> obtenerReportesProyecto(@Param("idProyecto") Long idProyecto);
}