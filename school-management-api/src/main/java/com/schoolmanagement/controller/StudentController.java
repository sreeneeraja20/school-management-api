package com.schoolmanagement.controller;

import com.schoolmanagement.dto.request.StudentRequest;
import com.schoolmanagement.dto.request.TablePageRequest;
import com.schoolmanagement.dto.response.ApiResponse;
import com.schoolmanagement.dto.response.PageResponse;
import com.schoolmanagement.dto.response.StudentResponse;
import com.schoolmanagement.service.StudentService;
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
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Students", description = "Student management endpoints")
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create student", description = "Create a new student")
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(@Valid @RequestBody StudentRequest request) {
        log.info("Creating student: {}", request.getName());
        StudentResponse response = studentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Student created successfully", response));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Get student by ID", description = "Get details of a specific student")
    public ResponseEntity<ApiResponse<StudentResponse>> getStudent(@PathVariable String id) {
        log.info("Fetching student: {}", id);
        StudentResponse response = studentService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Student retrieved successfully", response));
    }

    @PostMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @Operation(summary = "Search students", description = "Search/filter/sort students with pagination")
    public ResponseEntity<ApiResponse<PageResponse<StudentResponse>>> searchStudents(
            @RequestBody(required = false) TablePageRequest request) {
        TablePageRequest pageRequest = request == null ? new TablePageRequest() : request;
        log.info("Searching students - page: {}, size: {}", pageRequest.getPage(), pageRequest.getSize());
        PageResponse<StudentResponse> response = studentService.getAll(pageRequest);
        return ResponseEntity.ok(ApiResponse.success("Students retrieved successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update student", description = "Update student details")
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @PathVariable String id,
            @Valid @RequestBody StudentRequest request) {
        log.info("Updating student: {}", id);
        StudentResponse response = studentService.update(id, request);
        return ResponseEntity.ok(ApiResponse.success("Student updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete student", description = "Delete a student")
    public ResponseEntity<ApiResponse<String>> deleteStudent(@PathVariable String id) {
        log.info("Deleting student: {}", id);
        studentService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Student deleted successfully", null));
    }
}
