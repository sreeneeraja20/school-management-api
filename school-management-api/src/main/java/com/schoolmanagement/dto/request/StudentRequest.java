package com.schoolmanagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentRequest {

    @NotBlank(message = "Student name is required")
    private String name;

    @NotBlank(message = "Roll number is required")
    private String rollNumber;

    @NotNull(message = "Class ID is required")
    private String classId;

    @NotNull(message = "Section ID is required")
    private String sectionId;

    @NotNull(message = "Academic year ID is required")
    private String academicYearId;

    private LocalDate dateOfBirth;

    private String gender;

    private String parentName;

    private String parentPhone;

    private String parentEmail;

    private String address;

    private String photoUrl;
}