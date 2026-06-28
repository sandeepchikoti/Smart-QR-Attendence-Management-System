package com.university.attendance.session.repository;

import com.university.attendance.session.entity.AttendanceSession;
import com.university.attendance.session.entity.AttendanceSessionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceSessionRepository extends JpaRepository<AttendanceSession, Long> {
    Optional<AttendanceSession> findFirstByTimetableIdAndSessionDateAndStatus(Long timetableId, LocalDate date, AttendanceSessionStatus status);
    List<AttendanceSession> findByFacultyIdAndSessionDate(Long facultyId, LocalDate date);
}
