package com.schoolmanagement.util;

import com.schoolmanagement.dto.request.TablePageRequest;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class TableQueryUtils {

    private TableQueryUtils() {
    }

    public static Pageable buildPageable(TablePageRequest request, String defaultSortField) {
        int page = Math.max(request.getPage(), 0);
        int size = request.getSize() > 0 ? request.getSize() : 10;
        return PageRequest.of(page, size, buildSort(request, defaultSortField));
    }

    public static Sort buildSort(TablePageRequest request, String defaultSortField) {
        String sortField = StringUtils.hasText(request.getSortField()) ? request.getSortField() : defaultSortField;
        Sort.Direction direction = "desc".equalsIgnoreCase(request.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        return Sort.by(direction, sortField);
    }

    public static <T> Specification<T> buildSpecification(
            String tenantId,
            TablePageRequest request,
            List<String> defaultGlobalSearchFields,
            List<String> allowedFilterFields
    ) {
        Specification<T> specification = tenantSpecification(tenantId);

        if (StringUtils.hasText(request.getGlobalSearch())) {
            List<String> globalFields = (request.getGlobalSearchFields() == null || request.getGlobalSearchFields().isEmpty())
                    ? defaultGlobalSearchFields
                    : request.getGlobalSearchFields().stream()
                    .filter(defaultGlobalSearchFields::contains)
                    .collect(Collectors.toList());
            if (globalFields.isEmpty()) {
                globalFields = defaultGlobalSearchFields;
            }
            specification = specification.and(globalSearchSpecification(globalFields, request.getGlobalSearch()));
        }

        Map<String, Object> filters = request.getColumnFilters() == null ? Collections.emptyMap() : request.getColumnFilters();
        for (Map.Entry<String, Object> entry : filters.entrySet()) {
            if (!allowedFilterFields.contains(entry.getKey()) || entry.getValue() == null) {
                continue;
            }

            if (entry.getValue() instanceof String value && !StringUtils.hasText(value)) {
                continue;
            }

            specification = specification.and(columnFilterSpecification(entry.getKey(), entry.getValue()));
        }

        return specification;
    }

    private static <T> Specification<T> tenantSpecification(String tenantId) {
        return (root, query, cb) -> cb.equal(root.get("tenantId"), tenantId);
    }

    private static <T> Specification<T> globalSearchSpecification(List<String> fields, String searchValue) {
        return (root, query, cb) -> {
            String pattern = "%" + escapeLikePattern(searchValue.trim().toLowerCase()) + "%";
            Predicate[] predicates = fields.stream()
                    .map(field -> cb.like(cb.lower(resolvePath(root, field).as(String.class)), pattern, '\\'))
                    .toArray(Predicate[]::new);
            return cb.or(predicates);
        };
    }

    private static <T> Specification<T> columnFilterSpecification(String field, Object value) {
        return (root, query, cb) -> {
            Path<?> path = resolvePath(root, field);
            if (value instanceof String stringValue) {
                return cb.like(cb.lower(path.as(String.class)), "%" + escapeLikePattern(stringValue.trim().toLowerCase()) + "%", '\\');
            }
            return cb.equal(path, value);
        };
    }

    private static Path<?> resolvePath(jakarta.persistence.criteria.Root<?> root, String field) {
        if (!field.matches("^[A-Za-z0-9_\\.]+$")) {
            throw new IllegalArgumentException("Invalid field path: " + field);
        }
        if (!field.contains(".")) {
            return root.get(field);
        }
        Path<?> path = root;
        for (String part : field.split("\\.")) {
            path = path.get(part);
        }
        return path;
    }

    private static String escapeLikePattern(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
