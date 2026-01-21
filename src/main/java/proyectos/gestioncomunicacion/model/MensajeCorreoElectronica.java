package proyectos.gestioncomunicacion.model;

public class MensajeCorreoElectronico {

    private String direccionCorreoDestinatario;
    private String asunto;
    private String mensajePredeterminado;
    private String mensajePersonalizado;

    public MensajeCorreoElectronico() {
    }

    public void personalizarMensaje(String nombre) {
        this.mensajePersonalizado = mensajePredeterminado.replace("{nombre}", nombre);
    }

    public String generarCorreoRecordatorioNomina() {
        return "Estimado/a,\n\nRecordatorio de nómina mensual.\n\nSaludos.";
    }

    public String generarCorreoConfirmacionNomina() {
        return "La nómina mensual ha sido confirmada correctamente.";
    }

    // Getters y Setters
    public String getDireccionCorreoDestinatario() { return direccionCorreoDestinatario; }
    public void setDireccionCorreoDestinatario(String direccionCorreoDestinatario) {
        this.direccionCorreoDestinatario = direccionCorreoDestinatario;
    }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getMensajePredeterminado() { return mensajePredeterminado; }
    public void setMensajePredeterminado(String mensajePredeterminado) {
        this.mensajePredeterminado = mensajePredeterminado;
    }

    public String getMensajePersonalizado() { return mensajePersonalizado; }
}

