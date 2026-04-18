package com.schoolmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamResponse {

    private String id;

    private String academicYearId;

    private String name;

    private String type;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal maxMarks;

    private BigDecimal passingMarks;

    private Boolean isPublished;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}