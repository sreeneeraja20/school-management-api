package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, String>, JpaSpecificationExecutor<Student> {
    
    Page<Student> findByTenantId(String tenantId, Pageable pageable);
    
    List<Student> findByTenantIdAndClassIdAndSectionId(String tenantId, String classId, String sectionId);
    
    Page<Student> findByTenantIdAndClassId(String tenantId, String classId, Pageable pageable);
    
    Page<Student> findByTenantIdAndSectionId(String tenantId, String sectionId, Pageable pageable);
    
    Optional<Student> findByTenantIdAndRollNumber(String tenantId, String rollNumber);

    Optional<Student> findByIdAndTenantId(String id, String tenantId);
    
    @Query("SELECT s FROM Student s WHERE s.tenantId = :tenantId AND s.academicYearId = :academicYearId AND (s.name LIKE %:search% OR s.rollNumber LIKE %:search%)")
    Page<Student> searchStudents(@Param("tenantId") String tenantId,
                                 @Param("academicYearId") String academicYearId,
                                 @Param("search") String search,
                                 Pageable pageable);
    
    @Query("SELECT COUNT(s) FROM Student s WHERE s.tenantId = :tenantId AND s.academicYearId = :academicYearId")
    long countByTenantAndAcademicYear(@Param("tenantId") String tenantId, @Param("academicYearId") String academicYearId);
}
