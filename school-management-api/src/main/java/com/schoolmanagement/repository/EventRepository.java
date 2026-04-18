package com.schoolmanagement.repository;

import com.schoolmanagement.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, String> {
    
    Page<Event> findByTenantId(String tenantId, Pageable pageable);
    
    @Query("SELECT e FROM Event e WHERE e.tenantId = :tenantId AND e.startDate >= :startDate AND e.endDate <= :endDate ORDER BY e.startDate")
    List<Event> findEventsBetweenDates(@Param("tenantId") String tenantId,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT e FROM Event e WHERE e.tenantId = :tenantId AND e.startDate >= :today AND e.isActive = true ORDER BY e.startDate")
    List<Event> findUpcomingEvents(@Param("tenantId") String tenantId,
                                   @Param("today") LocalDate today);
    
    @Query("SELECT e FROM Event e WHERE e.tenantId = :tenantId AND (e.title LIKE %:search% OR e.description LIKE %:search%)")
    Page<Event> searchEvents(@Param("tenantId") String tenantId,
                             @Param("search") String search,
                             Pageable pageable);
}