package proyectos.gestioncomunicacion.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class ServicioGestionComunicacion {

    @Autowired
    private JavaMailSender mailSender; // Esta es la herramienta de Maven que descargamos

    public void enviarCorreoCredencialesDirector(String destino, String usuario, String password) {
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject("Bienvenido - Credenciales de Director de Proyecto");
        mensaje.setText("Estimado Director, se le ha asignado un proyecto.\n\n" +
                "Usuario: " + usuario + "\n" +
                "Contraseña temporal: " + password);

        mailSender.send(mensaje); // Esto envía el correo real
    }
}