package com.mercadolibre.mutant.infrastructure.exception;

import com.mercadolibre.mutant.infrastructure.controller.ErrorResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios para GlobalExceptionHandler
 * Cubre todos los handlers de excepciones
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler Tests")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException correctamente")
    void testHandleValidationExceptions() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError1 = new FieldError("dnaRequest", "dna", "DNA sequence cannot be null or empty");
        FieldError fieldError2 = new FieldError("dnaRequest", "dna", "DNA must be NxN matrix");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError1, fieldError2));

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("DNA sequence cannot be null or empty"));
        assertTrue(response.getBody().getMessage().contains("DNA must be NxN matrix"));
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar MethodArgumentNotValidException con un solo error")
    void testHandleValidationExceptions_SingleError() {
        // Arrange
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);

        FieldError fieldError = new FieldError("dnaRequest", "dna", "Invalid DNA format");

        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationExceptions(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid DNA format", response.getBody().getMessage());
    }

    @Test
    @DisplayName("Debe manejar IllegalArgumentException correctamente")
    void testHandleIllegalArgumentException() {
        // Arrange
        IllegalArgumentException ex = new IllegalArgumentException("DNA must contain only A, T, C, G characters");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleIllegalArgumentException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("DNA must contain only A, T, C, G characters", response.getBody().getMessage());
        assertEquals(400, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar Exception genérica correctamente")
    void testHandleGeneralException() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error occurred");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneralException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Internal server error"));
        assertTrue(response.getBody().getMessage().contains("Unexpected error occurred"));
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar DnaHashCalculationException correctamente")
    void testHandleDnaHashError() {
        // Arrange
        DnaHashCalculationException ex = new DnaHashCalculationException(
                "Error calculando hash de ADN",
                new RuntimeException("SHA-256 algorithm not found"));

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDnaHashError(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error calculando hash de ADN", response.getBody().getMessage());
        assertEquals(500, response.getBody().getStatus());
    }

    @Test
    @DisplayName("Debe manejar NullPointerException como excepción genérica")
    void testHandleNullPointerException() {
        // Arrange
        NullPointerException ex = new NullPointerException("Null value encountered");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGeneralException(ex);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getMessage().contains("Null value encountered"));
    }
}
