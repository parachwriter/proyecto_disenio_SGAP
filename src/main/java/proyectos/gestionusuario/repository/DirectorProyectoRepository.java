package proyectos.gestionusuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyectos.gestionusuario.model.DirectorProyecto;

@Repository
public interface DirectorProyectoRepository extends JpaRepository<DirectorProyecto, Long> {
    // Permite buscar directores en sgap_database.db para enviar recordatorios automáticos
}