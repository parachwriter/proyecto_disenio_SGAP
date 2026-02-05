package proyectos.gestiondocumentos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Documento de Planificación de Proyecto
 * Contiene el plan inicial del proyecto para un periodo académico específico
 */
@Entity
@Table(name = "planificacion_proyecto")
public class PlanificacionProyecto extends Documento {

    private String periodoAcademico; // Ej: "2025A", "2025B"

    @Column(name = "aprobado")
    private Boolean aprobado;

    private LocalDateTime fechaAprobacion;
    private String observaciones;

    // Ruta del documento firmado por la jefa (se guarda al aprobar)
    private String documentoFirmado;

    // --- CONSTRUCTORES ---
    public PlanificacionProyecto() {
        super();
        this.setTipo("PLANIFICACION");
    }

    public PlanificacionProyecto(String nombre, String formato, String rutaAlmacenamiento,
            LocalDateTime fecha, String periodoAcademico) {
        super(nombre, "PLANIFICACION", formato, rutaAlmacenamiento, fecha);
        this.periodoAcademico = periodoAcademico;
    }

    // --- MÉTODOS DE NEGOCIO ---
    public void aprobar(String observaciones) {
        this.aprobado = true;
        this.fechaAprobacion = LocalDateTime.now();
        this.observaciones = observaciones;
    }

    public void aprobar(String observaciones, String rutaDocumentoFirmado) {
        this.aprobado = true;
        this.fechaAprobacion = LocalDateTime.now();
        this.observaciones = observaciones;
        this.documentoFirmado = rutaDocumentoFirmado;
    }

    public void rechazar(String observaciones) {
        this.aprobado = false;
        this.fechaAprobacion = LocalDateTime.now(); // Guardar fecha de respuesta también para rechazos
        this.observaciones = observaciones;
    }

    public boolean estaAprobado() {
        return aprobado != null && aprobado;
    }

    // --- GETTERS Y SETTERS ---
    public String getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(String periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public Boolean getAprobado() {
        return aprobado;
    }

    public void setAprobado(Boolean aprobado) {
        this.aprobado = aprobado;
    }

    public LocalDateTime getFechaAprobacion() {
        return fechaAprobacion;
    }

    public void setFechaAprobacion(LocalDateTime fechaAprobacion) {
        this.fechaAprobacion = fechaAprobacion;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getDocumentoFirmado() {
        return documentoFirmado;
    }

    public void setDocumentoFirmado(String documentoFirmado) {
        this.documentoFirmado = documentoFirmado;
    }
}
