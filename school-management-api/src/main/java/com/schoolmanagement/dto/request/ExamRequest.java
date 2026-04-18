package com.schoolmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamRequest {

    @NotNull(message = "Academic year ID is required")
    private String academicYearId;

    @NotBlank(message = "Exam name is required")
    private String name;

    @NotBlank(message = "Exam type is required")
    private String type;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal maxMarks;

    private BigDecimal passingMarks;
}