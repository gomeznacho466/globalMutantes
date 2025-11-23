package com.mercadolibre.mutant.domain.detector;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de beans para el detector de mutantes
 */
@Configuration
public class MutantDetectorConfig {

    @Bean
    public MutantDetector mutantDetector() {
        return new MutantDetector();
    }
}
