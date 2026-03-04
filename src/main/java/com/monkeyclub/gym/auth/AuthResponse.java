package com.monkeyclub.gym.auth;

import com.monkeyclub.gym.permission.ModulePermissionResponse;

import java.util.List;

public record AuthResponse(
        String token,
        UserProfileResponse user,
        List<ModulePermissionResponse> permissions
) {
}
