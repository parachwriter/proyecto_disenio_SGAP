package test;

import java.io.File;

public class DiagnosticoIntegrantes {
    public static void main(String[] args) throws Exception {
        System.out.println("===== DIAGNÓSTICO INTEGRANTES =====\n");

        // 1. Verificar archivo BD
        File dbFile = new File("sgap_database.db");
        System.out.println("1. Archivo BD existe: " + dbFile.exists());
        System.out.println("   Tamaño: " + (dbFile.length() / 1024) + " KB");
        System.out.println("   Ruta absoluta: " + dbFile.getAbsolutePath());

        // 2. Verificar clases compiladas
        File targetClasses = new File("target/classes");
        System.out.println("\n2. Directorio target/classes existe: " + targetClasses.exists());

        // 3. Verificar configuración
        File appProps = new File("src/main/resources/application.properties");
        System.out.println("\n3. application.properties existe: " + appProps.exists());

        if (appProps.exists()) {
            System.out.println("   Leyendo configuración...");
            java.nio.file.Files.lines(appProps.toPath())
                    .filter(line -> line.contains("datasource.url"))
                    .forEach(line -> System.out.println("   " + line));
        }

        // 4. Verificar estructura del proyecto
        System.out.println("\n4. Estructura del proyecto:");
        checkDirectory("src/main/java/proyectos/gestionproyectos/repository");
        checkDirectory("src/main/java/proyectos/gestionproyectos/model");
        checkDirectory("src/main/java/proyectos/gestionasistentes/service");

        System.out.println("\n===== FIN DIAGNÓSTICO =====");
    }

    static void checkDirectory(String path) {
        File dir = new File(path);
        if (dir.exists() && dir.isDirectory()) {
            System.out.println("   ✓ " + path + " (" + dir.list().length + " archivos)");
        } else {
            System.out.println("   ✗ " + path + " NO ENCONTRADO");
        }
    }
}
