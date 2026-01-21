package proyectos.gestioncomunicacion.model;

public class Notificacion {

    private String contenido;
    private String hipervinculo;

    public Notificacion() {
    }

    public String construirMensaje() {
        if (contieneHipervinculo()) {
            return contenido + "\nMás información: " + hipervinculo;
        }
        return contenido;
    }

    public boolean contieneHipervinculo() {
        return hipervinculo != null && !hipervinculo.isEmpty();
    }

    // Getters y Setters
    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getHipervinculo() { return hipervinculo; }
    public void setHipervinculo(String hipervinculo) { this.hipervinculo = hipervinculo; }
}
