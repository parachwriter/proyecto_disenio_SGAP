package proyectos.gestionusuario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "directores_proyecto")
public class DirectorProyecto extends Usuario {

    private String areaInvestigacion;

    public DirectorProyecto() {
        super();
    }

    // Getter y Setter específico
    public String getAreaInvestigacion() { return areaInvestigacion; }
    public void setAreaInvestigacion(String areaInvestigacion) { this.areaInvestigacion = areaInvestigacion; }

    // Métodos definidos en el Diagrama de Clases
    public void solicitarServicioGestionAsistente() {
        // Conecta con el módulo GestionAsistentes
    }

    public void solicitarServicioGestionDocumento() {
        // Conecta con el módulo GestionDocumentos
    }

    public void reportarNominaMensual() {
        // Activa el flujo de ReporteNomina
    }

    public void confirmarActualizacionNomina() {
        // Finaliza el proceso de reporte
    }
}