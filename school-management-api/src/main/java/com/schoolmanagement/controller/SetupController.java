package com.schoolmanagement.controller;

import com.schoolmanagement.dto.request.*;
import com.schoolmanagement.dto.response.*;
import com.schoolmanagement.service.SetupService;
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
@RequestMapping("/api/setup")
@RequiredArgsConstructor
@Tag(name = "Setup", description = "School setup configuration endpoints")
@PreAuthorize("hasRole('ADMIN')")
public class SetupController {

    private final SetupService setupService;

    // ===================== ACADEMIC YEAR =====================

    @PostMapping("/academic-years")
    @Operation(summary = "Create academic year", description = "Create a new academic year")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> createAcademicYear(@Valid @RequestBody AcademicYearRequest request) {
        log.info("Creating academic year: {}", request.getName());
        AcademicYearResponse response = setupService.createAcademicYear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Academic year created successfully", response));
    }

    @GetMapping("/academic-years/{id}")
    @Operation(summary = "Get academic year by ID", description = "Get details of a specific academic year")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> getAcademicYear(@PathVariable String id) {
        log.info("Fetching academic year: {}", id);
        AcademicYearResponse response = setupService.getAcademicYearById(id);
        return ResponseEntity.ok(ApiResponse.success("Academic year retrieved successfully", response));
    }

    @GetMapping("/academic-years")
    @Operation(summary = "Get all academic years", description = "Get paginated list of academic years")
    public ResponseEntity<ApiResponse<PageResponse<AcademicYearResponse>>> getAllAcademicYears(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all academic years - page: {}, size: {}", page, size);
        PageResponse<AcademicYearResponse> response = setupService.getAllAcademicYears(page, size);
        return ResponseEntity.ok(ApiResponse.success("Academic years retrieved successfully", response));
    }

    @PutMapping("/academic-years/{id}")
    @Operation(summary = "Update academic year", description = "Update academic year details")
    public ResponseEntity<ApiResponse<AcademicYearResponse>> updateAcademicYear(
            @PathVariable String id,
            @Valid @RequestBody AcademicYearRequest request) {
        log.info("Updating academic year: {}", id);
        AcademicYearResponse response = setupService.updateAcademicYear(id, request);
        return ResponseEntity.ok(ApiResponse.success("Academic year updated successfully", response));
    }

    @DeleteMapping("/academic-years/{id}")
    @Operation(summary = "Delete academic year", description = "Delete an academic year")
    public ResponseEntity<ApiResponse<String>> deleteAcademicYear(@PathVariable String id) {
        log.info("Deleting academic year: {}", id);
        setupService.deleteAcademicYear(id);
        return ResponseEntity.ok(ApiResponse.success("Academic year deleted successfully", null));
    }

    // ===================== CLASS =====================

    @PostMapping("/classes")
    @Operation(summary = "Create class", description = "Create a new class")
    public ResponseEntity<ApiResponse<ClassResponse>> createClass(@Valid @RequestBody ClassRequest request) {
        log.info("Creating class: {}", request.getName());
        ClassResponse response = setupService.createClass(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Class created successfully", response));
    }

    @GetMapping("/classes/{id}")
    @Operation(summary = "Get class by ID", description = "Get details of a specific class")
    public ResponseEntity<ApiResponse<ClassResponse>> getClass(@PathVariable String id) {
        log.info("Fetching class: {}", id);
        ClassResponse response = setupService.getClassById(id);
        return ResponseEntity.ok(ApiResponse.success("Class retrieved successfully", response));
    }

    @GetMapping("/classes")
    @Operation(summary = "Get all classes", description = "Get paginated list of classes")
    public ResponseEntity<ApiResponse<PageResponse<ClassResponse>>> getAllClasses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all classes - page: {}, size: {}", page, size);
        PageResponse<ClassResponse> response = setupService.getAllClasses(page, size);
        return ResponseEntity.ok(ApiResponse.success("Classes retrieved successfully", response));
    }

    @PutMapping("/classes/{id}")
    @Operation(summary = "Update class", description = "Update class details")
    public ResponseEntity<ApiResponse<ClassResponse>> updateClass(
            @PathVariable String id,
            @Valid @RequestBody ClassRequest request) {
        log.info("Updating class: {}", id);
        ClassResponse response = setupService.updateClass(id, request);
        return ResponseEntity.ok(ApiResponse.success("Class updated successfully", response));
    }

