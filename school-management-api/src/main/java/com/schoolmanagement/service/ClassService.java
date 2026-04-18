package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.ClassRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.ClassResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.entity.Class;
import com.schoolmanagement.exception.DuplicateResourceException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.ClassRepository;
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
public class ClassService {

    private final ClassRepository classRepository;

    @Transactional
    public ClassResponse createClass(ClassRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating class: {} for tenant: {}", request.getName(), tenantId);

        if (classRepository.existsByTenantIdAndName(tenantId, request.getName())) {
            throw new DuplicateResourceException("Class already exists: " + request.getName());
        }

        Class classEntity = Class.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();

        Class saved = classRepository.save(classEntity);
        log.info("Class created with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    public ClassResponse getClassById(String id) {
        String tenantId = TenantContext.getTenantId();
        Class classEntity = classRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        return mapToResponse(classEntity);
    }

    public PageResponse<ClassResponse> searchClasses(TablePageRequest request) {
        String tenantId = TenantContext.getTenantId();
        Pageable pageable = TableQueryUtils.buildPageable(request, "sortOrder");
        Specification<Class> specification = TableQueryUtils.buildSpecification(
                tenantId,
                request,
                List.of("name"),
                List.of("name", "sortOrder")
        );
        Page<Class> page = classRepository.findAll(specification, pageable);
        return buildPageResponse(page.map(this::mapToResponse));
    }

    @Transactional
    public ClassResponse updateClass(String id, ClassRequest request) {
        String tenantId = TenantContext.getTenantId();
        Class classEntity = classRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

        classEntity.setName(request.getName());
        classEntity.setSortOrder(request.getSortOrder());

        return mapToResponse(classRepository.save(classEntity));
    }

    @Transactional
    public void deleteClass(String id) {
        String tenantId = TenantContext.getTenantId();
        Class classEntity = classRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
        classRepository.delete(classEntity);
        log.info("Class deleted: {}", id);
    }

    private PageResponse<ClassResponse> buildPageResponse(Page<ClassResponse> page) {
        return PageResponse.<ClassResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }

    private ClassResponse mapToResponse(Class classEntity) {
        return ClassResponse.builder()
                .id(classEntity.getId())
                .name(classEntity.getName())
                .sortOrder(classEntity.getSortOrder())
                .createdAt(classEntity.getCreatedAt())
                .updatedAt(classEntity.getUpdatedAt())
                .build();
    }
}
