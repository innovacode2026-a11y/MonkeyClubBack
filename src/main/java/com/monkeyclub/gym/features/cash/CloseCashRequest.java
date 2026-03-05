package com.monkeyclub.gym.features.cash;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CloseCashRequest(
        @NotNull(message = "Monto final obligatorio") @DecimalMin(value = "0.00", message = "Monto invalido") BigDecimal closingAmount
) {
}
