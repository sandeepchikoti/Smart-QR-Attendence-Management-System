package com.university.attendance.academic.repository;

import com.university.attendance.academic.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByIsDeletedFalse();
    Optional<Course> findByIdAndIsDeletedFalse(Long id);
    List<Course> findByDepartmentIdAndIsDeletedFalse(Long departmentId);
}
