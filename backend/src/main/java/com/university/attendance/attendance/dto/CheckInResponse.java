package com.university.attendance.attendance.dto;

import com.university.attendance.attendance.entity.AttendanceStatus;
import java.time.LocalDateTime;

public record CheckInResponse(
    Long attendanceId,
    AttendanceStatus status,
    LocalDateTime checkInTime,
    boolean isGpsVerified
) {}
