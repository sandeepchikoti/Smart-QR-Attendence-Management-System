package com.university.attendance.attendance.service;

import com.university.attendance.attendance.dto.CheckInRequest;
import com.university.attendance.attendance.dto.CheckInResponse;
import com.university.attendance.attendance.dto.OverrideRequest;
import com.university.attendance.attendance.dto.OverrideResponse;
import com.university.attendance.attendance.entity.Attendance;
import com.university.attendance.attendance.entity.AttendanceStatus;
import com.university.attendance.attendance.repository.AttendanceRepository;
import com.university.attendance.audit.entity.AuditLog;
import com.university.attendance.audit.repository.AuditLogRepository;
import com.university.attendance.exception.ResourceNotFoundException;
import com.university.attendance.exception.ValidationException;
import com.university.attendance.faculty.entity.Faculty;
import com.university.attendance.faculty.repository.FacultyRepository;
import com.university.attendance.session.entity.AttendanceSession;
import com.university.attendance.session.entity.AttendanceSessionStatus;
import com.university.attendance.session.service.SessionService;
import com.university.attendance.student.entity.Student;
import com.university.attendance.student.repository.StudentRepository;
import com.university.attendance.student.repository.SubjectEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final SessionService sessionService;
    private final SubjectEnrollmentRepository subjectEnrollmentRepository;
    private final AuditLogRepository auditLogRepository;

    @Override
    @Transactional
    public CheckInResponse checkInStudent(CheckInRequest request, String studentEmail, String ipAddress) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile not found for: " + studentEmail));

        AttendanceSession session = sessionService.getSessionEntity(request.sessionId());

        if (session.getStatus() != AttendanceSessionStatus.ACTIVE) {
            throw new ValidationException("SESSION_INACTIVE", "This class session is no longer active");
        }

        // 1. Dynamic Token Validation
        boolean isTokenValid = sessionService.validateToken(request.sessionId(), request.scannedToken());
        if (!isTokenValid) {
            throw new ValidationException("TOKEN_EXPIRED", "The QR code has expired or is invalid. Please scan again");
        }

        // 2. Enrollment Check
        boolean isEnrolled = subjectEnrollmentRepository.existsByStudentIdAndSubjectId(
                student.getId(), session.getTimetable().getSubject().getId()
        );
        if (!isEnrolled) {
            throw new ValidationException("NOT_ENROLLED", "You are not enrolled in this subject");
        }

        // 3. Double Submission check
        attendanceRepository.findBySessionIdAndStudentId(session.getId(), student.getId())
                .ifPresent(a -> {
                    throw new ValidationException("ALREADY_CHECKED_IN", "You have already registered attendance for this lecture");
                });

        // 4. Geofencing check
        double distance = calculateDistance(
                request.latitude().doubleValue(),
                request.longitude().doubleValue(),
                session.getClassroom().getLatitude().doubleValue(),
                session.getClassroom().getLongitude().doubleValue()
        );

        if (distance > session.getClassroom().getGeofenceRadiusMeters()) {
            log.warn("Student {} check-in rejected. Distance: {}m, Max allowed: {}m",
                    studentEmail, distance, session.getClassroom().getGeofenceRadiusMeters());
            throw new ValidationException("OUTSIDE_GEOFENCE", "Geofence limit exceeded. You are not inside the classroom");
        }

        // 5. Device fingerprint warning check
        String flag = null;
        boolean isDuplicateDevice = attendanceRepository.existsBySessionIdAndDeviceFingerprint(
                session.getId(), request.deviceFingerprint()
        );
        if (isDuplicateDevice) {
            flag = "SUSPICIOUS_DUPLICATE_DEVICE";
            log.warn("Suspicious activity: Student {} checking in with device fingerprint already used in session {}",
                    studentEmail, session.getId());
        }

        // 6. Check Lateness (Late if checked in 15 mins after class start)
        AttendanceStatus status = AttendanceStatus.PRESENT;
        if (LocalDateTime.now().isAfter(session.getStartTime().plusMinutes(15))) {
            status = AttendanceStatus.LATE;
        }

        Attendance attendance = Attendance.builder()
                .session(session)
                .student(student)
                .checkInTime(LocalDateTime.now())
                .status(status)
                .deviceFingerprint(request.deviceFingerprint())
                .ipAddress(ipAddress)
                .latitude(request.latitude())
                .longitude(request.longitude())
                .isGpsVerified(true)
                .flags(flag)
                .build();

        Attendance saved = attendanceRepository.save(attendance);

        return new CheckInResponse(
                saved.getId(),
                saved.getStatus(),
                saved.getCheckInTime(),
                saved.isGpsVerified()
        );
    }

    @Override
    @Transactional
    public OverrideResponse overrideAttendance(Long id, OverrideRequest request, String facultyEmail) {
        Faculty faculty = facultyRepository.findByUserEmail(facultyEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found for: " + facultyEmail));

        Attendance attendance = attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found: " + id));

        // Audit update event
        String detailsJson = String.format(
                "{\"studentEmail\":\"%s\",\"subject\":\"%s\",\"previousStatus\":\"%s\",\"newStatus\":\"%s\",\"reason\":\"%s\"}",
                attendance.getStudent().getUser().getEmail(),
                attendance.getSession().getTimetable().getSubject().getName(),
                attendance.getStatus(),
                request.status(),
                request.reason()
        );

        AuditLog auditLog = AuditLog.builder()
                .actor(faculty.getUser())
                .action("MANUAL_ATTENDANCE_OVERRIDE")
                .details(detailsJson)
                .ipAddress("0.0.0.0") // will be updated by interceptors/controllers in future
                .userAgent("Server Manual Override Pipeline")
                .build();
        auditLogRepository.save(auditLog);

        attendance.setStatus(request.status());
        Attendance updated = attendanceRepository.save(attendance);

        return new OverrideResponse(
                updated.getId(),
                updated.getStatus(),
                faculty.getUser().getFirstName() + " " + faculty.getUser().getLastName(),
                LocalDateTime.now()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckInResponse> getSessionAttendance(Long sessionId, String facultyEmail) {
        // Confirm faculty owns the session
        Faculty faculty = facultyRepository.findByUserEmail(facultyEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty profile not found"));

        List<Attendance> attendances = attendanceRepository.findBySessionId(sessionId);
        return attendances.stream()
                .map(a -> new CheckInResponse(a.getId(), a.getStatus(), a.getCheckInTime(), a.isGpsVerified()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CheckInResponse> getStudentHistory(String studentEmail) {
        Student student = studentRepository.findByUserEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        return attendanceRepository.findByStudentId(student.getId()).stream()
                .map(a -> new CheckInResponse(a.getId(), a.getStatus(), a.getCheckInTime(), a.isGpsVerified()))
                .collect(Collectors.toList());
    }

    // --- GEODISTANCE HA VERSINE FORMULA ---

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371000; // Radius of the earth in meters
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in meters
    }
}
