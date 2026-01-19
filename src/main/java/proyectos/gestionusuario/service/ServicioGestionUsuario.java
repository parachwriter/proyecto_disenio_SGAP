package proyectos.gestionusuario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import proyectos.gestionusuario.model.DirectorProyecto;
import proyectos.gestioncomunicacion.service.ServicioGestionComunicacion;

@Service
public class ServicioGestionUsuario {

    @Autowired
    private ServicioGestionComunicacion servicioComunicacion;

    public void crearCredencialesDirector(DirectorProyecto director) {
        String passwordTemporal = "Temp" + director.getCedula();
        String usuario = director.getCorreoInstitucional();

        // Llamamos al servicio para enviar el correo
        servicioComunicacion.enviarCorreoCredencialesDirector(
                director.getCorreoInstitucional(),
                usuario,
                passwordTemporal);
    }
}