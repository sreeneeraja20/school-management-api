package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.SubjectRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.dto.response.SubjectResponse;
import com.schoolmanagement.entity.Subject;
import com.schoolmanagement.exception.DuplicateResourceException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.SubjectRepository;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubjectService {

    private final SubjectRepository subjectRepository;

    @Transactional
    public SubjectResponse createSubject(SubjectRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating subject: {} for tenant: {}", request.getName(), tenantId);

        if (subjectRepository.existsByTenantIdAndName(tenantId, request.getName())) {
            throw new DuplicateResourceException("Subject already exists: " + request.getName());
        }

        Subject subject = Subject.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .code(request.getCode())
                .build();

        Subject saved = subjectRepository.save(subject);
        log.info("Subject created with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    public SubjectResponse getSubjectById(String id) {
        String tenantId = TenantContext.getTenantId();
        Subject subject = subjectRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        return mapToResponse(subject);
    }

    public PageResponse<SubjectResponse> searchSubjects(TablePageRequest request) {
        String tenantId = TenantContext.getTenantId();
        Pageable pageable = TableQueryUtils.buildPageable(request, "name");
        Specification<Subject> specification = TableQueryUtils.buildSpecification(
                tenantId,
                request,
                List.of("name", "code"),
                List.of("name", "code")
        );
        Page<Subject> page = subjectRepository.findAll(specification, pageable);
        return buildPageResponse(page.map(this::mapToResponse));
    }

    @Transactional
    public SubjectResponse updateSubject(String id, SubjectRequest request) {
        String tenantId = TenantContext.getTenantId();
        Subject subject = subjectRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));

        subject.setName(request.getName());
        subject.setCode(request.getCode());
        return mapToResponse(subjectRepository.save(subject));
    }

    @Transactional
    public void deleteSubject(String id) {
        String tenantId = TenantContext.getTenantId();
        Subject subject = subjectRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        subjectRepository.delete(subject);
        log.info("Subject deleted: {}", id);
    }

    private PageResponse<SubjectResponse> buildPageResponse(Page<SubjectResponse> page) {
        return PageResponse.<SubjectResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }

    private SubjectResponse mapToResponse(Subject subject) {
        return SubjectResponse.builder()
                .id(subject.getId())
                .name(subject.getName())
                .code(subject.getCode())
                .createdAt(subject.getCreatedAt())
                .updatedAt(subject.getUpdatedAt())
                .build();
    }
}
