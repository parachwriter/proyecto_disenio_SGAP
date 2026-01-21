package proyectos.gestionasistentes.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import proyectos.gestionproyectos.model.Asistente;

@Entity
public class ReporteNomina {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idReporte;

    private Integer mes;
    private Integer anio;

    private LocalDate fechaRegistro;

    private String estado;

    @ManyToMany
    @JoinTable(
        name = "reporte_nomina_asistentes",
        joinColumns = @JoinColumn(name = "id_reporte"),
        inverseJoinColumns = @JoinColumn(name = "id_asistente")
    )
    private List<Asistente> listaAsistentes = new ArrayList<>();

    // +ReporteNomina()
    public ReporteNomina() {
        // Constructor vacío requerido por JPA
    }

    // ===== Métodos del diagrama =====

    // +registrar()
    public void registrar() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }
        if (this.estado == null || this.estado.isBlank()) {
            this.estado = "REGISTRADO";
        }
    }

    // +validarCompleto()
    public boolean validarCompleto() {
        if (mes == null || anio == null) return false;
        if (fechaRegistro == null) return false;
        if (listaAsistentes == null || listaAsistentes.isEmpty()) return false;
        if (estado == null || estado.isBlank()) return false;
        return true;
    }

    // +esDelMesActual()
    public boolean esDelMesActual() {
        LocalDate hoy = LocalDate.now();
        if (mes == null || anio == null) return false;
        return (this.mes == hoy.getMonthValue()) && (this.anio == hoy.getYear());
    }

    // ===== Getters/Setters (no cambian nombres de atributos del diagrama) =====

    public Long getIdReporte() {
        return idReporte;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<Asistente> getListaAsistentes() {
        return listaAsistentes;
    }

    public void setListaAsistentes(List<Asistente> listaAsistentes) {
        this.listaAsistentes = listaAsistentes;
    }
}
