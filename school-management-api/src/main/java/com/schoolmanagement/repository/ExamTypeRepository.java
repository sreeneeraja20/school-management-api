package com.schoolmanagement.repository;

import com.schoolmanagement.entity.ExamType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamTypeRepository extends JpaRepository<ExamType, String> {
    
    Optional<ExamType> findByTenantIdAndName(String tenantId, String name);
    
    List<ExamType> findByTenantIdAndIsActiveTrue(String tenantId);
    
    Page<ExamType> findByTenantId(String tenantId, Pageable pageable);
    
    boolean existsByTenantIdAndName(String tenantId, String name);
}