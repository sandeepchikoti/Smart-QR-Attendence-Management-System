package com.university.attendance.academic.repository;

import com.university.attendance.academic.entity.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
    List<Classroom> findAllByIsDeletedFalse();
    Optional<Classroom> findByIdAndIsDeletedFalse(Long id);
    Optional<Classroom> findByRoomNumberAndIsDeletedFalse(String roomNumber);
}
