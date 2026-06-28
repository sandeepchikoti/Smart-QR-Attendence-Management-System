package com.university.attendance.user.service;

import com.university.attendance.user.dto.LoginRequest;
import com.university.attendance.user.dto.LoginResponse;
import com.university.attendance.user.dto.TokenRefreshResponse;

public interface AuthService {
    AuthResult login(LoginRequest request);
    TokenRefreshResponse refreshToken(String token);
    void logout(String email);

    record AuthResult(
        LoginResponse response,
        String refreshToken
    ) {}
}
