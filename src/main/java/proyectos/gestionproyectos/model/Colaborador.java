package proyectos.gestionproyectos.model;

import jakarta.persistence.Entity; // Importación necesaria

@Entity
public class Colaborador extends IntegranteProyecto {

    private String especialidad; // Según tu diagrama, esto representa el área de colaboración

    // Constructor vacío requerido por JPA
    public Colaborador() {
    }

    // Getters y Setters
    public String getEspecialidad() {
        return especialidad;
    }

    public void setEspecialidad(String especialidad) {
        this.especialidad = especialidad;
    }
}