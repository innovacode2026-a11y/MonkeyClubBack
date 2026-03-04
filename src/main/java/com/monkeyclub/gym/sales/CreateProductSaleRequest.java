package com.monkeyclub.gym.sales;

import com.monkeyclub.gym.common.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateProductSaleRequest(
        UUID clientId,
        @NotNull(message = "Medio de pago obligatorio") PaymentMethod paymentMethod,
        String notes,
        @NotEmpty(message = "Debe incluir al menos un producto") List<@Valid ProductSaleItemRequest> items
) {
}
