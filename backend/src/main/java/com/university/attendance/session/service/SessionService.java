package com.university.attendance.session.service;

import com.university.attendance.session.dto.SessionResponse;
import com.university.attendance.session.dto.TokenResponse;
import com.university.attendance.session.entity.AttendanceSession;

public interface SessionService {
    SessionResponse startSession(Long timetableId, String facultyEmail);
    TokenResponse getActiveToken(Long sessionId, String facultyEmail);
    SessionResponse endSession(Long sessionId, String facultyEmail);
    boolean validateToken(Long sessionId, String tokenValue);
    
    // Internal helper interface method
    AttendanceSession getSessionEntity(Long sessionId);
}
