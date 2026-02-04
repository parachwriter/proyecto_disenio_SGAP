package proyectos.gestionproyectos.model;

import jakarta.persistence.Entity;

/**
 * Clase base abstracta para todos los tipos de proyectos de investigación.
 * Los subtipos específicos son: Interno, Semilla, Grupal y Multidisciplinario.
 */
@Entity
public abstract class ProyectoInvestigacion extends Proyecto {

    // Atributos comunes a todos los proyectos de investigación
    // (Removed unused fields: hipotesis, metodologiaCientifica, estadoDelArte)

    // --- GETTERS Y SETTERS ---
    // (Removed unused getters/setters)
}