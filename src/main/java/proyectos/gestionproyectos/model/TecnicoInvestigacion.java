package proyectos.gestionproyectos.model;

import jakarta.persistence.Entity;

@Entity
public class TecnicoInvestigacion extends IntegranteProyecto {

    // Atributos específicos de Técnico de Investigación (Profesional)
    private String especialidadTecnica;
    private String laboratorioAsignado;
    private String certificacionVigente;

    // --- GETTERS Y SETTERS ---
    public String getEspecialidadTecnica() {
        return especialidadTecnica;
    }

    public void setEspecialidadTecnica(String especialidadTecnica) {
        this.especialidadTecnica = especialidadTecnica;
    }

    public String getLaboratorioAsignado() {
        return laboratorioAsignado;
    }

    public void setLaboratorioAsignado(String laboratorioAsignado) {
        this.laboratorioAsignado = laboratorioAsignado;
    }

    public String getCertificacionVigente() {
        return certificacionVigente;
    }

    public void setCertificacionVigente(String certificacionVigente) {
        this.certificacionVigente = certificacionVigente;
    }
}
