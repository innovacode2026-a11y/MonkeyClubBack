package com.monkeyclub.gym.features.client;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String firstName,
        String lastName,
        String fullName,
        String document,
        String phone,
        String email,
        ClientStatus status,
        String internalNotes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
