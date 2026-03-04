package com.monkeyclub.gym.membership;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository extends JpaRepository<Membership, UUID> {

    Optional<Membership> findTopByClientIdOrderByEndDateDesc(UUID clientId);

    @Query(value = """
            SELECT * FROM memberships
            WHERE end_date < :date
              AND status::text = :status
            """, nativeQuery = true)
    List<Membership> findByEndDateBeforeAndStatus(@Param("date") LocalDate date, @Param("status") String status);

    List<Membership> findByClientIdOrderByEndDateDesc(UUID clientId);

    List<Membership> findByEndDateBetweenOrderByEndDateAsc(LocalDate from, LocalDate to);
}
