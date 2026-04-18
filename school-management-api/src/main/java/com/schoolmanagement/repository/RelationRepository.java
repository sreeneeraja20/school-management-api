package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Relation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationRepository extends JpaRepository<Relation, String> {
    
    Optional<Relation> findByTenantIdAndName(String tenantId, String name);
    
    List<Relation> findByTenantIdAndIsActiveTrue(String tenantId);
    
    Page<Relation> findByTenantId(String tenantId, Pageable pageable);
    
    boolean existsByTenantIdAndName(String tenantId, String name);
}