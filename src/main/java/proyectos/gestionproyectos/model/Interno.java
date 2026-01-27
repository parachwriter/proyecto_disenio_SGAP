package proyectos.gestionproyectos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Proyecto de Investigación de tipo INTERNO.
 * Proyectos de investigación desarrollados internamente por la institución.
 */
@Entity
@DiscriminatorValue("INVESTIGACION_INTERNO")
public class Interno extends ProyectoInvestigacion {

    // Atributos específicos del proyecto interno
    private String departamentoResponsable;
    private String recursoInstitucional;

    // --- CONSTRUCTORES ---
    public Interno() {
    }

    // --- GETTERS Y SETTERS ---
    public String getDepartamentoResponsable() {
        return departamentoResponsable;
    }

    public void setDepartamentoResponsable(String departamentoResponsable) {
        this.departamentoResponsable = departamentoResponsable;
    }

    public String getRecursoInstitucional() {
        return recursoInstitucional;
    }

    public void setRecursoInstitucional(String recursoInstitucional) {
        this.recursoInstitucional = recursoInstitucional;
    }
}
