package proyectos.gestionproyectos.util;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import proyectos.gestionproyectos.model.PeriodoAcademico;

/**
 * Utilidad para cálculos relacionados con periodos académicos
 * Periodos:
 * - 2025A: Marzo - Agosto 2025
 * - 2025B: Septiembre 2025 - Febrero 2026
 */
public class PeriodoAcademicoUtil {

    /**
     * Calcula el periodo académico correspondiente a una fecha específica
     */
    public static PeriodoAcademico obtenerPeriodoDeFecha(LocalDate fecha) {
        int mes = fecha.getMonthValue();
        int anio = fecha.getYear();

        // Periodo A: Marzo (3) - Agosto (8)
        if (mes >= 3 && mes <= 8) {
            return new PeriodoAcademico(anio, 'A');
        }
        // Periodo B: Septiembre (9) - Diciembre (12)
        else if (mes >= 9 && mes <= 12) {
            return new PeriodoAcademico(anio, 'B');
        }
        // Periodo B del año anterior: Enero (1) - Febrero (2)
        else {
            return new PeriodoAcademico(anio - 1, 'B');
        }
    }

    /**
     * Obtiene todos los periodos académicos entre dos fechas (inclusive)
     */
    public static List<PeriodoAcademico> obtenerPeriodosEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio debe ser anterior o igual a la fecha de fin");
        }

        List<PeriodoAcademico> periodos = new ArrayList<>();

        PeriodoAcademico periodoInicio = obtenerPeriodoDeFecha(fechaInicio);
        PeriodoAcademico periodoFin = obtenerPeriodoDeFecha(fechaFin);

        PeriodoAcademico actual = periodoInicio;
        periodos.add(actual);

        // Agregar todos los periodos hasta llegar al periodo final
        while (actual.compareTo(periodoFin) < 0) {
            actual = actual.siguiente();
            periodos.add(actual);
        }

        return periodos;
    }

    /**
     * Obtiene todos los periodos académicos entre dos fechas como lista de códigos
     * (ej: ["2025A", "2025B"])
     */
    public static List<String> obtenerCodigosPeriodosEntreFechas(LocalDate fechaInicio, LocalDate fechaFin) {
        List<PeriodoAcademico> periodos = obtenerPeriodosEntreFechas(fechaInicio, fechaFin);
        return periodos.stream()
                .map(PeriodoAcademico::getCodigo)
                .toList();
    }

    /**
     * Verifica si un proyecto abarca múltiples periodos académicos
     */
    public static boolean abarcaMultiplesPeriodos(LocalDate fechaInicio, LocalDate fechaFin) {
        return obtenerPeriodosEntreFechas(fechaInicio, fechaFin).size() > 1;
    }

    /**
     * Calcula cuántos periodos académicos abarca un proyecto
     */
    public static int contarPeriodos(LocalDate fechaInicio, LocalDate fechaFin) {
        return obtenerPeriodosEntreFechas(fechaInicio, fechaFin).size();
    }

    /**
     * Verifica si una fecha está dentro de un periodo académico específico
     */
    public static boolean fechaEnPeriodo(LocalDate fecha, PeriodoAcademico periodo) {
        LocalDate inicioPeriodo = periodo.getFechaInicio();
        LocalDate finPeriodo = periodo.getFechaFin();

        return !fecha.isBefore(inicioPeriodo) && !fecha.isAfter(finPeriodo);
    }

    /**
     * Obtiene el periodo académico actual (basado en la fecha de hoy)
     */
    public static PeriodoAcademico obtenerPeriodoActual() {
        return obtenerPeriodoDeFecha(LocalDate.now());
    }

    /**
     * Parsea un código de periodo (ej: "2025A") a un objeto PeriodoAcademico
     */
    public static PeriodoAcademico parsearCodigo(String codigo) {
        if (codigo == null || codigo.length() < 5) {
            throw new IllegalArgumentException("Código de periodo inválido: " + codigo);
        }

        try {
            int anio = Integer.parseInt(codigo.substring(0, 4));
            char ciclo = codigo.charAt(4);
            return new PeriodoAcademico(anio, ciclo);
        } catch (Exception e) {
            throw new IllegalArgumentException("Código de periodo inválido: " + codigo, e);
        }
    }

    /**
     * Calcula la duración en meses de un periodo específico
     * (Siempre retorna 6 meses)
     */
    public static int obtenerDuracionEnMeses() {
        return 6;
    }
}
