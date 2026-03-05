package com.monkeyclub.gym.features.membership;

import com.monkeyclub.gym.common.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SellMembershipRequest(
        @NotNull(message = "Cliente obligatorio") UUID clientId,
        @NotNull(message = "Plan obligatorio") UUID planId,
        @NotNull(message = "Medio de pago obligatorio") PaymentMethod paymentMethod,
        String notes
) {
}
