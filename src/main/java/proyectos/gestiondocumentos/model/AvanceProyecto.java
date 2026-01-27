package proyectos.gestiondocumentos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Documento de Avance de Proyecto
 * Reporta el progreso del proyecto durante un periodo académico específico
 */
@Entity
@Table(name = "avance_proyecto")
public class AvanceProyecto extends Documento {

    private String periodoAcademico; // Ej: "2025A", "2025B"
    private String actividadesRealizadas;
    private String resultadosObtenidos;
    private String dificultadesEncontradas;
    private Integer porcentajeAvance; // 0-100

    @Column(name = "aprobado")
    private Boolean aprobado = false;

    private LocalDateTime fechaAprobacion;
    private String observaciones;

    // --- CONSTRUCTORES ---
    public AvanceProyecto() {
        super();
        this.setTipo("AVANCE");
    }

    public AvanceProyecto(String nombre, String formato, String rutaAlmacenamiento,
            LocalDateTime fecha, String periodoAcademico) {
        super(nombre, "AVANCE", formato, rutaAlmacenamiento, fecha);
        this.periodoAcademico = periodoAcademico;
    }

    // --- MÉTODOS DE NEGOCIO ---
    public void aprobar(String observaciones) {
        this.aprobado = true;
        this.fechaAprobacion = LocalDateTime.now();
        this.observaciones = observaciones;
    }

    public void rechazar(String observaciones) {
        this.aprobado = false;
        this.fechaAprobacion = null;
        this.observaciones = observaciones;
    }

    public boolean estaAprobado() {
        return aprobado != null && aprobado;
    }

    public boolean validarPorcentajeAvance() {
        return porcentajeAvance != null && porcentajeAvance >= 0 && porcentajeAvance <= 100;
    }

    // --- GETTERS Y SETTERS ---
    public String getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(String periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }

    public String getActividadesRealizadas() {
        return actividadesRealizadas;
    }

    public void setActividadesRealizadas(String actividadesRealizadas) {
        this.actividadesRealizadas = actividadesRealizadas;
    }

    public String getResultadosObtenidos() {
        return resultadosObtenidos;
    }

    public void setResultadosObtenidos(String resultadosObtenidos) {
        this.resultadosObtenidos = resultadosObtenidos;
    }

    public String getDificultadesEncontradas() {
        return dificultadesEncontradas;
    }

    public void setDificultadesEncontradas(String dificultadesEncontradas) {
        this.dificultadesEncontradas = dificultadesEncontradas;
    }

    public Integer getPorcentajeAvance() {
        return porcentajeAvance;
    }

    public void setPorcentajeAvance(Integer porcentajeAvance) {
        this.porcentajeAvance = porcentajeAvance;
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
}
