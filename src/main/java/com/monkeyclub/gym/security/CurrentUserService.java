package com.monkeyclub.gym.security;

import com.monkeyclub.gym.common.BusinessException;
import com.monkeyclub.gym.features.user.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }
        return user;
    }
}
