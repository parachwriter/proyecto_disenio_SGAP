package proyectos.gestiondocumentos.dto;

/**
 * DTO para validar si un proyecto puede cargar documentos
 */
public class ValidacionDocumentoDTO {
    private boolean puedeCargar;
    private String mensaje;
    private String tipoDocumento;
    private String periodoAcademico;

    public ValidacionDocumentoDTO() {
    }

    public ValidacionDocumentoDTO(boolean puedeCargar, String mensaje) {
        this.puedeCargar = puedeCargar;
        this.mensaje = mensaje;
    }

    // Getters y Setters
    public boolean isPuedeCargar() {
        return puedeCargar;
    }

    public void setPuedeCargar(boolean puedeCargar) {
        this.puedeCargar = puedeCargar;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getPeriodoAcademico() {
        return periodoAcademico;
    }

    public void setPeriodoAcademico(String periodoAcademico) {
        this.periodoAcademico = periodoAcademico;
    }
}
