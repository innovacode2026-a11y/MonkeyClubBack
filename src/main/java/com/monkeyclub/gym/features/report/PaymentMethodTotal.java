package com.monkeyclub.gym.features.report;

import com.monkeyclub.gym.common.PaymentMethod;

import java.math.BigDecimal;

public record PaymentMethodTotal(
        PaymentMethod paymentMethod,
        BigDecimal total
) {
}
