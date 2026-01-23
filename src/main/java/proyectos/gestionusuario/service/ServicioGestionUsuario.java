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
    // Retorna el DirectorProyecto managed (persistido) para evitar problemas de
    // detached entity
    public DirectorProyecto crearCredencialesDirector(DirectorProyecto director) {
        System.out.println("=== INICIO crearCredencialesDirector ===");
        System.out.println("Correo director: " + director.getCorreoInstitucional());

        try {
            // Verificar si el director ya existe por correo
            var directorExistente = directorRepository.findByCorreoInstitucional(director.getCorreoInstitucional());

            if (directorExistente.isPresent()) {
                DirectorProyecto dirExistente = directorExistente.get();
                System.out.println("Director encontrado en BD. ID: " + dirExistente.getId());

                // Verificar si el director existente ya tiene credenciales
                if (dirExistente.getUsuario() != null && !dirExistente.getUsuario().isEmpty()) {
                    // El director ya tiene credenciales, lo reutilizamos
                    System.out.println(
                            "Director con correo " + director.getCorreoInstitucional()
                                    + " ya existe con credenciales. No se envía correo (evita duplicados).");
                    // Retornamos el director existente (managed) en lugar de copiar datos
                    return dirExistente;
                } else {
                    // El director existe pero SIN credenciales, las creamos ahora
                    System.out.println(
                            "Director encontrado sin credenciales. Creando credenciales para: "
                                    + director.getCorreoInstitucional());
                    director.setId(dirExistente.getId());
                }
            } else {
                System.out.println("Director NO encontrado en BD. Creando nuevo director.");
            }

            // Si no existe O existe sin credenciales, crear/actualizar credenciales
            String passwordTemporal = "Temp" + director.getCedula();
            String usuario = director.getCorreoInstitucional();

            director.setUsuario(usuario);
            director.setPassword(passwordTemporal);

            System.out.println("Guardando director en BD...");
            // Guardamos al director con sus datos completos en la DB
            DirectorProyecto directorGuardado = directorRepository.save(director);
            System.out.println("Director guardado con ID: " + directorGuardado.getId());

            System.out.println("Llamando a enviarCorreoCredencialesDirector...");
            // Llamamos al servicio para enviar el correo
            servicioComunicacion.enviarCorreoCredencialesDirector(
                    director.getCorreoInstitucional(),
                    usuario,
                    passwordTemporal);

            // Retornamos el director guardado (managed)
            return directorGuardado;
        } catch (Exception e) {
            System.err.println("Error al crear credenciales de director: " + e.getMessage());
            e.printStackTrace();
            // Si hay error por duplicados, intentamos obtener el primero
            try {
                var directorExistente = directorRepository.findByCorreoInstitucional(director.getCorreoInstitucional());
                if (directorExistente.isPresent()) {
                    return directorExistente.get();
                }
            } catch (Exception ex) {
                System.err.println("Error crítico al recuperar director: " + ex.getMessage());
            }
            // Si todo falla, retornamos el director tal como vino
            return director;
        }
    }

    // 2. Método del diagrama: Autenticar Usuario (CORREGIDO PARA VALIDACIÓN REAL)
    public String autenticarUsuario(String usuario, String password) {
        // Caso Jefe de Departamento (Admin)
        if (usuario.equals("admin@sgap.com") && password.equals("admin123")) {
            return "ADMIN";
        }

        // Caso Director de Proyecto: Buscamos en la tabla 'usuarios' a través del
        // repositorio
        // Filtramos por el usuario (email) y el password que se generó antes
        return directorRepository.findAll().stream()
                .filter(d -> d.getUsuario().equals(usuario) && d.getPassword().equals(password))
                .findFirst()
                .map(d -> "DIRECTOR") // Si lo encuentra, devuelve este rol
                .orElse("INVALIDO"); // Si no coincide, devuelve este mensaje
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