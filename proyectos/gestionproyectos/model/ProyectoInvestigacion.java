import java.time.LocalDate;

@Entity 
plublic class ProyectoInvestigacion {
    @Id 
    @GeneratedValue(strategy = GeneratioType.IDENTITY)
    private Long id; 
    private String nombre; 
    private double presupuesto;

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado; 

    @OneToMany
    private List<IntegranteProyecto> integrantes;


}