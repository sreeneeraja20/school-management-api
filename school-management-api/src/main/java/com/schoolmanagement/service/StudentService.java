package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.StudentRequest;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.dto.response.StudentResponse;
import com.schoolmanagement.entity.Student;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.repository.ClassRepository;
import com.schoolmanagement.repository.SectionRepository;
import com.schoolmanagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;

    public StudentResponse create(StudentRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating student: {} for tenant: {}", request.getName(), tenantId);

        // Validate class and section exist
        classRepository.findById(request.getClassId())
            .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        sectionRepository.findById(request.getSectionId())
            .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        Student student = Student.builder()
            .id(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .name(request.getName())
            .rollNumber(request.getRollNumber())
            .classId(request.getClassId())
            .sectionId(request.getSectionId())
            .academicYearId(request.getAcademicYearId())
            .dateOfBirth(request.getDateOfBirth())
            .gender(request.getGender())
            .parentName(request.getParentName())
            .parentPhone(request.getParentPhone())
            .parentEmail(request.getParentEmail())
            .address(request.getAddress())
            .photoUrl(request.getPhotoUrl())
            .build();

        Student saved = studentRepository.save(student);
        log.info("Student created with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    public StudentResponse getById(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching student: {} for tenant: {}", id, tenantId);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (!student.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Student not found");
        }

        return mapToResponse(student);
    }

    public PageResponse<StudentResponse> getAll(int page, int size, String sortBy, String sortDir) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching all students for tenant: {} - page: {}, size: {}", tenantId, page, size);

        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Student> studentPage = studentRepository.findByTenantId(tenantId, pageable);

        return PageResponse.<StudentResponse>builder()
            .content(studentPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()))
            .page(studentPage.getNumber())
            .size(studentPage.getSize())
            .totalElements(studentPage.getTotalElements())
            .totalPages(studentPage.getTotalPages())
            .last(studentPage.isLast())
            .first(studentPage.isFirst())
            .build();
    }

    public StudentResponse update(String id, StudentRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Updating student: {} for tenant: {}", id, tenantId);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (!student.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Student not found");
        }

        student.setName(request.getName());
        student.setRollNumber(request.getRollNumber());
        student.setClassId(request.getClassId());
        student.setSectionId(request.getSectionId());
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());
        student.setParentName(request.getParentName());
        student.setParentPhone(request.getParentPhone());
        student.setParentEmail(request.getParentEmail());
        student.setAddress(request.getAddress());
        student.setPhotoUrl(request.getPhotoUrl());

        Student updated = studentRepository.save(student);
        log.info("Student updated: {}", id);

        return mapToResponse(updated);
    }

    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Deleting student: {} for tenant: {}", id, tenantId);

        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        if (!student.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Student not found");
        }

        studentRepository.delete(student);
        log.info("Student deleted: {}", id);
    }

    private StudentResponse mapToResponse(Student student) {
        var classEntity = classRepository.findById(student.getClassId()).orElse(null);
        var section = sectionRepository.findById(student.getSectionId()).orElse(null);

        return StudentResponse.builder()
            .id(student.getId())
            .name(student.getName())
            .rollNumber(student.getRollNumber())
            .classId(student.getClassId())
            .className(classEntity != null ? classEntity.getName() : "")
            .sectionId(student.getSectionId())
            .sectionName(section != null ? section.getName() : "")
            .academicYearId(student.getAcademicYearId())
            .dateOfBirth(student.getDateOfBirth())
            .gender(student.getGender())
            .parentName(student.getParentName())
            .parentPhone(student.getParentPhone())
            .parentEmail(student.getParentEmail())
            .address(student.getAddress())
            .photoUrl(student.getPhotoUrl())
            .createdAt(student.getCreatedAt())
            .updatedAt(student.getUpdatedAt())
            .build();
    }
}