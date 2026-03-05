package com.monkeyclub.gym.features.membership;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MembershipHistoryRepository extends JpaRepository<MembershipHistory, UUID> {

    List<MembershipHistory> findByClientIdOrderByCreatedAtDesc(UUID clientId);
}
