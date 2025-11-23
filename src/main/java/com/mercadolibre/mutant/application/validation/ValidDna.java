package com.mercadolibre.mutant.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación de validación personalizada para secuencias de ADN
 * Valida que:
 * - El array no sea nulo ni vacío
 * - La matriz sea NxN (cuadrada)
 * - Solo contenga caracteres válidos (A, T, C, G)
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DnaValidator.class)
@Documented
public @interface ValidDna {

    String message() default "Invalid DNA sequence: must be NxN matrix with only A, T, C, G characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
