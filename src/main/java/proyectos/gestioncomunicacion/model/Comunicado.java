package proyectos.gestioncomunicacion.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "comunicados")
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idComunicado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;
    private String destinatario;
    private String tipo;
    private String asunto;
    private String contenido;

    public Comunicado() {
        this.fechaEnvio = LocalDateTime.now();
    }

    public String generarContenido() {
        return "Comunicado de tipo: " + tipo + " para " + destinatario;
    }

    public String obtenerDestinatario() {
        return destinatario;
    }

    // Métodos estáticos para obtener asuntos y contenidos por tipo
    public static String getAsuntoPorTipo(String tipo) {
        switch (tipo) {
            case "CREDENCIALES_DIRECTOR":
                return "Sistema de Gestión Académica y Progreso – Credenciales de Director de Proyecto.";
            case "RECORDATORIO_NOMINA":
                return "Recordatorio: Actualización de Nómina Mensual";
            case "NOMINA_EXITOSA":
                return "Confirmación: Nómina Procesada Exitosamente";
            default:
                return "Comunicado del Sistema SGAP";
        }
    }

    public static String getContenidoCredencialesDirector(String usuario, String password) {
        return "Estimado/a usuario/a:\n\n" +
                "Le damos la más cordial bienvenida al Sistema de Gestión Académica y Progreso.\n\n" +
                "Nos complace informarle que el proyecto ha sido registrado correctamente y que usted ha sido asignado/a como Director/a del Proyecto.\n" +
                "Por este motivo, se ha generado una cuenta de acceso para que pueda ingresar al sistema y gestionar la información correspondiente a su proyecto.\n\n" +
                "A continuación, se detallan sus credenciales de acceso:\n\n" +
                "Usuario: " + usuario + "\n" +
                "Contraseña temporal: " + password + "\n\n" +
                "Con estas credenciales, usted podrá acceder al sistema y ejercer sus funciones como Director/a del Proyecto asignado.\n\n" +
                "Atentamente,\n" +
                "Sistema de Gestión Académica y Progreso.";
    }

    public static String getContenidoRecordatorioNomina(String nombreDirector, List<String> nominasPendientes) {
        StringBuilder sb = new StringBuilder();
        sb.append("Estimado ").append(nombreDirector).append(",\n\n");
        sb.append("Le recordamos que tiene las siguientes nóminas pendientes de reportar:\n\n");

        if (nominasPendientes.isEmpty()) {
            sb.append("- No hay nóminas pendientes.\n");
        } else {
            for (String pendiente : nominasPendientes) {
                sb.append("- ").append(pendiente).append("\n");
            }
        }

        sb.append("\nPor favor ingrese al sistema para reportar su nómina.\n\n");
        sb.append("Atentamente,\n");
        sb.append("Sistema de Gestión Académica y Progreso.");

        return sb.toString();
    }

    public static String getContenidoConfirmacionNomina(String nombreDirector) {
        return "Estimado/a " + nombreDirector + ",\n\n" +
                "Le informamos que el reporte de asistentes para su proyecto " +
                "ha sido registrado correctamente en la base de datos.\n\n" +
                "Fecha de registro: " + java.time.LocalDateTime.now();
    }

    // Getters y Setters
    public Long getIdComunicado() { return idComunicado; }
    public void setIdComunicado(Long idComunicado) { this.idComunicado = idComunicado; }

    public LocalDateTime getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(LocalDateTime fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public String getDestinatario() { return destinatario; }
    public void setDestinatario(String destinatario) { this.destinatario = destinatario; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getAsunto() { return asunto; }
    public void setAsunto(String asunto) { this.asunto = asunto; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }
}

