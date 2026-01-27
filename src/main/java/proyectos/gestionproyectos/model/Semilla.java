package proyectos.gestionproyectos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Proyecto de Investigación de tipo SEMILLA.
 * Proyectos iniciales de investigación con potencial de crecimiento.
 */
@Entity
@DiscriminatorValue("INVESTIGACION_SEMILLA")
public class Semilla extends ProyectoInvestigacion {

    // Atributos específicos del proyecto semilla
    private String areaConocimiento;
    private String potencialDesarrollo;
    private Integer duracionMesesEstimada;

    // --- CONSTRUCTORES ---
    public Semilla() {
    }

    // --- GETTERS Y SETTERS ---
    public String getAreaConocimiento() {
        return areaConocimiento;
    }

    public void setAreaConocimiento(String areaConocimiento) {
        this.areaConocimiento = areaConocimiento;
    }

    public String getPotencialDesarrollo() {
        return potencialDesarrollo;
    }

    public void setPotencialDesarrollo(String potencialDesarrollo) {
        this.potencialDesarrollo = potencialDesarrollo;
    }

    public Integer getDuracionMesesEstimada() {
        return duracionMesesEstimada;
    }

    public void setDuracionMesesEstimada(Integer duracionMesesEstimada) {
        this.duracionMesesEstimada = duracionMesesEstimada;
    }
}
