package proyectos.gestionproyectos.model;

import java.time.LocalDate;
import java.time.Month;
import java.util.Objects;

/**
 * Representa un periodo académico (ej: 2025A, 2025B)
 * - Periodo A: Marzo - Agosto
 * - Periodo B: Septiembre - Febrero del siguiente año
 */
public class PeriodoAcademico {
    private int anio;
    private char ciclo; // 'A' o 'B'

    public PeriodoAcademico(int anio, char ciclo) {
        if (ciclo != 'A' && ciclo != 'B') {
            throw new IllegalArgumentException("El ciclo debe ser 'A' o 'B'");
        }
        this.anio = anio;
        this.ciclo = ciclo;
    }

    public int getAnio() {
        return anio;
    }

    public char getCiclo() {
        return ciclo;
    }

    /**
     * Retorna el periodo en formato string (ej: "2025A")
     */
    public String getCodigo() {
        return anio + String.valueOf(ciclo);
    }

    /**
     * Retorna la fecha de inicio del periodo académico
     */
    public LocalDate getFechaInicio() {
        if (ciclo == 'A') {
            return LocalDate.of(anio, Month.MARCH, 1);
        } else {
            return LocalDate.of(anio, Month.SEPTEMBER, 1);
        }
    }

    /**
     * Retorna la fecha de fin del periodo académico
     */
    public LocalDate getFechaFin() {
        if (ciclo == 'A') {
            return LocalDate.of(anio, Month.AUGUST, 31);
        } else {
            // Periodo B termina en febrero del siguiente año
            return LocalDate.of(anio + 1, Month.FEBRUARY, 28); // Simplificado, ajustar para años bisiestos si es
                                                               // necesario
        }
    }

    /**
     * Retorna el siguiente periodo académico
     */
    public PeriodoAcademico siguiente() {
        if (ciclo == 'A') {
            return new PeriodoAcademico(anio, 'B');
        } else {
            return new PeriodoAcademico(anio + 1, 'A');
        }
    }

    /**
     * Retorna el periodo académico anterior
     */
    public PeriodoAcademico anterior() {
        if (ciclo == 'B') {
            return new PeriodoAcademico(anio, 'A');
        } else {
            return new PeriodoAcademico(anio - 1, 'B');
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PeriodoAcademico that = (PeriodoAcademico) o;
        return anio == that.anio && ciclo == that.ciclo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(anio, ciclo);
    }

    @Override
    public String toString() {
        return getCodigo();
    }

    /**
     * Compara dos periodos académicos
     * 
     * @return negativo si este periodo es anterior, 0 si son iguales, positivo si
     *         este periodo es posterior
     */
    public int compareTo(PeriodoAcademico otro) {
        if (this.anio != otro.anio) {
            return this.anio - otro.anio;
        }
        return this.ciclo - otro.ciclo;
    }
}
