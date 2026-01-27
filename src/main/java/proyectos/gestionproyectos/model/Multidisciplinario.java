package proyectos.gestionproyectos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Proyecto de Investigación de tipo MULTIDISCIPLINARIO.
 * Proyectos que integran múltiples disciplinas y áreas del conocimiento.
 */
@Entity
@DiscriminatorValue("INVESTIGACION_MULTIDISCIPLINARIO")
public class Multidisciplinario extends ProyectoInvestigacion {

    // Atributos específicos del proyecto multidisciplinario
    private String disciplinasInvolucradas;
    private String institucionesColaboradoras;
    private String objetivoIntegracion;

    // --- CONSTRUCTORES ---
    public Multidisciplinario() {
    }

    // --- GETTERS Y SETTERS ---
    public String getDisciplinasInvolucradas() {
        return disciplinasInvolucradas;
    }

    public void setDisciplinasInvolucradas(String disciplinasInvolucradas) {
        this.disciplinasInvolucradas = disciplinasInvolucradas;
    }

    public String getInstitucionesColaboradoras() {
        return institucionesColaboradoras;
    }

    public void setInstitucionesColaboradoras(String institucionesColaboradoras) {
        this.institucionesColaboradoras = institucionesColaboradoras;
    }

    public String getObjetivoIntegracion() {
        return objetivoIntegracion;
    }

    public void setObjetivoIntegracion(String objetivoIntegracion) {
        this.objetivoIntegracion = objetivoIntegracion;
    }
}
