package com.university.attendance.academic.service;

import com.university.attendance.academic.dto.ClassroomDTO;
import com.university.attendance.academic.dto.DepartmentDTO;
import com.university.attendance.academic.dto.SubjectDTO;

import java.util.List;

public interface AcademicService {
    // Departments
    DepartmentDTO createDepartment(DepartmentDTO dto);
    List<DepartmentDTO> getAllDepartments();
    DepartmentDTO getDepartmentById(Long id);
    void deleteDepartment(Long id);

    // Classrooms
    ClassroomDTO createClassroom(ClassroomDTO dto);
    List<ClassroomDTO> getAllClassrooms();
    ClassroomDTO getClassroomById(Long id);
    void deleteClassroom(Long id);

    // Subjects
    SubjectDTO createSubject(SubjectDTO dto);
    List<SubjectDTO> getAllSubjects();
    SubjectDTO getSubjectById(Long id);
    void deleteSubject(Long id);
}
