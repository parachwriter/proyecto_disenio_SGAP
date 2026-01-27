package proyectos.gestionasistentes.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import proyectos.gestionproyectos.model.Asistente;
import proyectos.gestionproyectos.model.Proyecto;

@Entity
public class ReporteNomina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReporte;

    private Integer mes;
    private Integer anio;
    private LocalDate fechaRegistro;
    private String estado;

    // --- NUEVA RELACIÓN: Un reporte pertenece a un proyecto ---
    @ManyToOne
    @JoinColumn(name = "id_proyecto")
    @JsonIgnore
    private Proyecto proyecto;

    @ManyToMany
    @JoinTable(name = "reporte_nomina_asistentes", joinColumns = @JoinColumn(name = "id_reporte"), inverseJoinColumns = @JoinColumn(name = "id_asistente"))
    private List<Asistente> listaAsistentes = new ArrayList<>();

    public ReporteNomina() {
    }

    // --- NUEVO GETTER Y SETTER (Soluciona el error de compilación) ---
    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    // --- Métodos existentes ---
    public void registrar() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }
    }

    public boolean validarCompleto() {
        return mes != null
                && anio != null
                && fechaRegistro != null
                && listaAsistentes != null
                && !listaAsistentes.isEmpty()
                && estado != null
                && !estado.isBlank();
    }

    public boolean esDelMesActual() {
        LocalDate hoy = LocalDate.now();
        return mes != null && anio != null
                && mes == hoy.getMonthValue()
                && anio == hoy.getYear();
    }

    // ===== Getters y Setters previos =====
    public Long getIdReporte() {
        return idReporte;
    }

    public void setIdReporte(Long idReporte) {
        this.idReporte = idReporte;
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