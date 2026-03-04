package com.monkeyclub.gym.sales;

import java.math.BigDecimal;
import java.util.UUID;

public record SaleItemResponse(
        UUID productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal subtotal
) {
}
