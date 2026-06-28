package com.university.attendance.attendance.repository;

import com.university.attendance.attendance.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findBySessionIdAndStudentId(Long sessionId, Long studentId);
    List<Attendance> findBySessionId(Long sessionId);
    List<Attendance> findByStudentId(Long studentId);
    boolean existsBySessionIdAndDeviceFingerprint(Long sessionId, String deviceFingerprint);
    long countByStudentId(Long studentId);
    
    // Count how many classes student was PRESENT or LATE
    long countByStudentIdAndStatusIn(Long studentId, List<com.university.attendance.attendance.entity.AttendanceStatus> statuses);
}
