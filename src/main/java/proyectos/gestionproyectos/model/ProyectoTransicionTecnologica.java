package proyectos.gestionproyectos.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("TRANSICION_TECNOLOGICA")
public class ProyectoTransicionTecnologica extends Proyecto {

    // Atributos específicos de ProyectoTransicionTecnologica
    private Integer nivelTRLInicial;
    private Integer nivelTRLFinal;
    private String sectorProductivo;
    private String potencialMercado;

    // --- GETTERS Y SETTERS ---
    public Integer getNivelTRLInicial() {
        return nivelTRLInicial;
    }

    public void setNivelTRLInicial(Integer nivelTRLInicial) {
        this.nivelTRLInicial = nivelTRLInicial;
    }

    public Integer getNivelTRLFinal() {
        return nivelTRLFinal;
    }

    public void setNivelTRLFinal(Integer nivelTRLFinal) {
        this.nivelTRLFinal = nivelTRLFinal;
    }

    public String getSectorProductivo() {
        return sectorProductivo;
    }

    public void setSectorProductivo(String sectorProductivo) {
        this.sectorProductivo = sectorProductivo;
    }

    public String getPotencialMercado() {
        return potencialMercado;
    }

    public void setPotencialMercado(String potencialMercado) {
        this.potencialMercado = potencialMercado;
    }
}
