package proyectos.gestionasistentes.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import proyectos.gestionasistentes.model.ReporteNomina;

public interface NominaRepository extends JpaRepository<ReporteNomina, Long> {

    Optional<ReporteNomina> findByMesAndAnio(Integer mes, Integer anio);

}
