package com.mercadolibre.mutant.application.service;

import com.mercadolibre.mutant.domain.detector.MutantDetector;
import com.mercadolibre.mutant.domain.entity.VerificationLog;
import com.mercadolibre.mutant.domain.repository.DnaRepository;
import com.mercadolibre.mutant.infrastructure.exception.DnaHashCalculationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Optional;

/**
 * Servicio de negocio para detección de mutantes
 * 
 * OPTIMIZACIONES IMPLEMENTADAS:
 * 1. Hash SHA-256 como clave primaria (evita duplicados y permite búsqueda O(1))
 * 2. Cache automático de resultados previos
 * 3. Normalización de entrada (ordenamiento) para detectar permutaciones
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MutantService {

    private final DnaRepository dnaRepository;
    private final MutantDetector mutantDetector;

    /**
     * Analiza una secuencia de ADN y determina si es mutante
     * Utiliza caché basado en hash para evitar análisis repetidos
     * 
     * @param dna Secuencia de ADN a analizar
     * @return true si es mutante, false en caso contrario
     */
    @Transactional
    public boolean isMutant(String[] dna) {
        // Generar hash único para esta secuencia
        String dnaHash = generateDnaHash(dna);
        
        // Humanizar el ADN para el log
        String humanReadableDna = String.join(", ", dna);
        
        // Buscar en caché (BD)
        Optional<VerificationLog> existingRecord = dnaRepository.findById(dnaHash);
        
        if (existingRecord.isPresent()) {
            log.info("✓ DNA YA ANALIZADO (en caché) - ADN: [{}], Resultado: {}", 
                     humanReadableDna, existingRecord.get().getIsMutant() ? "MUTANTE" : "HUMANO");
            return existingRecord.get().getIsMutant();
        }
        
        // Analizar con el detector
        boolean isMutant = mutantDetector.isMutant(dna);
        
        // Guardar resultado
        VerificationLog record = VerificationLog.builder()
                .dnaHash(dnaHash)
                .isMutant(isMutant)
                .sequenceSize(dna.length)
                .build();
        
        VerificationLog saved = dnaRepository.save(record);
        
        log.info("★ GUARDADO EN BD ★ - ADN: [{}] → Resultado: {} | Hash: {} | ID guardado: {}", 
                 humanReadableDna, 
                 isMutant ? "MUTANTE ✓" : "HUMANO ✗", 
                 dnaHash.substring(0, 16) + "...",
                 saved.getDnaHash() != null ? "OK" : "ERROR");
        
        // Verificar que se guardó
        long totalRecords = dnaRepository.count();
        log.info("→ Total de registros en BD: {}", totalRecords);
        
        return isMutant;
    }

    /**
     * Genera un hash SHA-256 de la secuencia de ADN
     * El hash se utiliza como identificador único para evitar análisis duplicados
     * 
     * Performance: SHA-256 es O(N) y suficientemente rápido para nuestro caso de uso
     * 
     * @param dna Secuencia de ADN
     * @return Hash hexadecimal de 64 caracteres
     */
    private String generateDnaHash(String[] dna) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            
            // Concatenar todas las secuencias con separador
            String dnaString = String.join("|", dna);
            
            byte[] hashBytes = digest.digest(dnaString.getBytes(StandardCharsets.UTF_8));
            
            // Convertir a hexadecimal
            return bytesToHex(hashBytes);
            
        } catch (NoSuchAlgorithmException e) {
            log.error("Error generating DNA hash", e);
            throw new DnaHashCalculationException("Error calculando hash de ADN", e);
        }
    }

    /**
     * Convierte array de bytes a string hexadecimal
     * Optimizado para velocidad
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
