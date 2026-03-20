package com.demo.layered;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación Demo: Arquitectura en Capas
 *
 * Esta aplicación demuestra la arquitectura en capas tradicional,
 * mostrando cómo fluyen las peticiones desde el controlador hasta
 * la base de datos, y cómo la lógica de negocio depende de la
 * infraestructura.
 *
 * Capas:
 * 1. Presentación (Controller)
 * 2. Lógica de Negocio (Service)
 * 3. Acceso a Datos (Repository)
 * 4. Persistencia (Entity)
 */
@SpringBootApplication
public class LayeredOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(LayeredOrderApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Layered Architecture Demo Started!");
        System.out.println("========================================");
        System.out.println("API disponible en: http://localhost:8080");
        System.out.println("H2 Console: http://localhost:8080/h2-console");
        System.out.println("========================================\n");
    }
}
