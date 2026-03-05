package com.monkeyclub.gym.features.notification;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationLogResponse(
        UUID id,
        UUID clientId,
        String clientName,
        Integer daysBeforeExpiry,
        String message,
        String channel,
        OffsetDateTime createdAt
) {
}
