package proyectos.gestionusuario.model;

import jakarta.persistence.Entity;
import proyectos.gestionproyectos.model.IntegranteProyecto;

@Entity
public class DirectorProyecto extends IntegranteProyecto {
    private String areaInvestigacion;
    private String correoInstitucional;

    // Getters y Setters
    public String getCorreoInstitucional() {
        return correoInstitucional;
    }

    public void setCorreoInstitucional(String correo) {
        this.correoInstitucional = correo;
    }
}