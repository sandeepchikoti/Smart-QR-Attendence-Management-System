package com.university.attendance.academic.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SubjectDTO(
    Long id,

    @NotNull(message = "Semester ID is required")
    Long semesterId,

    @NotBlank(message = "Subject code is required")
    String code,

    @NotBlank(message = "Subject name is required")
    String name,

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be at least 1")
    Integer credits
) {}
