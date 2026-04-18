package com.schoolmanagement.repository;

import com.schoolmanagement.entity.ParentAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParentAccountRepository extends JpaRepository<ParentAccount, String> {
    
    Optional<ParentAccount> findByEmailAndTenantId(String email, String tenantId);
    
    Page<ParentAccount> findByTenantId(String tenantId, Pageable pageable);
    
    @Query("SELECT p FROM ParentAccount p WHERE p.tenantId = :tenantId AND (p.name LIKE %:search% OR p.email LIKE %:search% OR p.phone LIKE %:search%)")
    Page<ParentAccount> searchParents(@Param("tenantId") String tenantId,
                                      @Param("search") String search,
                                      Pageable pageable);
    
    @Query("SELECT COUNT(p) FROM ParentAccount p WHERE p.tenantId = :tenantId AND p.isActive = true")
    long countActiveParents(@Param("tenantId") String tenantId);
    
    @Query("SELECT COUNT(p) FROM ParentAccount p WHERE p.tenantId = :tenantId AND p.isFirstLogin = true")
    long countNeverLoggedIn(@Param("tenantId") String tenantId);
    
    boolean existsByEmailAndTenantId(String email, String tenantId);
}