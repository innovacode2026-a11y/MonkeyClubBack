package com.monkeyclub.gym.report;

import com.monkeyclub.gym.client.ClientStatus;

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
