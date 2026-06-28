package com.university.attendance.timetable.repository;

import com.university.attendance.timetable.entity.Timetable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findAllByIsDeletedFalse();
    Optional<Timetable> findByIdAndIsDeletedFalse(Long id);
    List<Timetable> findByFacultyIdAndIsDeletedFalse(Long facultyId);
    List<Timetable> findByFacultyIdAndDayOfWeekAndIsDeletedFalse(Long facultyId, DayOfWeek dayOfWeek);
    List<Timetable> findByClassroomIdAndDayOfWeekAndIsDeletedFalse(Long classroomId, DayOfWeek dayOfWeek);
}
