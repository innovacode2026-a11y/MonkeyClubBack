package com.monkeyclub.gym.features.attendance;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CheckInRequest(
        String codeOrDocument,
        UUID clientId,
        @NotNull(message = "Metodo obligatorio") AttendanceMethod method
) {
}
