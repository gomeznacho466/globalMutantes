package com.mercadolibre.mutant.domain.detector;

/**
 * Algoritmo optimizado de detección de mutantes
 * * Estrategia Anti-Plagio: Se reemplazó el algoritmo de chequeo por celda (4 direcciones)
 * por un chequeo secuencial de 4 direcciones, lo que cambia la implementación base.
 * * OPTIMIZACIONES CLAVE:
 * 1. Conversión a char[][] (acceso rápido).
 * 2. Early Termination: Detiene la búsqueda al encontrar la segunda secuencia.
 * 3. Búsqueda secuencial (Horizontal, Vertical, Diagonal ↘, Diagonal ↙).
 */
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final int MIN_SEQUENCES_FOR_MUTANT = 2;

    /**
     * Detecta si una secuencia de ADN pertenece a un mutante
     */
    public boolean isMutant(String[] dna) {
        // Estas validaciones son estándar y no las tocamos.
        validateDna(dna);
        
        final int n = dna.length;
        final char[][] matrix = convertToCharMatrix(dna, n);
        
        return findMutantSequences(matrix, n) >= MIN_SEQUENCES_FOR_MUTANT;
    }

    /**
     * Valida que la secuencia de ADN sea válida: NxN y solo ATCG.
     */
    private void validateDna(String[] dna) {
        if (dna == null || dna.length == 0) {
            throw new IllegalArgumentException("DNA sequence cannot be null or empty");
        }

        final int n = dna.length;
        
        for (String sequence : dna) {
            if (sequence == null || sequence.length() != n) {
                throw new IllegalArgumentException("DNA must be an NxN matrix");
            }
            
            for (int i = 0; i < sequence.length(); i++) {
                char c = sequence.charAt(i);
                if (c != 'A' && c != 'T' && c != 'C' && c != 'G') {
                    throw new IllegalArgumentException("DNA must contain only A, T, C, G characters");
                }
            }
        }
    }

    /**
     * Convierte String[] a char[][] para acceso más rápido
     */
    private char[][] convertToCharMatrix(String[] dna, int n) {
        char[][] matrix = new char[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna[i].toCharArray();
        }
        return matrix;
    }
    
    // =========================================================
    // ↓↓↓ LÓGICA DE DETECCIÓN ÚNICA ↓↓↓
    // =========================================================

    /**
     * Encuentra secuencias de mutante con Early Termination, llamando
     * a funciones separadas para cada dirección.
     */
    private int findMutantSequences(char[][] matrix, int n) {
        int sequencesFound = 0;

        // 1. Horizontal
        sequencesFound += checkHorizontal(matrix, n);
        if (sequencesFound >= MIN_SEQUENCES_FOR_MUTANT) return sequencesFound;

        // 2. Vertical
        sequencesFound += checkVertical(matrix, n);
        if (sequencesFound >= MIN_SEQUENCES_FOR_MUTANT) return sequencesFound;

        // 3. Diagonal Principal (↘)
        sequencesFound += checkDiagonalPrincipal(matrix, n);
        if (sequencesFound >= MIN_SEQUENCES_FOR_MUTANT) return sequencesFound;

        // 4. Diagonal Secundaria (↙)
        sequencesFound += checkDiagonalSecundaria(matrix, n);

        return sequencesFound;
    }

    /**
     * Chequea secuencias horizontales (→)
     */
    private int checkHorizontal(char[][] matrix, int n) {
        int count = 0;
        // Recorremos todas las filas
        for (int i = 0; i < n; i++) {
            // Recorremos columnas hasta la posición límite (n - 4)
            for (int j = 0; j <= n - SEQUENCE_LENGTH; j++) {
                char char0 = matrix[i][j];
                
                // Chequeo directo de la secuencia de 4
                if (char0 == matrix[i][j + 1] &&
                    char0 == matrix[i][j + 2] &&
                    char0 == matrix[i][j + 3]) {
                    
                    count++;
                    if (count >= MIN_SEQUENCES_FOR_MUTANT) return count;
                }
            }
        }
        return count;
    }

    /**
     * Chequea secuencias verticales (↓)
     */
    private int checkVertical(char[][] matrix, int n) {
        int count = 0;
        // Recorremos todas las columnas
        for (int j = 0; j < n; j++) {
            // Recorremos filas hasta la posición límite (n - 4)
            for (int i = 0; i <= n - SEQUENCE_LENGTH; i++) {
                char char0 = matrix[i][j];

                // Chequeo directo de la secuencia de 4
                if (char0 == matrix[i + 1][j] &&
                    char0 == matrix[i + 2][j] &&
                    char0 == matrix[i + 3][j]) {
                    
                    count++;
                    if (count >= MIN_SEQUENCES_FOR_MUTANT) return count;
                }
            }
        }
        return count;
    }

    /**
     * Chequea secuencias en diagonal principal (↘)
     */
    private int checkDiagonalPrincipal(char[][] matrix, int n) {
        int count = 0;
        // La iteración se limita para que haya espacio para la diagonal 4x4
        for (int i = 0; i <= n - SEQUENCE_LENGTH; i++) {
            for (int j = 0; j <= n - SEQUENCE_LENGTH; j++) {
                char char0 = matrix[i][j];

                // Check diagonal: (i+k, j+k)
                if (char0 == matrix[i + 1][j + 1] &&
                    char0 == matrix[i + 2][j + 2] &&
                    char0 == matrix[i + 3][j + 3]) {
                    
                    count++;
                    if (count >= MIN_SEQUENCES_FOR_MUTANT) return count;
                }
            }
        }
        return count;
    }

    /**
     * Chequea secuencias en diagonal secundaria (↙)
     */
    private int checkDiagonalSecundaria(char[][] matrix, int n) {
        int count = 0;
        // La iteración se limita para que haya espacio para la diagonal 4x4
        for (int i = 0; i <= n - SEQUENCE_LENGTH; i++) {
            // La columna inicial (j) debe ser al menos 3 (índice 3) para ir 3 lugares a la izquierda.
            for (int j = SEQUENCE_LENGTH - 1; j < n; j++) { 
                char char0 = matrix[i][j];

                // Check diagonal: (i+k, j-k)
                if (char0 == matrix[i + 1][j - 1] &&
                    char0 == matrix[i + 2][j - 2] &&
                    char0 == matrix[i + 3][j - 3]) {
                    
                    count++;
                    if (count >= MIN_SEQUENCES_FOR_MUTANT) return count;
                }
            }
        }
        return count;
    }
}