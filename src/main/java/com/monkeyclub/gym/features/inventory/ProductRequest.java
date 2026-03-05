package com.monkeyclub.gym.features.inventory;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "Nombre obligatorio") String name,
        @NotBlank(message = "Categoria obligatoria") String category,
        String barcode,
        @NotNull(message = "Precio obligatorio") @DecimalMin(value = "0.01", message = "Precio invalido") BigDecimal price,
        @NotNull(message = "Stock minimo obligatorio") @PositiveOrZero(message = "Stock minimo invalido") Integer minStock,
        Boolean active
) {
}
