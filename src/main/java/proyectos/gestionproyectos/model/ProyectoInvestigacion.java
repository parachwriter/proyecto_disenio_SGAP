package proyectos.gestionproyectos.model;

import jakarta.persistence.Entity;

/**
 * Clase base abstracta para todos los tipos de proyectos de investigación.
 * Los subtipos específicos son: Interno, Semilla, Grupal y Multidisciplinario.
 */
@Entity
public abstract class ProyectoInvestigacion extends Proyecto {

    // Atributos comunes a todos los proyectos de investigación
    private String hipotesis;
    private String metodologiaCientifica;
    private String estadoDelArte;

    // --- GETTERS Y SETTERS ---
    public String getHipotesis() {
        return hipotesis;
    }

    public void setHipotesis(String hipotesis) {
        this.hipotesis = hipotesis;
    }

    public String getMetodologiaCientifica() {
        return metodologiaCientifica;
    }

    public void setMetodologiaCientifica(String metodologiaCientifica) {
        this.metodologiaCientifica = metodologiaCientifica;
    }

    public String getEstadoDelArte() {
        return estadoDelArte;
    }

    public void setEstadoDelArte(String estadoDelArte) {
        this.estadoDelArte = estadoDelArte;
    }
}