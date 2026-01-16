@Entity
public class Asistente extends IntegranteProyecto {
    @Enumerated(EnumType.STRING)
    private EstadoAsistente estado; 
    public enum EstadoAsistente {
        ACTIVO,
        FUERA_NOMINA
    }
}

