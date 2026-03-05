package com.monkeyclub.gym.features.auth;

import com.monkeyclub.gym.features.permission.ModulePermissionResponse;

import java.util.List;

public record AuthResponse(
        String token,
        UserProfileResponse user,
        List<ModulePermissionResponse> permissions
) {
}
