package proyectos.gestioncomunicacion.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mensajes_correo")
public class MensajeCorreoElectronico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMensaje;

    @Column(nullable = false)
    private String direccionCorreoDestinatario;

    @Column(nullable = false)
    private String asunto;

    @Column(length = 1000)
    private String mensajePredeterminado;

    @Column(length = 1000)
    private String mensajePersonalizado;

    public MensajeCorreoElectronico() {
    }

    /**
     * Personaliza el mensaje reemplazando {nombre}
     */
    public void personalizarMensaje(String nombre) {
        if (mensajePredeterminado == null) {
            throw new IllegalStateException("El mensaje predeterminado no puede ser null");
        }
        this.mensajePersonalizado = mensajePredeterminado.replace("{nombre}", nombre);
    }

    // Getters y Setters

    public Long getIdMensaje() {
        return idMensaje;
    }

    public void setIdMensaje(Long idMensaje) {
        this.idMensaje = idMensaje;
    }

    public String getDireccionCorreoDestinatario() {
        return direccionCorreoDestinatario;
    }

    public void setDireccionCorreoDestinatario(String direccionCorreoDestinatario) {
        this.direccionCorreoDestinatario = direccionCorreoDestinatario;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getMensajePredeterminado() {
        return mensajePredeterminado;
    }

    public void setMensajePredeterminado(String mensajePredeterminado) {
        this.mensajePredeterminado = mensajePredeterminado;
    }

    public String getMensajePersonalizado() {
        return mensajePersonalizado;
    }

    public void setMensajePersonalizado(String mensajePersonalizado) {
        this.mensajePersonalizado = mensajePersonalizado;
    }
}
