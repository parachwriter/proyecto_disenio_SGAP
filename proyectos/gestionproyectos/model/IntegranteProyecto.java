@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class IntegranteProyecto {
    @Id
    @GeneratedValue(strategy = GenerationType. IDENTITY)
    private Long id; 
    private String cedula; 
    private LocalDate fechaNacimiento;
    public int calcularEdad(){
        return Period.beetween(FechaNacimiento, LocalDate.now().getYears());
        
    }


}