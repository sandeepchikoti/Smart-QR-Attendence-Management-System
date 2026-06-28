package com.university.attendance.academic.repository;

import com.university.attendance.academic.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findAllByIsDeletedFalse();
    Optional<Branch> findByIdAndIsDeletedFalse(Long id);
    List<Branch> findByCourseIdAndIsDeletedFalse(Long courseId);
}
