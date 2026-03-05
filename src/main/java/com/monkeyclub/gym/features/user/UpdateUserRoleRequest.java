package com.monkeyclub.gym.features.user;

import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull(message = "El rol es obligatorio") UserRole role
) {
}
