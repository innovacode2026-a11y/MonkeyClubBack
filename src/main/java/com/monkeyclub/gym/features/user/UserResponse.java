package com.monkeyclub.gym.features.user;

import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        UserRole role,
        boolean active
) {
}
