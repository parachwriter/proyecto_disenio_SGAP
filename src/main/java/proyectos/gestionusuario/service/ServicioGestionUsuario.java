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
        // Generamos los datos
        String passwordTemporal = "Temp" + director.getCedula();
        String usuario = director.getCorreoInstitucional();

        // --- ESTO ES LO QUE FALTABA ---
        // Debes setear los valores en el objeto para que Hibernate los vea
        director.setUsuario(usuario);
        director.setPassword(passwordTemporal);
        // ------------------------------

        // Ahora sí, guardamos al director con sus datos completos en la DB
        directorRepository.save(director);

        // Llamamos al servicio para enviar el correo
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