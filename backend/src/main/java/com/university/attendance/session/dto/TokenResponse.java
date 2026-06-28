package com.university.attendance.session.dto;

import java.time.LocalDateTime;

public record TokenResponse(
    Long sessionId,
    String token,
    long rotatesInMs,
    LocalDateTime timestamp
) {}
