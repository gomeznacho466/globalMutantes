package com.mercadolibre.mutant.application.service;

import com.mercadolibre.mutant.application.dto.StatsResponse;
import com.mercadolibre.mutant.domain.repository.DnaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Servicio para obtener estadísticas de análisis de ADN
 * Utiliza queries optimizadas con índices para alto rendimiento
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    private final DnaRepository dnaRepository;

    /**
     * Obtiene las estadísticas globales de verificaciones de ADN
     * 
     * Performance: Las queries están optimizadas con índices en is_mutant
     * Complejidad: O(1) gracias a los índices de BD
     * 
     * @return Estadísticas con contadores y ratio
     */
    @Transactional(readOnly = true)
    public StatsResponse getStats() {
        long mutantCount = dnaRepository.countMutants();
        long humanCount = dnaRepository.countHumans();
        
        double ratio = calculateRatio(mutantCount, humanCount);
        
        log.debug("Stats retrieved - Mutants: {}, Humans: {}, Ratio: {}", 
                  mutantCount, humanCount, ratio);
        
        return StatsResponse.builder()
                .countMutantDna(mutantCount)
                .countHumanDna(humanCount)
                .ratio(ratio)
                .build();
    }

    /**
     * Calcula el ratio de mutantes sobre humanos
     * Maneja el caso especial de división por cero
     * 
     * @param mutantCount Cantidad de mutantes
     * @param humanCount Cantidad de humanos
     * @return Ratio redondeado a 2 decimales
     */
    private double calculateRatio(long mutantCount, long humanCount) {
        if (humanCount == 0) {
            // Si no hay humanos pero hay mutantes, ratio = infinito (representado como mutantCount)
            // Si no hay ninguno, ratio = 0
            return mutantCount > 0 ? mutantCount : 0.0;
        }
        
        BigDecimal mutants = BigDecimal.valueOf(mutantCount);
        BigDecimal humans = BigDecimal.valueOf(humanCount);
        
        return mutants.divide(humans, 2, RoundingMode.HALF_UP).doubleValue();
    }
}
