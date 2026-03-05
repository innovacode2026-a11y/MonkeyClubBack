package com.monkeyclub.gym.features.report;

import com.monkeyclub.gym.features.client.ClientStatus;

import java.time.LocalDate;
import java.util.UUID;

public record ExpirationReportItem(
        UUID clientId,
        String clientName,
        String document,
        String phone,
        LocalDate endDate,
        long daysToExpire,
        ClientStatus currentStatus
) {
}
