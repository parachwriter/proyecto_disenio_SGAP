package proyectos.gestiondocumentos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "memorandos_contratacion")
@PrimaryKeyJoinColumn(name = "idDocumento")
public class MemorandoContratacionAsistente extends Documento {

    private String idAsistente;
    private String idProyecto;
    private int mesesContrato;

    // --- CONSTRUCTOR VACÍO OBLIGATORIO JPA ---
    public MemorandoContratacionAsistente() { super(); }

    public MemorandoContratacionAsistente(String nombre, String tipo, String formato, String rutaAlmacenamiento, LocalDateTime fecha,
                                          String idAsistente, String idProyecto, int mesesContrato) {
        super(nombre, tipo, formato, rutaAlmacenamiento, fecha);
        this.idAsistente = idAsistente;
        this.idProyecto = idProyecto;
        this.mesesContrato = mesesContrato;
    }

    // --- MÉTODOS DE NEGOCIO ---
    public LocalDateTime calcularFechaFinContrato() {
        if (this.getFecha() == null) return LocalDateTime.now();
        return this.getFecha().plusMonths(mesesContrato);
    }

    public boolean esContratoVigente() {
        LocalDateTime fechaFin = calcularFechaFinContrato();
        return fechaFin.isAfter(LocalDateTime.now());
    }

    // Getters y Setters
    public String getIdAsistente() { return idAsistente; }
    public void setIdAsistente(String id) { this.idAsistente = id; }
    public String getIdProyecto() { return idProyecto; }
    public void setIdProyecto(String id) { this.idProyecto = id; }
    public int getMesesContrato() { return mesesContrato; }
    public void setMesesContrato(int meses) { this.mesesContrato = meses; }
}