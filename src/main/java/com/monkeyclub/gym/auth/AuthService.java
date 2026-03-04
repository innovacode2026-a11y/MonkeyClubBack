package com.monkeyclub.gym.auth;

import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.permission.ModulePermissionResponse;
import com.monkeyclub.gym.permission.RolePermissionService;
import com.monkeyclub.gym.security.CurrentUserService;
import com.monkeyclub.gym.security.JwtService;
import com.monkeyclub.gym.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final CurrentUserService currentUserService;
    private final RolePermissionService rolePermissionService;

    public AuthService(AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       CurrentUserService currentUserService,
                       RolePermissionService rolePermissionService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.currentUserService = currentUserService;
        this.rolePermissionService = rolePermissionService;
    }

    public AuthResponse login(LoginRequest request) {
        User user;
        try {
            user = (User) authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.identifier(), request.password()))
                    .getPrincipal();
        } catch (BadCredentialsException ex) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Credenciales incorrectas");
        }

        return buildResponse(user);
    }

    public AuthResponse me() {
        User user = currentUserService.getCurrentUser();
        return buildResponse(user);
    }

    private AuthResponse buildResponse(User user) {
        String token = jwtService.generateToken(user);
        List<ModulePermissionResponse> permissions = rolePermissionService.permissionsFor(user.getRole())
                .entrySet()
                .stream()
                .map(e -> new ModulePermissionResponse(e.getKey(), e.getValue()))
                .toList();

        UserProfileResponse profile = new UserProfileResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
        return new AuthResponse(token, profile, permissions);
    }
}
