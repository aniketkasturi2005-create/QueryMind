package com.querymind.auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthDtos.AuthResponse register(AuthDtos.RegisterRequest request) {

        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        String encodedPassword =
                passwordEncoder.encode(request.password());

       Role role = Role.VIEWER;

        User user = new User(
                request.username(),
                encodedPassword,
                role
        );

        User savedUser = userRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return new AuthDtos.AuthResponse(
                token,
                savedUser.getUsername(),
                savedUser.getRole()
        );
    }

    public AuthDtos.AuthResponse login(AuthDtos.LoginRequest request) {

        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() ->
                        new BadCredentialsException("Invalid username or password")
                );

        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            throw new BadCredentialsException(
                    "Invalid username or password"
            );
        }

        String token = jwtService.generateToken(user);

        return new AuthDtos.AuthResponse(
                token,
                user.getUsername(),
                user.getRole()
        );
    }
}
