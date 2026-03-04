package com.monkeyclub.gym.report;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record IncomeReportResponse(
        LocalDate from,
        LocalDate to,
        BigDecimal totalMemberships,
        BigDecimal totalProducts,
        BigDecimal totalGeneral,
        List<PaymentMethodTotal> totalsByPaymentMethod
) {
}
