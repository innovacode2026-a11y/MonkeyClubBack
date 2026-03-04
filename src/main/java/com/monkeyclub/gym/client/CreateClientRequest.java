package com.monkeyclub.gym.client;

import jakarta.validation.constraints.NotBlank;

public record CreateClientRequest(
        @NotBlank(message = "Nombres es obligatorio") String firstName,
        @NotBlank(message = "Apellidos es obligatorio") String lastName,
        @NotBlank(message = "Documento es obligatorio") String document,
        @NotBlank(message = "Telefono es obligatorio") String phone,
        String email,
        String internalNotes
) {
}
