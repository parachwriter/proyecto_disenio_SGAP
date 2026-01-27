package proyectos.gestionproyectos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyectos.gestionproyectos.model.IntegranteProyecto;
import proyectos.gestionproyectos.model.Proyecto;
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

    public void registrarProyecto(Proyecto proyecto) {
        // 1. Extraemos el director ANTES de guardar el proyecto
        DirectorProyecto directorAsignado = proyecto.getDirector();

        // 2. Primero creamos/obtenemos las credenciales del director
        if (directorAsignado != null) {
            DirectorProyecto directorManaged = servicioUsuario.crearCredencialesDirector(directorAsignado);
            // 3. Asignamos el director managed al proyecto (evita detached entity error)
            proyecto.setDirector(directorManaged);
        }

        // 4. Ahora guardamos el proyecto con el director ya configurado y managed
        proyectoRepo.save(proyecto);
    }

    public IntegranteProyecto registrarIntegrante(IntegranteProyecto i) {
        return integranteRepo.save(i);
    }

    public void asignarIntegranteAProyecto(Long proyectoId, Long integranteId) {
        Proyecto proyecto = proyectoRepo.findById(proyectoId)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        IntegranteProyecto integrante = integranteRepo.findById(integranteId)
                .orElseThrow(() -> new RuntimeException("Integrante no encontrado"));

        proyecto.getListaIntegrantes().add(integrante);
        proyectoRepo.save(proyecto);
    }

    public List<Proyecto> obtenerProyectosPorDirector(String correo) {
        return proyectoRepo.findByDirectorCorreoInstitucional(correo);
    }


    //retornar todos los proyectos
    public List<Proyecto> listarTodos() {
      return proyectoRepo.findAll();
    }
}