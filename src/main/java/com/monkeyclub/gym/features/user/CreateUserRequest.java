package com.monkeyclub.gym.features.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "El usuario es obligatorio") String username,
        @Email(message = "Correo invalido") @NotBlank(message = "El correo es obligatorio") String email,
        @NotBlank(message = "La contrasena es obligatoria") @Size(min = 6, message = "La contrasena debe tener al menos 6 caracteres") String password,
        @NotNull(message = "El rol es obligatorio") UserRole role
) {
}
