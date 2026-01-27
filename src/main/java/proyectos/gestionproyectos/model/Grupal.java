package proyectos.gestionproyectos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/**
 * Proyecto de Investigación de tipo GRUPAL.
 * Proyectos de investigación desarrollados por grupos de investigación.
 */
@Entity
@DiscriminatorValue("INVESTIGACION_GRUPAL")
public class Grupal extends ProyectoInvestigacion {

    // Atributos específicos del proyecto grupal
    private String nombreGrupoInvestigacion;
    private Integer numeroInvestigadores;
    private String lineaInvestigacionGrupo;

    // --- CONSTRUCTORES ---
    public Grupal() {
    }

    // --- GETTERS Y SETTERS ---
    public String getNombreGrupoInvestigacion() {
        return nombreGrupoInvestigacion;
    }

    public void setNombreGrupoInvestigacion(String nombreGrupoInvestigacion) {
        this.nombreGrupoInvestigacion = nombreGrupoInvestigacion;
    }

    public Integer getNumeroInvestigadores() {
        return numeroInvestigadores;
    }

    public void setNumeroInvestigadores(Integer numeroInvestigadores) {
        this.numeroInvestigadores = numeroInvestigadores;
    }

    public String getLineaInvestigacionGrupo() {
        return lineaInvestigacionGrupo;
    }

    public void setLineaInvestigacionGrupo(String lineaInvestigacionGrupo) {
        this.lineaInvestigacionGrupo = lineaInvestigacionGrupo;
    }
}
