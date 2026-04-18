package com.schoolmanagement.repository;

import com.schoolmanagement.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
    
    Optional<UserRole> findByTenantIdAndName(String tenantId, String name);
    
    List<UserRole> findByTenantIdAndIsActiveTrue(String tenantId);
    
    Page<UserRole> findByTenantId(String tenantId, Pageable pageable);
    
    boolean existsByTenantIdAndName(String tenantId, String name);
}