package proyectos.gestionusuario.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import proyectos.gestionusuario.model.JefeDepartamento;

@Repository
public interface JefeDepartamentoRepository extends JpaRepository<JefeDepartamento, Long> {
    // Permite la persistencia del Jefe para que pueda registrar proyectos.
}   