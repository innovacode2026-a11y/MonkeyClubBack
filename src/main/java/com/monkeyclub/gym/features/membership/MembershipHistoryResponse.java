package com.monkeyclub.gym.features.membership;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record MembershipHistoryResponse(
        UUID id,
        MembershipHistoryAction action,
        String planName,
        LocalDate startDate,
        LocalDate previousEndDate,
        LocalDate newEndDate,
        String receiptNumber,
        String performedBy,
        OffsetDateTime createdAt
) {
}
