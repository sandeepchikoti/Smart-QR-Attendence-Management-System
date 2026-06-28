package com.university.attendance.session.service;

import com.university.attendance.academic.entity.Classroom;
import com.university.attendance.exception.ResourceNotFoundException;
import com.university.attendance.exception.ValidationException;
import com.university.attendance.faculty.entity.Faculty;
import com.university.attendance.faculty.repository.FacultyRepository;
import com.university.attendance.session.dto.SessionResponse;
import com.university.attendance.session.dto.TokenResponse;
import com.university.attendance.session.entity.AttendanceSession;
import com.university.attendance.session.entity.AttendanceSessionStatus;
import com.university.attendance.session.repository.AttendanceSessionRepository;
import com.university.attendance.timetable.entity.Timetable;
import com.university.attendance.timetable.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HexFormat;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements SessionService {

    private final AttendanceSessionRepository sessionRepository;
    private final TimetableRepository timetableRepository;
    private final FacultyRepository facultyRepository;

    @Override
    @Transactional
    public SessionResponse startSession(Long timetableId, String facultyEmail) {
        Faculty faculty = facultyRepository.findByUserEmail(facultyEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found for: " + facultyEmail));

        Timetable timetable = timetableRepository.findByIdAndIsDeletedFalse(timetableId)
                .orElseThrow(() -> new ResourceNotFoundException("Timetable entry not found: " + timetableId));

        if (!timetable.getFaculty().getId().equals(faculty.getId())) {
            throw new ValidationException("UNAUTHORIZED_SESSION", "You are not scheduled to teach this lecture");
        }

        LocalDate today = LocalDate.now();
        // Overlap check: Return active session if already exists today
        return sessionRepository.findFirstByTimetableIdAndSessionDateAndStatus(
                timetableId, today, AttendanceSessionStatus.ACTIVE
        ).map(this::mapToSessionResponse)
        .orElseGet(() -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime end = today.atTime(timetable.getEndTime());
            if (end.isBefore(now)) {
                // If lecture time has already passed, fail session start
                throw new ValidationException("LECTURE_EXPIRED", "The scheduled lecture end time has already passed");
            }

            AttendanceSession session = AttendanceSession.builder()
                    .timetable(timetable)
                    .faculty(faculty)
                    .classroom(timetable.getClassroom())
                    .sessionDate(today)
                    .startTime(now)
                    .endTime(end)
                    .tokenSecret(UUID.randomUUID().toString())
                    .status(AttendanceSessionStatus.ACTIVE)
                    .build();

            return mapToSessionResponse(sessionRepository.save(session));
        });
    }

    @Override
    @Transactional
    public TokenResponse getActiveToken(Long sessionId, String facultyEmail) {
        AttendanceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));

        if (!session.getFaculty().getUser().getEmail().equals(facultyEmail)) {
            throw new ValidationException("UNAUTHORIZED_ACCESS", "Access denied to session");
        }

        LocalDateTime now = LocalDateTime.now();
        if (session.getStatus() == AttendanceSessionStatus.ACTIVE && session.getEndTime().isBefore(now)) {
            session.setStatus(AttendanceSessionStatus.COMPLETED);
            sessionRepository.save(session);
        }

        if (session.getStatus() != AttendanceSessionStatus.ACTIVE) {
            throw new ValidationException("SESSION_INACTIVE", "Attendance session is no longer active");
        }

        // Calculate dynamic rotated token
        long window = System.currentTimeMillis() / 10000; // 10 seconds interval
        String data = session.getId() + ":" + window;
        String token = computeHmac(data, session.getTokenSecret());

        return new TokenResponse(session.getId(), token, 10000, now);
    }

    @Override
    @Transactional
    public SessionResponse endSession(Long sessionId, String facultyEmail) {
        AttendanceSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found: " + sessionId));

        if (!session.getFaculty().getUser().getEmail().equals(facultyEmail)) {
            throw new ValidationException("UNAUTHORIZED_ACCESS", "Access denied to session");
        }

        session.setStatus(AttendanceSessionStatus.COMPLETED);
        session.setEndTime(LocalDateTime.now());
        return mapToSessionResponse(sessionRepository.save(session));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean validateToken(Long sessionId, String tokenValue) {
        AttendanceSession session = sessionRepository.findById(sessionId)
                .orElse(null);

        if (session == null || session.getStatus() != AttendanceSessionStatus.ACTIVE) {
            return false;
        }

        // Validate token against current and previous 10-second windows (for latency)
        long currentWindow = System.currentTimeMillis() / 10000;
        
        for (int i = 0; i <= 1; i++) {
            long window = currentWindow - i;
            String data = session.getId() + ":" + window;
            String computedToken = computeHmac(data, session.getTokenSecret());
            if (computedToken.equals(tokenValue)) {
                return true;
            }
        }

        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceSession getSessionEntity(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Session not found with ID: " + sessionId));
    }

    // --- HELPERS ---

    private SessionResponse mapToSessionResponse(AttendanceSession session) {
        return new SessionResponse(
                session.getId(),
                session.getTimetable().getSubject().getName(),
                session.getClassroom().getRoomNumber(),
                session.getStatus(),
                session.getEndTime()
        );
    }

    private String computeHmac(String data, String key) {
        try {
            Mac sha256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256.init(secretKey);
            byte[] hash = sha256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("HMAC computation failed", e);
        }
    }
}
