package proyectos.gestionproyectos.dto;

import proyectos.gestionusuario.model.DirectorProyecto;

/**
 * DTO para recibir datos de creación de proyecto desde el frontend.
 * Incluye tipo y subtipo para determinar qué clase instanciar.
 */
public class ProyectoRequestDTO {
    private String tipoProyecto; // INVESTIGACION, VINCULACION, TRANSICION
    private String subtipoInvestigacion; // INTERNO, SEMILLA, GRUPAL, MULTIDISCIPLINARIO (solo si es INVESTIGACION)

    // Datos comunes
    private String nombre;
    private Double presupuesto;
    private Integer maxAsistentes;
    private String fechaInicio;
    private String fechaFin;

    private DirectorProyecto director;
    private String lineaInvestigacion;

    // Getters y Setters
    public String getTipoProyecto() {
        return tipoProyecto;
    }

    public void setTipoProyecto(String tipoProyecto) {
        this.tipoProyecto = tipoProyecto;
    }

    public String getSubtipoInvestigacion() {
        return subtipoInvestigacion;
    }

    public void setSubtipoInvestigacion(String subtipoInvestigacion) {
        this.subtipoInvestigacion = subtipoInvestigacion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Double getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(Double presupuesto) {
        this.presupuesto = presupuesto;
    }

    public Integer getMaxAsistentes() {
        return maxAsistentes;
    }

    public void setMaxAsistentes(Integer maxAsistentes) {
        this.maxAsistentes = maxAsistentes;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public DirectorProyecto getDirector() {
        return director;
    }

    public void setDirector(DirectorProyecto director) {
        this.director = director;
    }

    public String getLineaInvestigacion() {
        return lineaInvestigacion;
    }

    public void setLineaInvestigacion(String lineaInvestigacion) {
        this.lineaInvestigacion = lineaInvestigacion;
    }
}
