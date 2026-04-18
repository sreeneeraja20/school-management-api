package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Subject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, String> {
    
    Page<Subject> findByTenantId(String tenantId, Pageable pageable);
    
    List<Subject> findByTenantId(String tenantId);
    
    Optional<Subject> findByTenantIdAndName(String tenantId, String name);
    
    boolean existsByTenantIdAndName(String tenantId, String name);
    
    @Query("SELECT s FROM Subject s WHERE s.tenantId = :tenantId AND (s.name LIKE %:search% OR s.code LIKE %:search%)")
    Page<Subject> searchSubjects(@Param("tenantId") String tenantId, 
                                 @Param("search") String search, 
                                 Pageable pageable);
}