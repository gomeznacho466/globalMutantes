package com.mercadolibre.mutant.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un registro de ADN analizado
 * Utiliza hash SHA-256 como identificador único para evitar análisis duplicados
 * 
 * Performance Optimization: El hash actúa como índice único permitiendo búsquedas O(1)
 */
@Entity
@Table(name = "dna_records", indexes = {
    @Index(name = "idx_is_mutant", columnList = "is_mutant")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerificationLog {

    @Id
    @Column(name = "dna_hash", length = 64, nullable = false, unique = true)
    private String dnaHash;

    @Column(name = "is_mutant", nullable = false)
    private Boolean isMutant;

    @Column(name = "sequence_size", nullable = false)
    private Integer sequenceSize;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime analyzedAt;

    @PrePersist
    protected void onCreate() {
        if (analyzedAt == null) {
            analyzedAt = LocalDateTime.now();
        }
    }
}
