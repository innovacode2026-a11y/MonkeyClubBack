package com.monkeyclub.gym.features.sales;

import com.monkeyclub.gym.common.PaymentMethod;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SaleResponse(
        UUID id,
        String saleNumber,
        SaleType type,
        SaleStatus status,
        String clientName,
        PaymentMethod paymentMethod,
        BigDecimal totalAmount,
        String notes,
        String annulmentReason,
        OffsetDateTime createdAt,
        List<SaleItemResponse> items
) {
}
