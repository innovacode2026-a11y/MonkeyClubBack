package com.monkeyclub.gym.cash;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record CashSessionResponse(
        UUID id,
        String cashier,
        BigDecimal openingAmount,
        BigDecimal closingAmount,
        BigDecimal expectedAmount,
        BigDecimal difference,
        OffsetDateTime openedAt,
        OffsetDateTime closedAt,
        CashSessionStatus status
) {
}
