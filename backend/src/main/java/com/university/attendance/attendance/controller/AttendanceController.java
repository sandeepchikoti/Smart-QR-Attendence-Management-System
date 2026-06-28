package com.university.attendance.attendance.controller;

import com.university.attendance.audit.Audit;
import com.university.attendance.attendance.dto.CheckInRequest;
import com.university.attendance.attendance.dto.CheckInResponse;
import com.university.attendance.attendance.dto.OverrideRequest;
import com.university.attendance.attendance.dto.OverrideResponse;
import com.university.attendance.attendance.service.AttendanceService;
import com.university.attendance.common.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/check-in")
    @PreAuthorize("hasRole('STUDENT')")
    @Audit(action = "STUDENT_CHECK_IN")
    public ResponseEntity<ApiResponse<CheckInResponse>> checkIn(
            @Valid @RequestBody CheckInRequest request,
            @AuthenticationPrincipal UserDetails userDetails,
            HttpServletRequest servletRequest
    ) {
        String ipAddress = getClientIp(servletRequest);
        CheckInResponse response = attendanceService.checkInStudent(request, userDetails.getUsername(), ipAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response));
    }

    @PutMapping("/{id}/override")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<ApiResponse<OverrideResponse>> overrideAttendance(
            @PathVariable Long id,
            @Valid @RequestBody OverrideRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        OverrideResponse response = attendanceService.overrideAttendance(id, request, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/session/{sessionId}")
    @PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<CheckInResponse>>> getSessionAttendance(
            @PathVariable Long sessionId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<CheckInResponse> response = attendanceService.getSessionAttendance(sessionId, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/my-history")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ApiResponse<List<CheckInResponse>>> getMyHistory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        List<CheckInResponse> response = attendanceService.getStudentHistory(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    // --- IP UTILITIES ---

    private String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isBlank()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
