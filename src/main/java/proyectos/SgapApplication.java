package proyectos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // Necesario para los recordatorios mensuales automáticos de nómina
public class SgapApplication {
    public static void main(String[] args) {
        SpringApplication.run(SgapApplication.class, args);
    }
}