package com.monkeyclub.gym.attendance;

import java.time.OffsetDateTime;
import java.util.UUID;

public record AttendanceResponse(
        UUID id,
        UUID clientId,
        String clientName,
        AttendanceMethod method,
        boolean accessGranted,
        String message,
        String registeredBy,
        OffsetDateTime checkInAt
) {
}
