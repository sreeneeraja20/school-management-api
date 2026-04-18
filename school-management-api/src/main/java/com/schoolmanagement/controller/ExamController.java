package com.schoolmanagement.controller;

import com.schoolmanagement.dto.request.ExamRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.dto.response.ExamResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.service.ExamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@Tag(name = "Exams", description = "Exam management endpoints")
@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
public class ExamController {

    private final ExamService examService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create exam", description = "Create a new exam")
    public ResponseEntity<ApiResponse<ExamResponse>> createExam(@Valid @RequestBody ExamRequest request) {
        log.info("Creating exam: {}", request.getName());
        ExamResponse response = examService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Exam created successfully", response));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get exam by ID", description = "Get details of a specific exam")
    public ResponseEntity<ApiResponse<ExamResponse>> getExam(@PathVariable String id) {
        log.info("Fetching exam: {}", id);
        ExamResponse response = examService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Exam retrieved successfully", response));
    }

    @PostMapping("/search")
    @Operation(summary = "Search exams", description = "Search/filter/sort exams with pagination")
    public ResponseEntity<ApiResponse<PageResponse<ExamResponse>>> searchExams(
            @RequestBody(required = false) TablePageRequest request) {
        TablePageRequest pageRequest = request == null ? new TablePageRequest() : request;
        log.info("Searching exams - page: {}, size: {}", pageRequest.getPage(), pageRequest.getSize());
        PageResponse<ExamResponse> response = examService.getAll(pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Exams retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update exam", description = "Update exam details")
    public ResponseEntity<ApiResponse<ExamResponse>> updateExam(
            @PathVariable String id,
            @Valid @RequestBody ExamRequest request) {
        log.info("Updating exam: {}", id);
        ExamResponse response = examService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Exam updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete exam", description = "Delete an exam")
    public ResponseEntity<ApiResponse<String>> deleteExam(@PathVariable String id) {
        log.info("Deleting exam: {}", id);
        examService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Exam deleted successfully", null));
    }
}
