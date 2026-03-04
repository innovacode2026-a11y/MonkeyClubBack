package com.monkeyclub.gym.auth;

import com.monkeyclub.gym.user.UserRole;

import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        String username,
        String email,
        UserRole role
) {
}
