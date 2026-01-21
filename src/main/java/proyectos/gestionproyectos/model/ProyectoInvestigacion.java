package proyectos.gestionproyectos.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import proyectos.gestionusuario.model.DirectorProyecto;

@Entity
public class ProyectoInvestigacion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    // --- NUEVO: Estos atributos crean las columnas faltantes ---
    private String nombre;
    private Double presupuesto;
    // ---------------------------------------------------------

    // Relación con el Director
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "director_id") // Es mejor especificar el nombre de la columna
    private DirectorProyecto director;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "proyecto_id")
    private List<IntegranteProyecto> listaIntegrantes = new ArrayList<>();

    // --- NUEVOS GETTERS Y SETTERS (Obligatorios para que Spring funcione) ---
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

    // --- GETTERS Y SETTERS EXISTENTES ---
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
}