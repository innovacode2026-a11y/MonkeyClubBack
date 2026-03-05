package com.monkeyclub.gym.features.inventory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record InventoryAdjustmentRequest(
        @NotNull(message = "Producto obligatorio") UUID productId,
        @NotNull(message = "Cantidad obligatoria") @Positive(message = "Cantidad invalida") Integer quantity,
        @NotNull(message = "Debe indicar si suma o resta") Boolean increase,
        @NotBlank(message = "Motivo obligatorio") String reason
) {
}
