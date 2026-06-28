package com.university.attendance.academic.controller;

import com.university.attendance.academic.dto.ClassroomDTO;
import com.university.attendance.academic.dto.DepartmentDTO;
import com.university.attendance.academic.dto.SubjectDTO;
import com.university.attendance.academic.service.AcademicService;
import com.university.attendance.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AcademicController {

    private final AcademicService academicService;

    // --- DEPARTMENTS ---

    @PostMapping("/departments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<DepartmentDTO>> createDepartment(@Valid @RequestBody DepartmentDTO dto) {
        DepartmentDTO result = academicService.createDepartment(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @GetMapping("/departments")
    public ResponseEntity<ApiResponse<List<DepartmentDTO>>> getAllDepartments() {
        List<DepartmentDTO> result = academicService.getAllDepartments();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/departments/{id}")
    public ResponseEntity<ApiResponse<DepartmentDTO>> getDepartmentById(@PathVariable Long id) {
        DepartmentDTO result = academicService.getDepartmentById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/departments/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteDepartment(@PathVariable Long id) {
        academicService.deleteDepartment(id);
        return ResponseEntity.ok(ApiResponse.success("Department soft-deleted successfully"));
    }

    // --- CLASSROOMS ---

    @PostMapping("/classrooms")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ClassroomDTO>> createClassroom(@Valid @RequestBody ClassroomDTO dto) {
        ClassroomDTO result = academicService.createClassroom(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @GetMapping("/classrooms")
    public ResponseEntity<ApiResponse<List<ClassroomDTO>>> getAllClassrooms() {
        List<ClassroomDTO> result = academicService.getAllClassrooms();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/classrooms/{id}")
    public ResponseEntity<ApiResponse<ClassroomDTO>> getClassroomById(@PathVariable Long id) {
        ClassroomDTO result = academicService.getClassroomById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/classrooms/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteClassroom(@PathVariable Long id) {
        academicService.deleteClassroom(id);
        return ResponseEntity.ok(ApiResponse.success("Classroom soft-deleted successfully"));
    }

    // --- SUBJECTS ---

    @PostMapping("/subjects")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<SubjectDTO>> createSubject(@Valid @RequestBody SubjectDTO dto) {
        SubjectDTO result = academicService.createSubject(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(result));
    }

    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<SubjectDTO>>> getAllSubjects() {
        List<SubjectDTO> result = academicService.getAllSubjects();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/subjects/{id}")
    public ResponseEntity<ApiResponse<SubjectDTO>> getSubjectById(@PathVariable Long id) {
        SubjectDTO result = academicService.getSubjectById(id);
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/subjects/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteSubject(@PathVariable Long id) {
        academicService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject soft-deleted successfully"));
    }
}
