package com.querymind.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {

    public User get() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                !(authentication.getPrincipal() instanceof User)) {
            return null;
        }

        return (User) authentication.getPrincipal();
    }

    public String getUsername() {
        User user = get();

        return user != null ? user.getUsername() : null;
    }

    public Role getRole() {
        User user = get();

        return user != null ? user.getRole() : null;
    }
}