package com.schoolmanagement.repository;

import com.schoolmanagement.entity.DayOfWeek;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DayOfWeekRepository extends JpaRepository<DayOfWeek, String> {
    
    Optional<DayOfWeek> findByTenantIdAndCode(String tenantId, String code);
    
    List<DayOfWeek> findByTenantIdAndIsActiveTrueOrderBySortOrder(String tenantId);
    
    List<DayOfWeek> findByTenantIdAndIsWorkingDayTrueAndIsActiveTrueOrderBySortOrder(String tenantId);
    
    boolean existsByTenantIdAndCode(String tenantId, String code);
}