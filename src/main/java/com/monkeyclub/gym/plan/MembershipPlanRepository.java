package com.monkeyclub.gym.plan;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MembershipPlanRepository extends JpaRepository<MembershipPlan, UUID> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, UUID id);
}
