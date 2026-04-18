package com.schoolmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TablePageRequest {
    @Builder.Default
    private int page = 0;

    @Builder.Default
    private int size = 10;

    private String sortField;

    @Builder.Default
    private String sortOrder = "asc";

    private String globalSearch;

    private List<String> globalSearchFields;

    private Map<String, Object> columnFilters;
}
