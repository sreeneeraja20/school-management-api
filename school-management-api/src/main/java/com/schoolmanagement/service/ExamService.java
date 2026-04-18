package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.ExamRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.ExamResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.entity.Exam;
import com.schoolmanagement.entity.ExamType;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.ExamRepository;
import com.schoolmanagement.repository.ExamTypeRepository;
import com.schoolmanagement.security.TenantContext;
import com.schoolmanagement.util.TableQueryUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExamService {

    private final ExamRepository examRepository;
    private final ExamTypeRepository examTypeRepository;

    @Transactional
    public ExamResponse create(ExamRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating exam: {} for tenant: {}", request.getName(), tenantId);

        ExamType examType = examTypeRepository.findByTenantIdAndName(tenantId, request.getType())
            .orElseThrow(() -> new ResourceNotFoundException("Exam type not found: " + request.getType()));

        Exam exam = Exam.builder()
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

        Exam exam = examRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

        return mapToResponse(exam);
    }

    public PageResponse<ExamResponse> getAll(TablePageRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching all exams for tenant: {}", tenantId);

        Pageable pageable = TableQueryUtils.buildPageable(request, "startDate");
        Specification<Exam> specification = TableQueryUtils.buildSpecification(
                tenantId,
                request,
                List.of("name", "examType.name"),
                List.of("name", "academicYearId", "isPublished", "examType.name")
        );

        Page<Exam> examPage = examRepository.findAll(specification, pageable);

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

    @Transactional
    public ExamResponse update(String id, ExamRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Updating exam: {} for tenant: {}", id, tenantId);

        Exam exam = examRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

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

    @Transactional
    public void delete(String id) {
        String tenantId = TenantContext.getTenantId();
        log.info("Deleting exam: {} for tenant: {}", id, tenantId);

        Exam exam = examRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));

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
