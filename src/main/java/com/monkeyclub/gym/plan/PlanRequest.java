package com.monkeyclub.gym.plan;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PlanRequest(
        @NotBlank(message = "Nombre obligatorio") String name,
        String description,
        @NotNull(message = "Duracion obligatoria") @Positive(message = "Duracion debe ser mayor a cero") Integer durationDays,
        @NotNull(message = "Precio obligatorio") @DecimalMin(value = "0.01", message = "Precio invalido") BigDecimal price,
        Boolean active
) {
}
