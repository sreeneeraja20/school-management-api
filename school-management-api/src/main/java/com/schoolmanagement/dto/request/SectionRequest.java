package com.schoolmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionRequest {

    @NotNull(message = "Class ID is required")
    private String classId;

    @NotBlank(message = "Section name is required")
    private String name;

    private Integer maxStudents;
}