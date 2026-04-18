package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.*;
import com.schoolmanagement.dto.response.*;
import com.schoolmanagement.entity.*;
import com.schoolmanagement.entity.Class;
import com.schoolmanagement.exception.DuplicateResourceException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.*;
import com.schoolmanagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SetupService {

    private final AcademicYearRepository academicYearRepository;
    private final ClassRepository classRepository;
    private final SectionRepository sectionRepository;
    private final SubjectRepository subjectRepository;

    // ===================== ACADEMIC YEAR =====================

    public AcademicYearResponse createAcademicYear(AcademicYearRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating academic year: {} for tenant: {}", request.getName(), tenantId);

        if (academicYearRepository.existsByTenantIdAndName(tenantId, request.getName())) {
            throw new DuplicateResourceException("Academic year already exists: " + request.getName());
        }

        AcademicYear academicYear = AcademicYear.builder()
            .id(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .name(request.getName())
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .isActive(request.getIsActive() != null ? request.getIsActive() : false)
            .build();

        AcademicYear saved = academicYearRepository.save(academicYear);
        log.info("Academic year created with ID: {}", saved.getId());

        return mapAcademicYearToResponse(saved);
    }

    public AcademicYearResponse getAcademicYearById(String id) {
        String tenantId = TenantContext.getTenantId();
        AcademicYear academicYear = academicYearRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));

        if (!academicYear.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Academic year not found");
        }

        return mapAcademicYearToResponse(academicYear);
    }

    public PageResponse<AcademicYearResponse> getAllAcademicYears(int page, int size) {
        String tenantId = TenantContext.getTenantId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("startDate").descending());
        Page<AcademicYear> academicYearPage = academicYearRepository.findByTenantId(tenantId, pageable);

        return PageResponse.<AcademicYearResponse>builder()
            .content(academicYearPage.getContent().stream()
                .map(this::mapAcademicYearToResponse)
                .collect(Collectors.toList()))
            .page(academicYearPage.getNumber())
            .size(academicYearPage.getSize())
            .totalElements(academicYearPage.getTotalElements())
            .totalPages(academicYearPage.getTotalPages())
            .last(academicYearPage.isLast())
            .first(academicYearPage.isFirst())
            .build();
    }

    public AcademicYearResponse updateAcademicYear(String id, AcademicYearRequest request) {
        String tenantId = TenantContext.getTenantId();
        AcademicYear academicYear = academicYearRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));

        if (!academicYear.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Academic year not found");
        }

        academicYear.setName(request.getName());
        academicYear.setStartDate(request.getStartDate());
        academicYear.setEndDate(request.getEndDate());
        academicYear.setIsActive(request.getIsActive());
