package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, String> {
    
    Page<Section> findByTenantId(String tenantId, Pageable pageable);
    
    List<Section> findByTenantIdAndClassId(String tenantId, String classId);
    
    Optional<Section> findByTenantIdAndClassIdAndName(String tenantId, String classId, String name);
    
    boolean existsByTenantIdAndClassIdAndName(String tenantId, String classId, String name);
    
    @Query("SELECT s FROM Section s WHERE s.tenantId = :tenantId AND s.classId = :classId AND (s.name LIKE %:search%)")
    Page<Section> searchSections(@Param("tenantId") String tenantId, 
                                 @Param("classId") String classId,
                                 @Param("search") String search, 
                                 Pageable pageable);
}