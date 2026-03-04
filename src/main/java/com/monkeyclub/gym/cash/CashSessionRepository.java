package com.monkeyclub.gym.cash;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CashSessionRepository extends JpaRepository<CashSession, UUID> {

    @Query(value = """
            SELECT * FROM cash_sessions
            WHERE cashier_id = :cashierId
              AND status::text = 'ABIERTA'
            LIMIT 1
            """, nativeQuery = true)
    Optional<CashSession> findOpenByCashierId(@Param("cashierId") UUID cashierId);

    List<CashSession> findByCashierIdOrderByOpenedAtDesc(UUID cashierId);
}