//        academicYear.setUpdatedAt(LocalDateTime.now());

        AcademicYear updated = academicYearRepository.save(academicYear);
        return mapAcademicYearToResponse(updated);
    }

    public void deleteAcademicYear(String id) {
        String tenantId = TenantContext.getTenantId();
        AcademicYear academicYear = academicYearRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));

        if (!academicYear.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Academic year not found");
        }

        academicYearRepository.delete(academicYear);
        log.info("Academic year deleted: {}", id);
    }

    // ===================== CLASS =====================

    public ClassResponse createClass(ClassRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating class: {} for tenant: {}", request.getName(), tenantId);

        if (classRepository.existsByTenantIdAndName(tenantId, request.getName())) {
            throw new DuplicateResourceException("Class already exists: " + request.getName());
        }

        Class classEntity = Class.builder()
            .id(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .name(request.getName())
            .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
            .build();

        Class saved = classRepository.save(classEntity);
        log.info("Class created with ID: {}", saved.getId());

        return mapClassToResponse(saved);
    }

    public ClassResponse getClassById(String id) {
        String tenantId = TenantContext.getTenantId();
        Class classEntity = classRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

        if (!classEntity.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Class not found");
        }

        return mapClassToResponse(classEntity);
    }

    public PageResponse<ClassResponse> getAllClasses(int page, int size) {
        String tenantId = TenantContext.getTenantId();
        Pageable pageable = PageRequest.of(page, size, Sort.by("sortOrder"));
        Page<Class> classPage = classRepository.findByTenantId(tenantId, pageable);

        return PageResponse.<ClassResponse>builder()
            .content(classPage.getContent().stream()
                .map(this::mapClassToResponse)
                .collect(Collectors.toList()))
            .page(classPage.getNumber())
            .size(classPage.getSize())
            .totalElements(classPage.getTotalElements())
            .totalPages(classPage.getTotalPages())
            .last(classPage.isLast())
            .first(classPage.isFirst())
            .build();
    }

    public ClassResponse updateClass(String id, ClassRequest request) {
        String tenantId = TenantContext.getTenantId();
        Class classEntity = classRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

        if (!classEntity.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Class not found");
        }

        classEntity.setName(request.getName());
        classEntity.setSortOrder(request.getSortOrder());
//        classEntity.setUpdatedAt(LocalDateTime.now());

        Class updated = classRepository.save(classEntity);
        return mapClassToResponse(updated);
    }

    public void deleteClass(String id) {
        String tenantId = TenantContext.getTenantId();
        Class classEntity = classRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

        if (!classEntity.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Class not found");
        }

        classRepository.delete(classEntity);
        log.info("Class deleted: {}", id);
    }

    // ===================== SECTION =====================

    public SectionResponse createSection(SectionRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating section: {} for tenant: {}", request.getName(), tenantId);

        if (sectionRepository.existsByTenantIdAndClassIdAndName(tenantId, request.getClassId(), request.getName())) {
            throw new DuplicateResourceException("Section already exists: " + request.getName());
        }

        Section section = Section.builder()
            .id(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .classId(request.getClassId())
            .name(request.getName())
            .maxStudents(request.getMaxStudents() != null ? request.getMaxStudents() : 40)
            .build();

        Section saved = sectionRepository.save(section);
        log.info("Section created with ID: {}", saved.getId());

        return mapSectionToResponse(saved);
    }

    public SectionResponse getSectionById(String id) {
        String tenantId = TenantContext.getTenantId();
        Section section = sectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        if (!section.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Section not found");
        }

        return mapSectionToResponse(section);
    }

    public PageResponse<SectionResponse> getAllSections(int page, int size) {
        String tenantId = TenantContext.getTenantId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Section> sectionPage = sectionRepository.findByTenantId(tenantId, pageable);

        return PageResponse.<SectionResponse>builder()
            .content(sectionPage.getContent().stream()
                .map(this::mapSectionToResponse)
                .collect(Collectors.toList()))
            .page(sectionPage.getNumber())
            .size(sectionPage.getSize())
            .totalElements(sectionPage.getTotalElements())
            .totalPages(sectionPage.getTotalPages())
            .last(sectionPage.isLast())
            .first(sectionPage.isFirst())
            .build();
    }

    public SectionResponse updateSection(String id, SectionRequest request) {
        String tenantId = TenantContext.getTenantId();
        Section section = sectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        if (!section.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Section not found");
        }

        section.setName(request.getName());
        section.setMaxStudents(request.getMaxStudents());
//        section.setUpdatedAt(LocalDateTime.now());

        Section updated = sectionRepository.save(section);
        return mapSectionToResponse(updated);
    }

    public void deleteSection(String id) {
        String tenantId = TenantContext.getTenantId();
        Section section = sectionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        if (!section.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Section not found");
        }

        sectionRepository.delete(section);
        log.info("Section deleted: {}", id);
    }

    // ===================== SUBJECT =====================

    public SubjectResponse createSubject(SubjectRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating subject: {} for tenant: {}", request.getName(), tenantId);

        if (subjectRepository.existsByTenantIdAndName(tenantId, request.getName())) {
            throw new DuplicateResourceException("Subject already exists: " + request.getName());
        }

        Subject subject = Subject.builder()
            .id(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .name(request.getName())
            .code(request.getCode())
            .build();

        Subject saved = subjectRepository.save(subject);
        log.info("Subject created with ID: {}", saved.getId());

        return mapSubjectToResponse(saved);
    }

    public SubjectResponse getSubjectById(String id) {
        String tenantId = TenantContext.getTenantId();
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        if (!subject.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Subject not found");
        }

        return mapSubjectToResponse(subject);
    }

    public PageResponse<SubjectResponse> getAllSubjects(int page, int size) {
        String tenantId = TenantContext.getTenantId();
        Pageable pageable = PageRequest.of(page, size);
        Page<Subject> subjectPage = subjectRepository.findByTenantId(tenantId, pageable);

        return PageResponse.<SubjectResponse>builder()
            .content(subjectPage.getContent().stream()
                .map(this::mapSubjectToResponse)
                .collect(Collectors.toList()))
            .page(subjectPage.getNumber())
            .size(subjectPage.getSize())
            .totalElements(subjectPage.getTotalElements())
            .totalPages(subjectPage.getTotalPages())
            .last(subjectPage.isLast())
            .first(subjectPage.isFirst())
            .build();
    }

    public SubjectResponse updateSubject(String id, SubjectRequest request) {
        String tenantId = TenantContext.getTenantId();
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        if (!subject.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Subject not found");
        }

        subject.setName(request.getName());
        subject.setCode(request.getCode());
//        subject.setUpdatedAt(LocalDateTime.now());

        Subject updated = subjectRepository.save(subject);
        return mapSubjectToResponse(updated);
    }

    public void deleteSubject(String id) {
        String tenantId = TenantContext.getTenantId();
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        if (!subject.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Subject not found");
        }

        subjectRepository.delete(subject);
        log.info("Subject deleted: {}", id);
    }

    // ===================== MAPPERS =====================

    private AcademicYearResponse mapAcademicYearToResponse(AcademicYear academicYear) {
        return AcademicYearResponse.builder()
            .id(academicYear.getId())
            .name(academicYear.getName())
            .startDate(academicYear.getStartDate())
            .endDate(academicYear.getEndDate())
            .isActive(academicYear.getIsActive())
            .createdAt(academicYear.getCreatedAt())
            .updatedAt(academicYear.getUpdatedAt())
            .build();
    }

    private ClassResponse mapClassToResponse(Class classEntity) {
        return ClassResponse.builder()
            .id(classEntity.getId())
            .name(classEntity.getName())
            .sortOrder(classEntity.getSortOrder())
            .createdAt(classEntity.getCreatedAt())
            .updatedAt(classEntity.getUpdatedAt())
            .build();
    }

    private SectionResponse mapSectionToResponse(Section section) {
        return SectionResponse.builder()
            .id(section.getId())
            .classId(section.getClassId())
            .name(section.getName())
            .maxStudents(section.getMaxStudents())
            .createdAt(section.getCreatedAt())
            .updatedAt(section.getUpdatedAt())
            .build();
    }

    private SubjectResponse mapSubjectToResponse(Subject subject) {
        return SubjectResponse.builder()
            .id(subject.getId())
            .name(subject.getName())
            .code(subject.getCode())
            .createdAt(subject.getCreatedAt())
            .updatedAt(subject.getUpdatedAt())
            .build();
    }
}