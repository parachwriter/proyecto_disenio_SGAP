package proyectos.gestioncomunicacion.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "comunicados")
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComunicado;

    private LocalDate fechaEmision;
    private String destinatario;
    private String tipo;

    public Comunicado() {
        this.fechaEmision = LocalDate.now();
    }

    public String generarContenido() {
        return "Comunicado de tipo: " + tipo + " para " + destinatario;
    }

    public String obtenerDestinatario() {
        return destinatario;
    }

    // Getters y Setters
    public Long getIdComunicado() { return idComunicado; }
    public void setIdComunicado(Long idComunicado) { this.idComunicado = idComunicado; }

    public LocalDate getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(LocalDate fechaEmision) { this.fechaEmision = fechaEmision; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}

