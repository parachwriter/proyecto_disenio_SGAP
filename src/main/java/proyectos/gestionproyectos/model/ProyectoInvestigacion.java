package proyectos.gestionproyectos.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import proyectos.gestionusuario.model.DirectorProyecto;

@Entity
public class ProyectoInvestigacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación con el Director
    @OneToOne(cascade = CascadeType.ALL)
    private DirectorProyecto director;

    // Relación con la lista de integrantes (Asistentes/Colaboradores)
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "proyecto_id")
    private List<IntegranteProyecto> listaIntegrantes = new ArrayList<>();

    // --- GETTERS Y SETTERS CLAVE ---
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