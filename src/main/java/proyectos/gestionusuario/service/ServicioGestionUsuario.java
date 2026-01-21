package proyectos.gestionusuario.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import proyectos.gestioncomunicacion.service.ServicioGestionComunicacion;
import proyectos.gestionusuario.model.DirectorProyecto;
import proyectos.gestionusuario.model.Usuario;
import proyectos.gestionusuario.repository.DirectorProyectoRepository;

@Service
public class ServicioGestionUsuario {

    @Autowired
    private ServicioGestionComunicacion servicioComunicacion;

    @Autowired
    private DirectorProyectoRepository directorRepository; // Para persistencia real

    // 1. Método del diagrama: Crear credenciales y NOTIFICAR
    public void crearCredencialesDirector(DirectorProyecto director) {
        String passwordTemporal = "Temp" + director.getCedula();
        String usuario = director.getCorreoInstitucional();

        // Guardamos al director en la DB antes de enviar el correo
        directorRepository.save(director);

        // Llamamos al servicio para enviar el correo (según tu diseño)
        servicioComunicacion.enviarCorreoCredencialesDirector(
                director.getCorreoInstitucional(),
                usuario,
                passwordTemporal);
    }

    // 2. Método del diagrama: Autenticar Usuario
    public boolean autenticarUsuario(String usuario, String password) {
        // Aquí luego buscaremos en el repositorio de Usuarios
        System.out.println("Autenticando al usuario: " + usuario);
        return true;
    }

    // 3. Método del diagrama: Verificar Credenciales
    public boolean verificarCredencialesUsuario(Usuario usuario) {
        return usuario.getPassword() != null && !usuario.getPassword().isEmpty();
    }

    // 4. Método del diagrama: Asignar Permisos
    public void asignarPermisos(Usuario usuario, String rol) {
        System.out.println("Asignando rol " + rol + " a " + usuario.getNombre());
        // Lógica para roles según el diseño
    }
}