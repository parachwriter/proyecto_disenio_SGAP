import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestQuery {
    public static void main(String[] args) {
        String url = "jdbc:sqlite:sgap_database.db";

        System.out.println("=== DIAGNÓSTICO DE BASE DE DATOS ===\n");

        try (Connection conn = DriverManager.getConnection(url)) {
            // 1. Contar total de integrantes
            String sql1 = "SELECT COUNT(*) as total FROM integrante_proyecto";
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql1)) {
                System.out.println("1. Total integrantes en integrante_proyecto: " + rs.getInt("total"));
            }

            // 2. Ver los tipos de integrantes
            String sql2 = "SELECT ip.id, ip.nombre, ip.cedula, " +
                    "CASE " +
                    "  WHEN a.id IS NOT NULL THEN 'ASISTENTE' " +
                    "  WHEN ai.id IS NOT NULL THEN 'AYUDANTE' " +
                    "  WHEN ti.id IS NOT NULL THEN 'TECNICO' " +
                    "  ELSE 'OTRO' " +
                    "END as tipo, " +
                    "a.estado " +
                    "FROM integrante_proyecto ip " +
                    "LEFT JOIN asistente a ON ip.id = a.id " +
                    "LEFT JOIN ayudante_investigacion ai ON ip.id = ai.id " +
                    "LEFT JOIN tecnico_investigacion ti ON ip.id = ti.id";

            System.out.println("\n2. Listado de integrantes:");
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql2)) {
                while (rs.next()) {
                    System.out.printf("  - ID: %d | Nombre: %s | Tipo: %s | Estado: %s%n",
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("tipo"),
                            rs.getString("estado") != null ? rs.getString("estado") : "N/A");
                }
            }

            // 3. Probar la consulta problemática
            Long proyectoId = 1L; // Cambia esto por un ID de proyecto válido
            String sql3 = "SELECT ip.* FROM integrante_proyecto ip " +
                    "LEFT JOIN asistente a ON ip.id = a.id " +
                    "WHERE ip.proyecto_id = ? " +
                    "AND (a.id IS NULL OR a.estado = 'ACTIVO')";

            System.out.println("\n3. Consulta del repositorio con proyecto_id=" + proyectoId + ":");
            try (PreparedStatement pstmt = conn.prepareStatement(sql3)) {
                pstmt.setLong(1, proyectoId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        System.out.printf("  - ID: %d | Nombre: %s%n",
                                rs.getInt("id"),
                                rs.getString("nombre"));
                    }
                    System.out.println("  Total encontrados: " + count);
                }
            }

            // 4. Ver todos los proyectos disponibles
            String sql4 = "SELECT id, nombre FROM proyecto";
            System.out.println("\n4. Proyectos disponibles:");
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql4)) {
                while (rs.next()) {
                    System.out.printf("  - Proyecto ID: %d | Nombre: %s%n",
                            rs.getInt("id"),
                            rs.getString("nombre"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
