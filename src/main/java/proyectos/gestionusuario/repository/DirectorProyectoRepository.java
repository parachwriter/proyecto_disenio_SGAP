package proyectos.gestionusuario.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import proyectos.gestionusuario.model.DirectorProyecto;

@Repository
public interface DirectorProyectoRepository extends JpaRepository<DirectorProyecto, Long> {

    // Buscar director por correo institucional (obtiene solo el primero si hay
    // duplicados)
    @Query("SELECT d FROM DirectorProyecto d WHERE d.correoInstitucional = :correo ORDER BY d.id ASC")
    Optional<DirectorProyecto> findByCorreoInstitucional(@Param("correo") String correo);

    // Buscar todos los directores con un correo específico
    @Query("SELECT d FROM DirectorProyecto d WHERE d.correoInstitucional = :correo")
    List<DirectorProyecto> findAllByCorreoInstitucional(@Param("correo") String correo);
}