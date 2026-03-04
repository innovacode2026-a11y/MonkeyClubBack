package com.monkeyclub.gym.sales;

import jakarta.validation.constraints.NotBlank;

public record AnnulSaleRequest(
        @NotBlank(message = "Motivo obligatorio") String reason
) {
}
