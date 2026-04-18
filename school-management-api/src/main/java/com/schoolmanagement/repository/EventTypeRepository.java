package com.schoolmanagement.repository;

import com.schoolmanagement.entity.EventType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, String> {
    
    Optional<EventType> findByTenantIdAndName(String tenantId, String name);
    
    List<EventType> findByTenantIdAndIsActiveTrue(String tenantId);
    
    Page<EventType> findByTenantId(String tenantId, Pageable pageable);
    
    boolean existsByTenantIdAndName(String tenantId, String name);
}