package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Class;
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
public interface ClassRepository extends JpaRepository<Class, String>, JpaSpecificationExecutor<Class> {
    
    Page<Class> findByTenantId(String tenantId, Pageable pageable);
    
    List<Class> findByTenantIdOrderBySortOrder(String tenantId);
    
    Optional<Class> findByTenantIdAndName(String tenantId, String name);

    Optional<Class> findByIdAndTenantId(String id, String tenantId);

    List<Class> findByTenantIdAndIdIn(String tenantId, List<String> ids);
    
    boolean existsByTenantIdAndName(String tenantId, String name);
    
    @Query("SELECT c FROM Class c WHERE c.tenantId = :tenantId AND (c.name LIKE %:search%)")
    Page<Class> searchClasses(@Param("tenantId") String tenantId, 
                              @Param("search") String search, 
                              Pageable pageable);
}
