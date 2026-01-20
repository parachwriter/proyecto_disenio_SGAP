package proyectos.gestionusuario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "jefes_departamento")
public class JefeDepartamento extends Usuario {

    private String periodoGestion;

    public JefeDepartamento() {
        super();
    }

    // Getter y Setter específico
    public String getPeriodoGestion() { return periodoGestion; }
    public void setPeriodoGestion(String periodoGestion) { this.periodoGestion = periodoGestion; }

    // Método definido en el Diagrama de Clases
    public void solicitarServicioGestionProyecto() {
        // Inicia el registro de un nuevo ProyectoInvestigacion
    }
}