package com.mercadolibre.mutant.infrastructure.exception;

public class DnaHashCalculationException extends RuntimeException {
    public DnaHashCalculationException(String message, Throwable cause) {
        super(message, cause);
    }
}
