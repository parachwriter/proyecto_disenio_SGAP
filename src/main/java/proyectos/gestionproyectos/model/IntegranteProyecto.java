package proyectos.gestionproyectos.model;

import java.time.LocalDate;
import java.time.Period;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity; // Cambiado para incluir JoinColumn y ManyToOne
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class IntegranteProyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String cedula;
    private String nombre;
    private LocalDate fechaNacimiento;

    // --- NUEVA RELACIÓN (Necesaria para solucionar el error de compilación) ---
    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    @JsonIgnore
    private Proyecto proyecto;

    // --- MÉTODOS GETTER Y SETTER ---

    public Long getId() {
        return id;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    // ... (Mantén tus getters y setters existentes de cedula, nombre y
    // fechaNacimiento)

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

    public void setId(Long id) {
        this.id = id;
    }

    // --- LÓGICA DE NEGOCIO ---
    public int calcularEdad() {
        if (fechaNacimiento == null)
            return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}