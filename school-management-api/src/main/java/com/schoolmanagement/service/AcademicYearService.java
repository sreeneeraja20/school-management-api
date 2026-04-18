package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.AcademicYearRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.AcademicYearResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.entity.AcademicYear;
import com.schoolmanagement.exception.DuplicateResourceException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.AcademicYearRepository;
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
public class AcademicYearService {

    private final AcademicYearRepository academicYearRepository;

    @Transactional
    public AcademicYearResponse createAcademicYear(AcademicYearRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Creating academic year: {} for tenant: {}", request.getName(), tenantId);

        if (academicYearRepository.existsByTenantIdAndName(tenantId, request.getName())) {
            throw new DuplicateResourceException("Academic year already exists: " + request.getName());
        }

        AcademicYear academicYear = AcademicYear.builder()
                .tenantId(tenantId)
                .name(request.getName())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(request.getIsActive() != null ? request.getIsActive() : false)
                .build();

        AcademicYear saved = academicYearRepository.save(academicYear);
        log.info("Academic year created with ID: {}", saved.getId());

        return mapToResponse(saved);
    }

    public AcademicYearResponse getAcademicYearById(String id) {
        String tenantId = TenantContext.getTenantId();
        AcademicYear academicYear = academicYearRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));
        return mapToResponse(academicYear);
    }

    public PageResponse<AcademicYearResponse> searchAcademicYears(TablePageRequest request) {
        String tenantId = TenantContext.getTenantId();
        Pageable pageable = TableQueryUtils.buildPageable(request, "startDate");
        Specification<AcademicYear> specification = TableQueryUtils.buildSpecification(
                tenantId,
                request,
                List.of("name"),
                List.of("name", "isActive")
        );

        Page<AcademicYear> page = academicYearRepository.findAll(specification, pageable);
        return buildPageResponse(page.map(this::mapToResponse));
    }

    @Transactional
    public AcademicYearResponse updateAcademicYear(String id, AcademicYearRequest request) {
        String tenantId = TenantContext.getTenantId();
        AcademicYear academicYear = academicYearRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));

        academicYear.setName(request.getName());
        academicYear.setStartDate(request.getStartDate());
        academicYear.setEndDate(request.getEndDate());
        academicYear.setIsActive(request.getIsActive());

        return mapToResponse(academicYearRepository.save(academicYear));
    }

    @Transactional
    public void deleteAcademicYear(String id) {
        String tenantId = TenantContext.getTenantId();
        AcademicYear academicYear = academicYearRepository.findByIdAndTenantId(id, tenantId)
                .orElseThrow(() -> new ResourceNotFoundException("Academic year not found"));
        academicYearRepository.delete(academicYear);
        log.info("Academic year deleted: {}", id);
    }

    private PageResponse<AcademicYearResponse> buildPageResponse(Page<AcademicYearResponse> page) {
        return PageResponse.<AcademicYearResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .first(page.isFirst())
                .build();
    }

    private AcademicYearResponse mapToResponse(AcademicYear academicYear) {
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
}
