package com.monkeyclub.gym.cash;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OpenCashRequest(
        @NotNull(message = "Monto inicial obligatorio") @DecimalMin(value = "0.00", message = "Monto invalido") BigDecimal openingAmount
) {
}
