package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, String> {
    
    Optional<Attendance> findByStudentIdAndDate(String studentId, LocalDate date);
    
    List<Attendance> findByTenantIdAndClassIdAndSectionIdAndDate(String tenantId, String classId, String sectionId, LocalDate date);
    
    Page<Attendance> findByStudentId(String studentId, Pageable pageable);
    
    @Query(value = "SELECT a FROM Attendance a WHERE a.tenantId = :tenantId AND a.studentId = :studentId AND YEAR(a.date) = :year AND MONTH(a.date) = :month ORDER BY a.date DESC",
           nativeQuery = false)
    List<Attendance> findStudentAttendanceByMonth(@Param("tenantId") String tenantId,
                                                   @Param("studentId") String studentId,
                                                   @Param("year") int year,
                                                   @Param("month") int month);
    
    @Query("SELECT a FROM Attendance a WHERE a.tenantId = :tenantId AND a.classId = :classId AND a.sectionId = :sectionId AND a.date = :date ORDER BY a.studentId")
    List<Attendance> findByClassAndSectionAndDate(@Param("tenantId") String tenantId,
                                                   @Param("classId") String classId,
                                                   @Param("sectionId") String sectionId,
                                                   @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Attendance a WHERE a.tenantId = :tenantId AND a.studentId = :studentId AND a.status.name = 'PRESENT'")
    long countPresentDays(@Param("tenantId") String tenantId, @Param("studentId") String studentId);
}