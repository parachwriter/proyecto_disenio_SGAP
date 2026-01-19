package proyectos.gestionproyectos.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ProyectoInvestigacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private double presupuesto;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;

    @OneToMany
    @JoinColumn(name = "proyecto_id")
    private List<IntegranteProyecto> integrantes = new ArrayList<>();

    public ProyectoInvestigacion() {
    }

    // Dentro de ProyectoInvestigacion.java
    @ManyToMany // O @OneToMany según prefieras
    private List<IntegranteProyecto> listaIntegrantes;

    public List<IntegranteProyecto> getListaIntegrantes() {
        return listaIntegrantes;
    }

    // Getters y setters aquí
}
