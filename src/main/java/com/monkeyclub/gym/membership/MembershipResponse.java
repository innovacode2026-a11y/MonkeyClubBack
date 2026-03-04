package com.monkeyclub.gym.membership;

import com.monkeyclub.gym.client.ClientStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MembershipResponse(
        UUID id,
        UUID clientId,
        String clientName,
        ClientStatus clientStatus,
        UUID planId,
        String planName,
        BigDecimal planPrice,
        LocalDate startDate,
        LocalDate endDate,
        MembershipStatus status,
        String receiptNumber
) {
}
