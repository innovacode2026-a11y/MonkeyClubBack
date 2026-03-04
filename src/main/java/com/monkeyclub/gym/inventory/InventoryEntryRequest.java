package com.monkeyclub.gym.inventory;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record InventoryEntryRequest(
        @NotNull(message = "Producto obligatorio") UUID productId,
        @NotNull(message = "Cantidad obligatoria") @Positive(message = "Cantidad invalida") Integer quantity,
        String provider,
        String reason
) {
}