    @DeleteMapping("/classes/{id}")
    @Operation(summary = "Delete class", description = "Delete a class")
    public ResponseEntity<ApiResponse<String>> deleteClass(@PathVariable String id) {
        log.info("Deleting class: {}", id);
        setupService.deleteClass(id);
        return ResponseEntity.ok(ApiResponse.success("Class deleted successfully", null));
    }

    // ===================== SECTION =====================

    @PostMapping("/sections")
    @Operation(summary = "Create section", description = "Create a new section")
    public ResponseEntity<ApiResponse<SectionResponse>> createSection(@Valid @RequestBody SectionRequest request) {
        log.info("Creating section: {}", request.getName());
        SectionResponse response = setupService.createSection(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Section created successfully", response));
    }

    @GetMapping("/sections/{id}")
    @Operation(summary = "Get section by ID", description = "Get details of a specific section")
    public ResponseEntity<ApiResponse<SectionResponse>> getSection(@PathVariable String id) {
        log.info("Fetching section: {}", id);
        SectionResponse response = setupService.getSectionById(id);
        return ResponseEntity.ok(ApiResponse.success("Section retrieved successfully", response));
    }

    @GetMapping("/sections")
    @Operation(summary = "Get all sections", description = "Get paginated list of sections")
    public ResponseEntity<ApiResponse<PageResponse<SectionResponse>>> getAllSections(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all sections - page: {}, size: {}", page, size);
        PageResponse<SectionResponse> response = setupService.getAllSections(page, size);
        return ResponseEntity.ok(ApiResponse.success("Sections retrieved successfully", response));
    }

    @PutMapping("/sections/{id}")
    @Operation(summary = "Update section", description = "Update section details")
    public ResponseEntity<ApiResponse<SectionResponse>> updateSection(
            @PathVariable String id,
            @Valid @RequestBody SectionRequest request) {
        log.info("Updating section: {}", id);
        SectionResponse response = setupService.updateSection(id, request);
        return ResponseEntity.ok(ApiResponse.success("Section updated successfully", response));
    }

    @DeleteMapping("/sections/{id}")
    @Operation(summary = "Delete section", description = "Delete a section")
    public ResponseEntity<ApiResponse<String>> deleteSection(@PathVariable String id) {
        log.info("Deleting section: {}", id);
        setupService.deleteSection(id);
        return ResponseEntity.ok(ApiResponse.success("Section deleted successfully", null));
    }

    // ===================== SUBJECT =====================

    @PostMapping("/subjects")
    @Operation(summary = "Create subject", description = "Create a new subject")
    public ResponseEntity<ApiResponse<SubjectResponse>> createSubject(@Valid @RequestBody SubjectRequest request) {
        log.info("Creating subject: {}", request.getName());
        SubjectResponse response = setupService.createSubject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Subject created successfully", response));
    }

    @GetMapping("/subjects/{id}")
    @Operation(summary = "Get subject by ID", description = "Get details of a specific subject")
    public ResponseEntity<ApiResponse<SubjectResponse>> getSubject(@PathVariable String id) {
        log.info("Fetching subject: {}", id);
        SubjectResponse response = setupService.getSubjectById(id);
        return ResponseEntity.ok(ApiResponse.success("Subject retrieved successfully", response));
    }

    @GetMapping("/subjects")
    @Operation(summary = "Get all subjects", description = "Get paginated list of subjects")
    public ResponseEntity<ApiResponse<PageResponse<SubjectResponse>>> getAllSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Fetching all subjects - page: {}, size: {}", page, size);
        PageResponse<SubjectResponse> response = setupService.getAllSubjects(page, size);
        return ResponseEntity.ok(ApiResponse.success("Subjects retrieved successfully", response));
    }

    @PutMapping("/subjects/{id}")
    @Operation(summary = "Update subject", description = "Update subject details")
    public ResponseEntity<ApiResponse<SubjectResponse>> updateSubject(
            @PathVariable String id,
            @Valid @RequestBody SubjectRequest request) {
        log.info("Updating subject: {}", id);
        SubjectResponse response = setupService.updateSubject(id, request);
        return ResponseEntity.ok(ApiResponse.success("Subject updated successfully", response));
    }

    @DeleteMapping("/subjects/{id}")
    @Operation(summary = "Delete subject", description = "Delete a subject")
    public ResponseEntity<ApiResponse<String>> deleteSubject(@PathVariable String id) {
        log.info("Deleting subject: {}", id);
        setupService.deleteSubject(id);
        return ResponseEntity.ok(ApiResponse.success("Subject deleted successfully", null));
    }
}