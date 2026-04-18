package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {
    
    Page<Staff> findByTenantId(String tenantId, Pageable pageable);
    
    List<Staff> findByTenantIdAndIsActive(String tenantId, Boolean isActive);
    
    Optional<Staff> findByTenantIdAndEmail(String tenantId, String email);
    
    @Query("SELECT s FROM Staff s WHERE s.tenantId = :tenantId AND (s.name LIKE %:search% OR s.email LIKE %:search% OR s.phone LIKE %:search%)")
    Page<Staff> searchStaff(@Param("tenantId") String tenantId, 
                            @Param("search") String search, 
                            Pageable pageable);
    
    @Query("SELECT COUNT(s) FROM Staff s WHERE s.tenantId = :tenantId AND s.isActive = true")
    long countActiveStaff(@Param("tenantId") String tenantId);
}