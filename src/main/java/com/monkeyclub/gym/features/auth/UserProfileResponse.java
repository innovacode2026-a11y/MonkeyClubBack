package com.monkeyclub.gym.features.auth;

import com.monkeyclub.gym.features.user.UserRole;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String email,
        UserRole role
) {
}
