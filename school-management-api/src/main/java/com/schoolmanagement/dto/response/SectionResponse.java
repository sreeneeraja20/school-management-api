package com.schoolmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SectionResponse {

    private String id;

    private String classId;

    private String name;

    private Integer maxStudents;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}