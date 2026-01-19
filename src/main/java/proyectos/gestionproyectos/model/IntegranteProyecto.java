package proyectos.gestionproyectos.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class IntegranteProyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cedula;
    private String nombre;
    private LocalDate fechaNacimiento;

    // --- MÉTODOS GETTER Y SETTER (Indispensables para que el Servicio funcione)
    // ---

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    // --- LÓGICA DE NEGOCIO ---

    public int calcularEdad() {
        if (fechaNacimiento == null)
            return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}