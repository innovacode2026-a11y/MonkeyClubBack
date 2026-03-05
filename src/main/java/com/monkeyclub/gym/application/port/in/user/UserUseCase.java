package com.monkeyclub.gym.application.port.in.user;

import com.monkeyclub.gym.features.user.CreateUserRequest;
import com.monkeyclub.gym.features.user.UpdateUserRoleRequest;
import com.monkeyclub.gym.features.user.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserUseCase {

    List<UserResponse> listUsers();

    UserResponse createUser(CreateUserRequest request);

    UserResponse updateRole(UUID userId, UpdateUserRoleRequest request);

    UserResponse toggleActive(UUID userId);
}
