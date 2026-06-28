package com.university.attendance.academic.repository;

import com.university.attendance.academic.entity.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
    List<Semester> findAllByIsDeletedFalse();
    Optional<Semester> findByIdAndIsDeletedFalse(Long id);
    List<Semester> findByBranchIdAndIsDeletedFalse(Long branchId);
}
