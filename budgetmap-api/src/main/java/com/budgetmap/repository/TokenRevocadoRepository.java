package com.budgetmap.repository;

import com.budgetmap.model.TokenRevocado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TokenRevocadoRepository extends JpaRepository<TokenRevocado, Long> {
    
    boolean existsByToken(String token);

    @Modifying
    @Query("DELETE FROM TokenRevocado t WHERE t.fechaExpiracion < :now")
    void deleteExpiredTokens(LocalDateTime now);
}
