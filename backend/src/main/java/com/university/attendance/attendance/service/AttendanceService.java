package com.university.attendance.attendance.service;

import com.university.attendance.attendance.dto.CheckInRequest;
import com.university.attendance.attendance.dto.CheckInResponse;
import com.university.attendance.attendance.dto.OverrideRequest;
import com.university.attendance.attendance.dto.OverrideResponse;

import java.util.List;

public interface AttendanceService {
    CheckInResponse checkInStudent(CheckInRequest request, String studentEmail, String ipAddress);
    OverrideResponse overrideAttendance(Long id, OverrideRequest request, String facultyEmail);
    List<CheckInResponse> getSessionAttendance(Long sessionId, String facultyEmail);
    List<CheckInResponse> getStudentHistory(String studentEmail);
}
