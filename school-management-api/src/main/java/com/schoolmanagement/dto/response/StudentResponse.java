package com.schoolmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentResponse {

    private String id;

    private String name;

    private String rollNumber;

    private String classId;

    private String className;

    private String sectionId;

    private String sectionName;

    private String academicYearId;

    private LocalDate dateOfBirth;

    private String gender;

    private String parentName;

    private String parentPhone;

    private String parentEmail;

    private String address;

    private String photoUrl;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}