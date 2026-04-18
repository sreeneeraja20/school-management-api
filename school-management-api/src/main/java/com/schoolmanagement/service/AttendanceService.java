package com.schoolmanagement.service;

import com.schoolmanagement.dto.request.AttendanceRequest;
import com.schoolmanagement.dto.response.AttendanceResponse;
import com.schoolmanagement.entity.Attendance;
import com.schoolmanagement.entity.AttendanceStatus;
import com.schoolmanagement.exception.BadRequestException;
import com.schoolmanagement.exception.ResourceNotFoundException;
import com.schoolmanagement.repository.AttendanceRepository;
import com.schoolmanagement.repository.AttendanceStatusRepository;
import com.schoolmanagement.repository.StudentRepository;
import com.schoolmanagement.security.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceStatusRepository attendanceStatusRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void markAttendance(AttendanceRequest request) {
        String tenantId = TenantContext.getTenantId();
        log.info("Marking attendance for {} students on {}", request.getRecords().size(), request.getDate());

        // Validate attendance status exists
        AttendanceStatus status = attendanceStatusRepository.findByTenantIdAndName(tenantId, request.getStatus())
            .orElseThrow(() -> new ResourceNotFoundException("Attendance status not found: " + request.getStatus()));

        for (AttendanceRequest.AttendanceRecord record : request.getRecords()) {
            // Check if record already exists
            Attendance existing = attendanceRepository.findByStudentIdAndDate(record.getStudentId(), request.getDate())
                .orElse(null);

            if (existing != null) {
                // Update existing record
                existing.setStatus(status);
                existing.setRemarks(record.getRemarks());
                existing.setUpdatedAt(LocalDateTime.now());
                attendanceRepository.save(existing);
            } else {
                // Create new record
                Attendance attendance = Attendance.builder()
                    .tenantId(tenantId)
                    .studentId(record.getStudentId())
                    .classId(request.getClassId())
                    .sectionId(request.getSectionId())
                    .date(request.getDate())
                    .status(status)
                    .remarks(record.getRemarks())
                    .markedBy(record.getMarkedBy())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

                attendanceRepository.save(attendance);
            }
        }

        log.info("Attendance marked successfully");
    }

    public List<AttendanceResponse> getAttendanceForDate(String classId, String sectionId, LocalDate date) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching attendance for class: {}, section: {}, date: {}", classId, sectionId, date);

        List<Attendance> records = attendanceRepository.findByTenantIdAndClassIdAndSectionIdAndDate(
            tenantId, classId, sectionId, date);

        return records.stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    public AttendanceResponse getStudentAttendance(String studentId, LocalDate date) {
        String tenantId = TenantContext.getTenantId();
        log.info("Fetching attendance for student: {} on date: {}", studentId, date);

        Attendance attendance = attendanceRepository.findByStudentIdAndDate(studentId, date)
            .orElseThrow(() -> new ResourceNotFoundException("Attendance record not found"));

        if (!attendance.getTenantId().equals(tenantId)) {
            throw new ResourceNotFoundException("Attendance record not found");
        }

        return mapToResponse(attendance);
    }

    private AttendanceResponse mapToResponse(Attendance attendance) {
        return AttendanceResponse.builder()
            .id(attendance.getId())
            .studentId(attendance.getStudentId())
            .date(attendance.getDate())
            .status(attendance.getStatus().getName())
            .remarks(attendance.getRemarks())
            .createdAt(attendance.getCreatedAt())
            .build();
    }
}
