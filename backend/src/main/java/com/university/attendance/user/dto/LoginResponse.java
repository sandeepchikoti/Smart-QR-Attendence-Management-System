package com.university.attendance.user.dto;

import com.university.attendance.user.entity.Role;

public record LoginResponse(
    String accessToken,
    Role role,
    String firstName,
    String lastName,
    String email
) {}
