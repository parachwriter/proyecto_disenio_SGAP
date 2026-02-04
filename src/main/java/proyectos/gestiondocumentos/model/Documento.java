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

    /**
     * Obtiene la ruta completa del archivo físico
     * Formato almacenado nuevo: "directorio|nombreOriginal" con nombre = UUID.pdf
     * Formato almacenado viejo: "directorio" con nombre = nombreOriginal.pdf
     */
    public String obtenerRutaCompleta() {
        String directorio = rutaAlmacenamiento;

        // Si rutaAlmacenamiento contiene "|", extraer solo el directorio
        if (rutaAlmacenamiento != null && rutaAlmacenamiento.contains("|")) {
            directorio = rutaAlmacenamiento.split("\\|")[0];
        }

        // Si el directorio es null o vacío, usar valor por defecto
        if (directorio == null || directorio.isEmpty()) {
            directorio = "uploads/documentos";
        }

        return directorio + "/" + nombre;
    }

    /**
     * Obtiene el nombre original del archivo para mostrar al usuario
     * Si hay formato nuevo (con |), extraer el nombre original
     * Si no, el nombre guardado ES el original
     */
    public String getNombreOriginal() {
        if (rutaAlmacenamiento != null && rutaAlmacenamiento.contains("|")) {
            String[] partes = rutaAlmacenamiento.split("\\|");
            if (partes.length > 1) {
                return partes[1];
            }
        }
        // Formato antiguo o sin separador: el nombre es el original
        return nombre;
    }

    /**
     * Verifica si el nombre del archivo parece ser un UUID
     */
    public boolean tieneNombreUUID() {
        if (nombre == null)
            return false;
        // UUID format: xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx.pdf
        String nombreSinExtension = nombre.replaceAll("\\.[^.]+$", "");
        return nombreSinExtension.matches("[a-f0-9]{8}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{4}-[a-f0-9]{12}");
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