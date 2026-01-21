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

    // Métodos del UML
    public void activar() {
        this.estado = EstadoAsistente.ACTIVO;
    }

    public void desactivar() {
        this.estado = EstadoAsistente.FUERA_NOMINA;
    }

    public boolean estaActivo() {
        return this.estado == EstadoAsistente.ACTIVO;
    }

    public void marcarEnNomina() {
        this.estado = EstadoAsistente.ACTIVO;
    }

    public void marcarFueraNomina() {
        this.estado = EstadoAsistente.FUERA_NOMINA;
    }

    public boolean estaEnNomina() {
        return this.estado == EstadoAsistente.ACTIVO;
    }
}