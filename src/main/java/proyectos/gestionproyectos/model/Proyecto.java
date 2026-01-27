package proyectos.gestionproyectos.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import proyectos.gestionusuario.model.DirectorProyecto;

@Entity
@Table(name = "proyecto_investigacion") // Mantiene la tabla existente
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_proyecto")
public abstract class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // Atributos comunes a todos los proyectos
    private String nombre; // titulo del proyecto
    private Double presupuesto; // presupuestoSolicitado
    private Integer maxAsistentes;
    private String fechaInicio;
    private String fechaFin;
    private Integer duracionMeses;
    private String lineaInvestigacion;

    // Relación con el Director (ManyToOne: muchos proyectos pueden tener el mismo
    // director)
    @ManyToOne
    @JoinColumn(name = "director_id")
    private DirectorProyecto director;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "proyecto_id")
    private List<IntegranteProyecto> listaIntegrantes = new ArrayList<>();

    // --- GETTERS Y SETTERS ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DirectorProyecto getDirector() {
        return director;
    }

    public void setDirector(DirectorProyecto director) {
        this.director = director;
    }

    public List<IntegranteProyecto> getListaIntegrantes() {
        return listaIntegrantes;
    }

    public void setListaIntegrantes(List<IntegranteProyecto> listaIntegrantes) {
        this.listaIntegrantes = listaIntegrantes;
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

    public Integer getDuracionMeses() {
        return duracionMeses;
    }

    public void setDuracionMeses(Integer duracionMeses) {
        this.duracionMeses = duracionMeses;
    }

    /**
     * Calcula la duración en meses entre fechaInicio y fechaFin.
     */
    public void calcularDuracionMeses() {
        if (this.fechaInicio != null && this.fechaFin != null) {
            try {
                java.time.LocalDate inicio = java.time.LocalDate.parse(this.fechaInicio);
                java.time.LocalDate fin = java.time.LocalDate.parse(this.fechaFin);
                java.time.Period periodo = java.time.Period.between(inicio, fin);
                this.duracionMeses = (int) (periodo.toTotalMonths() + 1); // +1 para incluir el mes final
            } catch (Exception e) {
                this.duracionMeses = 0;
            }
        }
    }

    public String getLineaInvestigacion() {
        return lineaInvestigacion;
    }

    public void setLineaInvestigacion(String lineaInvestigacion) {
        this.lineaInvestigacion = lineaInvestigacion;
    }
}
