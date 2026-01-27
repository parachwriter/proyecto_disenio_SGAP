package proyectos.gestionproyectos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("VINCULACION")
public class ProyectoVinculacion extends Proyecto {

    // Atributos específicos de ProyectoVinculacion
    private Integer beneficiariosDirectos;
    private String zonaPlanificacion;
    private String objetivoSocial;
    private String convenioInstitucional;

    // --- GETTERS Y SETTERS ---
    public Integer getBeneficiariosDirectos() {
        return beneficiariosDirectos;
    }

    public void setBeneficiariosDirectos(Integer beneficiariosDirectos) {
        this.beneficiariosDirectos = beneficiariosDirectos;
    }

    public String getZonaPlanificacion() {
        return zonaPlanificacion;
    }

    public void setZonaPlanificacion(String zonaPlanificacion) {
        this.zonaPlanificacion = zonaPlanificacion;
    }

    public String getObjetivoSocial() {
        return objetivoSocial;
    }

    public void setObjetivoSocial(String objetivoSocial) {
        this.objetivoSocial = objetivoSocial;
    }

    public String getConvenioInstitucional() {
        return convenioInstitucional;
    }

    public void setConvenioInstitucional(String convenioInstitucional) {
        this.convenioInstitucional = convenioInstitucional;
    }
}
