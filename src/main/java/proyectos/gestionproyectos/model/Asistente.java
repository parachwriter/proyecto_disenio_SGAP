package proyectos.gestionproyectos.model;

import jakarta.persistence.*; // Para @Entity y @Enumerated

@Entity
public class Asistente extends IntegranteProyecto {

    @Enumerated(EnumType.STRING)
    private EstadoAsistente estado;

    public enum EstadoAsistente {
        ACTIVO,
        FUERA_NOMINA
    }

    // Getters y Setters
    public EstadoAsistente getEstado() {
        return estado;
    }

    public void setEstado(EstadoAsistente estado) {
        this.estado = estado;
    }
}