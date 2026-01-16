import proyectos.gestionproyectos.service.ServicioGestionProyecto;

@RestController
@RequestMapping("/proyectos")
public class ProyectoController {
    @Autowired
    private ServicioGestionProyecto servicio;
    @PostMapping
    public ProyectoInvestigacion crearProyecto(@RequestBody ProyectoInvestigacion p){
        return servicio.registrarProyecto(p);

    }

    @PostMapping("/{proyectoId}/integrantes/{integranteId}")
    public String asignarIntegrante(
        @PathVariable Long proyectoId,
        @PathVariable Long integranteId) {
      servicio.asignarIntegranteAProyecto(proyectoId, integranteId);
      return "Integrante asignado con éxito";
        }
}