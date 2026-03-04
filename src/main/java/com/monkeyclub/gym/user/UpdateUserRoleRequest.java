package com.monkeyclub.gym.user;

import jakarta.validation.constraints.NotNull;

public record UpdateUserRoleRequest(
        @NotNull(message = "El rol es obligatorio") UserRole role
) {
}
