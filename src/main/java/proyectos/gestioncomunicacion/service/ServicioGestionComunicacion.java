package proyectos.gestioncomunicacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service; // Importa tu repositorio

import proyectos.gestionusuario.model.DirectorProyecto; // Importa tu modelo
import proyectos.gestionusuario.repository.DirectorProyectoRepository;

@Service
public class ServicioGestionComunicacion {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private DirectorProyectoRepository directorRepository; // Inyectamos el repositorio para persistencia

    // 1. Enviar credenciales (Se activa manualmente al crear el proyecto)
    public void enviarCorreoCredencialesDirector(String destino, String usuario, String password) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject("Bienvenido al Sistema - Credenciales de Director");
        mensaje.setText("Se le ha asignado un nuevo proyecto.\n\n" +
                "Usuario: " + usuario + "\n" +
                "Contraseña temporal: " + password);
        mailSender.send(mensaje);
    }

    // 2. Recordatorio mensual automático (PERSISTENTE)
    @Scheduled(cron = "0 0 9 1 * ?")
    public void enviarCorreoRecordatorioNominaMensual() {
        // BUSCAMOS EN LA BASE DE DATOS REAL
        List<DirectorProyecto> directores = directorRepository.findAll();
        
        for (DirectorProyecto director : directores) {
            if (director.getCorreoInstitucional() != null) {
                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(director.getCorreoInstitucional());
                mensaje.setSubject("Recordatorio: Actualización de Nómina Mensual");
                mensaje.setText("Estimado " + director.getNombre() + 
                                ", por favor ingrese al sistema para reportar su nómina.");
                mailSender.send(mensaje);
            }
        }
    }

    // 3. Confirmación de actualización exitosa (PERSISTENTE)
    public void enviarConfirmacionActualizadaNominaMensual(String destino, String nombreDirector) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject("Confirmación: Nómina Actualizada Exitosamente");
        mensaje.setText("Estimado/a " + nombreDirector + ",\n\n" +
                "Le informamos que el reporte de asistentes para su proyecto " +
                "ha sido registrado correctamente en la base de datos.\n\n" +
                "Fecha de registro: " + java.time.LocalDateTime.now());
        
        mailSender.send(mensaje);
        System.out.println("Correo de confirmación enviado a: " + destino);
    }


}