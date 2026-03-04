package com.monkeyclub.gym.auth;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "El usuario o correo es obligatorio") String identifier,
        @NotBlank(message = "La contrasena es obligatoria") String password
) {
}
