package proyectos.gestionasistentes.dto;

import java.util.List;

public class NominaRequestDTO {
    private Long proyectoId;
    private Integer mes;
    private Integer anio;
    private List<AsistenteDTO> asistentes;

    public Long getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Long proyectoId) {
        this.proyectoId = proyectoId;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public List<AsistenteDTO> getAsistentes() {
        return asistentes;
    }

    public void setAsistentes(List<AsistenteDTO> asistentes) {
        this.asistentes = asistentes;
    }

    public static class AsistenteDTO {
        private Long id;
        private String nombre;
        private String cedula;
        private String correoPersonal;
        private String estado;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getCedula() {
            return cedula;
        }

        public void setCedula(String cedula) {
            this.cedula = cedula;
        }

        public String getCorreoPersonal() {
            return correoPersonal;
        }

        public void setCorreoPersonal(String correoPersonal) {
            this.correoPersonal = correoPersonal;
        }

        public String getEstado() {
            return estado;
        }

        public void setEstado(String estado) {
            this.estado = estado;
        }
    }
}