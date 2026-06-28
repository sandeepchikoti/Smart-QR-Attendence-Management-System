package com.university.attendance.academic.service;

import com.university.attendance.academic.dto.ClassroomDTO;
import com.university.attendance.academic.dto.DepartmentDTO;
import com.university.attendance.academic.dto.SubjectDTO;
import com.university.attendance.academic.entity.Classroom;
import com.university.attendance.academic.entity.Department;
import com.university.attendance.academic.entity.Semester;
import com.university.attendance.academic.entity.Subject;
import com.university.attendance.academic.repository.ClassroomRepository;
import com.university.attendance.academic.repository.DepartmentRepository;
import com.university.attendance.academic.repository.SemesterRepository;
import com.university.attendance.academic.repository.SubjectRepository;
import com.university.attendance.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AcademicServiceImpl implements AcademicService {

    private final DepartmentRepository departmentRepository;
    private final ClassroomRepository classroomRepository;
    private final SubjectRepository subjectRepository;
    private final SemesterRepository semesterRepository;

    // --- DEPARTMENTS ---

    @Override
    @Transactional
    public DepartmentDTO createDepartment(DepartmentDTO dto) {
        Department department = Department.builder()
                .name(dto.name())
                .code(dto.code())
                .isDeleted(false)
                .build();
        Department saved = departmentRepository.save(department);
        return mapToDepartmentDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        return departmentRepository.findAllByIsDeletedFalse().stream()
                .map(this::mapToDepartmentDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        return mapToDepartmentDTO(department);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + id));
        department.setDeleted(true);
        departmentRepository.save(department);
    }

    // --- CLASSROOMS ---

    @Override
    @Transactional
    public ClassroomDTO createClassroom(ClassroomDTO dto) {
        Classroom classroom = Classroom.builder()
                .roomNumber(dto.roomNumber())
                .building(dto.building())
                .floor(dto.floor())
                .latitude(dto.latitude())
                .longitude(dto.longitude())
                .geofenceRadiusMeters(dto.geofenceRadiusMeters())
                .isDeleted(false)
                .build();
        Classroom saved = classroomRepository.save(classroom);
        return mapToClassroomDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClassroomDTO> getAllClassrooms() {
        return classroomRepository.findAllByIsDeletedFalse().stream()
                .map(this::mapToClassroomDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClassroomDTO getClassroomById(Long id) {
        Classroom classroom = classroomRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with ID: " + id));
        return mapToClassroomDTO(classroom);
    }

    @Override
    @Transactional
    public void deleteClassroom(Long id) {
        Classroom classroom = classroomRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Classroom not found with ID: " + id));
        classroom.setDeleted(true);
        classroomRepository.save(classroom);
    }

    // --- SUBJECTS ---

    @Override
    @Transactional
    public SubjectDTO createSubject(SubjectDTO dto) {
        Semester semester = semesterRepository.findByIdAndIsDeletedFalse(dto.semesterId())
                .orElseThrow(() -> new ResourceNotFoundException("Semester not found with ID: " + dto.semesterId()));

        Subject subject = Subject.builder()
                .semester(semester)
                .code(dto.code())
                .name(dto.name())
                .credits(dto.credits())
                .isDeleted(false)
                .build();
        Subject saved = subjectRepository.save(subject);
        return mapToSubjectDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAllByIsDeletedFalse().stream()
                .map(this::mapToSubjectDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SubjectDTO getSubjectById(Long id) {
        Subject subject = subjectRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
        return mapToSubjectDTO(subject);
    }

    @Override
    @Transactional
    public void deleteSubject(Long id) {
        Subject subject = subjectRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
        subject.setDeleted(true);
        subjectRepository.save(subject);
    }

    // --- MAPPERS ---

    private DepartmentDTO mapToDepartmentDTO(Department department) {
        return new DepartmentDTO(department.getId(), department.getName(), department.getCode());
    }

    private ClassroomDTO mapToClassroomDTO(Classroom classroom) {
        return new ClassroomDTO(
                classroom.getId(),
                classroom.getRoomNumber(),
                classroom.getBuilding(),
                classroom.getFloor(),
                classroom.getLatitude(),
                classroom.getLongitude(),
                classroom.getGeofenceRadiusMeters()
        );
    }

    private SubjectDTO mapToSubjectDTO(Subject subject) {
        return new SubjectDTO(
                subject.getId(),
                subject.getSemester().getId(),
                subject.getCode(),
                subject.getName(),
                subject.getCredits()
        );
    }
}
