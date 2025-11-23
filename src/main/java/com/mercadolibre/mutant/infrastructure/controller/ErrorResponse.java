package com.mercadolibre.mutant.infrastructure.controller;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO para respuestas de error
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private String message;
    private int status;
}
