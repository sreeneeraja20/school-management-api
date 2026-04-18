package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<Exam, String> {
    
    Page<Exam> findByTenantId(String tenantId, Pageable pageable);
    
    List<Exam> findByTenantIdAndAcademicYearId(String tenantId, String academicYearId);
    
    Page<Exam> findByTenantIdAndAcademicYearId(String tenantId, String academicYearId, Pageable pageable);
    
    @Query("SELECT e FROM Exam e WHERE e.tenantId = :tenantId AND (e.name LIKE %:search%)")
    Page<Exam> searchExams(@Param("tenantId") String tenantId,
                           @Param("search") String search,
                           Pageable pageable);
    
    Optional<Exam> findByTenantIdAndName(String tenantId, String name);
}