package com.university.attendance.student.repository;

import com.university.attendance.student.entity.SubjectEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubjectEnrollmentRepository extends JpaRepository<SubjectEnrollment, Long> {
    boolean existsByStudentIdAndSubjectId(Long studentId, Long subjectId);
    List<SubjectEnrollment> findByStudentId(Long studentId);
}
