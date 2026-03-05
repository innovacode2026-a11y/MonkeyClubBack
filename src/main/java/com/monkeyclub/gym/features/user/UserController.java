package com.monkeyclub.gym.features.user;

import com.monkeyclub.gym.application.port.in.user.UserUseCase;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserUseCase userUseCase;

    public UserController(UserUseCase userUseCase) {
        this.userUseCase = userUseCase;
    }

    @GetMapping
    public List<UserResponse> listUsers() {
        return userUseCase.listUsers();
    }

    @PostMapping
    public UserResponse create(@Valid @RequestBody CreateUserRequest request) {
        return userUseCase.createUser(request);
    }

    @PatchMapping("/{userId}/role")
    public UserResponse updateRole(@PathVariable UUID userId,
                                   @Valid @RequestBody UpdateUserRoleRequest request) {
        return userUseCase.updateRole(userId, request);
    }

    @PatchMapping("/{userId}/toggle-active")
    public UserResponse toggleActive(@PathVariable UUID userId) {
        return userUseCase.toggleActive(userId);
    }
}
