package proyectos.gestiondocumentos.model;

import java.time.LocalDateTime;

public class MemorandoContratacionAsistente extends Documento {
    private String idAsistente;
    private String idProyecto;
    private int mesesContrato; // Cambiado a int para poder operar matemáticamente

    public MemorandoContratacionAsistente(String idDocumento, String nombre, String tipo, String formato, String rutaAlmacenamiento, LocalDateTime fecha,
                                          String idAsistente, String idProyecto, int mesesContrato) {
        super(idDocumento, nombre, tipo, formato, rutaAlmacenamiento, fecha);
        this.idAsistente = idAsistente;
        this.idProyecto = idProyecto;
        this.mesesContrato = mesesContrato;
    }

    public LocalDateTime calcularFechaFinContrato() {
        LocalDateTime fechaFin = this.getFecha().plusMonths(mesesContrato);
        return fechaFin;
    }

    public boolean esContratoVigente() {
        LocalDateTime fechaFin = calcularFechaFinContrato();
        return fechaFin.isAfter(LocalDateTime.now());
    }
}