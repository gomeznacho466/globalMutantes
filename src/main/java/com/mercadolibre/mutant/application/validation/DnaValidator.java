package com.mercadolibre.mutant.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validador customizado para secuencias de ADN
 * Implementa validaciones a nivel de entrada antes de procesar el ADN
 */
public class DnaValidator implements ConstraintValidator<ValidDna, String[]> {

    // El patrón RegEx ya no es necesario

    @Override
    public boolean isValid(String[] dna, ConstraintValidatorContext context) {
        // Null check
        if (dna == null || dna.length == 0) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("DNA sequence cannot be null or empty")
                   .addConstraintViolation();
            return false;
        }

        int n = dna.length;

        // Validar cada fila
        for (int i = 0; i < n; i++) {
            String sequence = dna[i];
            
            // Validar que no sea null
            if (sequence == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate("DNA sequence row cannot be null")
                       .addConstraintViolation();
                return false;
            }

            // Validar matriz cuadrada NxN
            if (sequence.length() != n) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    String.format("DNA must be NxN matrix. Expected size: %d, but row %d has size: %d", 
                                  n, i, sequence.length()))
                       .addConstraintViolation();
                return false;
            }

            // Validar solo caracteres ATCG (case-sensitive) - ¡IMPLEMENTACIÓN ÚNICA!
            for (int j = 0; j < sequence.length(); j++) {
                char c = sequence.charAt(j);
                
                switch (c) {
                    case 'A':
                    case 'T':
                    case 'C':
                    case 'G':
                        break; // Carácter válido
                    default:
                        context.disableDefaultConstraintViolation();
                        context.buildConstraintViolationWithTemplate(
                            String.format("DNA sequence contains invalid characters in row %d. Only A, T, C, G are allowed", i))
                               .addConstraintViolation();
                        return false; // Carácter inválido
                }
            }
        }

        return true;
    }
}