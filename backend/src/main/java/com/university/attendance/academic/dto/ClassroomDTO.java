package com.university.attendance.academic.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record ClassroomDTO(
    Long id,

    @NotBlank(message = "Room number is required")
    String roomNumber,

    @NotBlank(message = "Building name is required")
    String building,

    @NotNull(message = "Floor is required")
    Integer floor,

    @NotNull(message = "Latitude is required")
    @Min(value = -90, message = "Latitude must be between -90 and 90")
    @Max(value = 90, message = "Latitude must be between -90 and 90")
    BigDecimal latitude,

    @NotNull(message = "Longitude is required")
    @Min(value = -180, message = "Longitude must be between -180 and 180")
    @Max(value = 180, message = "Longitude must be between -180 and 180")
    BigDecimal longitude,

    @NotNull(message = "Geofence radius is required")
    @Min(value = 5, message = "Geofence radius must be at least 5 meters")
    Integer geofenceRadiusMeters
) {}
