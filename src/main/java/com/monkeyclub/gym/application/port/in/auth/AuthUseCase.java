package com.monkeyclub.gym.application.port.in.auth;

import com.monkeyclub.gym.features.auth.AuthResponse;
import com.monkeyclub.gym.features.auth.LoginRequest;

public interface AuthUseCase {

    AuthResponse login(LoginRequest request);

    AuthResponse me();
}
