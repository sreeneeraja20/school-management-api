package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.SectionRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.dto.response.SectionResponse;
import com.schoolmanagement.entity.Section;
import com.schoolmanagement.exception.DuplicateResourceException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.SectionRepository;
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
public class SectionService {

    private final SectionRepository sectionRepository;

    @Transactional
    public SectionResponse createSection(SectionRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating section: {} for tenant: {}", request.getName(), tenantId);

        if (sectionRepository.existsByTenantIdAndClassIdAndName(tenantId, request.getClassId(), request.getName())) {
            throw new DuplicateResourceException("Section already exists: " + request.getName());
        }

        Section section = Section.builder()
                .tenantId(tenantId)
                .classId(request.getClassId())
                .name(request.getName())
                .maxStudents(request.getMaxStudents() != null ? request.getMaxStudents() : 40)
                .build();

        Section saved = sectionRepository.save(section);
        log.info("Section created with ID: {}", saved.getId());
        return mapToResponse(saved);
    }

    public SectionResponse getSectionById(String id) {
        String tenantId = TenantContext.getTenantId();
        Section section = sectionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        return mapToResponse(section);
    }

    public PageResponse<SectionResponse> searchSections(TablePageRequest request) {
        String tenantId = TenantContext.getTenantId();
        Pageable pageable = TableQueryUtils.buildPageable(request, "name");
        Specification<Section> specification = TableQueryUtils.buildSpecification(
                tenantId,
                request,
                List.of("name"),
                List.of("name", "classId", "maxStudents")
        );
        Page<Section> page = sectionRepository.findAll(specification, pageable);
        return buildPageResponse(page.map(this::mapToResponse));
    }

    @Transactional
    public SectionResponse updateSection(String id, SectionRequest request) {
        String tenantId = TenantContext.getTenantId();
        Section section = sectionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));

        section.setName(request.getName());
        section.setMaxStudents(request.getMaxStudents());
        return mapToResponse(sectionRepository.save(section));
    }

    @Transactional
    public void deleteSection(String id) {
        String tenantId = TenantContext.getTenantId();
        Section section = sectionRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Section not found"));
        sectionRepository.delete(section);
        log.info("Section deleted: {}", id);
    }

    private PageResponse<SectionResponse> buildPageResponse(Page<SectionResponse> page) {
        return PageResponse.<SectionResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }

    private SectionResponse mapToResponse(Section section) {
        return SectionResponse.builder()
                .id(section.getId())
                .classId(section.getClassId())
                .name(section.getName())
                .maxStudents(section.getMaxStudents())
                .createdAt(section.getCreatedAt())
                .updatedAt(section.getUpdatedAt())
                .build();
    }
}
