package proyectos.gestionasistentes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyectos.gestionasistentes.model.ReporteNomina;

@Repository
public interface NominaRepository extends JpaRepository<ReporteNomina, Long> {

    Optional<ReporteNomina> findByMesAndAnio(Integer mes, Integer anio);

}
