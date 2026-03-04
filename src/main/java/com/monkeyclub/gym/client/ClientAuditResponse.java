package com.monkeyclub.gym.client;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClientAuditResponse(
        UUID id,
        String action,
        String detail,
        String changedBy,
        OffsetDateTime createdAt
) {
}
