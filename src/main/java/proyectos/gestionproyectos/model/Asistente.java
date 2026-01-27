package proyectos.gestionproyectos.model;

import jakarta.persistence.*; // Para @Entity y @Enumerated

@Entity
public class Asistente extends IntegranteProyecto {

    @Override
    public String getTipo() {
        return "ASISTENTE";
    }

    // Asistente ahora hereda el campo estado y todos los métodos relacionados de
    // IntegranteProyecto
}