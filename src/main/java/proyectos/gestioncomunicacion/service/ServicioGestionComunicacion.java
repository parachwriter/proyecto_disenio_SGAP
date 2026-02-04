package proyectos.gestioncomunicacion.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import proyectos.gestioncomunicacion.model.Comunicado;
import proyectos.gestioncomunicacion.repository.ComunicacionRepository;
import proyectos.gestionusuario.model.DirectorProyecto;
import proyectos.gestionusuario.repository.DirectorProyectoRepository;

@Service
public class ServicioGestionComunicacion {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private DirectorProyectoRepository directorRepository;

    @Autowired
    private ComunicacionRepository comunicacionRepository;

    // 1. Enviar credenciales (Se activa manualmente al crear el proyecto)
    public void enviarCorreoCredencialesDirector(String destino, String usuario, String password) {
        try {
            System.out.println("=== Intentando enviar correo ===");
            System.out.println("Destino: " + destino);
            System.out.println("Usuario: " + usuario);

            String subject = Comunicado.getAsuntoPorTipo("CREDENCIALES_DIRECTOR");
            String content = Comunicado.getContenidoCredencialesDirector(usuario, password);

            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("troyacarlos2001@gmail.com");
            mensaje.setTo(destino);
            mensaje.setSubject(subject);
            mensaje.setText(content);

            System.out.println("Enviando mensaje...");
            mailSender.send(mensaje);
            System.out.println("✓ Correo enviado exitosamente a: " + destino);

            // Registrar en BD
            Comunicado comunicado = new Comunicado();
            comunicado.setTipo("CREDENCIALES_DIRECTOR");
            comunicado.setDestinatario(destino);
            comunicado.setAsunto(subject);
            comunicado.setContenido(content);
            comunicacionRepository.save(comunicado);
            System.out.println("✓ Comunicado registrado en BD: " + comunicado.getIdComunicado());

        } catch (Exception e) {
            System.err.println("✗ Error al enviar correo a " + destino + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 2. Recordatorio mensual automático (PERSISTENTE)
    @Scheduled(cron = "0 0 9 1 * ?")
    public void enviarCorreoRecordatorioNominaMensual() {
        // BUSCAMOS EN LA BASE DE DATOS REAL
        List<DirectorProyecto> directores = directorRepository.findAll();

        for (DirectorProyecto director : directores) {
            if (director.getCorreoInstitucional() != null) {
                String subject = "Recordatorio: Actualización de Nómina Mensual";
                String content = "Estimado " + director.getNombre() +
                        ", por favor ingrese al sistema para reportar su nómina.";

                SimpleMailMessage mensaje = new SimpleMailMessage();
                mensaje.setTo(director.getCorreoInstitucional());
                mensaje.setSubject(subject);
                mensaje.setText(content);
                mailSender.send(mensaje);

                // Registrar en BD
                Comunicado comunicado = new Comunicado();
                comunicado.setTipo("RECORDATORIO_NOMINA");
                comunicado.setDestinatario(director.getCorreoInstitucional());
                comunicado.setAsunto(subject);
                comunicado.setContenido(content);
                comunicacionRepository.save(comunicado);
                System.out.println("✓ Comunicado de recordatorio registrado en BD para: " + director.getCorreoInstitucional());
            }
        }
    }

    // 3. Confirmación de actualización exitosa (PERSISTENTE)
    public void enviarConfirmacionActualizadaNominaMensual(String destino, String nombreDirector) {
        String subject = "Confirmación: Nómina Actualizada Exitosamente";
        String content = "Estimado/a " + nombreDirector + ",\n\n" +
                "Le informamos que el reporte de asistentes para su proyecto " +
                "ha sido registrado correctamente en la base de datos.\n\n" +
                "Fecha de registro: " + java.time.LocalDateTime.now();

        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destino);
        mensaje.setSubject(subject);
        mensaje.setText(content);

        mailSender.send(mensaje);
        System.out.println("Correo de confirmación enviado a: " + destino);

        // Registrar en BD
        Comunicado comunicado = new Comunicado();
        comunicado.setTipo("CONFIRMACION_NOMINA");
        comunicado.setDestinatario(destino);
        comunicado.setAsunto(subject);
        comunicado.setContenido(content);
        comunicacionRepository.save(comunicado);
        System.out.println("✓ Comunicado de confirmación registrado en BD: " + comunicado.getIdComunicado());
    }

    // Método para notificación al jefe de departamento
    public void enviarNotificacionAJefeDepartamento() {
        // Lógica para enviar notificación al jefe
        System.out.println("Notificación enviada al jefe de departamento: Reporte mensual no cumplido");
    }

    // Método para enviar correo de nómina exitosa
    public void enviarCorreoNominaExitosa(String destino, String nombreDirector) {
        try {
            String subject = "Confirmación: Nómina Procesada Exitosamente";
            String content = "Estimado/a " + nombreDirector + ",\n\n" +
                    "Le informamos que la nómina de su proyecto ha sido procesada y registrada correctamente en el sistema.\n\n" +
                    "Fecha de procesamiento: " + java.time.LocalDateTime.now() + "\n\n" +
                    "Atentamente,\n" +
                    "Sistema de Gestión Académica y Progreso.";

            SimpleMailMessage mensaje = new SimpleMailMessage();
            mensaje.setFrom("troyacarlos2001@gmail.com");
            mensaje.setTo(destino);
            mensaje.setSubject(subject);
            mensaje.setText(content);

            mailSender.send(mensaje);
            System.out.println("✓ Correo de nómina exitosa enviado a: " + destino);

            // Registrar en BD
            Comunicado comunicado = new Comunicado();
            comunicado.setTipo("NOMINA_EXITOSA");
            comunicado.setDestinatario(destino);
            comunicado.setAsunto(subject);
            comunicado.setContenido(content);
            comunicacionRepository.save(comunicado);
            System.out.println("✓ Comunicado de nómina exitosa registrado en BD: " + comunicado.getIdComunicado());

        } catch (Exception e) {
            System.err.println("✗ Error al enviar correo de nómina exitosa a " + destino + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}