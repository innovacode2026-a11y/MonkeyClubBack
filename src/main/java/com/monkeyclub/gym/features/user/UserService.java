package com.monkeyclub.gym.features.user;

import com.monkeyclub.gym.application.port.in.user.UserUseCase;

import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.features.permission.PermissionAction;
import com.monkeyclub.gym.features.permission.RolePermissionService;
import com.monkeyclub.gym.features.permission.SystemModule;
import com.monkeyclub.gym.security.CurrentUserService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UserService implements UserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       CurrentUserService currentUserService,
                       RolePermissionService rolePermissionService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    public List<UserResponse> listUsers() {
        require(SystemModule.USUARIOS, PermissionAction.VER);
        return userRepository.findAll().stream().map(this::toResponse).toList();
    }

    public UserResponse createUser(CreateUserRequest request) {
        require(SystemModule.USUARIOS, PermissionAction.CREAR);

        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(HttpStatus.CONFLICT, "El nombre de usuario ya existe");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(HttpStatus.CONFLICT, "El correo ya existe");
        }

        User user = new User();
        user.setUsername(request.username().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(request.role());
        user.setActive(true);

        return toResponse(userRepository.save(user));
    }

    public UserResponse updateRole(UUID userId, UpdateUserRoleRequest request) {
        require(SystemModule.USUARIOS, PermissionAction.EDITAR);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setRole(request.role());
        return toResponse(userRepository.save(user));
    }

    public UserResponse toggleActive(UUID userId) {
        require(SystemModule.USUARIOS, PermissionAction.EDITAR);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        user.setActive(!user.isActive());
        return toResponse(userRepository.save(user));
    }

    private void require(SystemModule module, PermissionAction action) {
        User currentUser = currentUserService.getCurrentUser();
        rolePermissionService.requirePermission(currentUser.getRole(), module, action);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.isActive());
    }
}
