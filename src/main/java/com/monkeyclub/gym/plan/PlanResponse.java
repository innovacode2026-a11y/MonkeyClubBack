package com.monkeyclub.gym.plan;

import java.math.BigDecimal;
import java.util.UUID;

public record PlanResponse(
        UUID id,
        String name,
        String description,
        Integer durationDays,
        BigDecimal price,
        boolean active
) {
}
