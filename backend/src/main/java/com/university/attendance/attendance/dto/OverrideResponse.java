package com.university.attendance.attendance.dto;

import com.university.attendance.attendance.entity.AttendanceStatus;
import java.time.LocalDateTime;

public record OverrideResponse(
    Long attendanceId,
    AttendanceStatus status,
    String updatedBy,
    LocalDateTime updatedAt
) {}
