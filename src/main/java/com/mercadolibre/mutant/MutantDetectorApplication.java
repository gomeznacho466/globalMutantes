package com.mercadolibre.mutant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Aplicación principal del Mutant Detector
 * Sistema de detección de mutantes basado en secuencias de ADN
 * 
 * @author MercadoLibre Tech Challenge
 * @version 1.0.0
 */
@SpringBootApplication
public class MutantDetectorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MutantDetectorApplication.class, args);
    }
}
