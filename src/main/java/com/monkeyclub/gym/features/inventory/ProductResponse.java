package com.monkeyclub.gym.features.inventory;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        UUID id,
        String name,
        String category,
        String barcode,
        BigDecimal price,
        Integer stock,
        Integer minStock,
        boolean active
) {
}
