package proyectos.gestioncomunicacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled; // Necesario para la automatización
import org.springframework.stereotype.Service;

@Service
public class ServicioGestionComunicacion {

    @Autowired
    private JavaMailSender mailSender;

    // 1. Enviar credenciales cuando el Jefe de Departamento crea el proyecto
    public void enviarCorreoCredencialesDirector(String destino, String usuario, String password) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject("Bienvenido al Sistema - Credenciales de Director");
        mensaje.setText("Se le ha asignado un nuevo proyecto.\n\n" +
                "Usuario: " + usuario + "\n" +
                "Contraseña temporal: " + password + "\n\n" +
                "Debe ingresar mensualmente para reportar su nómina.");
        mailSender.send(mensaje);
    }

    // 2. Recordatorio mensual automático
    // Se ejecutará el día 1 de cada mes a las 9:00 AM
    @Scheduled(cron = "0 0 9 1 * ?")
    public void enviarCorreoRecordatorioNominaMensual(String destino) {
        // Nota: En una fase avanzada, aquí buscaremos los correos de la DB
        if (destino != null) {
            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setTo(destino);
            mensaje.setSubject("Recordatorio: Actualización de Nómina Mensual");
            mensaje.setText("Aún no ha realizado el reporte de asistentes de este mes. Por favor ingrese al sistema.");
            mailSender.send(mensaje);
        }
    }

    // 3. Confirmación de actualización exitosa
    public void enviarConfirmacionActualizadaNominaMensual(String destino) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject("Confirmación: Nómina Actualizada");
        mensaje.setText("Su reporte de nómina ha sido recibido correctamente.");
        mailSender.send(mensaje);
    }
}