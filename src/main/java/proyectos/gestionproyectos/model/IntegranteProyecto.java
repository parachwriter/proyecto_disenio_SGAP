package proyectos.gestionproyectos.model;

import java.time.LocalDate;
import java.time.Period;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import jakarta.persistence.Entity; // Cambiado para incluir JoinColumn y ManyToOne
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "tipo")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Asistente.class, name = "ASISTENTE"),
        @JsonSubTypes.Type(value = AyudanteInvestigacion.class, name = "AYUDANTE"),
        @JsonSubTypes.Type(value = TecnicoInvestigacion.class, name = "TECNICO")
})
public abstract class IntegranteProyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String cedula;
    private String nombre;
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    private EstadoIntegrante estado = EstadoIntegrante.ACTIVO; // Por defecto ACTIVO

    public enum EstadoIntegrante {
        ACTIVO,
        FUERA_NOMINA
    }

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

    public EstadoIntegrante getEstado() {
        return estado;
    }

    public void setEstado(EstadoIntegrante estado) {
        this.estado = estado;
    }

    // --- LÓGICA DE NEGOCIO ---
    public int calcularEdad() {
        if (fechaNacimiento == null)
            return 0;
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }

    // Métodos relacionados con el estado
    public void activar() {
        this.estado = EstadoIntegrante.ACTIVO;
    }

    public void desactivar() {
        this.estado = EstadoIntegrante.FUERA_NOMINA;
    }

    public boolean estaActivo() {
        return this.estado == EstadoIntegrante.ACTIVO;
    }

    public void marcarEnNomina() {
        this.estado = EstadoIntegrante.ACTIVO;
    }

    public void marcarFueraNomina() {
        this.estado = EstadoIntegrante.FUERA_NOMINA;
    }

    public boolean estaEnNomina() {
        return this.estado == EstadoIntegrante.ACTIVO;
    }

    // Método abstracto para que cada subclase devuelva su tipo
    public abstract String getTipo();
}