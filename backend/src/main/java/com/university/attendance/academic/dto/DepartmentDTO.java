package com.university.attendance.academic.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentDTO(
    Long id,

    @NotBlank(message = "Department name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    String name,

    @NotBlank(message = "Department code is required")
    @Size(max = 10, message = "Code must not exceed 10 characters")
    String code
) {}
