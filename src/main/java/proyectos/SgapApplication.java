package proyectos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Necesario para los recordatorios mensuales automáticos de nómina
public class SgapApplication {
    public static void main(String[] args) {

        /*
         * // Forzado de Base de Datos (Ya funcionó)
         * System.setProperty("spring.datasource.url", "jdbc:sqlite:sgap_database.db");
         * System.setProperty("spring.datasource.driver-class-name", "org.sqlite.JDBC");
         * System.setProperty("spring.jpa.database-platform",
         * "org.hibernate.community.dialect.SQLiteDialect");
         * System.setProperty("spring.jpa.hibernate.ddl-auto", "update");
         * 
         * // FORZADO DE CORREO (Para solucionar el error de JavaMailSender)
         * System.setProperty("spring.mail.host", "smtp.gmail.com");
         * System.setProperty("spring.mail.port", "587");
         * System.setProperty("spring.mail.username", "troyacarlos2001@gmail.com"); //
         * Pon tu correo real aquí
         * System.setProperty("spring.mail.password", "piquiliaosakira2025"); // Pon tu
         * token real aquí
         * System.setProperty("spring.mail.properties.mail.smtp.auth", "true");
         * System.setProperty("spring.mail.properties.mail.smtp.starttls.enable",
         * "true");
         */
        SpringApplication.run(SgapApplication.class, args);
    }
}