package proyectos.gestiondocumentos.model;

import java.time.LocalDateTime;

public class CronogramaActividadesAsistente extends Documento {
    private String idAsistente;
    private String idProyecto;
    private String mesActividad;

    public CronogramaActividadesAsistente(String idDocumento, String nombre, String tipo, String formato, String rutaAlmacenamiento, LocalDateTime fecha,
                                          String idAsistente, String idProyecto, String mesActividad) {
        super(idDocumento, nombre, tipo, formato, rutaAlmacenamiento, fecha);
        this.idAsistente = idAsistente;
        this.idProyecto = idProyecto;
        this.mesActividad = mesActividad;
    }


    public void registrarActividad(String descripcion) {
        System.out.println("Registrando actividad para el asistente " + idAsistente + ": \n" + descripcion);
    }

    // Sobreescritura (Polimorfismo): Si el cronograma se valida diferente al documento normal
    @Override
    public boolean validarFormato() {
        //valida con formato pdf
        return "pdf".equalsIgnoreCase(this.getFormato());
    }
}