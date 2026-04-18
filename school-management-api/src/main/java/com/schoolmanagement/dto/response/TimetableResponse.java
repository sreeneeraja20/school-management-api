package com.schoolmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimetableResponse {

    private String id;

    private String classId;

    private String sectionId;

    private String dayOfWeek;

    private Integer periodNumber;

    private LocalTime startTime;

    private LocalTime endTime;

    private String subjectId;

    private String staffId;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}