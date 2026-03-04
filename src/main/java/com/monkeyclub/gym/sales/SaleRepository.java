package com.monkeyclub.gym.sales;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SaleRepository extends JpaRepository<Sale, UUID> {

    Optional<Sale> findBySaleNumber(String saleNumber);

    List<Sale> findByCreatedAtBetweenOrderByCreatedAtDesc(OffsetDateTime from, OffsetDateTime to);

    @Query(value = """
            SELECT * FROM sales
            WHERE created_at BETWEEN :from AND :to
              AND status::text = :status
            ORDER BY created_at DESC
            """, nativeQuery = true)
    List<Sale> findByCreatedAtBetweenAndStatusOrderByCreatedAtDesc(@Param("from") OffsetDateTime from,
                                                                    @Param("to") OffsetDateTime to,
                                                                    @Param("status") String status);

    @Query(value = """
            SELECT * FROM sales
            WHERE created_by = :createdById
              AND created_at BETWEEN :from AND :to
              AND status::text = :status
            ORDER BY created_at DESC
            """, nativeQuery = true)
    List<Sale> findByCreatedByIdAndCreatedAtBetweenAndStatus(@Param("createdById") UUID createdById,
                                                             @Param("from") OffsetDateTime from,
                                                             @Param("to") OffsetDateTime to,
                                                             @Param("status") String status);
}
