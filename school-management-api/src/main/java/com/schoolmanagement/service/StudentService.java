package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.StudentRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.dto.response.StudentResponse;
import com.schoolmanagement.entity.Class;
import com.schoolmanagement.entity.Section;
import com.schoolmanagement.entity.Student;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.ClassRepository;
import com.schoolmanagement.repository.SectionRepository;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.security.TenantContext;
import com.schoolmanagement.util.TableQueryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudentService {

    private final StudentRepository studentRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;

    @Transactional
    public StudentResponse create(StudentRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating student: {} for tenant: {}", request.getName(), tenantId);

        // Validate class and section exist
        classRepository.findByIdAndTenantId(request.getClassId(), tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        sectionRepository.findByIdAndTenantId(request.getSectionId(), tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        Student student = Student.builder()
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

        return mapToResponse(saved, Collections.emptyMap(), Collections.emptyMap());
    }

    public StudentResponse getById(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching student: {} for tenant: {}", id, tenantId);

        Student student = studentRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        Map<String, Class> classMap = classRepository.findByTenantIdAndIdIn(tenantId, List.of(student.getClassId()))
                .stream()
                .collect(Collectors.toMap(Class::getId, Function.identity()));
        Map<String, Section> sectionMap = sectionRepository.findByTenantIdAndIdIn(tenantId, List.of(student.getSectionId()))
                .stream()
                .collect(Collectors.toMap(Section::getId, Function.identity()));
        return mapToResponse(student, classMap, sectionMap);
    }

    public PageResponse<StudentResponse> getAll(TablePageRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching students for tenant: {} - page: {}, size: {}", tenantId, request.getPage(), request.getSize());

        Pageable pageable = TableQueryUtils.buildPageable(request, "name");
        Specification<Student> specification = TableQueryUtils.buildSpecification(
                tenantId,
                request,
                List.of("name", "rollNumber", "parentName", "parentEmail", "parentPhone"),
                List.of("classId", "sectionId", "gender", "academicYearId", "rollNumber", "name")
        );

        Page<Student> studentPage = studentRepository.findAll(specification, pageable);

        Set<String> classIds = studentPage.getContent().stream().map(Student::getClassId).filter(Objects::nonNull).collect(Collectors.toSet());
        Set<String> sectionIds = studentPage.getContent().stream().map(Student::getSectionId).filter(Objects::nonNull).collect(Collectors.toSet());

        Map<String, Class> classMap = classIds.isEmpty()
                ? Collections.emptyMap()
                : classRepository.findByTenantIdAndIdIn(tenantId, new ArrayList<>(classIds))
                .stream()
                .collect(Collectors.toMap(Class::getId, Function.identity()));
        Map<String, Section> sectionMap = sectionIds.isEmpty()
                ? Collections.emptyMap()
                : sectionRepository.findByTenantIdAndIdIn(tenantId, new ArrayList<>(sectionIds))
                .stream()
                .collect(Collectors.toMap(Section::getId, Function.identity()));

        return PageResponse.<StudentResponse>builder()
            .content(studentPage.getContent().stream()
                .map(student -> mapToResponse(student, classMap, sectionMap))
                .collect(Collectors.toList()))
            .page(studentPage.getNumber())
            .size(studentPage.getSize())
            .totalElements(studentPage.getTotalElements())
            .totalPages(studentPage.getTotalPages())
            .last(studentPage.isLast())
            .first(studentPage.isFirst())
            .build();
    }

    @Transactional
    public StudentResponse update(String id, StudentRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Updating student: {} for tenant: {}", id, tenantId);

        Student student = studentRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

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

        return mapToResponse(updated, Collections.emptyMap(), Collections.emptyMap());
    }

    @Transactional
    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Deleting student: {} for tenant: {}", id, tenantId);

        Student student = studentRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found"));

        studentRepository.delete(student);
        log.info("Student deleted: {}", id);
    }

    private StudentResponse mapToResponse(Student student, Map<String, Class> classMap, Map<String, Section> sectionMap) {
        Class classEntity = classMap.get(student.getClassId());
        if (classEntity == null && student.getClassId() != null) {
            classEntity = classRepository.findByIdAndTenantId(student.getClassId(), student.getTenantId()).orElse(null);
        }
        Section section = sectionMap.get(student.getSectionId());
        if (section == null && student.getSectionId() != null) {
            section = sectionRepository.findByIdAndTenantId(student.getSectionId(), student.getTenantId()).orElse(null);
        }

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
