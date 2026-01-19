package proyectos.gestionproyectos.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyectos.gestionproyectos.model.ProyectoInvestigacion;
import proyectos.gestionproyectos.model.IntegranteProyecto;
import proyectos.gestionproyectos.repository.ProyectoRepository;
import proyectos.gestionproyectos.repository.IntegranteRepository;

import java.util.List;

@Service
public class ServicioGestionProyecto {

    @Autowired
    private ProyectoRepository proyectoRepo;

    @Autowired
    private IntegranteRepository integranteRepo;

    public ProyectoInvestigacion registrarProyecto(ProyectoInvestigacion p) {
        return proyectoRepo.save(p);
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