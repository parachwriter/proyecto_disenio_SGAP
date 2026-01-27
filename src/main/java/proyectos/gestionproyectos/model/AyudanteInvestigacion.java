package proyectos.gestionproyectos.model;

import jakarta.persistence.Entity;

@Entity
public class AyudanteInvestigacion extends IntegranteProyecto {

    @Override
    public String getTipo() {
        return "AYUDANTE";
    }

    // Atributos específicos de Ayudante de Investigación (Pasante)
    private String nua; // Número Único Alumno
    private String facultad;
    private Integer semestreActual;
    private Integer horasPasantiaRequeridas;

    // --- GETTERS Y SETTERS ---
    public String getNua() {
        return nua;
    }

    public void setNua(String nua) {
        this.nua = nua;
    }

    public String getFacultad() {
        return facultad;
    }

    public void setFacultad(String facultad) {
        this.facultad = facultad;
    }

    public Integer getSemestreActual() {
        return semestreActual;
    }

    public void setSemestreActual(Integer semestreActual) {
        this.semestreActual = semestreActual;
    }

    public Integer getHorasPasantiaRequeridas() {
        return horasPasantiaRequeridas;
    }

    public void setHorasPasantiaRequeridas(Integer horasPasantiaRequeridas) {
        this.horasPasantiaRequeridas = horasPasantiaRequeridas;
    }
}
