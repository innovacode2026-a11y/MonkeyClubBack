package com.monkeyclub.gym.features.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClientAuditRepository extends JpaRepository<ClientAudit, UUID> {

    List<ClientAudit> findByClientIdOrderByCreatedAtDesc(UUID clientId);
}
