package proyectos.gestiondocumentos.model;

import jakarta.persistence.*;
import proyectos.gestionproyectos.model.Proyecto;
import java.time.LocalDateTime;

@Entity
@Table(name = "documentos")
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia clave para herencia en BD
public abstract class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long idDocumento; // Cambiado a Long para autoincremento

    protected String nombre;
    protected String tipo;
    protected String formato;
    protected String rutaAlmacenamiento;
    protected LocalDateTime fecha;

    // Relación con Proyecto
    @ManyToOne
    @JoinColumn(name = "proyecto_id")
    protected Proyecto proyecto;

    // --- CONSTRUCTOR VACÍO OBLIGATORIO JPA ---
    public Documento() {
    }

    public Documento(String nombre, String tipo, String formato, String rutaAlmacenamiento, LocalDateTime fecha) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.formato = formato;
        this.rutaAlmacenamiento = rutaAlmacenamiento;
        this.fecha = fecha;
    }

    // --- MÉTODOS DE NEGOCIO ---
    public boolean validarFormato() {
        if (formato == null || formato.isEmpty())
            return false;
        return nombre.endsWith("." + formato.toLowerCase());
    }

    public String obtenerRutaCompleta() {
        return rutaAlmacenamiento + "/" + nombre;
    }

    // --- GETTERS Y SETTERS ---
    public Long getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(Long id) {
        this.idDocumento = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public String getRutaAlmacenamiento() {
        return rutaAlmacenamiento;
    }

    public void setRutaAlmacenamiento(String ruta) {
        this.rutaAlmacenamiento = ruta;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }
}