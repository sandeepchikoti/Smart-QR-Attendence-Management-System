package com.university.attendance.user.service;

import com.university.attendance.exception.ResourceNotFoundException;
import com.university.attendance.exception.ValidationException;
import com.university.attendance.security.JwtService;
import com.university.attendance.user.dto.LoginRequest;
import com.university.attendance.user.dto.LoginResponse;
import com.university.attendance.user.dto.TokenRefreshResponse;
import com.university.attendance.user.entity.RefreshToken;
import com.university.attendance.user.entity.User;
import com.university.attendance.user.repository.RefreshTokenRepository;
import com.university.attendance.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${app.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    @Override
    @Transactional
    public AuthResult login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = userRepository.findByEmailAndIsDeletedFalse(request.email())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + request.email()));

        String accessToken = jwtService.generateToken(user);
        
        // Delete existing refresh tokens for the user
        refreshTokenRepository.deleteByUser(user);

        // Generate and save new refresh token
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(refreshExpirationMs))
                .build();
        refreshTokenRepository.save(refreshToken);

        LoginResponse loginResponse = new LoginResponse(
                accessToken,
                user.getRole(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail()
        );

        return new AuthResult(loginResponse, refreshToken.getToken());
    }

    @Override
    @Transactional(readOnly = true)
    public TokenRefreshResponse refreshToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new ValidationException("REFRESH_TOKEN_INVALID", "Invalid refresh token"));

        if (token.getExpiryDate().isBefore(Instant.now())) {
            // Need to delete expired token
            throw new ValidationException("REFRESH_TOKEN_EXPIRED", "Refresh token has expired. Please login again");
        }

        String accessToken = jwtService.generateToken(token.getUser());
        return new TokenRefreshResponse(accessToken);
    }

    @Override
    @Transactional
    public void logout(String email) {
        User user = userRepository.findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        refreshTokenRepository.deleteByUser(user);
    }
}
