package com.monkeyclub.gym.features.inventory;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InventoryMovementResponse(
        UUID id,
        UUID productId,
        String productName,
        InventoryMovementType type,
        Integer quantity,
        String reason,
        String provider,
        String performedBy,
        OffsetDateTime createdAt
) {
}
