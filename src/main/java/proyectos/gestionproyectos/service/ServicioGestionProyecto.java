package proyectos.gestionproyectos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyectos.gestionproyectos.model.IntegranteProyecto;
import proyectos.gestionproyectos.model.ProyectoInvestigacion;
import proyectos.gestionproyectos.repository.IntegranteRepository;
import proyectos.gestionproyectos.repository.ProyectoRepository;
import proyectos.gestionusuario.model.DirectorProyecto;
import proyectos.gestionusuario.service.ServicioGestionUsuario;

@Service
public class ServicioGestionProyecto {

    @Autowired
    private ProyectoRepository proyectoRepo;

    @Autowired
    private IntegranteRepository integranteRepo;

    @Autowired
    private ServicioGestionUsuario servicioUsuario;

    public void registrarProyecto(ProyectoInvestigacion proyecto) {
        // 1. Guardamos el proyecto (esto guarda la relación en SQLite)
        proyectoRepo.save(proyecto);

        // 2. Extraemos el director que ya viene asociado en el modelo
        DirectorProyecto directorAsignado = proyecto.getDirector();

        // 3. Delegamos al experto en usuarios la creación de accesos
        if (directorAsignado != null) {
            servicioUsuario.crearCredencialesDirector(directorAsignado);
        }
    }

    public IntegranteProyecto registrarIntegrante(IntegranteProyecto i) {
        return integranteRepo.save(i);
    }

    public void asignarIntegranteAProyecto(Long proyectoId, Long integranteId) {
        ProyectoInvestigacion proyecto = proyectoRepo.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        IntegranteProyecto integrante = integranteRepo.findById(integranteId)
                .orElseThrow(() -> new RuntimeException("Integrante no encontrado"));

        proyecto.getListaIntegrantes().add(integrante); // Asegúrate que este nombre coincida con tu clase
                                                        // ProyectoInvestigacion
        proyectoRepo.save(proyecto);
    }
}