package com.schoolmanagement.controller;

import com.schoolmanagement.dto.request.AttendanceRequest;
import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.dto.response.AttendanceResponse;
import com.schoolmanagement.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
@Tag(name = "Attendance", description = "Attendance management endpoints")
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping("/mark")
    @Operation(summary = "Mark attendance", description = "Mark attendance for multiple students")
    public ResponseEntity<ApiResponse<String>> markAttendance(@Valid @RequestBody AttendanceRequest request) {
        log.info("Marking attendance for date: {}", request.getDate());
        attendanceService.markAttendance(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Attendance marked successfully", null));
    }

    @GetMapping
    @Operation(summary = "Get attendance for date", description = "Get attendance records for a specific date and class")
    public ResponseEntity<ApiResponse<List<AttendanceResponse>>> getAttendance(
            @RequestParam String classId,
            @RequestParam String sectionId,
            @RequestParam LocalDate date) {
        log.info("Fetching attendance for class: {}, section: {}, date: {}", classId, sectionId, date);
        List<AttendanceResponse> response = attendanceService.getAttendanceForDate(classId, sectionId, date);
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved successfully", response));
    }

    @GetMapping("/student/{studentId}")
    @Operation(summary = "Get student attendance", description = "Get attendance record for a specific student on a date")
    public ResponseEntity<ApiResponse<AttendanceResponse>> getStudentAttendance(
            @PathVariable String studentId,
            @RequestParam LocalDate date) {
        log.info("Fetching attendance for student: {} on date: {}", studentId, date);
        AttendanceResponse response = attendanceService.getStudentAttendance(studentId, date);
        return ResponseEntity.ok(ApiResponse.success("Attendance retrieved successfully", response));
    }
}