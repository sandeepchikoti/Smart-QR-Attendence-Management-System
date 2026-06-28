package com.university.attendance.attendance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record CheckInRequest(
    @NotNull(message = "Session ID is required")
    Long sessionId,

    @NotBlank(message = "Scanned token is required")
    String scannedToken,

    @NotNull(message = "Latitude is required")
    BigDecimal latitude,

    @NotNull(message = "Longitude is required")
    BigDecimal longitude,

    @NotNull(message = "GPS Accuracy is required")
    Double accuracy,

    @NotBlank(message = "Device fingerprint is required")
    String deviceFingerprint
) {}
