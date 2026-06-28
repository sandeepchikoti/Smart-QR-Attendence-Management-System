package com.university.attendance.session.dto;

import jakarta.validation.constraints.NotNull;

public record StartSessionRequest(
    @NotNull(message = "Timetable ID is required")
    Long timetableId
) {}
