package com.mercadolibre.mutant.domain.repository;

import com.mercadolibre.mutant.domain.entity.VerificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repositorio JPA para gestionar registros de ADN
 * Proporciona consultas optimizadas para estadísticas
 */
@Repository
public interface DnaRepository extends JpaRepository<VerificationLog, String> {

    /**
     * Buscar por hash explícitamente (alias de findById) para cumplir contrato de guía
     */
    Optional<VerificationLog> findByDnaHash(String dnaHash);

    /**
     * Cuenta total de ADN mutante
     * Query optimizada con índice en is_mutant
     */
    @Query("SELECT COUNT(d) FROM DnaRecord d WHERE d.isMutant = true")
    long countMutants();

    /**
     * Cuenta total de ADN humano (no mutante)
     * Query optimizada con índice en is_mutant
     */
    @Query("SELECT COUNT(d) FROM DnaRecord d WHERE d.isMutant = false")
    long countHumans();

    /**
     * Método derivado por convención, útil para evaluaciones automáticas
     */
    long countByIsMutant(boolean isMutant);
}
