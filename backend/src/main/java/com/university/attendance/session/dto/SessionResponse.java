package com.university.attendance.session.dto;

import com.university.attendance.session.entity.AttendanceSessionStatus;
import java.time.LocalDateTime;

public record SessionResponse(
    Long sessionId,
    String subjectName,
    String roomNumber,
    AttendanceSessionStatus status,
    LocalDateTime expiryTime
) {}
