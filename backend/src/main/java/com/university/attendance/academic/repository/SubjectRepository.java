package com.university.attendance.academic.repository;

import com.university.attendance.academic.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findAllByIsDeletedFalse();
    Optional<Subject> findByIdAndIsDeletedFalse(Long id);
    List<Subject> findBySemesterIdAndIsDeletedFalse(Long semesterId);
}
