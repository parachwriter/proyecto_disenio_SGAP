package proyectos.gestiondocumentos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cronogramas_actividades")
@PrimaryKeyJoinColumn(name = "idDocumento") // Se une a la tabla padre
public class CronogramaActividadesAsistente extends Documento {

    private String idAsistente;
    private String idProyecto;
    private String mesActividad;

    // --- CONSTRUCTOR VACÍO OBLIGATORIO JPA ---
    public CronogramaActividadesAsistente() { super(); }

    public CronogramaActividadesAsistente(String nombre, String tipo, String formato, String rutaAlmacenamiento, LocalDateTime fecha,
                                          String idAsistente, String idProyecto, String mesActividad) {
        // Nota: Quitamos el idDocumento del constructor porque la BD lo genera solo
        super(nombre, tipo, formato, rutaAlmacenamiento, fecha);
        this.idAsistente = idAsistente;
        this.idProyecto = idProyecto;
        this.mesActividad = mesActividad;
    }

    // --- MÉTODOS DE NEGOCIO ---
    public void registrarActividad(String descripcion) {
        System.out.println("Registrando actividad para el asistente " + idAsistente + ": \n" + descripcion);
    }

    @Override
    public boolean validarFormato() {
        return "pdf".equalsIgnoreCase(this.getFormato());
    }

    // Getters y Setters
    public String getIdAsistente() { return idAsistente; }
    public void setIdAsistente(String id) { this.idAsistente = id; }
    public String getIdProyecto() { return idProyecto; }
    public void setIdProyecto(String id) { this.idProyecto = id; }
    public String getMesActividad() { return mesActividad; }
    public void setMesActividad(String mes) { this.mesActividad = mes; }
}