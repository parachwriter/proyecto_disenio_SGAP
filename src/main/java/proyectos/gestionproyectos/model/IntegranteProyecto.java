package proyectos.gestionproyectos.model;

import jakarta.persistence.*; // Importa las anotaciones de Base de Datos
import java.time.LocalDate;
import java.time.Period;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class IntegranteProyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cedula;
    private LocalDate fechaNacimiento;

    public int calcularEdad() {
        if (fechaNacimiento == null)
            return 0;
        // Corregido: 'between' lleva una sola 'e' y usa fechas completas
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();

    }

}