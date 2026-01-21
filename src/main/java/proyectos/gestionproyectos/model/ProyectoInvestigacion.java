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