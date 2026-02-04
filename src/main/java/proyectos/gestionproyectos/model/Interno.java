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
    // (Removed unused fields: departamentoResponsable, recursoInstitucional)

    // --- CONSTRUCTORES ---
    public Interno() {
    }

    // --- GETTERS Y SETTERS ---
    // (Removed unused getters/setters)
}
