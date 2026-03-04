package com.monkeyclub.gym.sales;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record ProductSaleItemRequest(
        @NotNull(message = "Producto obligatorio") UUID productId,
        @NotNull(message = "Cantidad obligatoria") @Positive(message = "Cantidad invalida") Integer quantity
) {
}
