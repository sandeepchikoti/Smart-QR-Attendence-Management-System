package com.university.attendance.attendance.dto;

import com.university.attendance.attendance.entity.AttendanceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OverrideRequest(
    @NotNull(message = "Attendance status is required")
    AttendanceStatus status,

    @NotBlank(message = "Override reason is required")
    @Size(min = 10, max = 255, message = "Reason must be between 10 and 255 characters")
    String reason
) {}
