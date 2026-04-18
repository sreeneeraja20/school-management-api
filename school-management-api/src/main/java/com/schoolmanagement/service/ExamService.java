package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.ExamRequest;
import com.schoolmanagement.dto.response.ExamResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.entity.Exam;
import com.schoolmanagement.entity.ExamType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.ExamRepository;
import com.schoolmanagement.repository.ExamTypeRepository;
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
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamTypeRepository examTypeRepository;

    public ExamResponse create(ExamRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating exam: {} for tenant: {}", request.getName(), tenantId);

        ExamType examType = examTypeRepository.findByTenantIdAndName(tenantId, request.getType())
            .orElseThrow(() -> new ResourceNotFoundException("Exam type not found: " + request.getType()));

        Exam exam = Exam.builder()
            .id(UUID.randomUUID().toString())
            .tenantId(tenantId)
            .academicYearId(request.getAcademicYearId())
            .name(request.getName())
            .examType(examType)
            .startDate(request.getStartDate())
            .endDate(request.getEndDate())
            .maxMarks(request.getMaxMarks())
            .passingMarks(request.getPassingMarks())
            .isPublished(false)
            .build();

        Exam saved = examRepository.save(exam);
        log.info("Exam created with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    public ExamResponse getById(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching exam: {} for tenant: {}", id, tenantId);

        Exam exam = examRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Exam not found");
        }

        return mapToResponse(exam);
    }

    public PageResponse<ExamResponse> getAll(int page, int size, String sortBy, String sortDir) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching all exams for tenant: {}", tenantId);

        Sort.Direction direction = Sort.Direction.fromString(sortDir.toUpperCase());
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<Exam> examPage = examRepository.findByTenantId(tenantId, pageable);

        return PageResponse.<ExamResponse>builder()
            .content(examPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList()))
            .page(examPage.getNumber())
            .size(examPage.getSize())
            .totalElements(examPage.getTotalElements())
            .totalPages(examPage.getTotalPages())
            .last(examPage.isLast())
            .first(examPage.isFirst())
            .build();
    }

    public ExamResponse update(String id, ExamRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Updating exam: {} for tenant: {}", id, tenantId);

        Exam exam = examRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Exam not found");
        }

        ExamType examType = examTypeRepository.findByTenantIdAndName(tenantId, request.getType())
            .orElseThrow(() -> new ResourceNotFoundException("Exam type not found: " + request.getType()));

        exam.setName(request.getName());
        exam.setExamType(examType);
        exam.setStartDate(request.getStartDate());
        exam.setEndDate(request.getEndDate());
        exam.setMaxMarks(request.getMaxMarks());
        exam.setPassingMarks(request.getPassingMarks());
//        exam.setUpdatedAt(LocalDateTime.now());

        Exam updated = examRepository.save(exam);
        log.info("Exam updated: {}", id);

        return mapToResponse(updated);
    }

    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Deleting exam: {} for tenant: {}", id, tenantId);

        Exam exam = examRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        if (!exam.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Exam not found");
        }

        examRepository.delete(exam);
        log.info("Exam deleted: {}", id);
    }

    private ExamResponse mapToResponse(Exam exam) {
        return ExamResponse.builder()
            .id(exam.getId())
            .academicYearId(exam.getAcademicYearId())
            .name(exam.getName())
            .type(exam.getExamType().getName())
            .startDate(exam.getStartDate())
            .endDate(exam.getEndDate())
            .maxMarks(exam.getMaxMarks())
            .passingMarks(exam.getPassingMarks())
            .isPublished(exam.getIsPublished())
            .createdAt(exam.getCreatedAt())
            .updatedAt(exam.getUpdatedAt())
            .build();
    }
}