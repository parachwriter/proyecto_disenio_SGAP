package proyectos.gestiondocumentos.model;

import java.time.LocalDateTime;

public abstract class Documento {
    private String idDocumento;
    private String nombre;
    private String tipo;
    private String formato;
    private String rutaAlmacenamiento;
    private LocalDateTime fecha;

    public Documento(String idDocumento, String nombre, String tipo, String formato, String rutaAlmacenamiento, LocalDateTime fecha) {
        this.idDocumento = idDocumento;
        this.nombre = nombre;
        this.tipo = tipo;
        this.formato = formato;
        this.rutaAlmacenamiento = rutaAlmacenamiento;
        this.fecha = fecha;
    }

    public boolean validarFormato() {
        if (formato == null || formato.isEmpty()) {
            return false;
        }
        // Ejemplo: validar que tenga extensión
        return nombre.endsWith("." + formato.toLowerCase());
    }

    public String obtenerRutaCompleta() {
        return rutaAlmacenamiento + "/" + nombre;
    }

    // Getters necesarios para que los hijos accedan a los datos (Encapsulamiento)
    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getFormato() {
        return formato;
    }

}